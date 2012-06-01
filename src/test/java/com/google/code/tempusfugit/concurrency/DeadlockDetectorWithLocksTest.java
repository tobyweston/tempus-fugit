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

import com.google.code.tempusfugit.concurrency.kidnapping.*;
import com.google.code.tempusfugit.temporal.Condition;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.code.tempusfugit.concurrency.DeadlockMatcher.detected;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class DeadlockDetectorWithLocksTest {

    private final Cash cash = new InterruptibleLock();
    private final Cat nibbles = new InterruptibleLock();

    private final CountDownLatch latch = new CountDownLatch(2);

    private final Kidnapper kidnapper = new Kidnapper(cash, nibbles, latch);
    private final Negotiator negotiator = new Negotiator(cash, nibbles, latch);

    private final Deadlocks deadlocks = new Deadlocks();

    @Test
    public void noDeadlock() {
        DeadlockDetector.printDeadlocks(deadlocks);
        assertThat(deadlocks, Matchers.not(detected()));
    }

    @Test
    public void detectsLockBasedDeadlock() throws InterruptedException, TimeoutException {
        kidnapper.start();
        negotiator.start();

        waitOrTimeout(deadlock(), timeout(millis(250)));
        verify(deadlocks);

        waitForThreadsToFinish();
    }

    private Condition deadlock() {
        return new Condition() {
            @Override
            public boolean isSatisfied() {
                DeadlockDetector.printDeadlocks(deadlocks);
                return deadlocks.detected();
            }
        };
    }

    private void verify(Deadlocks deadlocks) {
        assertThat(deadlocks.toString(), containsString("Deadlock detected"));
        assertThat(deadlocks.toString(), containsString("Negotiator-Thread"));
        assertThat(deadlocks.toString(), containsString("waiting to lock Monitor of " + ReentrantLock.class.getName()));
        assertThat(deadlocks.toString(), containsString("which is held by \"Kidnapper-Thread"));
        assertThat(deadlocks.toString(), containsString("Kidnapper-Thread"));
        assertThat(deadlocks.toString(), containsString("waiting to lock Monitor of " + ReentrantLock.class.getName()));
        assertThat(deadlocks.toString(), containsString("which is held by \"Negotiator-Thread"));
    }

    private void waitForThreadsToFinish() throws TimeoutException, InterruptedException {
        kidnapper.interruptAndWaitToFinish();
        negotiator.interruptAndWaitToFinish();
    }

}
