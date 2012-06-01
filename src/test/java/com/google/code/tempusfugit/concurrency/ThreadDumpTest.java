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

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ThreadDumpTest {

    private final OutputStream stream = new StubOutputStream();

    @Test
    @Ignore("example usage")
    public void exampleUsage() {
        ThreadDump.dumpThreads(System.out);
    }

    @Test
    public void outputsThreadDetails() {
        ThreadDump.dumpThreads(stream);
        assertThat(stream.toString(), containsString("\nThread main@1: (state = RUNNABLE)"));
    }

    @Test
    public void outputsThreadStackTraceDetails() {
        ThreadDump.dumpThreads(stream);
        assertThat(stream.toString(), containsString(" - java.lang.Thread.dumpThreads(Native Method)"));
    }

    private static class StubOutputStream extends OutputStream {
        private final ByteArrayOutputStream stream = new ByteArrayOutputStream();

        @Override
        public void write(int b) throws IOException {
            stream.write(b);
        }

        @Override
        public String toString() {
            return stream.toString();
        }
    }
}
