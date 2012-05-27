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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(JMock.class)
public class CompositeFactoryTest {

    private final Mockery context = new JUnit4Mockery();

    private final Factory<String> factory1 = context.mock(Factory.class, "factory1");
    private final Factory<String> factory2 = context.mock(Factory.class, "factory2");
    private final Factory<String> factory3 = context.mock(Factory.class, "factory3");


    @Test
    public void returnsOnSuccessfulDelegateCreate() {
        final Sequence sequence = context.sequence("enumeration");
        context.checking(new Expectations() {{
            one(factory1).create(); will(returnValue("done")); inSequence(sequence);
            never(factory2).create();
            never(factory3).create();
        }});
        assertThat(new CompositeFactory<String>(factory1, factory2, factory3).create(), is("done"));
    }

    @Test
    public void skipFactoriesThatThrowExceptions() {
        final Sequence sequence = context.sequence("enumeration");
        context.checking(new Expectations() {{
            one(factory1).create(); will(throwException(new FactoryException())); inSequence(sequence);
            one(factory2).create(); will(returnValue("done")); inSequence(sequence);
            never(factory3).create();
        }});
        assertThat(new CompositeFactory<String>(factory1, factory2, factory3).create(), is("done"));
    }

    @Test (expected = FactoryException.class)
    public void throwsExceptionIfAllFactoriesThrowExceptions() {
        final Sequence sequence = context.sequence("enumeration");
        context.checking(new Expectations() {{
            one(factory1).create(); will(throwException(new FactoryException())); inSequence(sequence);
            one(factory2).create(); will(throwException(new FactoryException())); inSequence(sequence);
            one(factory3).create(); will(throwException(new FactoryException())); inSequence(sequence);
        }});
        new CompositeFactory<String>(factory1, factory2, factory3).create();
    }
}
