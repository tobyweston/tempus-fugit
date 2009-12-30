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

package com.google.code.tempusfugit.temporal;

import org.junit.Test;

import static com.google.code.tempusfugit.temporal.Conditions.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ConditionsTest {

    private Condition TRUE = new Condition() {
        public boolean isSatisfied() {
            return true;
        }
    };

    private Condition FALSE = new Condition() {
        public boolean isSatisfied() {
            return false;
        }
    };

    @Test
    public void notCondition() {
        assertThat(not(TRUE).isSatisfied(), is(false));
        assertThat(not(FALSE).isSatisfied(), is(true));
    }
}
