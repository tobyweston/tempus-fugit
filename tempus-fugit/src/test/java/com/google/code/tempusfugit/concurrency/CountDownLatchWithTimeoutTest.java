package com.google.code.tempusfugit.concurrency;

import static com.google.code.tempusfugit.concurrency.CountDownLatchWithTimeout.await;
import com.google.code.tempusfugit.temporal.Duration;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.concurrent.TimeoutException;

public class CountDownLatchWithTimeoutTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private static final Duration TIMEOUT = millis(1);

    private CountDownLatch latch = context.mock(CountDownLatch.class);

    @Test
    public void latchCountsDown() throws InterruptedException, TimeoutException {
        awaitWillTimeOut(true);
        await(latch).with(TIMEOUT);
    }

    @Test (expected = TimeoutException.class)
    public void latchTimesOut() throws TimeoutException, InterruptedException {
        awaitWillTimeOut(false);
        await(latch).with(TIMEOUT);
    }

    private void awaitWillTimeOut(final boolean timeout) throws InterruptedException {
        context.checking(new Expectations() {{
            one(latch).await(TIMEOUT.inMillis(), MILLISECONDS); will(returnValue(timeout));
        }});
    }

}
