/*
 * Copyright (c) 2009-2018, toby weston & tempus-fugit committers
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

import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;

public class ThreadWaitingCondition implements Condition {
    private final Thread thread;

    public ThreadWaitingCondition(Thread thread) {
        this.thread = thread;
    }

    @Override
    public boolean isSatisfied() {
        return (thread.getState() == TIMED_WAITING) || (thread.getState() == WAITING);
    }
}
