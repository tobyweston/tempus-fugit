/*
 * Copyright (c) 2009-2015, toby weston & tempus-fugit committers
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

import com.google.code.tempusfugit.condition.Conditions;
import com.google.code.tempusfugit.temporal.Duration;
import com.google.code.tempusfugit.temporal.Timeout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.concurrency.ThreadUtils.resetInterruptFlagWhen;
import static com.google.code.tempusfugit.condition.Conditions.*;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class ExecutorServiceShutdown {

    private final ExecutorService executor;

    private ExecutorServiceShutdown(ExecutorService executor) {
        this.executor = executor;
    }

    public static ExecutorServiceShutdown shutdown(ExecutorService executor) {
        return new ExecutorServiceShutdown(executor);
    }

    /* @return {@code true} if the executor terminated and {@code false} if the timeout elapsed before termination */
    public Boolean waitingForCompletion(final Duration duration) {
        if (executor == null)
            return false;
        executor.shutdown();
        return resetInterruptFlagWhen(awaitingTerminationIsInterrupted(duration));
    }

    public Boolean waitingForShutdown(Timeout timeout) throws TimeoutException, InterruptedException {
        if (executor == null)
            return false;
        executor.shutdownNow();
        waitOrTimeout(Conditions.shutdown(executor), timeout);
        return true;
    }

    private Interruptible<Boolean> awaitingTerminationIsInterrupted(final Duration timeout) {
        return () -> executor.awaitTermination(timeout.inMillis(), MILLISECONDS);
    }

}
