package com.google.code.tempusfugit.temporal;

import com.google.code.tempusfugit.condition.SelfDescribingMatcherCondition;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.google.code.tempusfugit.temporal.SimulateJUnitFailure.failOnTimeout;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class SimulateJUnitFailureTest {

    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test (expected = AssertionError.class)
    public void throwsException() {
        failOnTimeout(aCondition()).call();
    }

    @Test
    public void throwsExceptionWithSpecificMessage() {
        exception.expectMessage("I failed because I didn't try hard enough");
        failOnTimeout(aCondition()).call();
    }

    @Test
    public void shouldSimulateJUnitFailureOnTimeout() {
        exception.expect(AssertionError.class);
        exception.expectMessage(Matchers.<String>allOf(
                containsString("test lambda"),
                containsString("Expected: is \"the best\""),
                containsString("but: <was \"the worst\">")
        ));
        SelfDescribingMatcherCondition<String> condition = new SelfDescribingMatcherCondition<String>(probe("the worst"), is("the best"));
        condition.isSatisfied();
        failOnTimeout(condition).call();
    }

    private SelfDescribingCondition aCondition() {
        return new SelfDescribingCondition() {
            @Override
            public boolean isSatisfied() {
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("I failed because I didn't try hard enough");
            }
        };
    }

    private static ProbeFor<String> probe(final String value) {
        return new ProbeFor<String>() {
            @Override
            public String call() throws RuntimeException {
                return value;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("test lambda");
            }
        };
    }
}
