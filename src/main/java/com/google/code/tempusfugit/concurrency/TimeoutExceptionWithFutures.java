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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public final class TimeoutExceptionWithFutures extends TimeoutException {

    private List<?> futures = new ArrayList();

    public TimeoutExceptionWithFutures(String message) {
        super(message);
    }

    public <T> TimeoutExceptionWithFutures(String message, List<T> futures) {
        super(message);
        this.futures = new ArrayList<T>(futures);
    }

    public <T> TimeoutExceptionWithFutures(List<T> futures) {
        super();
        this.futures = futures;
    }

    public <T> List<T> getFutures() {
        return (List<T>) futures;
    }
}
