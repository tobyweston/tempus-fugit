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

package com.google.code.tempusfugit.concurrency.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Based on the annotation of the same name by Brian Goetz and Tim Tim Peierls.
 * <p/>
 * Broadly intended for use in the same context as described by Goetz in Concurrency In Practice
 * but with some attempt at type safety on the parameter types.
 * <p/>
 * @see com.google.code.tempusfugit.concurrency.annotations.GuardedBy.Type 
 */
@Documented
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface GuardedBy {

    Type lock();
    String details() default "";

    public static enum Type {
        /** An intrinsic lock */
        THIS,
        /** An inner class disambiguation from {@link GuardedBy.Type.THIS} monitor, qualify with @{link GuardedBy#details} */
        @Deprecated INNER_CLASS_THIS,
        /** A class monitor, qualify with {@link GuardedBy#details} */
        CLASS,
        /** When members are guarded by themselves (some collection classes for example) */
        ITSELF,
        /** A field, qualify with {@link GuardedBy#details} */
        FIELD,
        /** A static field, qualify with {@link GuardedBy#details}*/
        STATIC_FIELD,
        /** The lock object is returned by calling the method, qualify with {@link GuardedBy#details} */
        METHOD;
    }
}
