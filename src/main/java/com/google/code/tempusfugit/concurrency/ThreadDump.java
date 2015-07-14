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

package com.google.code.tempusfugit.concurrency;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.google.code.tempusfugit.ExceptionWrapper.wrapAsRuntimeException;
import static java.lang.String.format;

public class ThreadDump {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static void dumpThreads(OutputStream stream) {
        DeadlockDetector.printDeadlocks(stream);
        wrapAsRuntimeException(printThreadDump(stream));
    }

    private static Callable<Void, IOException> printThreadDump(final OutputStream writer) {
        return new Callable<Void, IOException>() {
            @Override
            public Void call() throws IOException {
                Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
                for (Thread thread : stackTraces.keySet())
                    print(thread, stackTraces.get(thread));
                return null;
            }

            private void print(Thread thread, StackTraceElement[] stackTraceElements) throws IOException {
                writeln(writer, format("%sThread %s@%d: (state = %s)", LINE_SEPARATOR, thread.getName(), thread.getId(), thread.getState()));
                printStackTrace (writer, stackTraceElements);
            }
        };
    }

    static void printStackTrace (final OutputStream writer, StackTraceElement[] stackTraceElements) throws IOException {
        for (StackTraceElement stackTraceElement : stackTraceElements)
            writeln(writer, format(" - %s", stackTraceElement.toString()));
    }

    private static void writeln(OutputStream writer, String string) throws IOException {
        writer.write(format("%s%s", string, LINE_SEPARATOR).getBytes());
    }

}
