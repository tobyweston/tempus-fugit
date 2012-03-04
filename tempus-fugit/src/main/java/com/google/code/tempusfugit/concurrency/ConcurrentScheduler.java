/*
 * Copyright (c) 2009-2012, toby weston & tempus-fugit committers
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

import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.concurrency.ExecutorServiceShutdown.shutdown;
import static com.google.code.tempusfugit.temporal.Duration.days;

class ConcurrentScheduler implements RunnerScheduler {

    private final ExecutorService executor;

    public ConcurrentScheduler(ExecutorService executor) {
        this.executor = executor;
    }

    public void schedule(Runnable childStatement) {
        executor.submit(childStatement);
    }

    public void finished() {
        Boolean completed = shutdown(executor).waitingForCompletion(days(365));
        if (Thread.currentThread().isInterrupted())
            throw new RuntimeException(new InterruptedException("scheduler shutdown was interrupted"));
        if (!completed)
            throw new RuntimeException(new TimeoutException("scheduler shutdown timed out before tests completed"));
    }
}
