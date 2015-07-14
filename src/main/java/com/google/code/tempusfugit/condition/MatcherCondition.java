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

package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.temporal.Condition;
import org.hamcrest.Matcher;

public class MatcherCondition<T> implements Condition {
    private final Matcher<T> matcher;
    private final T actual;

    public MatcherCondition(T actual, Matcher<T> matcher) {
        this.matcher = matcher;
        this.actual = actual;
    }

    @Override
    public boolean isSatisfied() {
        return matcher.matches(actual);
    }
}
