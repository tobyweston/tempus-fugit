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

package com.google.code.tempusfugit.temporal;

import static com.google.code.tempusfugit.temporal.Duration.millis;

import java.util.concurrent.TimeoutException;

public final class WaitFor {

    public static final Duration SLEEP_PERIOD = millis(100);

    private WaitFor() {
    }

    public static void waitOrTimeout(Condition condition, final Duration duration) throws TimeoutException, InterruptedException {
        waitOrTimeout(condition, duration, startDefaultStopWatch());
    }

    public static void waitOrTimeout(Condition condition, final Duration duration, final StopWatch stopWatch) throws TimeoutException, InterruptedException {
        final Timeout timeout = new Timeout(duration, stopWatch);
        if (success(condition, timeout))
            return;
        throw new TimeoutException();
    }

    public static void waitUntil(Timeout timeout) throws InterruptedException {
        while (!timeout.hasExpired())
            Thread.sleep(SLEEP_PERIOD.inMillis());
    }

    private static boolean success(Condition condition, Timeout timeout) throws InterruptedException {
        while (!timeout.hasExpired()) {
            if (condition.isSatisfied()) {
                return true;
            }
            Thread.sleep(SLEEP_PERIOD.inMillis());
        }
        return false;
    }

    private static StopWatch startDefaultStopWatch() {
        return StopWatch.start(new DefaultDateFactory());
    }

}