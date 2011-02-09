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

import com.google.code.tempusfugit.temporal.Duration;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.concurrency.CountDownLatchWithTimeout.await;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

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
