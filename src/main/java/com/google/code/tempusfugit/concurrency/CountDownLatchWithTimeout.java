package com.google.code.tempusfugit.concurrency;

import com.google.code.tempusfugit.temporal.Duration;

import java.util.concurrent.CountDownLatch;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import java.util.concurrent.TimeoutException;

public class CountDownLatchWithTimeout {

    private final CountDownLatch latch;

    private CountDownLatchWithTimeout(CountDownLatch latch) {
        this.latch = latch;
    }

    public static CountDownLatchWithTimeout await(CountDownLatch latch) {
        return new CountDownLatchWithTimeout(latch);
    }

    public void with(Duration timeout) throws InterruptedException, TimeoutException {
        if (!latch.await(timeout.inMillis(), MILLISECONDS))
            throw new TimeoutException();
    }
}