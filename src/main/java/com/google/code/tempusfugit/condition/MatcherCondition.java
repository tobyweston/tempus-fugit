package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.temporal.Condition;
import org.hamcrest.Matcher;

public class MatcherCondition<T> implements Condition {
    private final Matcher<T> matcher;
    private final T actual;

    public MatcherCondition(T actual, Matcher<T> matcher) {
        this.matcher = matcher;
        this.actual = actual;
    }

    @Override
    public boolean isSatisfied() {
        return matcher.matches(actual);
    }
}
