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

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExecuteUsingLock<T, E extends Exception> {

    private final Callable<T, E> callable;

    private ExecuteUsingLock(Callable<T, E> callable) {
        this.callable = callable;
    }

    public static <T, E extends Exception> ExecuteUsingLock<T, E> execute(Callable<T, E> callable) {
        return new ExecuteUsingLock<T, E>(callable);
    }

    public T using(ReentrantReadWriteLock.WriteLock write) throws E {
        try {
            write.lock();
            return callable.call();
        } finally {
            write.unlock();
        }
    }

    public T using(ReentrantReadWriteLock.ReadLock read) throws E {
        try {
            read.lock();
            return callable.call();
        } finally {
            read.unlock();
        }
    }

}
