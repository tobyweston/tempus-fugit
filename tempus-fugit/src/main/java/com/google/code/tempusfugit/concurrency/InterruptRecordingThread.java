/*
 * Copyright (c) 2009, tempus-fugit committers
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class InterruptRecordingThread extends Thread {

    private final CopyOnWriteArrayList<StackTraceElement[]> interrupterStackTraces = new CopyOnWriteArrayList<StackTraceElement[]>();

    public InterruptRecordingThread() {
    }

    public InterruptRecordingThread(Runnable runnable) {
        super(runnable);
    }

    public InterruptRecordingThread(ThreadGroup threadGroup, Runnable runnable) {
        super(threadGroup, runnable);
    }

    public InterruptRecordingThread(String name) {
        super(name);
    }

    public InterruptRecordingThread(ThreadGroup threadGroup, String name) {
        super(threadGroup, name);
    }

    public InterruptRecordingThread(Runnable runnable, String name) {
        super(runnable, name);
    }

    public InterruptRecordingThread(ThreadGroup threadGroup, Runnable runnable, String name) {
        super(threadGroup, runnable, name);
    }

    public InterruptRecordingThread(ThreadGroup threadGroup, Runnable runnable, String name, long stackSize) {
        super(threadGroup, runnable, name, stackSize);
    }

    @Override
    public void interrupt() {
        interrupterStackTraces.add(Thread.currentThread().getStackTrace());
        super.interrupt();
    }
    
    public List<StackTraceElement[]> getInterrupters() {
        return new ArrayList<StackTraceElement[]>(interrupterStackTraces);
    }

    public void printStackTraceOfInterruptingThreads(PrintStream out) {
        for (StackTraceElement[] stackTraceElements : interrupterStackTraces) {
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                out.print(stackTraceElement + "\n   ");
            }
            out.print("\n");
        }

    }
}
