/*
 * Copyright (c) 2009-2011, tempus-fugit committers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.code.tempusfugit;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;

import static com.google.code.tempusfugit.ExceptionWrapper.*;
import static com.google.code.tempusfugit.WithException.as;
import static com.google.code.tempusfugit.WithException.with;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(JMock.class)
public class ExceptionWrapperTest {

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
        try {
            wrapAnyException(callable, with(SomeOtherCheckedException.class));
            fail();
        } catch (SomeOtherCheckedException e) {
            assertThat((SomeCheckedException) e.getCause(), is(cause));
            assertThat(e.getMessage(), containsString("ExceptionWrapperTest$SomeCheckedException: message"));
            assertThat(e.getCause().getMessage(), is("message"));
        }
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
        try {
            wrapAsRuntimeException(callable);
            fail();
        } catch (RuntimeException e) {
            assertThat((SomeCheckedException) e.getCause(), is(cause));
            assertThat(e.getMessage(), containsString("ExceptionWrapperTest$SomeCheckedException: message"));
            assertThat(e.getCause().getMessage(), is("message"));
        }
    }

    @Test
    public void shouldWrapCheckedExceptionAsRuntimeException() {
        SomeCheckedException cause = new SomeCheckedException("I'm scared daddy");
        try {
            throwAsRuntimeException(cause);
            fail();
        } catch (RuntimeException e) {
            assertThat((SomeCheckedException) e.getCause(), is(cause));
            assertThat(e.getMessage(), containsString("ExceptionWrapperTest$SomeCheckedException: I'm scared daddy"));
            assertThat(e.getCause().getMessage(), is("I'm scared daddy"));
        }
    }

    @Test
    public void shouldWrapCheckedExceptionAsSubclassOfRuntimeException() {
        SomeCheckedException cause = new SomeCheckedException("I'm still scared daddy");
        try {
            ExceptionWrapper.throwException(cause, as(SomeOtherRuntimeException.class));
            fail();
        } catch (SomeOtherRuntimeException e) {
            assertThat((SomeCheckedException) e.getCause(), is(cause));
            assertThat(e.getMessage(), containsString("ExceptionWrapperTest$SomeCheckedException: I'm still scared daddy"));
            assertThat(e.getCause().getMessage(), is("I'm still scared daddy"));
        } catch (RuntimeException e) {
            fail();
        }
    }

    private void callWill(final Action action) throws Exception {
        context.checking(new Expectations() {{
            one(callable).call(); will(action);
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
}