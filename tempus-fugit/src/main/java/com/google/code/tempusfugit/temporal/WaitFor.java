/*
 * Copyright (c) 2009-2011, tempus-fugit committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.tempusfugit.temporal;

import com.google.code.tempusfugit.concurrency.Callable;

import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.temporal.Duration.millis;

public final class WaitFor {

    public static final Duration SLEEP_PERIOD = millis(50);

    private WaitFor() {
    }

    /** @since 1.1 */
    public static void waitOrTimeout(Condition condition, Timeout timeout) throws InterruptedException, TimeoutException {
        waitOrTimeout(condition, timeout, new ThreadSleep(SLEEP_PERIOD));
    }

    /** @since 1.1 */
    public static void waitOrTimeout(Condition condition, Timeout timeout, Sleeper sleeper) throws TimeoutException, InterruptedException {
        if (success(condition, timeout, sleeper))
            return;
        throw new TimeoutException();
    }

    /** @since 1.2 */
    public static <T, E extends Exception> void waitOrTimeout(Condition condition, Callable<T, E> onTimeout, Timeout timeout) throws InterruptedException, E {
        try {
            waitOrTimeout(condition, timeout);
        } catch (TimeoutException e) {
            onTimeout.call();
        }
    }

    public static void waitUntil(Timeout timeout) throws InterruptedException {
        while (!timeout.hasExpired())
            Thread.sleep(SLEEP_PERIOD.inMillis());
    }

    private static boolean success(Condition condition, Timeout timeout, Sleeper sleeper) throws InterruptedException {
        while (!timeout.hasExpired()) {
            if (condition.isSatisfied()) {
                return true;
            }
            sleeper.sleep();
        }
        return false;
    }

}