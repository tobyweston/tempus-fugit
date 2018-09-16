/*
 * Copyright (c) 2009-2018, toby weston & tempus-fugit committers
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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Ignore ("example only")
@RunWith(JMock.class)
public class BetterStopWatchTest {

    private final Mockery context = new JUnit4Mockery();
    private Clock clock = context.mock(Clock.class);

    @Test
    public void getElapsedTimeFromBetterStopWatch() {
        context.checking(new Expectations() {{
            oneOf(clock).now(); will(returnValue(new Date(0)));
            oneOf(clock).now(); will(returnValue(new Date(100)));
        }});
        BetterStopWatch watch = new BetterStopWatch(clock);
        assertThat(watch.getElapsedTime(), is(millis(100)));
    }

    @Test
    public void getElapsedTimeUsingMovableClock() {
        MovableClock time = new MovableClock();
        BetterStopWatch watch = new BetterStopWatch(time);
        assertThat(watch.getElapsedTime(), is(millis(0)));
        time.incrementBy(millis(100));
        assertThat(watch.getElapsedTime(), is(millis(100)));
    }

    public static class BetterStopWatch {

    private Date startDate;
    private Clock clock;

    public BetterStopWatch(Clock coock) {
        this.clock = coock;
        this.startDate = coock.now();
    }

    public Duration getElapsedTime() {
        return millis(clock.now().getTime() - startDate.getTime());
    }

}
}
