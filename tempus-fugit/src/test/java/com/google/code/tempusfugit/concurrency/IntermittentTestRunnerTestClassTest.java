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

import com.google.code.tempusfugit.concurrency.annotations.Intermittent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(IntermittentTestRunner.class)
@Intermittent(repetition = 10)
public class IntermittentTestRunnerTestClassTest {
    private static int testCounter = 0;
    private static int afterCounter = 0;
    private static int afterClassCounter = 0;

    @Test
    public void annotatedTest() {
        testCounter++;
    }

    @Test
    public void anotherAnnotatedTest() {
        testCounter++;
    }

    @After
    public void assertAfterIsCalledRepeatedlyForAnnotatedTests() {
        assertThat(testCounter, is(equalTo(++afterCounter)));
    }

    @AfterClass
    public static void assertAfterClassIsCalledOnce() {
        assertThat(++afterClassCounter, is(equalTo(1)));
    }

    @AfterClass
    public static void assertAnnotatedTestRunsMultipleTimes() {
        assertThat(testCounter, is(equalTo(20)));
    }
}
