/**
 *
 */
package com.google.code.tempusfugit.concurrency;

import static com.google.code.tempusfugit.concurrency.OverrideConcurrentTestRunnerTest.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.AfterClass;

import com.google.code.tempusfugit.concurrency.annotations.Concurrent;

@Concurrent(count = OVERRIDDEN_CONCURRENT_COUNT)
public class OverrideConcurrentTestRunnerTest extends AbstractConcurrentTestRunnerTest {

    protected final static int OVERRIDDEN_CONCURRENT_COUNT = 4;

    @AfterClass
    public static void assertTestThreadsSpawned() {
        assertThat(THREADS.size(), is(OVERRIDDEN_CONCURRENT_COUNT));
    }

    @Override
    protected int getConcurrentCount() {
        return OVERRIDDEN_CONCURRENT_COUNT;
    }

}
