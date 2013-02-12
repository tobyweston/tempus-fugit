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

package com.google.code.tempusfugit.temporal;

import com.google.code.tempusfugit.ClassInvariantViolation;
import com.google.code.tempusfugit.concurrency.annotations.ThreadSafe;

import java.util.Date;

import static com.google.code.tempusfugit.temporal.Duration.millis;

/**
 * The {@link ThreadLocal} based stop watch is safe to use with multiple threads as it will time each thread's call
 * between {@link #reset()} (or construction) and {@link #lap()}. It is intended to use one shared {@link Clock}.
 * For example, {@link RealClock} but you could use a clock based on {@link ThreadLocal} if you need to provide different
 * clocks per thread (although, outside of a testing context, I'm not sure why you'd want to).
 *
 * @since 1.2
 */
@ThreadSafe
public class ThreadLocalTimer implements StopWatch {

    private final Clock clock;

    private final ThreadLocal<Long> started;
    private final ThreadLocal<Long> stopped;

    public ThreadLocalTimer(Clock clock) {
        this.clock = clock;
        Date now = this.clock.now();
        started = new ThreadLocalLong(now.getTime());
        stopped = new ThreadLocalLong(now.getTime());
    }

    @Override
    public void reset() {
        started.set(clock.now().getTime());
    }

    @Override
    public void lap() {
        stopped.set(clock.now().getTime());
    }

    @Override
    public Duration elapsedTime() {
        if (stopped.get() < started.get())
            throw new ClassInvariantViolation("please start the stop watch before stopping it");
        return millis(stopped.get() - started.get());
    }

    private static class ThreadLocalLong extends ThreadLocal<Long> {
        private final long initialValue;

        public ThreadLocalLong(long initialValue) {
            this.initialValue = initialValue;
        }

        @Override
        protected Long initialValue() {
            return Long.valueOf(initialValue);
        }
    }
}
