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

package com.google.code.tempusfugit.concurrency;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;

import static com.google.code.tempusfugit.concurrency.CallableAdapter.runnable;
import static org.jmock.Expectations.returnValue;
import static org.jmock.Expectations.throwException;

@RunWith(JMock.class)
public class CallableAdapterTest {

    private final Mockery context = new Mockery();
    private final Callable callable = context.mock(Callable.class);
    private static final Object RESULT = new Object();

    @Test
    public void delegates() throws Exception {
        callableWill(returnValue(RESULT));
        runnable(callable).run();
    }

    @Test(expected = RuntimeException.class)
    public void exceptionBubblesUp() throws Exception {
        callableWill(throwException(new Exception()));
        runnable(callable).run();
    }

    private void callableWill(final Action action) throws Exception {
        context.checking(new Expectations() {{
            one(callable).call(); will(action);
        }});
    }

}