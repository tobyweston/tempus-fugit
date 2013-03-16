package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.temporal.Condition;
import com.google.code.tempusfugit.temporal.Conditions;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class AssertThatTest {

    private final Mockery context = new JUnit4Mockery();
    private final Condition condition = context.mock(Condition.class);
    private final Matcher<Boolean> matcher = context.mock(Matcher.class);

    @Test
    public void matches() {
        context.checking(new Expectations() {{
            oneOf(condition).isSatisfied(); will(returnValue(true));
            oneOf(matcher).matches(true); will(returnValue(true));
        }});
        Conditions.assertThat(condition, matcher);
    }

}
