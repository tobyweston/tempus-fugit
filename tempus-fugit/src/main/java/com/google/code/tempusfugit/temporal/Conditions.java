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

package com.google.code.tempusfugit.temporal;

import org.hamcrest.Matcher;
import org.junit.Assert;

import java.util.concurrent.ExecutorService;

import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;

public final class Conditions {

    public static Condition not(Condition condition) {
        return new NotCondition(condition);
    }

    public static Condition shutdown(ExecutorService service) {
        return new ExecutorShutdownCondition(service);
    }

    public static Condition isAlive(Thread thread) {
        return new ThreadAliveCondition(thread);
    }

    public static Condition isWaiting(Thread thread) {
        return new ThreadWaitingCondition(thread);
    }

    public static Condition is(Thread thread, Thread.State state) {
        return new ThreadStateCondition(thread, state);
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
     *
     * @since 1.1 */
    public static <T> Condition assertion(T actual, Matcher<T> matcher) {
        return new MatcherCondition<T>(matcher, actual);
    }


    private static class NotCondition implements Condition {

        private final Condition condition;

        public NotCondition(Condition condition) {
            this.condition = condition;
        }

        public boolean isSatisfied() {
            return !condition.isSatisfied();
        }
    }

    private static class ExecutorShutdownCondition implements Condition {

        private final ExecutorService executor;

        public ExecutorShutdownCondition(ExecutorService executor) {
            this.executor = executor;
        }

        public boolean isSatisfied() {
            return executor.isShutdown();
        }
    }

    private static class ThreadAliveCondition implements Condition {
        private final Thread thread;

        public ThreadAliveCondition(Thread thread) {
            this.thread = thread;
        }

        public boolean isSatisfied() {
            return thread.isAlive();
        }
    }

    private static class ThreadWaitingCondition implements Condition {
        private final Thread thread;

        public ThreadWaitingCondition(Thread thread) {
            this.thread = thread;
        }

        public boolean isSatisfied() {
            return (thread.getState() == TIMED_WAITING) || (thread.getState() == WAITING);
        }
    }

    private static class ThreadStateCondition implements Condition {
        private final Thread thread;
        private final Thread.State state;

        public ThreadStateCondition(Thread thread, Thread.State state) {
            this.thread = thread;
            this.state = state;
        }

        public boolean isSatisfied() {
            return thread.getState() == state;
        }

    }

    private static class MatcherCondition<T> implements Condition {
        private final Matcher<T> matcher;
        private final T actual;

        public MatcherCondition(Matcher<T> matcher, T actual) {
            this.matcher = matcher;
            this.actual = actual;
        }

        public boolean isSatisfied() {
            return matcher.matches(actual);
        }
    }
}
