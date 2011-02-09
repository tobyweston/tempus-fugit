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

package com.google.code.tempusfugit.concurrency;

import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import junit.framework.AssertionFailedError;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.synchronizedSet;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RunConcurrentlyTest {

    @Rule public ConcurrentRule rule = new ConcurrentRule();

    private static final AtomicInteger counter = new AtomicInteger();

    private static final Set<String> threads = synchronizedSet(new HashSet<String>());

    @Test
    @Concurrent (count = 5)
    public void runsMultipleTimes() {
        counter.getAndIncrement();
    }

    @AfterClass
    public static void assertTestMethodRanMultipleTimes() {
        assertThat(counter.get(), is(5));
    }

    @Test
    @Concurrent (count = 5)
    public void spawnTestThreads() {
        threads.add(Thread.currentThread().getName());
    }

    @AfterClass
    public static void assertTestThreadsSpawned() {
        assertThat(threads.size(), is(5));
    }

    @Test (expected = AssertionFailedError.class)
    @Concurrent
    public void expectedExceptionFailsInMainTestThread() {
        fail();
    }

    @Test (expected = RuntimeException.class)
    public void expectedExceptionFailsInMainTestThreadWithoutJUnitFrameworkIntervention() throws Throwable {
        Mockery context = new JUnit4Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};
        FrameworkMethod method = createMethodWithExpectations(context);
        Statement statement = createStatementWithExpectations(context);
        new RunConcurrently(method, statement).evaluate();
        context.assertIsSatisfied();
    }

    private FrameworkMethod createMethodWithExpectations(Mockery context) {
        final FrameworkMethod method = context.mock(FrameworkMethod.class);
        context.checking(new Expectations() {{
            allowing(method).getAnnotation(Concurrent.class); will(returnValue(new ConcurrentAnnotation()));
            allowing(method).getName();
        }});
        return method;
    }

    private Statement createStatementWithExpectations(Mockery context) throws Throwable {
        final Statement statement = context.mock(Statement.class);
        context.checking(new Expectations() {{
            one(statement).evaluate(); will(throwException(new RuntimeException("oops")));
        }});
        return statement;
    }

}
