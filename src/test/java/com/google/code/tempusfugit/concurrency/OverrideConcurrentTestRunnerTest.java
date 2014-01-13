/**
 *
 */
package com.google.code.tempusfugit.concurrency;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.AfterClass;

import com.google.code.tempusfugit.concurrency.annotations.Concurrent;

/**
 * @author lpouzac
 *
 */
@Concurrent(count = OverrideConcurrentTestRunnerTest.OVERRIDE_CONCURRENT_COUNT)
public class OverrideConcurrentTestRunnerTest extends AbstractConcurrentTestRunnerTest {

    protected final static int OVERRIDE_CONCURRENT_COUNT = 3;

    @AfterClass
    public static void assertTestThreadsSpawned() {
        assertThat(threads.size(), is(OVERRIDE_CONCURRENT_COUNT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getConcurrentCount() {
        return OVERRIDE_CONCURRENT_COUNT;
    }

}
