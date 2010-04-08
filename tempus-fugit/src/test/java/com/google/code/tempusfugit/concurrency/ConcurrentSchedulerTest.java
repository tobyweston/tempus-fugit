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
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.google.code.tempusfugit.temporal.Duration.days;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ConcurrentSchedulerTest {

    private final Mockery context = new JUnit4Mockery();

    private final ExecutorService executor = context.mock(ExecutorService.class);
    private final Runnable child = context.mock(Runnable.class);

    @Test
    public void childrenAreScheduled() {
        context.checking(new Expectations() {{
            one(executor).submit(child);
        }});
        new ConcurrentScheduler(executor).schedule(child);
    }

    @Test
    public void waitForCompletion() throws InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdown();
            one(executor).awaitTermination(days(365).inMillis(), MILLISECONDS); will(returnValue(true));
        }});
        new ConcurrentScheduler(executor).finished();   
    }

    @Test (expected = RuntimeException.class)
    public void waitForCompletionTimesOut() throws InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdown();
            one(executor).awaitTermination(with(any(Long.class)), with(any(TimeUnit.class))); will(returnValue(false));
        }});
        new ConcurrentScheduler(executor).finished();
    }

    @Test (expected = RuntimeException.class)
    public void waitForCompletionIsInterrupted() throws InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdown();
            one(executor).awaitTermination(with(any(Long.class)), with(any(TimeUnit.class))); will(throwException(new InterruptedException()));
        }});
        new ConcurrentScheduler(executor).finished();
    }
}
