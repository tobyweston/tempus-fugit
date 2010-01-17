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

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(ConcurrentTestRunner.class)
public class TimeoutExceptionWithResultsTest {

    private static final String MESSAGE = "message";

    private final List<String> strings = new ArrayList<String>(asList("hello", "goodbye", "bonjour"));
    
    @Test
    public void resultsAreInitialised() {
        TimeoutExceptionWithResults exception = new TimeoutExceptionWithResults(MESSAGE);
        assertThat(exception.getResults(), is(not(nullValue())));
        assertThat(exception.getResults().size(), is(0));
    }

    @Test
    public void resultsAreImmutable() {
        verify(new TimeoutExceptionWithResults(MESSAGE, strings));
    }

    @Test
    public void resultsAreImmutableUsingAlternativeConstructor() {
        verify(new TimeoutExceptionWithResults(strings));
    }

    private void verify(TimeoutExceptionWithResults exception) {
        assertThat(exception.getResults().size(), is(3));
        strings.add("new string");
        assertThat(exception.getResults().size(), is(3));
    }
}
