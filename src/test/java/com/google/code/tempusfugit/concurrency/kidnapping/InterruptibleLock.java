/*
 * Copyright (c) 2009-2015, toby weston & tempus-fugit committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.tempusfugit.concurrency.kidnapping;

import com.google.code.tempusfugit.concurrency.Callable;
import com.google.code.tempusfugit.concurrency.Interruptible;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.code.tempusfugit.concurrency.ThreadUtils.resetInterruptFlagWhen;

public class InterruptibleLock implements Cash, Cat {

    private final Lock lock = new ReentrantLock();
    private final CountDownLatch latch;

    public InterruptibleLock(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void hold(Callable<Void, RuntimeException> callable) {
        resetInterruptFlagWhen(locking(lock));
        callable.call();
    }

    @Override
    public Callable<Void, RuntimeException> take() {
        return new Callable<Void, RuntimeException>() {
            @Override
            public Void call() throws RuntimeException {
                countdownAndAwait(latch);
                return resetInterruptFlagWhen(locking(lock));
            }
        };
    }

    @Override
    public void release() {
        if (lock.tryLock())
            lock.unlock();
    }

    private static Interruptible<Void> locking(final Lock lock) {
        return new Interruptible<Void>() {
            @Override
            public Void call() throws InterruptedException {
                lock.lockInterruptibly();
                return null;
            }
        };
    }

    void countdownAndAwait(CountDownLatch latch) {
        latch.countDown();
        resetInterruptFlagWhen(waitingFor(latch));
    }

    private Interruptible<Void> waitingFor(final CountDownLatch latch) {
        return new Interruptible<Void>() {
            public Void call() throws InterruptedException {
                latch.await();
                return null;
            }
        };
    }

}
