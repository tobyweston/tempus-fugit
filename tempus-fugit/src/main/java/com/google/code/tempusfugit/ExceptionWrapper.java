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

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;

import static com.google.code.tempusfugit.ExceptionWrapper.ExceptionFactory.newException;

/**
 * @since 1.1
 */
public class ExceptionWrapper {

    public static <V> V wrapAsRuntimeException(Callable<V> callable) throws RuntimeException {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <V, E extends Exception> V wrapAnyException(Callable<V> callable, final WithException<E> wrapper) throws E {
        try {
            return callable.call();
        } catch (Throwable e) {
            throw newException(wrapper, e).create();
        }
    }

    /** @since 1.2 */
    public static void throwAsRuntimeException(Exception throwable) {
        throw new RuntimeException(throwable);
    }

    /** @since 1.2 */
    public static <E extends RuntimeException> void throwException(Exception throwable, WithException<E> wrapper) throws E {
        throw newException(wrapper, throwable).create();
    }

    static class ExceptionFactory<E extends Exception> implements Factory<E> {
        private final Class wrapped;
        private final Throwable throwable;

        static <E extends Exception> ExceptionFactory<E> newException(WithException<E> wrapped, Throwable throwable) {
            return new ExceptionFactory<E>(wrapped.getType(), throwable);
        }

        private ExceptionFactory(Class<E> wrapped, Throwable throwable) {
            this.wrapped = wrapped;
            this.throwable = throwable;
        }

        @Override
        public E create() throws FactoryException {
            try {
                Constructor<E> constructor = wrapped.getConstructor(Throwable.class);
                return constructor.newInstance(throwable);
            } catch (Exception e) {
                throw new FactoryException(e);
            }
        }
    }

}
