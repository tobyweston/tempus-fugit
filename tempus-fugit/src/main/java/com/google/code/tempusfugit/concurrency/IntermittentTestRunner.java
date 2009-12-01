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

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Method;

/**
 * Run a test annotated with {@link com.google.code.tempusfugit.concurrency.Intermittent} several times. The annotation
 * *must* follow the {@link org.junit.runner.RunWith} annotation if its in the class declaration. Methods
 * can be annotated but if the class is annotated, it takes precedence.
 * <p/>
 * Only works for JUnit 4 test cases.
 */
public class IntermittentTestRunner extends JUnit4ClassRunner {

    private static final int REPEAT = 100;

    private boolean repeatAllTests;

    public IntermittentTestRunner(Class<?> type) throws InitializationError {
        super(type);
        if (intermittent(type))
            repeatAllTests = true;
    }

    @Override
    protected void invokeTestMethod(Method method, RunNotifier runNotifier) {
        if (repeatAllTests || intermittent(method)) {
            for (int i = 0; i < REPEAT; i++)
                super.invokeTestMethod(method, runNotifier);
        } else {
            super.invokeTestMethod(method, runNotifier);
        }
    }

    private boolean intermittent(Class<?> type) {
        return type.getAnnotation(Intermittent.class) != null;
    }

    private boolean intermittent(Method method) {
        return method.getAnnotation(Intermittent.class) != null;
    }

}
