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

import com.google.code.tempusfugit.temporal.Duration;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.concurrency.ExecutorServiceShutdown.shutdown;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(ConcurrentTestRunner.class)
public class ExecutorServiceShutdownTest {

    private final Mockery context = new JUnit4Mockery();

    private static final Duration TIMEOUT = millis(100);
    private static final boolean PASSES = true;
    private static final boolean FAILS = false;

    private final ExecutorService executor = context.mock(ExecutorService.class);

    @Test
    public void awaitingTerminationWithNullExecutorService() {
        assertThat(shutdown(null).waitingForCompletion(TIMEOUT), is(false));
        context.assertIsSatisfied();
    }

    @Test
    public void awaitingTermination() throws InterruptedException {
        awaitingTermination(PASSES);
        awaitingTermination(FAILS);
        context.assertIsSatisfied();
    }

    @Test
    public void awaitingTerminationIsInterrupted() throws InterruptedException {
        context.checking(new Expectations() {{
            allowing(executor).shutdown();
            one(executor).awaitTermination(with(any(long.class)), with(any(TimeUnit.class))); will(throwException(new InterruptedException()));
        }});
        shutdown(executor).waitingForCompletion(TIMEOUT);
        assertThat(Thread.interrupted(), is(true));
        context.assertIsSatisfied();
    }

    @Test
    public void waitingForShutdownWithNullExecutorService() throws TimeoutException, InterruptedException {
        assertThat(shutdown(null).waitingForShutdown(TIMEOUT), is(false));
        context.assertIsSatisfied();
    }

    @Test
    public void waitingForShutdown() throws TimeoutException, InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdownNow();
            one(executor).isShutdown(); will(returnValue(true));
        }});
        assertThat(shutdown(executor).waitingForShutdown(TIMEOUT), is(true));
        context.assertIsSatisfied();
    }

    @Test(expected = TimeoutException.class)
    public void waitingForShutdownTimesOut() throws TimeoutException, InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdownNow();
            atLeast(1).of(executor).isShutdown(); will(returnValue(false));
        }});
        shutdown(executor).waitingForShutdown(TIMEOUT);
        context.assertIsSatisfied();
    }

    private void awaitingTermination(final boolean result) throws InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdown();
            one(executor).awaitTermination(TIMEOUT.inMillis(), MILLISECONDS); will(returnValue(result));
        }});
        assertThat(shutdown(executor).waitingForCompletion(TIMEOUT), is(result));
    }

}
