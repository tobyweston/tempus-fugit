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

import com.google.code.tempusfugit.concurrency.annotations.Intermittent;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class IntermittentTestRunner extends BlockJUnit4ClassRunner {
    private final Class<?> type;

    public IntermittentTestRunner(Class<?> type) throws InitializationError {
        super(type);
        this.type = type;
    }

    @Override
    protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
        for (int i = 0; i < repeatCount(method); i++)
            super.runChild(method, notifier);
    }

    private int repeatCount(FrameworkMethod method) {
        if (intermittent(type))
            return repetition(type);
        if (intermittent(method))
            return repetition(method);
        return 1;
    }

    private static boolean intermittent(FrameworkMethod method) {
        return method.getAnnotation(Intermittent.class) != null;
    }

    private static boolean intermittent(Class<?> type) {
        return type.getAnnotation(Intermittent.class) != null;
    }

    private static int repetition(FrameworkMethod method) {
        return method.getAnnotation(Intermittent.class).repetition();
    }

    private static int repetition(Class<?> type) {
        return type.getAnnotation(Intermittent.class).repetition();
    }

}

