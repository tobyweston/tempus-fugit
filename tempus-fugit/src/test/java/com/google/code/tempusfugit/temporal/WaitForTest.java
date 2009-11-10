/*
 * Copyright (c) 2009, Toby Weston 
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
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.concurrent.TimeoutException;

import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;

@RunWith(JMock.class)
public class WaitForTest {
    
    private Date now = new Date(0);

    private final DateFactory dateProvider = new DateFactory() {
        public Date create() {
            return new Date(now.getTime());
        }
    };

    private final Mockery context = new JUnit4Mockery();

    private final Sequence sequence = context.sequence("sequence");
    private final Condition condition = context.mock(Condition.class);
    private static final Duration TIMEOUT = millis(10);

    @Test
    public void whenConditionPassesWaitContinues() throws TimeoutException {
        context.checking(new Expectations(){{
            one(condition).isSatisfied(); will(returnValue(true));
        }});
        waitOrTimeout(condition, TIMEOUT, StopWatch.start(dateProvider));
    }

    @Test
    public void whenConditionEventuallyPassesWaitContinues() throws TimeoutException {
        context.checking(new Expectations(){{
            one(condition).isSatisfied(); inSequence(sequence); will(returnValue(false));
            one(condition).isSatisfied(); inSequence(sequence); will(returnValue(true));
        }});
        waitOrTimeout(condition, TIMEOUT, StopWatch.start(dateProvider));
    }

    @Test(expected = TimeoutException.class)
    public void timesout() throws TimeoutException {
        waitOrTimeout(new ForceTimeout(), TIMEOUT, StopWatch.start(dateProvider));
    }

    private class ForceTimeout implements Condition {
        public boolean isSatisfied() {
            now.setTime(TIMEOUT.inMillis()+1);
            return false;
        }
    }
    
}
