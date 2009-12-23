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

import junit.framework.AssertionFailedError;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

class RunRepeatedly extends Statement {

    private final FrameworkMethod method;
    private final Statement statement;

    public RunRepeatedly(FrameworkMethod method, Statement statement) {
        this.method = method;
        this.statement = statement;
    }

    public void evaluate() throws Throwable {
        if (intermittent(method))
            for (int i = 0; i < repetition(method); i++) {
                try {
                    statement.evaluate();
                } catch (AssertionFailedError e) {
                    throw new AssertionFailedError(String.format("%s (failed after %d successful attempts)", e.getMessage(), i));
                }
            }
        else
            statement.evaluate();
    }

    private static boolean intermittent(FrameworkMethod method) {
        return method.getAnnotation(Intermittent.class) != null;
    }

    private static int repetition(FrameworkMethod method) {
        return method.getAnnotation(Intermittent.class).repetition();
    }
}
