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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.code.tempusfugit.concurrency.ExecuteUsingLock.execute;

@RunWith(ConcurrentTestRunner.class)
public class ExecuteUsingLockTest {

    private final Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private ReentrantReadWriteLock.ReadLock readLock = context.mock(ReentrantReadWriteLock.ReadLock.class);
    private ReentrantReadWriteLock.WriteLock writeLock = context.mock(ReentrantReadWriteLock.WriteLock.class);

    @Test
    public void readLock() {
        setExpectationsOn(readLock);
        execute(something()).using(readLock);
        context.assertIsSatisfied();
    }

    @Test
    public void writeLock() {
        setExpectationsOn(writeLock);
        execute(something()).using(writeLock);
        context.assertIsSatisfied();
    }

    @Test(expected = Exception.class)
    public void readLockThrowingException() throws Exception {
        setExpectationsOn(readLock);
        execute(somethingThatThrowsException()).using(readLock);
        context.assertIsSatisfied();
    }

    @Test(expected = Exception.class)
    public void writeLockThrowingException() throws Exception {
        setExpectationsOn(writeLock);
        execute(somethingThatThrowsException()).using(writeLock);
        context.assertIsSatisfied();
    }

    private Callable<Void, RuntimeException> something() {
        return new Callable<Void, RuntimeException>() {
            public Void call() throws RuntimeException {
                return null;
            }
        };
    }

    private Callable<Void, Exception> somethingThatThrowsException() {
        return new Callable<Void, Exception>() {
            public Void call() throws Exception {
                throw new RuntimeException("go go go");
            }
        };
    }

    private void setExpectationsOn(final Lock lock) {
        context.checking(new Expectations() {{
            one(lock).lock();
            one(lock).unlock();
        }});
    }

}
