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

package com.google.code.tempusfugit.concurrency;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.PrintStream;

@RunWith(JMock.class)
public class ThreadDumpTest {

    private final Mockery context = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private PrintStream stream = context.mock(PrintStream.class);
    private static final String EXPECTED_THREAD_DETAILS_LINE = "\nThread main@1: (state = RUNNABLE)";

    @Test
    public void outputsThreadDetails() {
        context.checking(new Expectations() {{
            one(stream).println(EXPECTED_THREAD_DETAILS_LINE);
            ignoring(stream).println(with(any(String.class)));
            ignoring(stream).println();
        }});
        ThreadDump.dumpThreads(stream);
    }

    @Test
    public void outputsThreadStackTraceDetails() {
        context.checking(new Expectations() {{
            ignoring(stream).println(EXPECTED_THREAD_DETAILS_LINE);
            one(stream).println(" - java.lang.Thread.dumpThreads(Native Method)");
            allowing(stream).println(with(any(String.class)));
            allowing(stream).println();
        }});
        ThreadDump.dumpThreads(stream);
    }
    
}
