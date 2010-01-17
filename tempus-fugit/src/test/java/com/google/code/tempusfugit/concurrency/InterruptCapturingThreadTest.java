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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.temporal.Conditions.isWaiting;
import static com.google.code.tempusfugit.temporal.Conditions.not;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(ConcurrentTestRunner.class)
public class InterruptCapturingThreadTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private PrintStream stream = context.mock(PrintStream.class);


    @Test (timeout = 500)
    public void interruptingThreadsStackTraceIsRecorded() throws TimeoutException, InterruptedException {
        verify(startSleepingThreadAndInterrupt().getInterrupters());
        context.assertIsSatisfied();
    }

    @Test
    public void outputsStackTraceDetails() throws TimeoutException, InterruptedException {
        final Sequence sequence = context.sequence("order");
        context.checking(new Expectations() {{
            one(stream).print(with(containsString("java.lang.Thread.getStackTrace(Thread.java"))); inSequence(sequence);
            one(stream).print(with(containsString("com.google.code.tempusfugit.concurrency.InterruptCapturingThread.interrupt(InterruptCapturingThread.java"))); inSequence(sequence);
            one(stream).print(with(containsString("com.google.code.tempusfugit.concurrency.InterruptCapturingThreadTest.startSleepingThreadAndInterrupt(InterruptCapturingThreadTest"))); inSequence(sequence);
            one(stream).print(with(containsString("com.google.code.tempusfugit.concurrency.InterruptCapturingThreadTest.outputsStackTraceDetails(InterruptCapturingThreadTest.java"))); inSequence(sequence);
            allowing(stream).print(with(any(String.class)));
        }});
        startSleepingThreadAndInterrupt().printStackTraceOfInterruptingThreads(stream);
        context.assertIsSatisfied();
    }

    private InterruptCapturingThread startSleepingThreadAndInterrupt() throws TimeoutException, InterruptedException {
        InterruptCapturingThread thread = sleepingThread();
        thread.start();
        waitOrTimeout(isWaiting(thread), millis(500));
        thread.interrupt();
        waitOrTimeout(not(isWaiting(thread)), millis(500));
        return thread;
    }

    private void verify(List<StackTraceElement[]> stackTraceElements) {
        assertThat(stackTraceElements.size(), is(1));
        StackTraceElement[] firstStackTrace = stackTraceElements.get(0);
        assertThat(firstStackTrace[0].getMethodName(), is(equalTo("getStackTrace")));
        assertThat(firstStackTrace[1].getMethodName(), is(equalTo("interrupt")));
        assertThat(firstStackTrace[2].getMethodName(), is(equalTo("startSleepingThreadAndInterrupt")));
        assertThat(firstStackTrace[3].getMethodName(), is(equalTo("interruptingThreadsStackTraceIsRecorded")));
    }

    private static InterruptCapturingThread sleepingThread() {
        return new InterruptCapturingThread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // this is supossed to happen
                }
            }
        }, "thread-to-interrupt");
    }
}
