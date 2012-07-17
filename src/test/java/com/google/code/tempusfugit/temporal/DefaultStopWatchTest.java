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

package com.google.code.tempusfugit.temporal;

import com.google.code.tempusfugit.ClassInvariantViolation;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class DefaultStopWatchTest {

    private Date date = new Date();
    private StubClock clock = new StubClock(date);

    private final StopWatch timer = new DefaultStopWatch(clock);

    @Test
    public void freshlyInitialised() {
        assertThat(timer.elapsedTime(), is(millis(0)));
    }

    @Test
    public void startedButNotStopped() {
        timer.reset();
        assertThat(timer.elapsedTime(), is(millis(0)));
    }

    @Test (expected = ClassInvariantViolation.class)
    public void stoppedThenStarted() {
        timer.reset();
        timer.lap();
        advanceTimeBy(millis(100));
        timer.reset();
        assertThat(timer.elapsedTime(), is(millis(-100)));
    }

    @Test
    public void returnsTotalElapsedTime() {
        timer.reset();
        advanceTimeBy(millis(5));
        timer.lap();
        assertThat(timer.elapsedTime(), is(equalTo(millis(5))));
    }

    @Test
    public void stoppingMultipleTimesReturnsTotalElapsedTime() {
        timer.reset();
        advanceTimeBy(millis(5));
        timer.lap();
        advanceTimeBy(millis(5));
        timer.lap();
        assertThat(timer.elapsedTime(), is(equalTo(millis(10))));
    }

    private void advanceTimeBy(Duration duration) {
        date = addMillisecondsTo(date, new Long(duration.inMillis()).intValue());
        clock.setDate(date);
    }

    private static Date addMillisecondsTo(Date date, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, amount);
        return calendar.getTime();
    }

    private class StubClock implements Clock {
        private Date date;

        private StubClock(Date date) {
            this.date = date;
        }

        public Date create() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
