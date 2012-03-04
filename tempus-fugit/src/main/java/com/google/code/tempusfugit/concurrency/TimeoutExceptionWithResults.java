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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public final class TimeoutExceptionWithResults extends TimeoutException {

    private List<?> results = new ArrayList();

    public TimeoutExceptionWithResults(String message) {
        super(message);
    }

    public <T> TimeoutExceptionWithResults(String message, List<T> results) {
        this(message);
        this.results = new ArrayList<T>(results);
    }

    public <T> TimeoutExceptionWithResults(List<T> results) {
        super();
        this.results = new ArrayList<T>(results);
    }

    public <T> List<T> getResults() {
        return (List<T>) results;
    }
}
