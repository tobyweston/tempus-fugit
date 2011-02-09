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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.temporal.Conditions.isAlive;
import static com.google.code.tempusfugit.temporal.Conditions.not;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitUntil;
import static java.lang.Thread.currentThread;

@RunWith(JMock.class)
public class WaitForTest {

    private final MovableClock date = new MovableClock();

    private final Mockery context = new JUnit4Mockery();

    private final Sequence sequence = context.sequence("sequence");
    private final Condition condition = context.mock(Condition.class);
    private final Sleeper sleeper = context.mock(Sleeper.class);

    private static final Duration DURATION = millis(10);

    @Test
    public void whenConditionPassesWaitContinues() throws TimeoutException, InterruptedException {
        context.checking(new Expectations(){{
            one(condition).isSatisfied(); will(returnValue(true));
        }});
        waitOrTimeout(condition, timeout(DURATION, StopWatch.start(date)));
    }

    @Test
    public void whenConditionEventuallyPassesWaitContinues() throws TimeoutException, InterruptedException {
        context.checking(new Expectations(){{
            one(condition).isSatisfied(); inSequence(sequence); will(returnValue(false));
            one(condition).isSatisfied(); inSequence(sequence); will(returnValue(true));
        }});
        waitOrTimeout(condition, timeout(DURATION, StopWatch.start(date)));
    }

    @Test(expected = TimeoutException.class)
    public void timesout() throws TimeoutException, InterruptedException {
        waitOrTimeout(new ForceTimeout(), timeout(DURATION, StopWatch.start(date)));
    }

    @Test (expected = InterruptedException.class, timeout = 500)
    public void waitForCanBeInterrupted() throws TimeoutException, InterruptedException {
        waitOrTimeout(InterruptWaitFor(), timeout(seconds(10)));
    }

    @Test (timeout = 500)
    public void shouldWaitForTimeoutCanBeInterrupted() throws TimeoutException, InterruptedException {
        Thread thread = threadWaitsForever();
        thread.start();
        waitForStartup(thread);
        thread.interrupt();
        waitForShutdown(thread);
    }

    @Test
    public void sleepPeriodShouldBeConfigurable() throws TimeoutException, InterruptedException {
        context.checking(new Expectations(){{
            one(condition).isSatisfied(); inSequence(sequence); will(returnValue(false));
            one(condition).isSatisfied(); inSequence(sequence); will(returnValue(true));
            atLeast(1).of(sleeper).sleep();
        }});
        waitOrTimeout(condition, timeout(millis(100)), sleeper);
    }

    private Thread threadWaitsForever() {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    waitUntil(timeout(seconds(1), StopWatch.start(date)));
                } catch (InterruptedException e) {
                    currentThread().interrupt();
                }
            }
        }, "blocking-thread");
    }

    private void waitForStartup(final Thread thread) throws TimeoutException, InterruptedException {
        waitOrTimeout(isAlive(thread), timeout(seconds(1)));
    }

    private void waitForShutdown(final Thread thread) throws TimeoutException, InterruptedException {
        waitOrTimeout(not(isAlive(thread)), timeout(seconds(1)));
    }

    private Condition InterruptWaitFor() {
        return new Condition() {
            public boolean isSatisfied() {
                currentThread().interrupt();
                return false;
            }
        };
    }

    private class ForceTimeout implements Condition {
        public boolean isSatisfied() {
            date.setTime(DURATION.plus(millis(1)));
            return false;
        }
    }

}
