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

package com.google.code.tempusfugit.temporal;

import com.google.code.tempusfugit.concurrency.Callable;
import org.hamcrest.Description;
import org.hamcrest.StringDescription;

public class SimulateJUnitFailure implements Callable<Void, RuntimeException> {

    private final SelfDescribingCondition condition;

    public static SimulateJUnitFailure failOnTimeout(SelfDescribingCondition condition) {
        return new SimulateJUnitFailure(condition);
    }

    private SimulateJUnitFailure(SelfDescribingCondition condition) {
        this.condition = condition;
    }

    @Override
    public Void call() throws RuntimeException {
        Description description = new StringDescription();
        condition.describeTo(description);
        throw new AssertionError(description.toString());
    }
}
