package com.google.code.tempusfugit.temporal;

import com.google.code.tempusfugit.concurrency.Callable;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

public class SimulateJUnitFailure implements Callable<Void, RuntimeException> {

    private final SelfDescribingCondition condition;
    private final Description description = new StringDescription();

    public static SimulateJUnitFailure failOnTimeout(SelfDescribingCondition condition) {
        return new SimulateJUnitFailure(condition);
    }

    private SimulateJUnitFailure(SelfDescribingCondition description) {
        this.condition = description;
    }

    @Override
    public Void call() throws RuntimeException {
        condition.describeTo(description);
        throw new AssertionError(description.toString());
    }
}
