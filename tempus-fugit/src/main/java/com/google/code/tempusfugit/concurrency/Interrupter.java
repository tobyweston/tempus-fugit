/*
 * Copyright (c) 2009-2010, tempus-fugit committers
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

import com.google.code.tempusfugit.temporal.Clock;
import com.google.code.tempusfugit.temporal.Duration;
import com.google.code.tempusfugit.temporal.StopWatch;
import com.google.code.tempusfugit.temporal.Timeout;

import java.util.concurrent.atomic.AtomicLong;

import static com.google.code.tempusfugit.temporal.DefaultClock.now;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitUntil;
import static java.lang.Thread.currentThread;

public final class Interrupter {

    private static final AtomicLong counter = new AtomicLong(0);

    private final Thread threadToInterrupt;
    private Thread interrupterThread;
    private Clock time = now();

    private Interrupter(Thread threadToInterrupt) {
        this.threadToInterrupt = threadToInterrupt;
    }

    public static Interrupter interrupt(Thread thread) {
        return new Interrupter(thread);
    }

    Interrupter using(Clock time) {
        if (interrupterThread != null)
            throw new IllegalStateException("Controlling time after events have been put in motion will have no affect");
        this.time = time;
        return this;
    }

    public Interrupter after(final Duration duration) {
        final Timeout timeout = timeout(duration, startStopWatch());
        interrupterThread = new Thread(new Runnable() {
            public void run() {
                try {
                    waitUntil(timeout);
                    if (!interrupterThread.isInterrupted()) {
                        Interrupter.this.threadToInterrupt.interrupt();
                    }
                } catch (InterruptedException e) {
                    currentThread().interrupt();
                }
            }
        }, "Interrupter-Thread-" + counter.incrementAndGet());
        interrupterThread.start();
        return this;
    }

    public void cancel() {
        if (interrupterThread.isAlive())
            interrupterThread.interrupt();
    }

    private StopWatch startStopWatch() {
        return StopWatch.start(time);
    }

}
