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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutorService;

import static com.google.code.tempusfugit.temporal.Conditions.assertThat;
import static com.google.code.tempusfugit.temporal.Conditions.shutdown;
import static org.hamcrest.core.Is.is;

@RunWith(JMock.class)
public class ExecutorShutdownConditionTest {

    private final Mockery context = new JUnit4Mockery();

    private final ExecutorService service = context.mock(ExecutorService.class);

    @Test
    public void shutdownFails() {
        willShutdown(false);
        assertThat(shutdown(service), is(false));
    }

    @Test
    public void shutdownOk() {
        willShutdown(true);
        assertThat(shutdown(service), is(true));
    }

    private void willShutdown(final boolean shutdown) {
        context.checking(new Expectations() {{
            one(service).isShutdown(); will(returnValue(shutdown));
        }});
    }

}
