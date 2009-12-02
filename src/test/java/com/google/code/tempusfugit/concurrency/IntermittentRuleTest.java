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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;

@RunWith(JMock.class)
public class IntermittentRuleTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final FrameworkMethod method = context.mock(FrameworkMethod.class);

    private static final Object NOTHING = new Object();

    private final IntermittentRule rule = new IntermittentRule();

    @Test
    public void nonAnnotatedMethod() throws Throwable {
        markMethodAsNotIntermittent();
        rule.apply(new VoidStatement(), method, NOTHING).evaluate();
    }

    @Test
    public void annotatedMethod() throws Throwable {
        markMethodAsIntermittent();
        rule.apply(new VoidStatement(), method, NOTHING).evaluate();
    }

    private void markMethodAsNotIntermittent() throws Throwable {
        context.checking(new Expectations() {{
            one(method).getAnnotation(with(Intermittent.class)); will(returnValue(null));
            one(method).invokeExplosively(NOTHING);
        }});
    }

    private void markMethodAsIntermittent() throws Throwable {
        context.checking(new Expectations() {{
            one(method).getAnnotation(with(Intermittent.class)); will(returnValue(new Annotation(){
                public Class<? extends Annotation> annotationType() {
                    return null;
                }
            }));
            exactly(100).of(method).invokeExplosively(NOTHING);
        }});
    }

    private static class VoidStatement extends Statement {
        @Override
        public void evaluate() throws Throwable {
        }
    }
}
