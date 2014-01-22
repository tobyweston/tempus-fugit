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

package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.concurrency.Callable;
import com.google.code.tempusfugit.temporal.*;
import org.hamcrest.Matcher;
import org.junit.Assert;

import java.util.concurrent.ExecutorService;

import static java.lang.Thread.*;
import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;

public final class Conditions {

    public static Condition not(Condition condition) {
        return () -> !condition.isSatisfied();
    }

    public static Condition shutdown(ExecutorService service) {
        return service::isShutdown;
    }

    public static Condition isAlive(Thread thread) {
        return new ThreadAliveCondition(thread);
    }

    public static Condition isWaiting(Thread thread) {
        return () -> (thread.getState() == TIMED_WAITING) || (thread.getState() == WAITING);
    }

    public static Condition is(Thread thread, State state) {
        return () -> thread.getState() == state;
    }

    public static void assertThat(Condition condition, Matcher<Boolean> booleanMatcher) {
        Assert.assertThat(condition.isSatisfied(), booleanMatcher);
    }

    public static void assertThat(String message, Condition condition, Matcher<Boolean> booleanMatcher) {
        Assert.assertThat(message, condition.isSatisfied(), booleanMatcher);
    }

    /** Useful when waiting for an assertion in tests, for example;
     * <p></p>
     * <code>WaitFor.waitOrTimeout(assertion(limit, is(5)), timeout(millis(500)))</code>
     * <p></p>
     * Not that if the actual value isn't updated by some asynchronous code, the matcher may never match so it'd be
     * pointless calling inside a <code>WaitFor.waitOrTimeout</code> call.
     *
     * @since 1.1
     * @deprecated use {@link #assertion(com.google.code.tempusfugit.concurrency.Callable, org.hamcrest.Matcher)} instead
     */
    @Deprecated
    public static <T> Condition assertion(T actual, Matcher<T> matcher) {
        return new MatcherCondition<>(actual, matcher);
    }

    public static <T> SelfDescribingCondition assertion(Callable<T, RuntimeException> actual, Matcher<T> matcher) {
        return new SelfDescribingMatcherCondition(actual, matcher);
    }

}
