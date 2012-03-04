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

import java.io.PrintStream;
import java.util.Map;

public class ThreadDump {

    public static void dumpThreads(PrintStream writer) {
        DeadlockDetector.printDeadlocks(writer);
        Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
        for (Thread thread : traces.keySet()) {
            writer.println(String.format("\nThread %s@%d: (state = %s)", thread.getName(), thread.getId(), thread.getState()));
            for (StackTraceElement stackTraceElement : traces.get(thread)) {
                writer.println(" - " + stackTraceElement);
            }
        }
    }

}
