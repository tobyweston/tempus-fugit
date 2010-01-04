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

import com.google.code.tempusfugit.concurrency.annotations.Intermittent;
import junit.framework.AssertionFailedError;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class RunRepeatedlyTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private static final Intermittent VALID_ANNOTATION = new IntermittentAnnotation();
    private static final Intermittent NO_ANNOTATION = null;

    private final FrameworkMethod method = context.mock(FrameworkMethod.class);
    private final Statement statement = context.mock(Statement.class);
    private final RunRepeatedly runner = new RunRepeatedly(method, statement);


    @Test
    public void evaulateIntermittenMethod() throws Throwable {
        context.checking(new Expectations() {{
            allowing(method).getAnnotation(with(Intermittent.class)); will(returnValue(VALID_ANNOTATION));
            exactly(100).of(statement).evaluate();
        }});
        runner.evaluate();
    }

    @Test
    public void nonAnnotatedMethod() throws Throwable {
        context.checking(new Expectations() {{
            one(method).getAnnotation(with(Intermittent.class)); will(returnValue(NO_ANNOTATION));
            one(statement).evaluate();
        }});
        runner.evaluate();
    }

    @Test
    public void exceptionOnEvaulation() throws Throwable {
        context.checking(new Expectations() {{
            allowing(method).getAnnotation(with(Intermittent.class)); will(returnValue(VALID_ANNOTATION));
            one(statement).evaluate(); will(throwException(new AssertionFailedError("chazzwazzer")));
        }});
        try {
            runner.evaluate();
            fail();
        } catch (AssertionFailedError e) {
            assertThat(e.getMessage(), containsString("(failed after 0 successful attempts)"));
        }
    }
    
}
