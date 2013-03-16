package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.concurrency.Callable;
import com.google.code.tempusfugit.temporal.SelfDescribingCondition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class SelfDescribingMatcherCondition<T> implements SelfDescribingCondition {

    private final Callable<T, RuntimeException> actual;
    private final Matcher<T> matcher;

    public SelfDescribingMatcherCondition(Callable<T, RuntimeException> actual, Matcher<T> matcher) {
        this.actual = actual;
        this.matcher = matcher;
    }

    @Override
    public boolean isSatisfied() {
        return matcher.matches(actual.call());
    }

    @Override
    public void describeTo(Description description) {
        matcher.describeTo(description);
    }
}
