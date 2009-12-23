/*
 * Copyright (c) 2009, tempus-fugit committers
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

package com.google.code.tempusfugit.concurrency;

import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.synchronizedSet;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RunConcurrentlyTest {

    @Rule public ConcurrentRule rule = new ConcurrentRule();

    private static final AtomicInteger counter = new AtomicInteger();

    private static final Set<String> threads = synchronizedSet(new HashSet<String>());

    @Test
    @Concurrent (count = 5)
    public void runsMultipleTimes() {        
        counter.getAndIncrement();
    }

    @AfterClass
    public static void assertTestMethodRanMultipleTimes() {
        assertThat(counter.get(), is(5));
    }

    @Test
    @Concurrent (count = 5)
    public void spawnTestThreads() {
        threads.add(Thread.currentThread().getName());
        System.out.println(threads.size() + " " + Thread.currentThread().getName());
    }

    @AfterClass
    public static void assertTestThreadsSpawned() {
        assertThat(threads.size(), is(5));
    }

    @Test (expected = AssertionFailedError.class)
    @Concurrent
    public void concurrentFailuresFailInTheMainTestThread() throws InterruptedException {
        fail();
    }

}
