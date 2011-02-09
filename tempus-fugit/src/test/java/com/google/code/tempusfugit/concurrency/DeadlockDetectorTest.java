/*
 * Copyright (c) 2009-2011, tempus-fugit committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.tempusfugit.concurrency;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

import static com.google.code.tempusfugit.concurrency.ThreadUtils.resetInterruptFlagWhen;
import static org.hamcrest.Matchers.containsString;

@RunWith(JMock.class)
public class DeadlockDetectorTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private PrintStream stream = context.mock(PrintStream.class);

    private final Cash cash = new Cash();
    private final Cat nibbles = new Cat();

    private final CountDownLatch latch = new CountDownLatch(2);

    @Test
    public void noDeadlock() {
        DeadlockDetector.printDeadlocks(stream);
    }

    @Test (timeout = 1000)
    public void deadlock() throws InterruptedException {
        new Kidnapper().start();
        new Negotiator().start();

        setExpectationsOn(stream);
        DeadlockDetector.printDeadlocks(stream);
    }
    
    private void setExpectationsOn(final PrintStream stream) {
        final Sequence sequence = context.sequence("output");
        context.checking(new Expectations() {{
            one(stream).println(with(containsString("Deadlock detected"))); inSequence(sequence);
            one(stream).println(with(containsString("Negotiator-Thread"))); inSequence(sequence);
            one(stream).println(with(containsString("waiting to lock Monitor of " + Cat.class.getName()))); inSequence(sequence);
            one(stream).println(with(containsString("which is held by \"Kidnapper-Thread"))); inSequence(sequence);
            one(stream).println(with(containsString("Kidnapper-Thread"))); inSequence(sequence);
            one(stream).println(with(containsString("waiting to lock Monitor of " + Cash.class.getName()))); inSequence(sequence);
            one(stream).println(with(containsString("which is held by \"Negotiator-Thread"))); inSequence(sequence);
            allowing(stream).println();
        }});
    }

    class Kidnapper extends Thread {
        Kidnapper() {
            setName("Kidnapper-" + getName());
        }

        @Override
        public void run() {
            notWillingToLetNibblesGoWithoutCash();
        }

        private void notWillingToLetNibblesGoWithoutCash() {
            synchronized (nibbles) {
                countdownAndAwait(latch);
                synchronized (cash) {
                    take(cash);
                }
            }
        }

        private void take(Cash cash) {
        }

    }

    class Negotiator extends Thread {

        Negotiator() {
            setName("Negotiator-" + getName());
        }

        @Override
        public void run() {
            notWillingToLetCashGoWithoutNibbles();
        }

        private void notWillingToLetCashGoWithoutNibbles() {
            synchronized (cash) {
                countdownAndAwait(latch);
                synchronized (nibbles) {
                    take(nibbles);
                }
            }
        }

        private void take(Cat nibbles) {
        }

    }

    private void countdownAndAwait(CountDownLatch latch) {
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


    static class Cash { }

    static class Cat { }

}
