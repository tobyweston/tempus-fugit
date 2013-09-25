/*
 * Copyright (c) 2009-2013, toby weston & tempus-fugit committers
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

package com.google.code.tempusfugit.concurrency.kidnapping;

public class Kidnapper extends Person {

    private final Cash cash;
    private final Cat nibbles;

    public Kidnapper(Cash cash, Cat nibbles) {
        setName("Kidnapper-" + getName());
        this.cash = cash;
        this.nibbles = nibbles;
    }

    @Override
    public void run() {
        try {
            nibbles.hold(cash.take());
        } finally {
            nibbles.release();
        }
    }

}
