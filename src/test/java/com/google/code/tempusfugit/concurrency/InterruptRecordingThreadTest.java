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

import static com.google.code.tempusfugit.concurrency.ThreadUtils.threadIsWaiting;
import static com.google.code.tempusfugit.temporal.Conditions.not;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class InterruptRecordingThreadTest {

    @Test (timeout = 500)
    public void interruptingThreadsStackTraceIsRecorded() throws TimeoutException, InterruptedException {
        InterruptRecordingThread thread = sleepingThread();
        thread.start();
        waitOrTimeout(threadIsWaiting(thread), millis(500));
        thread.interrupt();
        waitOrTimeout(not(threadIsWaiting(thread)), millis(500));
        verify(thread.getInterrupters());
    }
    
    private void verify(List<StackTraceElement[]> stackTraceElements) {
        assertThat(stackTraceElements.size(), is(1));
        StackTraceElement[] firstStackTrace = stackTraceElements.get(0);
        assertThat(firstStackTrace[0].getMethodName(), is(equalTo("getStackTrace")));
        assertThat(firstStackTrace[1].getMethodName(), is(equalTo("interrupt")));
        assertThat(firstStackTrace[2].getMethodName(), is(equalTo("interruptingThreadsStackTraceIsRecorded")));
    }

    private static InterruptRecordingThread sleepingThread() {
        return new InterruptRecordingThread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // this is supossed to happen
                }
            }
        }, "thread-to-interrupt");
    }
}
