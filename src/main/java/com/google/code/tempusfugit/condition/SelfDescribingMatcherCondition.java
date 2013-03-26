package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.concurrency.Callable;
import com.google.code.tempusfugit.temporal.ProbeFor;
import com.google.code.tempusfugit.temporal.SelfDescribingCondition;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

import java.util.ArrayList;
import java.util.List;

public class SelfDescribingMatcherCondition<T> implements SelfDescribingCondition {

    private final Callable<T, RuntimeException> actual;
    private final Matcher<T> matcher;
    private final List<Description> description = new ArrayList<Description>();

    public static <T> SelfDescribingMatcherCondition probe(ProbeFor<T> probe, Matcher<T> matcher) {
        return new SelfDescribingMatcherCondition(probe, matcher);
    }

    public SelfDescribingMatcherCondition(Callable<T, RuntimeException> actual, Matcher<T> matcher) {
        this.actual = actual;
        this.matcher = matcher;
    }

    @Override
    public boolean isSatisfied() {
        T value = actual.call();
        boolean matches = matcher.matches(value);
        if (!matches) {
            StringDescription failure = new StringDescription();
            matcher.describeMismatch(value, failure);
            description.add(failure);
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getDescriptionOf(actual))
                .appendText("\nExpected: ")
                .appendDescriptionOf(matcher)
                .appendText("\n     but: ")
                .appendValueList("", ", ", "", this.description)
        ;
    }

    private String getDescriptionOf(Callable<T, RuntimeException> actual) {
        if (actual instanceof SelfDescribing) {
            StringDescription description = new StringDescription();
            ((SelfDescribing) actual).describeTo(description);
            return description.toString();
        }
        return actual.getClass().getSimpleName();
    }
}
