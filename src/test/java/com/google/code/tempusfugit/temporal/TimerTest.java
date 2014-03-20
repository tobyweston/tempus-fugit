/*
 * Copyright (c) 2009-2013, toby weston & tempus-fugit committers
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
import com.google.code.tempusfugit.concurrency.ThreadUtils;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.code.tempusfugit.concurrency.ThreadUtils.sleep;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.Duration.seconds;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.number.OrderingComparison.greaterThan;

public class TimerTest {

    private final MovableClock clock = new MovableClock();
    private final StopWatch timer = new Timer(clock);

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
        clock.incrementBy(millis(100));
        timer.reset();
        timer.elapsedTime();
    }

    @Test
    public void stoppedButNotStarted() {
        timer.lap();
        assertThat(timer.elapsedTime(), is(millis(0)));
    }

    @Test
    public void returnsTotalElapsedTime() {
        timer.reset();
        clock.incrementBy(millis(5));
        timer.lap();
        assertThat(timer.elapsedTime(), is(equalTo(millis(5))));
    }

    @Test
    public void stoppingMultipleTimesReturnsTotalElapsedTime() {
        timer.reset();
        clock.incrementBy(millis(5));
        timer.lap();
        clock.incrementBy(millis(5));
        timer.lap();
        assertThat(timer.elapsedTime(), is(millis(10)));
    }

    @Test
    public void shouldRecordElapsedTimeBetweenStartAndStop() {
        timer.reset();
        clock.incrementBy(millis(5));
        timer.lap();
        assertThat(timer.elapsedTime(), is(millis(5)));
        clock.incrementBy(millis(5));
        timer.lap();
        assertThat(timer.elapsedTime(), is(millis(10)));
        timer.reset();
        clock.incrementBy(millis(20));
        timer.lap();
        assertThat(timer.elapsedTime(), is(millis(20)));
    }

    @Test
    public void timeSomething() {
        Duration duration = Timer.time(() -> sleep(millis(25)));
        assertThat(duration, is(both(greaterThan(millis(25))).and(lessThan(millis(45)))));
    }

}
