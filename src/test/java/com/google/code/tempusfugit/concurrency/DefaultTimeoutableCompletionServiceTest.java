/*
 * Copyright (c) 2009-2018, toby weston & tempus-fugit committers
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


import com.google.code.tempusfugit.temporal.Clock;
import com.google.code.tempusfugit.temporal.Condition;
import com.google.code.tempusfugit.temporal.Duration;
import com.google.code.tempusfugit.temporal.RealClock;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

@RunWith(JMock.class)
public class DefaultTimeoutableCompletionServiceTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
        setThreadingPolicy(new Synchroniser());
    }};

    private final Callable<String> task1 = context.mock(Callable.class, "task1");
    private final Callable<String> task2 = context.mock(Callable.class, "task2");
    private final Callable<String> task3 = context.mock(Callable.class, "task3");

    private static final String TASK1_RESULT = "batman";

    private static final Date START_DATE = new Date(0);
    private static final Duration TIMEOUT = millis(5);
    private static final Date EXPIRED_TIMEOUT = new Date(TIMEOUT.inMillis() + 1);

    private final ExecutorCompletionService completionService = context.mock(ExecutorCompletionService.class);

    private final Clock time = new Clock() {
        private int count = 0;
        @Override
        public synchronized Date now() {
            count++;
            if (count == 1)
                return START_DATE;
            if (count == 2)
                return EXPIRED_TIMEOUT;
            throw new IllegalStateException("this clock can only be called twice");
        }
    };

    @Test
    public void taskSubmitionIsDelegated() throws Exception {
        context.checking(new Expectations() {{
            oneOf(completionService).submit(with(task1));
            oneOf(completionService).submit(with(task2));
            oneOf(completionService).submit(with(task3));
            allowing(completionService).take();
        }});
        new DefaultTimeoutableCompletionService(completionService).submit(asList(task1, task2, task3));
    }

    @Test (expected = TimeoutException.class, timeout = 5000)
    public void tasksSubmittedButNeverCompleteTimeout() throws Exception {
        final States taken = context.states("taken").startsAs("none");
        context.checking(new Expectations() {{
            oneOf(completionService).submit(task1);
            oneOf(completionService).submit(task2);

            oneOf(completionService).take(); will(returnValue(new StubFuture(TASK1_RESULT))); then(taken.is("one"));
            oneOf(completionService).take(); will(waitForever()); when(taken.is("one"));
        }});

        new DefaultTimeoutableCompletionService(completionService, TIMEOUT, time).submit(asList(task1, task2));
    }

    @Test
    public void timeoutReturnsPartialResults() throws Exception {
        context.checking(new Expectations() {{
            oneOf(completionService).submit(task1);
            oneOf(completionService).submit(task2);

            oneOf(completionService).take(); will(returnValue(new StubFuture(TASK1_RESULT)));
            oneOf(completionService).take(); will(waitForever());
        }});

        try {
            new DefaultTimeoutableCompletionService(completionService, TIMEOUT, time).submit(asList(task1, task2));
            fail("should have timed out");
        } catch (final TimeoutExceptionWithResults e) {
            assertThat(e.getResults().get(0), is(TASK1_RESULT));
        }
    }

    @Test
    public void noInterruptOccursIfCompletionServiceFinishes() throws Exception {
        context.checking(new Expectations() {{
            oneOf(completionService).submit(task1);
            oneOf(completionService).take(); will(returnValue(new StubFuture(TASK1_RESULT)));
        }});

        new DefaultTimeoutableCompletionService(completionService, millis(100), time).submit(asList(task1));
    }

    @Test
    public void incompleteTasksAreInterrupted() throws Exception {
        final AtomicBoolean interrupted = new AtomicBoolean(false);
        Callable<Void> callable = () -> {
            while (!Thread.currentThread().isInterrupted())
                Thread.yield();
            interrupted.set(true);
            return null;
        };

        try {
            new DefaultTimeoutableCompletionService(new ExecutorCompletionService(newSingleThreadExecutor()), millis(1), new RealClock()).submit(asList(callable));
            fail("didn't timeout");
        } catch (TimeoutException e) {
            waitOrTimeout(interrupted::get, timeout(seconds(10)));
        }
    }

    private class StubFuture implements Future<String> {
        private final String string;

        public StubFuture(String string) {
            this.string = string;
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new UnsupportedOperationException();
        }

        public boolean isCancelled() {
            throw new UnsupportedOperationException();
        }

        public boolean isDone() {
            throw new UnsupportedOperationException();
        }

        public String get() {
            return string;
        }

        public String get(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }
    }

    public static Action waitForever() {
        return new Action() {
            public Object invoke(Invocation invocation) throws Throwable {
                Thread.sleep(Integer.MAX_VALUE);
                return null;
            }

            public void describeTo(Description description) {
                description.appendText("waiting forever, well not for *ever* but a long time none the less");
            }
        };
    }

}

