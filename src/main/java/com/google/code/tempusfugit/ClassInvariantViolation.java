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

package com.google.code.tempusfugit;

/**
 * This exception is used to explicitly capture invariants and force runtime exceptions if they are violated. Use
 * this rather than the <code>assert</code> keyword if you don't want to conditionally check invariants (ie, you wont
 * need to -enableassertions flag on the VM).
 *
 * From Wikipedia "Methods of a class should preserve any class invariants. The class invariant constrains the state stored in the
 * object and should be constantly maintained between calls to public methods. An object invariant, or representation
 * invariant, is a programming construct consisting of a set of invariant properties that remain uncompromised
 * regardless of the state of the object. This ensures that the object will always meet predefined conditions,
 * and that methods may, therefore, always reference the object without the risk of making inaccurate presumptions."
 *
 * @see http://en.wikipedia.org/wiki/Class_invariant
 * @since 1.2
 */
public class ClassInvariantViolation extends RuntimeException {

    public ClassInvariantViolation() {
        super();
    }

    public ClassInvariantViolation(String message) {
        super(message);
    }

    public ClassInvariantViolation(String message, Throwable cause) {
        super(message, cause);
    }

    public ClassInvariantViolation(Throwable cause) {
        super(cause);
    }
}
