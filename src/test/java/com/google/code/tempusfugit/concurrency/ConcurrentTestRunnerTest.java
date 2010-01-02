/*
 * Copyright (c) 2009-2010, tempus-fugit committers
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
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.synchronizedSet;
import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(ConcurrentTestRunner.class)
public class ConcurrentTestRunnerTest {

    private static final Set<String> threads = synchronizedSet(new HashSet<String>());

    @Test
    public void shouldRunInParallel1() {
        add();
    }

    @Test
    public void shouldRunInParallel2() {
        add();
    }

    @Test
    public void shouldRunInParallel3() {
        add();
    }

    @Test
    public void shouldRunInParallel4() {
        add();
    }

    @Test
    public void shouldRunInParallel5() {
        add();
    }

    private void add() {
        threads.add(Thread.currentThread().getName());
    }

    @AfterClass
    public static void assertTestThreadsSpawned() {
        assertThat(threads.size(), is(5));
    }

    @Test(expected = AssertionFailedError.class)
    public void concurrentFailuresFailInTheMainTestThread() throws InterruptedException {
        fail();
    }

}