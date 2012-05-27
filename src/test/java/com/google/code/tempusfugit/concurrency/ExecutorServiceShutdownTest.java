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

import com.google.code.tempusfugit.temporal.Duration;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.concurrency.ExecutorServiceShutdown.shutdown;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class ExecutorServiceShutdownTest {

    private final Mockery context = new JUnit4Mockery();

    private static final Duration TIMEOUT = millis(100);
    private static final boolean PASSES = true;
    private static final boolean FAILS = false;

    private final ExecutorService executor = context.mock(ExecutorService.class);

    @Test
    public void awaitingTerminationWithNullExecutorService() {
        assertThat(shutdown(null).waitingForCompletion(TIMEOUT), is(false));
    }

    @Test
    public void awaitingTermination() throws InterruptedException {
        awaitingTermination(PASSES);
        awaitingTermination(FAILS);
    }

    @Test
    public void awaitingTerminationIsInterrupted() throws InterruptedException {
        awaitTerminationWillBeInterrupted();
        shutdown(executor).waitingForCompletion(TIMEOUT);
        assertThat(Thread.interrupted(), is(true));
    }

    @Test
    @Ignore
    public void awaitingTerminationIsInterruptedAvoidsNullReturnValue() throws InterruptedException {
        try {
            awaitTerminationWillBeInterrupted();
            assertThat(shutdown(executor).waitingForCompletion(TIMEOUT), is(false));
        } finally {
            Thread.interrupted();
        }
    }

    private void awaitTerminationWillBeInterrupted() throws InterruptedException {
        context.checking(new Expectations() {{
            allowing(executor).shutdown();
            one(executor).awaitTermination(with(any(long.class)), with(any(TimeUnit.class))); will(throwException(new InterruptedException()));
        }});
    }

    @Test
    public void waitingForShutdownWithNullExecutorService() throws TimeoutException, InterruptedException {
        assertThat(shutdown(null).waitingForShutdown(timeout(millis(5))), is(false));
    }

    @Test
    public void waitingForShutdown() throws TimeoutException, InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdownNow();
            one(executor).isShutdown(); will(returnValue(true));
        }});
        assertThat(shutdown(executor).waitingForShutdown(timeout(millis(5))), is(true));
    }

    @Test(expected = TimeoutException.class)
    public void waitingForShutdownTimesOut() throws TimeoutException, InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdownNow();
            atLeast(1).of(executor).isShutdown(); will(returnValue(false));
        }});
        shutdown(executor).waitingForShutdown(timeout(millis(5)));
    }

    private void awaitingTermination(final boolean result) throws InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdown();
            one(executor).awaitTermination(TIMEOUT.inMillis(), MILLISECONDS); will(returnValue(result));
        }});
        assertThat(shutdown(executor).waitingForCompletion(TIMEOUT), is(result));
    }

}
