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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Detect Java-level deadlocks.
 * <p/>
 * Java 1.5 only supports finding monitor based deadlocks. 1.6's {@link ThreadMXBean} supports {@link java.util.concurrent.locks.Lock}
 * based deadlocks.
 */
public class DeadlockDetector {

    private static final ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
    private static final String lineSeparator = System.getProperty("line.separator");

    public static void printDeadlocks(OutputStream writer) {
        List<ThreadInfo> deadlocks = findDeadlocks();
        if (deadlocks.isEmpty())
            return;
        print(writer, deadlocks);
    }

    private static void print(OutputStream writer, List<ThreadInfo> deadlocks) {
        try {
            writeln(writer, "Deadlock detected");
            writeln(writer, "=================");
            for (ThreadInfo thread : deadlocks) {
                writeln(writer, format("%s\"%s\":", lineSeparator, thread.getThreadName()));
                writeln(writer, format("  waiting to lock Monitor of %s ", thread.getLockName()));
                writeln(writer, format("  which is held by \"%s\"", thread.getLockOwnerName()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeln(OutputStream writer, String string) throws IOException {
        writer.write(format("%s%s", string, lineSeparator).getBytes());
    }

    private static List<ThreadInfo> findDeadlocks() {
        long[] result;
        if (mbean.isSynchronizerUsageSupported())
            result = mbean.findDeadlockedThreads();
        else
            result = mbean.findMonitorDeadlockedThreads();
        long[] monitorDeadlockedThreads = result;
        if (monitorDeadlockedThreads == null)
            return emptyList();
        return asList(mbean.getThreadInfo(monitorDeadlockedThreads));
    }

}
