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

import static com.google.code.tempusfugit.concurrency.Interrupter.interrupt;
import com.google.code.tempusfugit.temporal.Condition;
import com.google.code.tempusfugit.temporal.DeterministicDateFactory;
import com.google.code.tempusfugit.temporal.Duration;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.WaitFor.SLEEP_PERIOD;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Test;

import static java.lang.Thread.currentThread;
import java.util.concurrent.TimeoutException;

public class InterrupterTest {

    private boolean interrupted;
    private Thread clientThread;

    private DeterministicDateFactory time = new DeterministicDateFactory();

    private final Thread thread = new Thread() {
        @Override
        public void interrupt() {
            interrupted = true;
            clientThread = currentThread();
        }
    };
    private static final Duration TIMEOUT = seconds(1);

    @Test
    public void interruptGetsCalled() throws TimeoutException {
        interrupt(thread).after(millis(1));
        assertInterruptedWithin(TIMEOUT);
    }

    @Test
    public void interruptGetsCalledFromAnotherThread() throws TimeoutException {
        interrupt(thread).after(millis(1));
        assertInterruptedWithin(TIMEOUT);
        assertThat(currentThread(), is(not(equalTo(clientThread))));
    }

    @Test
    public void interruptDoesntGetsCalledAfterFixedTime() throws TimeoutException {
        interrupt(thread).using(time).after(millis(1));
        assertNotInterruptedWithin(TIMEOUT);
    }

    @Test
    public void interruptGetsCalledAfterFixedTime() throws TimeoutException {
        interrupt(thread).using(time).after(seconds(5));
        assertNotInterruptedWithin(TIMEOUT);

        time.moveTimeForwardFromStartBy(seconds(4));
        assertNotInterruptedWithin(TIMEOUT);

        time.moveTimeForwardFromStartBy(seconds(5));
        assertNotInterruptedWithin(TIMEOUT);

        time.moveTimeForwardFromStartBy(seconds(6));
        assertInterruptedWithin(TIMEOUT);
    }

    @Test
    public void interruptCanBeCancelled() throws InterruptedException {
        Interrupter interrupter = interrupt(thread).using(time).after(millis(1));
        interrupter.cancel();
        time.moveTimeForwardFromStartBy(millis(1));
        assertNotInterruptedWithin(TIMEOUT);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToSetStopwatchAfterStopwatchHasStarted() {
        interrupt(thread).after(millis(1)).using(time);
    }

    private void assertInterruptedWithin(Duration duration) throws TimeoutException {
        assertDurationIsAlotBiggerThanWaitForSleepPeriod(duration);
        waitOrTimeout(new Condition() {
            public boolean isSatisfied() {
                return interrupted;
            }
        }, duration);
    }

    private void assertNotInterruptedWithin(Duration duration) {
        try {
            assertInterruptedWithin(duration);
            fail("didn't timeout, meaning interrupt was still called");
        } catch (TimeoutException e) {
            // didn't interrupt, timeout expired before it was interruptted
        }
    }

    private void assertDurationIsAlotBiggerThanWaitForSleepPeriod(Duration duration) {
        assertTrue("the wait for an assertions must be big enough to allow for multiple sleeps", duration.inMillis() > (SLEEP_PERIOD.inMillis() * 4));
    }

}
