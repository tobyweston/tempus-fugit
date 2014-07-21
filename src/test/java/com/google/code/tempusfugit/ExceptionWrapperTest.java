/*
 * Copyright (c) 2009-2014, toby weston & tempus-fugit committers
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

package com.google.code.tempusfugit;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;

import static com.google.code.tempusfugit.ExceptionWrapper.*;
import static com.google.code.tempusfugit.WithException.as;
import static com.google.code.tempusfugit.WithException.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

@RunWith(JMock.class)
public class ExceptionWrapperTest {

    @Rule public ExpectedException exception = ExpectedException.none();

    private final Mockery context = new JUnit4Mockery();
    private final Callable<String> callable = context.mock(Callable.class);

    @Test
    public void shouldDelegateCallReflectiveException() throws Exception {
        callWill(returnValue("value"));
        assertThat(wrapAnyException(callable, with(SomeOtherCheckedException.class)), is("value"));
    }

    @Test (expected = SomeOtherCheckedException.class)
    public void shouldThrowNewExceptionBasedOnReflection() throws Exception {
        callWill(throwException(new SomeCheckedException()));
        wrapAnyException(callable, with(SomeOtherCheckedException.class));
    }
                                                           
    @Test (expected = RuntimeException.class)
    public void shouldThrowNewExceptionBasedOnPrivateAccessException() throws Exception {
        callWill(throwException(new SomeCheckedException()));
        wrapAnyException(callable, with(SomeCheckedExceptionWithPrivateAccess.class));
    }

    @Test
    public void shouldThrowExceptionWithCauseReflectiveException() throws Exception {
        SomeCheckedException cause = new SomeCheckedException("message");
        callWill(throwException(cause));
        exception.expect(SomeOtherCheckedException.class);
        exception.expect(hasMessage(containsString("ExceptionWrapperTest$SomeCheckedException: message")));
        exception.expect(cause(is(sameInstance(cause))));
        exception.expect(cause(hasMessage(containsString("message"))));
        wrapAnyException(callable, with(SomeOtherCheckedException.class));
    }

    @Test
    public void shouldDelegateCallWhenWrappingAsRuntimeException() throws Exception {
        callWill(returnValue("value"));
        assertThat(wrapAsRuntimeException(callable), is("value"));
    }

    @Test (expected = RuntimeException.class)
    public void shouldThrowNewRuntimeException() throws Exception {
        callWill(throwException(new SomeCheckedException()));
        wrapAsRuntimeException(callable);
    }


    @Test
    public void shouldThrowExceptionWithCauseWhenWrappedAsRuntimeException() throws Exception {
        SomeCheckedException cause = new SomeCheckedException("message");
        callWill(throwException(cause));
        exception.expect(RuntimeException.class);
        exception.expect(hasMessage(containsString("ExceptionWrapperTest$SomeCheckedException: message")));
        exception.expect(cause(is(cause)));
        exception.expect(cause(hasMessage(containsString("message"))));
        wrapAsRuntimeException(callable);
    }

    @Test
    public void shouldWrapCheckedExceptionAsRuntimeException() {
        SomeCheckedException cause = new SomeCheckedException("I'm scared daddy");
        exception.expect(RuntimeException.class);
        exception.expect(hasMessage(containsString("ExceptionWrapperTest$SomeCheckedException: I'm scared daddy")));
        exception.expect(cause(is(cause)));
        exception.expect(cause(hasMessage(containsString("I'm scared daddy"))));
        throwAsRuntimeException(cause);
    }

    @Test
    public void shouldWrapCheckedExceptionAsSubclassOfRuntimeException() {
        SomeCheckedException cause = new SomeCheckedException("I'm still scared daddy");
        exception.expect(SomeOtherRuntimeException.class);
        exception.expect(hasMessage(containsString("ExceptionWrapperTest$SomeCheckedException: I'm still scared daddy")));
        exception.expect(cause(is(cause)));
        exception.expect(cause(hasMessage(containsString("I'm still scared daddy"))));
        ExceptionWrapper.throwException(cause, as(SomeOtherRuntimeException.class));
    }

    private void callWill(final Action action) throws Exception {
        context.checking(new Expectations() {{
            oneOf(callable).call(); will(action);
        }});
    }

    public static class SomeCheckedException extends Exception {
        public SomeCheckedException() {
        }

        public SomeCheckedException(String message) {
            super(message);
        }
    }

    private static class SomeOtherCheckedException extends Exception {
        public SomeOtherCheckedException() {
            super();
        }

        public SomeOtherCheckedException(Throwable cause) {
            super(cause);
        }
    }

    private static class SomeCheckedExceptionWithPrivateAccess extends Exception { }

    private static class SomeOtherRuntimeException extends RuntimeException {
        public SomeOtherRuntimeException() {
            super();
        }

        public SomeOtherRuntimeException(Throwable cause) {
            super(cause);
        }

    }

    private static Matcher<Throwable> cause(final Matcher<?> matcher) {
        return new TypeSafeMatcher<Throwable>() {
            @Override
            public boolean matchesSafely(Throwable item) {
                return matcher.matches(item.getCause());
            }

            public void describeTo(Description description) {
                description.appendText("exception with cause ");
                description.appendDescriptionOf(matcher);
            }
        };
    }

    private static Matcher<Throwable> hasMessage(final Matcher<String> matcher) {
        return new TypeSafeMatcher<Throwable>() {
            @Override
            public boolean matchesSafely(Throwable item) {
                return matcher.matches(item.getMessage());
            }

            public void describeTo(Description description) {
                description.appendText("exception with message ");
                description.appendDescriptionOf(matcher);
            }
        };
    }

}