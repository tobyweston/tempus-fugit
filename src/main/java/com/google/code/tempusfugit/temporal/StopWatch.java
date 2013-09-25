/*
 * Copyright (c) 2009-2013, toby weston & tempus-fugit committers
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

/**
 * Stop watch implementations should time the difference between construction (or a call to {@link #reset}) and a call to
 * {@link #lap}. They should allow for multiple calls to {@link #lap} but may preserve a class invariant that {@link #lap}
 * should not be called before {@link #reset} (which would indicate a negative difference).
 *
 * Stop watch implementations may start on construction or may provide an optional method to start. Check the
 * implementations.
 *
 * @since 1.2
 */
public interface StopWatch {

    /**
     * start or reset the stop watch starting position. This should be called before called {@link #lap}.
     */
    void reset();

    /**
     * mark a lap, this method can be invoked multiple times but should be called before making a call to the
     * {@link #elapsedTime()} method.
     */
    void lap();

    /**
     * @return the difference between construction (or reset time) and lap time.
     */
    Duration elapsedTime();
}
