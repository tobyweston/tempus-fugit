package com.google.code.tempusfugit.temporal;

import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.google.code.tempusfugit.temporal.SimulateJUnitFailure.failOnTimeout;

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

}
