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

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class StopWatchTest {
    private Date date = new Date();
    private StubClock clock = new StubClock(date);

    @Test
    public void returnsCorrectStartDate() {
        StopWatch stopWatch = StopWatch.start(clock);
        assertThat(stopWatch.getStartDate(), is(equalTo(date)));
    }

    @Test
    public void markingStopWatchReturnsTotalElapsedTime() {
        StopWatch stopWatch = StopWatch.start(clock);
        advanceTime(millis(5));
        assertThat(stopWatch.markAndGetTotalElapsedTime(), is(equalTo(millis(5))));
        advanceTime(millis(5));
        assertThat(stopWatch.markAndGetTotalElapsedTime(), is(equalTo(millis(10l))));
    }

    @Test
    public void markingMultipleTimesReturnsTotalElapsedTime() {
        StopWatch stopWatch = StopWatch.start(clock);
        advanceTime(millis(5));
        stopWatch.markAndGetTotalElapsedTime();
        advanceTime(millis(5));
        assertThat(stopWatch.markAndGetTotalElapsedTime(), is(equalTo(millis(10l))));
    }

    private void advanceTime(Duration duration) {
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
