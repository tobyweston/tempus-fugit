/*
 * Copyright (c) 2009-2013, toby weston & tempus-fugit committers
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

import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class ConcurrentTestRunner extends BlockJUnit4ClassRunner {

    public ConcurrentTestRunner(Class<?> type) throws InitializationError {
        super(type);
        setScheduler(new ConcurrentScheduler(createExecutor(type)));
    }

    private static ExecutorService createExecutor(Class<?> type) {
        if (type.getAnnotation(Concurrent.class) != null)
            return newFixedThreadPool(type.getAnnotation(Concurrent.class).count(), new ConcurrentTestRunnerThreadFactory());
        return newCachedThreadPool(new ConcurrentTestRunnerThreadFactory());
    }

    private static class ConcurrentTestRunnerThreadFactory implements ThreadFactory {
        private AtomicLong count = new AtomicLong();

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, ConcurrentTestRunner.class.getSimpleName() + "-Thread-" + count.getAndIncrement());
        }
    }
}