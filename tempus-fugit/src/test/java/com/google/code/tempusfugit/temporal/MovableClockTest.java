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

package com.google.code.tempusfugit.temporal;

import org.junit.Test;

import java.util.Date;

import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MovableClockTest {

    private final MovableClock clock = new MovableClock();

    @Test
    public void dateStartsAtZero() {
        assertThat(clock.create().getTime(), is(0L));
    }

    @Test
    public void dateCanMoveOn() {
        clock.incrementBy(seconds(1));
        assertThat(clock.create().getTime(), is(1000L));
        clock.incrementBy(seconds(2));
        assertThat(clock.create().getTime(), is(3000L));
        clock.incrementBy(seconds(3));
        assertThat(clock.create().getTime(), is(6000L));
    }

    @Test
    public void setTime() {
        clock.setTime(seconds(1));
        assertThat(clock.create().getTime(), is(1000L));
        clock.setTime(seconds(2));
        assertThat(clock.create().getTime(), is(2000L));
        clock.setTime(seconds(3));
        assertThat(clock.create().getTime(), is(3000L));
    }

    @Test
    public void dateStartsAtDatePassedIn() {
        Date date = new Date();
        MovableClock clock = new MovableClock(date);
        assertThat(clock.create().getTime(), is(date.getTime()));
    }

    @Test
    public void dateCanMoveOnAfterBeingPassedIn() {
        Date date = new Date();
        MovableClock clock = new MovableClock(date);
        clock.incrementBy(seconds(1));
        assertThat(clock.create().getTime(), is(date.getTime() + seconds(1).inMillis()));
        clock.incrementBy(seconds(2));
        assertThat(clock.create().getTime(), is(date.getTime() + seconds(3).inMillis()));
        clock.incrementBy(seconds(3));
        assertThat(clock.create().getTime(), is(date.getTime() + seconds(6).inMillis()));
    }

    @Test
    public void setTimeAfterBeingPassedIn() {
        Date date = new Date();
        MovableClock clock = new MovableClock(date);
        clock.setTime(seconds(1));
        assertThat(clock.create().getTime(), is(1000L));
        clock.setTime(seconds(2));
        assertThat(clock.create().getTime(), is(2000L));
        clock.setTime(seconds(3));
        assertThat(clock.create().getTime(), is(3000L));
    }

}
