/**
 *
 */
package com.google.code.tempusfugit.concurrency;

import static com.google.code.tempusfugit.concurrency.AbstractConcurrentTestRunnerTest.*;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static java.util.Collections.synchronizedSet;
import static junit.framework.Assert.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import junit.framework.AssertionFailedError;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.temporal.Condition;

@RunWith(ConcurrentTestRunner.class)
@Concurrent(count = CONCURRENT_COUNT)
public abstract class AbstractConcurrentTestRunnerTest {

    protected final static int CONCURRENT_COUNT = 3;

    protected static final Set<String> THREADS = synchronizedSet(new HashSet<String>());

    @Test
    public void shouldRunInParallel1() throws TimeoutException, InterruptedException {
        logCurrentThread();
    }

    @Test
    public void shouldRunInParallel2() throws TimeoutException, InterruptedException {
        logCurrentThread();
    }

    @Test
    public void shouldRunInParallel3() throws TimeoutException, InterruptedException {
        logCurrentThread();
    }

    @Test
    public void shouldRunInParallel4() throws TimeoutException, InterruptedException {
        logCurrentThread();
    }

    @Test
    public void shouldRunInParallel5() throws TimeoutException, InterruptedException {
        logCurrentThread();
    }

    private void logCurrentThread() throws TimeoutException, InterruptedException {
        THREADS.add(Thread.currentThread().getName());
        waitToForceCachedThreadPoolToCreateNewThread();
    }

    private void waitToForceCachedThreadPoolToCreateNewThread() throws InterruptedException, TimeoutException {
        waitOrTimeout(() -> THREADS.size() == getConcurrentCount(), timeout(seconds(1)));
    }

    @Test(expected = AssertionFailedError.class)
    public void concurrentFailuresFailInTheMainTestThread() throws InterruptedException {
        fail();
    }

    protected abstract int getConcurrentCount();

}
