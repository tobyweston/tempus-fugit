/*
 * Copyright (c) 2009-2015, toby weston & tempus-fugit committers
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

package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.temporal.Condition;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class AssertThatTest {

    private final Mockery context = new JUnit4Mockery();
    private final Condition condition = context.mock(Condition.class);
    private final Matcher<Boolean> matcher = context.mock(Matcher.class);

    @Test
    public void matches() {
        context.checking(new Expectations() {{
            oneOf(condition).isSatisfied(); will(returnValue(true));
            oneOf(matcher).matches(true); will(returnValue(true));
        }});
        Conditions.assertThat(condition, matcher);
    }

}
