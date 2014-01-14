/**
 *
 */
package com.google.code.tempusfugit.concurrency;

import static com.google.code.tempusfugit.concurrency.OverrideConcurrentTestRunnerTest.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.AfterClass;

import com.google.code.tempusfugit.concurrency.annotations.Concurrent;

@Concurrent(count = overiddenConcurrentCount)
public class OverrideConcurrentTestRunnerTest extends AbstractConcurrentTestRunnerTest {

    protected final static int overiddenConcurrentCount = 4;

    @AfterClass
    public static void assertTestThreadsSpawned() {
        assertThat(threads.size(), is(overiddenConcurrentCount));
    }

    @Override
    protected int getConcurrentCount() {
        return overiddenConcurrentCount;
    }

}
