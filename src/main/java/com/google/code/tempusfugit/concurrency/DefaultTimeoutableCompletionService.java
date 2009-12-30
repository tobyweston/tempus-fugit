/*
 * Copyright (c) 2009, tempus-fugit committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.google.code.tempusfugit.concurrency;

import com.google.code.tempusfugit.temporal.DateFactory;
import com.google.code.tempusfugit.temporal.DefaultDateFactory;
import com.google.code.tempusfugit.temporal.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.*;

import static com.google.code.tempusfugit.concurrency.Interrupter.interrupt;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static java.lang.Thread.currentThread;

public class DefaultTimeoutableCompletionService implements TimeoutableCompletionService {

    private static final boolean INTERRUPT_IF_RUNNING = true;
    private static final Duration DEFAULT_TIMEOUT = seconds(30);

    private final java.util.concurrent.CompletionService completionService;
    private final Duration timeout;
    private final DateFactory time;

    public DefaultTimeoutableCompletionService(CompletionService completionService) {
        this(completionService, DEFAULT_TIMEOUT, new DefaultDateFactory());
    }

    public DefaultTimeoutableCompletionService(CompletionService completionService, Duration timeout, DateFactory time) {
        this.timeout = timeout;
        this.time = time;
        this.completionService = completionService;
    }

    public <T> List<T> submit(List<? extends java.util.concurrent.Callable<T>> tasks) throws ExecutionException, TimeoutException {
        List<Future<T>> submitted = new ArrayList<Future<T>>();
        try {
            for (Callable task : tasks) {
                submitted.add(completionService.submit(task));
            }
            return waitFor(tasks.size(), timeout);
        } finally {
            for (Future<T> future : submitted) {
                future.cancel(INTERRUPT_IF_RUNNING);
            }
        }
    }

    private <T> List<T> waitFor(int tasks, Duration timeout) throws ExecutionException, TimeoutException {
        List<T> completed = new ArrayList<T>();
        Interrupter interrupter = interrupt(currentThread()).using(time).after(timeout);
        try {
            for (int i = 0; i < tasks; i++) {
                try {
                    Future<T> future = completionService.take();
                    completed.add(future.get());
                } catch (InterruptedException e) {
                    throw new TimeoutExceptionWithResults("timed out after " + timeout.toString(), completed);
                }
            }
        } finally {
            interrupter.cancel();
        }
        return completed;
    }
}
