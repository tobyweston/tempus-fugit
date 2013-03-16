package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.concurrency.Callable;
import com.google.code.tempusfugit.temporal.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SelfDescribingMatcherConditionTest {

    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void isSatisfied() {
        Condition condition = new SelfDescribingMatcherCondition<String>(lambda("the best"), is("the best"));
        assertThat(condition.isSatisfied(), is(true));
    }

    @Test
    public void isNotSatisfied() {
        Condition condition = new SelfDescribingMatcherCondition<String>(lambda("the worst"), is("the best"));
        assertThat(condition.isSatisfied(), is(false));
    }

    private Callable<String, RuntimeException> lambda(final String value) {
        return new Callable<String, RuntimeException>() {
            @Override
            public String call() throws RuntimeException {
                return value;
            }
        };
    }

}
