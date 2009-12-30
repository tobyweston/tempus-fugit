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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.concurrency.ThreadUtils.threadIsWaiting;
import static com.google.code.tempusfugit.temporal.Conditions.not;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class ThreadUtilsTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final Interruptible interruptible = context.mock(Interruptible.class);
    private final Thread thread = context.mock(Thread.class);

    @Test
    public void resetInterruptFlagReturnsValue() throws InterruptedException {
        context.checking(new Expectations() {{
            one(interruptible).call(); will(returnValue(true));
        }});
        assertThat(ThreadUtils.<Boolean>resetInterruptFlagWhen(interruptible), is(true));
    }

    @Test
    public void resetInterruptFlagThrowsException() throws InterruptedException {
        context.checking(new Expectations() {{
            one(interruptible).call(); will(throwException(new InterruptedException()));
        }});
        assertThat(Thread.currentThread().isInterrupted(), is(false));
        ThreadUtils.resetInterruptFlagWhen(interruptible);
        assertThat(Thread.interrupted(), is(true));
    }

    @Test
    public void sleepInterrupted() throws InterruptedException, TimeoutException {
        InterruptedIndicatingThread thread = threadSleepsForever();
        thread.start();
        waitForStartup(thread);
        thread.interrupt();
        waitForShutdown(thread);
        assertThat(thread.getName() + " wasn't interrupted", thread.hasBeenInterrupted(), is(true));
    }

    @Test
    public void threadIsInTimedWaitingState() {
        context.checking(new Expectations() {{
            allowing(thread).getState(); will(returnValue(Thread.State.TIMED_WAITING));
        }});
        assertThat(ThreadUtils.threadIsWaiting(thread).isSatisfied(), is(true));
    }

    @Test
    public void threadIsInWaitingState() {
        context.checking(new Expectations() {{
            allowing(thread).getState(); will(returnValue(Thread.State.WAITING));
        }});
        assertThat(ThreadUtils.threadIsWaiting(thread).isSatisfied(), is(true));
    }
    
    @Test
    public void threadIsNotInWaitingState() {
        context.checking(new Expectations() {{
            allowing(thread).getState(); will(returnValue(Thread.State.BLOCKED));
        }});
        assertThat(ThreadUtils.threadIsWaiting(thread).isSatisfied(), is(false));
    }

    private void waitForStartup(Thread thread) throws TimeoutException, InterruptedException {
        waitOrTimeout(threadIsWaiting(thread), seconds(10));
    }

    private InterruptedIndicatingThread threadSleepsForever() {
        return new InterruptedIndicatingThread(new Runnable() {
            public void run() {
               ThreadUtils.sleep(seconds(10));
            }
        }, "sleeping-thread");
    }

    private void waitForShutdown(final Thread thread) throws TimeoutException, InterruptedException {
        waitOrTimeout(not(threadIsWaiting(thread)), seconds(10));
    }

}
