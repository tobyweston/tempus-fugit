package com.google.code.tempusfugit.concurrency;

import static com.google.code.tempusfugit.concurrency.ExecutorServiceShutdown.shutdown;
import com.google.code.tempusfugit.temporal.Duration;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static org.hamcrest.Matchers.is;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.concurrent.TimeoutException;

@RunWith(JMock.class)
public class ExecutorServiceShutdownTest {

    private final Mockery context = new JUnit4Mockery();

    private static final Duration TIMEOUT = millis(100);
    private static final boolean PASSES = true;
    private static final boolean FAILS = false;

    private final ExecutorService executor = context.mock(ExecutorService.class);

    @Test
    public void awaitingTerminationWithNullExecutorService() {
        assertThat(shutdown(null).waitingForCompletion(TIMEOUT), is(false));
    }

    @Test
    public void awaitingTermination() throws InterruptedException {
        awaitingTermination(PASSES);
        awaitingTermination(FAILS);
    }

    @Test
    public void awaitingTerminationIsInterrupted() throws InterruptedException {
        context.checking(new Expectations() {{
            allowing(executor).shutdown();
            one(executor).awaitTermination(with(any(long.class)), with(any(TimeUnit.class))); will(throwException(new InterruptedException()));
        }});
        shutdown(executor).waitingForCompletion(TIMEOUT);
        assertThat(Thread.interrupted(), is(true)); 
    }

    @Test
    public void waitingForShutdownWithNullExecutorService() throws TimeoutException, InterruptedException {
        assertThat(shutdown(null).waitingForShutdown(TIMEOUT), is(false));
    }

    @Test
    public void waitingForShutdown() throws TimeoutException, InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdownNow();
            one(executor).isShutdown(); will(returnValue(true));
        }});
        assertThat(shutdown(executor).waitingForShutdown(TIMEOUT), is(true));
    }

    @Test(expected = TimeoutException.class)
    public void waitingForShutdownTimesOut() throws TimeoutException, InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdownNow();
            atLeast(1).of(executor).isShutdown(); will(returnValue(false));
        }});
        shutdown(executor).waitingForShutdown(TIMEOUT);
    }

    private void awaitingTermination(final boolean result) throws InterruptedException {
        context.checking(new Expectations() {{
            one(executor).shutdown();
            one(executor).awaitTermination(TIMEOUT.inMillis(), MILLISECONDS); will(returnValue(result));
        }});
        assertThat(shutdown(executor).waitingForCompletion(TIMEOUT), is(result));
    }

}
