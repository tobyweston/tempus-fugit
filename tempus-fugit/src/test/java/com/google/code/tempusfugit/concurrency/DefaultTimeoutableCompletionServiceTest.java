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


import com.google.code.tempusfugit.temporal.*;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.*;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(JMock.class)
public class DefaultTimeoutableCompletionServiceTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private Callable<String> task1 = context.mock(Callable.class, "task1");
    private Callable<String> task2 = context.mock(Callable.class, "task2");
    private Callable<String> task3 = context.mock(Callable.class, "task3");
    private static final String TASK1_RESULT = "batman";

    private static final Date START_DATE = new Date(0);
    private static final Duration TIMEOUT = millis(5);
    private static final Date EXPIRED_TIMEOUT = new Date(TIMEOUT.inMillis() + 1);

    private ExecutorCompletionService completionService = context.mock(ExecutorCompletionService.class);

    private final DateFactory time = context.mock(DateFactory.class);

    @Test
    public void taskSubmitionIsDelegated() throws Exception {
        context.checking(new Expectations(){{
            one(completionService).submit(with(task1));
            one(completionService).submit(with(task2));
            one(completionService).submit(with(task3));
            allowing(completionService).take();
        }});
        new DefaultTimeoutableCompletionService(completionService).submit(asList(task1, task2, task3));
    }

    @Test (expected = TimeoutException.class)
    public void tasksSubmittedButNeverCompleteTimeout() throws Exception {
        final Sequence sequence = context.sequence("sequence");
        context.checking(new Expectations() {{
            one(completionService).submit(task1);
            one(completionService).submit(task2);

            one(completionService).take(); will(returnValue(new StubFuture(TASK1_RESULT)));
            one(completionService).take(); will(waitForever());

            one(time).create(); will(returnValue(START_DATE)); inSequence(sequence);
            one(time).create(); will(returnValue(EXPIRED_TIMEOUT)); inSequence(sequence);
        }});

        new DefaultTimeoutableCompletionService(completionService, TIMEOUT, time).submit(asList(task1, task2));
    }

    @Test
    public void timeoutReturnsPartialResults() throws Exception {
        final Sequence sequence = context.sequence("sequence");
        context.checking(new Expectations() {{
            one(completionService).submit(task1);
            one(completionService).submit(task2);

            one(completionService).take(); will(returnValue(new StubFuture(TASK1_RESULT)));
            one(completionService).take(); will(waitForever());

            one(time).create(); will(returnValue(START_DATE)); inSequence(sequence);
            one(time).create(); will(returnValue(EXPIRED_TIMEOUT)); inSequence(sequence);
        }});

        try {
            new DefaultTimeoutableCompletionService(completionService, TIMEOUT, time).submit(asList(task1, task2));
            fail("should have timed out");
        } catch (final TimeoutExceptionWithResults e) {
            assertThat((String) e.getResults().get(0), is(TASK1_RESULT));
        }
    }

    @Test
    public void noInterruptOccursIfCompletionServiceFinishes() throws Exception {
        context.checking(new Expectations() {{
            one(completionService).submit(task1);
            one(completionService).take(); will(returnValue(new StubFuture(TASK1_RESULT)));
            allowing(time).create(); will(returnValue(START_DATE));
        }});

        new DefaultTimeoutableCompletionService(completionService, millis(100), time).submit(asList(task1));
    }

    @Test
    public void incompleteTasksAreInterrupted() throws Exception {
        final AtomicBoolean interrupted = new AtomicBoolean(false);
        Callable<Void> callable = new Callable<Void>() {
            public Void call() throws Exception {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.yield();
                }
                interrupted.set(true);
                return null;
            }
        };

        try {
            new DefaultTimeoutableCompletionService(new ExecutorCompletionService(newSingleThreadExecutor()), millis(1), new DefaultDateFactory()).submit(asList(callable));
            fail("didn't timeout");
        } catch (TimeoutException e) {
            waitOrTimeout(new Condition(){
                public boolean isSatisfied() {
                    return interrupted.get();
                }
            }, timeout(seconds(10)));
        }
    }

    private class StubFuture implements Future<String> {
        private final String string;

        public StubFuture(String string) {
            this.string = string;
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new RuntimeException("Implement me");
        }

        public boolean isCancelled() {
            throw new RuntimeException("Implement me");
        }

        public boolean isDone() {
            throw new RuntimeException("Implement me");
        }

        public String get() throws InterruptedException, ExecutionException {
            return string;
        }

        public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            throw new RuntimeException("Implement me");
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
