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

package com.google.code.tempusfugit.temporal;

import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class DeterministicDateFactoryTest {

    private final DeterministicDateFactory date = new DeterministicDateFactory();

    @Test
    public void dateStartsAtZero() {
        assertThat(date.create().getTime(), is(0L));
    }

    @Test
    public void dateCanMoveOn() {
        date.moveTimeForwardBy(seconds(1));
        assertThat(date.create().getTime(), is(1000L));        
        date.moveTimeForwardBy(seconds(2));
        assertThat(date.create().getTime(), is(3000L));
        date.moveTimeForwardBy(seconds(3));
        assertThat(date.create().getTime(), is(6000L));        
    }

    @Test
    public void setTime() {
        date.setTime(seconds(1));
        assertThat(date.create().getTime(), is(1000L));
        date.setTime(seconds(2));
        assertThat(date.create().getTime(), is(2000L));
        date.setTime(seconds(3));
        assertThat(date.create().getTime(), is(3000L));
    }

}
