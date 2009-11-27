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

import static com.google.code.tempusfugit.temporal.Duration.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class DurationTest {

    @Test
    public void secondsConversionTest() {
        Duration duration = seconds(60);
        assertThat(duration.inMillis(), is(60000L));
        assertThat(duration.inSeconds(), is(60L));
        assertThat(duration.inMinutes(), is(1L));
        assertThat(duration.inHours(), is(0L));
    }

    @Test
    public void secondsConversionRounding() {
        Duration duration = seconds(95);
        assertThat(duration.inMillis(), is(90000L));
        assertThat(duration.inSeconds(), is(90L));
        assertThat(duration.inMinutes(), is(1L));
        assertThat(duration.inHours(), is(0L));
    }

    @Test
    public void minutesConversionTest() {
        Duration duration = minutes(50);
        assertThat(duration.inMillis(), is(3000000L));
        assertThat(duration.inSeconds(), is(3000L));
        assertThat(duration.inMinutes(), is(50L));
        assertThat(duration.inHours(), is(0L));
    }

    @Test
    public void hoursConversionTest() {
        Duration duration = hours(24);
        assertThat(duration.inMillis(), is(86400000L));
        assertThat(duration.inSeconds(), is(86400L));
        assertThat(duration.inMinutes(), is(1440L));
        assertThat(duration.inHours(), is(24L));
    }

    @Test
    public void canAddDurations() {
        assertThat(millis(1).plus(seconds(1)), is(equalTo(millis(1001))));
    }


    @Test(expected = IllegalArgumentException.class)
    public void maxSecondsLongTest() {
        seconds(Long.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxMillisLongTest() {
        millis(Long.MAX_VALUE);
    }


    @Test
    public void sameUnitEquality() {
        assertThat(seconds(10), is(equalTo(seconds(10))));
    }

    @Test
    public void differentUnitEquality() {
        assertThat(millis(10000), is(equalTo(seconds(10))));
    }

    @Test
    public void differntUnitNotEqual() {
        assertThat(millis(1500), is(not(equalTo(seconds(2)))));
    }

    @Test
    public void sameUnitNotEqual() {
        assertThat(seconds(1), is(not(equalTo(seconds(2)))));
    }

    @Test
    public void hashcode() {
        assertThat(seconds(1).hashCode(), is(equalTo(millis(1000).hashCode())));
    }

    @Test
    public void greaterThan() {
        assertThat(seconds(1).greaterThan(seconds(2)), is(false));
        assertThat(seconds(2).greaterThan(seconds(1)), is(true));
        assertThat(seconds(1).greaterThan(seconds(1)), is(false));
        assertThat(seconds(-1).greaterThan(seconds(1)), is(false));
    }

}
