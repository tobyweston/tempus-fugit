package com.google.code.tempusfugit.temporal;

import com.google.code.tempusfugit.concurrency.Callable;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

public class SimulateJUnitFailure implements Callable<Void, RuntimeException> {

    private final SelfDescribingCondition condition;

    public static SimulateJUnitFailure failOnTimeout(SelfDescribingCondition condition) {
        return new SimulateJUnitFailure(condition);
    }

    private SimulateJUnitFailure(SelfDescribingCondition condition) {
        this.condition = condition;
    }

    @Override
    public Void call() throws RuntimeException {
        Description description = new StringDescription();
        condition.describeTo(description);
        throw new AssertionError(description.toString());
    }
}
