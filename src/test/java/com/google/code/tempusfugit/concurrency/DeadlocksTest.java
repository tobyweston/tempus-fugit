/*
 * Copyright (c) 2009-2014, toby weston & tempus-fugit committers
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

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DeadlocksTest {

    private final Deadlocks deadlocks = new Deadlocks();

    @Test
    public void doesNotHaveDeadlockWhenStreamIsEmpty() {
        assertThat(deadlocks.detected(), is(false));
    }

    @Test
    public void hasDeadlockWhenStreamIsNotEmpty() throws IOException {
        deadlocks.write("hello world!".getBytes());
        assertThat(deadlocks.detected(), is(true));
    }

    @Test
    public void shouldBeAbleToOutputDeadlocks() throws IOException {
        deadlocks.write("hello world!".getBytes());
        assertThat(deadlocks.toString(), is("hello world!"));
    }
}
