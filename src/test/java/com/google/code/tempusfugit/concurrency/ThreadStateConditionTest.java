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

package com.google.code.tempusfugit.concurrency;

import com.google.code.tempusfugit.temporal.Conditions;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.code.tempusfugit.temporal.Conditions.assertThat;
import static java.lang.Thread.State.BLOCKED;
import static java.lang.Thread.State.NEW;
import static org.hamcrest.core.Is.is;

@RunWith(JMock.class)
public class ThreadStateConditionTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private final Thread thread = context.mock(Thread.class);

    @Test
    public void testIs() {
        setThreadStateTo(NEW);
        assertThat(Conditions.is(thread, NEW), is(true));
        assertThat(Conditions.is(thread, BLOCKED), is(false));
    }

    private void setThreadStateTo(final Thread.State state) {
        context.checking(new Expectations() {{
            allowing(thread).getState(); will(returnValue(state));
        }});
    }

}
