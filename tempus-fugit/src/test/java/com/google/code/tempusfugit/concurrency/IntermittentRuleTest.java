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
