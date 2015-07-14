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

import com.google.code.tempusfugit.concurrency.kidnapping.*;
import com.google.code.tempusfugit.temporal.Condition;
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
import static org.hamcrest.Matchers.not;

/*
 When run as part of a suite, this intrinsic monitor test will leave the deadlocked threads hanging around. There's no
 way to break the deadlock that the test creates.
 */
public class DeadlockDetectorTest {

    private static final int withEntireStackTrace = Integer.MAX_VALUE;

    private final CountDownLatch latch = new CountDownLatch(2);
    private final Deadlocks deadlocks = new Deadlocks();

    @Test
    public void noDeadlock() {
        DeadlockDetector.printDeadlocks(deadlocks);
        assertThat(deadlocks, not(detected()));
    }

    @Test
    public void detectsLockBasedDeadlock() throws InterruptedException, TimeoutException {
        Cash cash = new InterruptibleLock(latch);
        Cat nibbles = new InterruptibleLock(latch);

        Kidnapper kidnapper = new Kidnapper(cash, nibbles);
        Negotiator negotiator = new Negotiator(cash, nibbles);

        kidnapper.start();
        negotiator.start();

        waitOrTimeout(forDeadlockDetected(), timeout(millis(250)));
        verify(deadlocks, ReentrantLock.class);

        kidnapper.interruptAndWaitToFinish();
        negotiator.interruptAndWaitToFinish();
    }

    @Test
    public void detectsLockBasedDeadlockWithStack() throws InterruptedException, TimeoutException {
        Cash cash = new InterruptibleLock(latch);
        Cat nibbles = new InterruptibleLock(latch);

        Kidnapper kidnapper = new Kidnapper(cash, nibbles);
        Negotiator negotiator = new Negotiator(cash, nibbles);

        kidnapper.start();
        negotiator.start();

        waitOrTimeout(forDeadlockDetected(withEntireStackTrace), timeout(millis(250)));
        verify(deadlocks, ReentrantLock.class);

        assertThat(deadlocks.toString(), containsString(" - com.google.code.tempusfugit.concurrency.kidnapping.Negotiator.run"));
        assertThat(deadlocks.toString(), containsString(" - com.google.code.tempusfugit.concurrency.kidnapping.Kidnapper.run"));

        kidnapper.interruptAndWaitToFinish();
        negotiator.interruptAndWaitToFinish();
    }

    @Test
    public void detectsIntrinsicMonitorBasedDeadlock() throws InterruptedException, TimeoutException {
        Cash cash = new SynchronizedLock(latch);
        Cat nibbles = new SynchronizedLock(latch);

        new Kidnapper(cash, nibbles).start();
        new Negotiator(cash, nibbles).start();

        waitOrTimeout(forDeadlockDetected(), timeout(millis(250)));
        verify(deadlocks, Object.class);
    }

    private Condition forDeadlockDetected() {
        return forDeadlockDetected(0);
    }

    private Condition forDeadlockDetected(final int stackDepth) {
        return new Condition() {
            @Override
            public boolean isSatisfied() {
                DeadlockDetector.printDeadlocks(deadlocks, stackDepth);
                return deadlocks.detected();
            }
        };
    }

    private void verify(Deadlocks deadlocks, Class<?> lockClass) {
        assertThat(deadlocks.toString(), containsString("Deadlock detected"));
        assertThat(deadlocks.toString(), containsString("Negotiator-Thread"));
        assertThat(deadlocks.toString(), containsString("waiting to lock Monitor of " + lockClass.getName()));
        assertThat(deadlocks.toString(), containsString("which is held by \"Kidnapper-Thread"));
        assertThat(deadlocks.toString(), containsString("Kidnapper-Thread"));
        assertThat(deadlocks.toString(), containsString("waiting to lock Monitor of " + lockClass.getName()));
        assertThat(deadlocks.toString(), containsString("which is held by \"Negotiator-Thread"));
    }

}
