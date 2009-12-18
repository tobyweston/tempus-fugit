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

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;

public class TimeoutExceptionWithFuturesTest {

    private static final String MESSAGE = "message";

    @Test
    public void furturesInitialised() {
        TimeoutExceptionWithFutures exception = new TimeoutExceptionWithFutures(MESSAGE);
        assertThat(exception.getFutures(), is(not(nullValue())));
        assertThat(exception.getFutures().size(), is(0));
    }

    @Test
    public void futuresAreImmutable() {
        List<String> strings = new ArrayList<String>(asList("hello", "goodbye", "bonjour"));
        TimeoutExceptionWithFutures exception = new TimeoutExceptionWithFutures(MESSAGE, strings);
        assertThat(exception.getFutures().size(), is(3));
        strings.add("new string");
        assertThat(exception.getFutures().size(), is(3));
    }
}
