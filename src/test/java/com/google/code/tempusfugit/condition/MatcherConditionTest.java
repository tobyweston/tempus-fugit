package com.google.code.tempusfugit.condition;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MatcherConditionTest {

    @Test
    public void matches() {
        assertThat(Conditions.assertion("hello", is(equalTo("hello"))).isSatisfied(), is(true));
    }

    @Test
    public void doesNotMatch() {
        assertThat(Conditions.assertion("hello", is(equalTo("goodbye"))).isSatisfied(), is(false));
    }

}
