/*
 * Copyright (c) 2009-2015, toby weston & tempus-fugit committers
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

import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import junit.framework.AssertionFailedError;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import static org.hamcrest.Matchers.containsString;

@RunWith(JMock.class)
public class RunRepeatedlyTest {

    @Rule public ExpectedException exception = ExpectedException.none();

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private static final Repeating VALID_ANNOTATION = new RepeatingAnnotation();
    private static final Repeating NO_ANNOTATION = null;

    private final FrameworkMethod method = context.mock(FrameworkMethod.class);
    private final Statement statement = context.mock(Statement.class);
    private final RunRepeatedly runner = new RunRepeatedly(method, statement);


    @Test
    public void evaluateIntermittentMethod() throws Throwable {
        context.checking(new Expectations() {{
            allowing(method).getAnnotation(with(Repeating.class)); will(returnValue(VALID_ANNOTATION));
            exactly(100).of(statement).evaluate();
        }});
        runner.evaluate();
    }

    @Test
    public void nonAnnotatedMethod() throws Throwable {
        context.checking(new Expectations() {{
            oneOf(method).getAnnotation(with(Repeating.class)); will(returnValue(NO_ANNOTATION));
            oneOf(statement).evaluate();
        }});
        runner.evaluate();
    }

    @Test
    public void exceptionOnEvaluation() throws Throwable {
        context.checking(new Expectations() {{
            allowing(method).getAnnotation(with(Repeating.class)); will(returnValue(VALID_ANNOTATION));
            oneOf(statement).evaluate(); will(throwException(new AssertionFailedError("chazzwazzer")));
        }});
        exception.expect(AssertionFailedError.class);
        exception.expectMessage(containsString("(failed after 0 successful attempts)"));
        runner.evaluate();
    }
    
}
