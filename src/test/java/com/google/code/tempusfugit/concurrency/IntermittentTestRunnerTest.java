package com.google.code.tempusfugit.concurrency;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

@RunWith(IntermittentTestRunner.class)
public class IntermittentTestRunnerTest {

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
