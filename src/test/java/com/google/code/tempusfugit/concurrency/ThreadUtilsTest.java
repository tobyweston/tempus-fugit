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

import static com.google.code.tempusfugit.concurrency.ThreadUtils.threadIsWaiting;
import com.google.code.tempusfugit.temporal.Condition;
import static com.google.code.tempusfugit.temporal.Conditions.not;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static org.hamcrest.Matchers.is;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

public class ThreadUtilsTest {

    private final Mockery context = new JUnit4Mockery();
    private final Interruptable interruptable = context.mock(Interruptable.class);

    @Test
    public void resetInterruptFlagReturnsValue() throws InterruptedException {
        context.checking(new Expectations() {{
            one(interruptable).call(); will(returnValue(true));
        }});
        assertThat(ThreadUtils.<Boolean>resetInterruptFlagWhen(interruptable), is(true));
    }

    @Test
    public void resetInterruptFlagThrowsException() throws InterruptedException {
        context.checking(new Expectations() {{
            one(interruptable).call(); will(throwException(new InterruptedException()));
        }});
        assertThat(Thread.currentThread().isInterrupted(), is(false));
        ThreadUtils.resetInterruptFlagWhen(interruptable);
        assertThat(Thread.currentThread().isInterrupted(), is(true));
    }

    @Test
    public void threadIsWaitingCondition() throws TimeoutException, InterruptedException {
        Thread thread = threadSleepsForever();
        thread.start();
        waitForStartup(thread);
        thread.interrupt();
    }

    @Test
    @Ignore ("can't get this working with a reall thread, grrr")
    public void sleepInterrupted() throws InterruptedException, TimeoutException {
        Thread thread = threadSleepsForever();
        thread.start();
        waitForStartup(thread);
        thread.interrupt();
//        waitForShutdown(thread);
        waitForInterrupt(thread);
//        assertThat(thread.getName() + " wasn't interrupted", thread.isInterrupted(), is(true));
    }

    private void waitForInterrupt(final Thread thread) throws InterruptedException {
        try {
            waitOrTimeout(new Condition() {
               public boolean isSatisfied() {
                   return thread.isInterrupted();
               }
           }, seconds(1));
        } catch (TimeoutException e) {
            fail(thread.getName() + " wasn't interrupted");
        }
    }

    private void waitForStartup(Thread thread) throws TimeoutException, InterruptedException {
        waitOrTimeout(threadIsWaiting(thread), seconds(10));
    }

    private Thread threadSleepsForever() {
        return new Thread(new Runnable() {
            public void run() {
               ThreadUtils.sleep(seconds(10));
            }
        }, "sleeping-thread");
    }

    private void waitForShutdown(final Thread thread) throws TimeoutException, InterruptedException {
        waitOrTimeout(not(threadIsWaiting(thread)), seconds(10));
    }

}
