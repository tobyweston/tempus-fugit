/*
 * Copyright (c) 2009-2015, toby weston & tempus-fugit committers
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

import com.google.code.tempusfugit.condition.SelfDescribingMatcherCondition;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.google.code.tempusfugit.temporal.SimulateJUnitFailure.failOnTimeout;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class SimulateJUnitFailureTest {

    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test (expected = AssertionError.class)
    public void throwsException() {
        failOnTimeout(aCondition()).call();
    }

    @Test
    public void throwsExceptionWithSpecificMessage() {
        exception.expectMessage("I failed because I didn't try hard enough");
        failOnTimeout(aCondition()).call();
    }

    @Test
    public void shouldSimulateJUnitFailureOnTimeout() {
        exception.expect(AssertionError.class);
        exception.expectMessage(Matchers.<String>allOf(
                containsString("test lambda"),
                containsString("Expected: is \"the best\""),
                containsString("but: <was \"the worst\">")
        ));
        SelfDescribingMatcherCondition<String> condition = new SelfDescribingMatcherCondition<String>(probe("the worst"), is("the best"));
        condition.isSatisfied();
        failOnTimeout(condition).call();
    }

    private SelfDescribingCondition aCondition() {
        return new SelfDescribingCondition() {
            @Override
            public boolean isSatisfied() {
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("I failed because I didn't try hard enough");
            }
        };
    }

    private static ProbeFor<String> probe(final String value) {
        return new ProbeFor<String>() {
            @Override
            public String call() throws RuntimeException {
                return value;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("test lambda");
            }
        };
    }
}
