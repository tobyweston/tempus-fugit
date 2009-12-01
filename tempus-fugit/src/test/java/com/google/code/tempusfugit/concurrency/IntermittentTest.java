package com.google.code.tempusfugit.concurrency;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class IntermittentTest {

    @Rule public IntermittentRule rule = new IntermittentRule();

    private static final AtomicInteger counter = new AtomicInteger();

    @Test
    @Intermittent
    public void annotatedTest() {
        counter.getAndIncrement();
    }
    
    @Test
    public void annotatedTestRunsMultipleTimes() {
        assertThat(counter.get(), is(100));
    }

}
