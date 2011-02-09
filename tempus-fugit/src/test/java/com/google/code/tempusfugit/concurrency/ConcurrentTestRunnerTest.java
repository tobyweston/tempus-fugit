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

import com.google.code.tempusfugit.temporal.Condition;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static com.google.code.tempusfugit.temporal.Timeout.timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static java.util.Collections.synchronizedSet;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(ConcurrentTestRunner.class)
public class ConcurrentTestRunnerTest {

    private static final Set<String> threads = synchronizedSet(new HashSet<String>());

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
        threads.add(Thread.currentThread().getName());
        waitToForceCachedThreadPoolToCreateNewThread();
    }

    private void waitToForceCachedThreadPoolToCreateNewThread() throws InterruptedException, TimeoutException {
        waitOrTimeout(new Condition() {
            public boolean isSatisfied() {
                return threads.size() == 5;
            }
        }, timeout(seconds(1)));
    }

    @AfterClass
    public static void assertTestThreadsSpawned() {
        assertThat(threads.size(), is(5));
    }

    @Test (expected = AssertionFailedError.class)
    public void concurrentFailuresFailInTheMainTestThread() throws InterruptedException {
        fail();
    }

}