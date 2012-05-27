/*
 * Copyright (c) 2009-2012, toby weston & tempus-fugit committers
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

import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RepeatingRuleTest {

    @Rule public RepeatingRule rule = new RepeatingRule();

    private static int counter = 0;

    @Test
    @Repeating(repetition = 99)
    public void annotatedTest() {
        counter++;
    }

    @After
    public void annotatedTestRunsMultipleTimes() {
        assertThat(counter, is(99));
    }

}
