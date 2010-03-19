/*
 * Copyright (c) 2009-2010, tempus-fugit committers
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

public final class Timeout {

    private Duration duration;
    private StopWatch stopWatch;

    /** @since 1.1 */
    public Timeout(Duration duration) {
        this(duration, startDefaultStopWatch());
    }

    public Timeout(final Duration duration, final StopWatch stopWatch) {
        if (duration.inMillis() <= 0)
            throw new IllegalArgumentException();
        this.duration = duration;
        this.stopWatch = stopWatch;
    }

    public boolean hasExpired() {
        return stopWatch.markAndGetTotalElapsedTime().greaterThan(duration);
    }

    private static StopWatch startDefaultStopWatch() {
        return StopWatch.start(new DefaultDateFactory());
    }

}