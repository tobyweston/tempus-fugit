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

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

class RunConcurrently extends Statement {

    private final FrameworkMethod method;
    private final Statement statement;

    public RunConcurrently(FrameworkMethod method, Statement statement) {
        this.method = method;
        this.statement = statement;
    }

    public void evaluate() throws Throwable {
        if (concurrent(method)) {
            List<StatementEvaluatingThread> threads = createThreads();
            start(threads);
            join(threads);
        } else
            statement.evaluate();
    }

    private List<StatementEvaluatingThread> createThreads() {
        List<StatementEvaluatingThread> threads = new ArrayList<StatementEvaluatingThread>();
        for (int i = 0; i < threadCount(method); i++)
            threads.add(new StatementEvaluatingThread(statement));
        return threads;
    }

    private void start(List<StatementEvaluatingThread> threads) {
        for (Thread thread : threads)
            thread.start();
    }

    private void join(List<StatementEvaluatingThread> threads) throws Throwable {
        for (StatementEvaluatingThread thread : threads)
            thread.joinAndRethrowExceptions();
    }

    private static boolean concurrent(FrameworkMethod method) {
        return method.getAnnotation(Concurrent.class) != null;
    }

    private static int threadCount(FrameworkMethod method) {
        return method.getAnnotation(Concurrent.class).count();
    }

    private class StatementEvaluatingThread extends Thread {

        private final Statement statement;
        private final CountDownLatch finished = new CountDownLatch(1);

        private Throwable throwable;

        private StatementEvaluatingThread(Statement statement) {
            this.statement = statement;
        }

        @Override
        public void run() {
            try {
                statement.evaluate();
            } catch (Throwable throwable) {
                this.throwable = throwable;
            } finally {
                finished.countDown();
            }
        }

        public void joinAndRethrowExceptions() throws Throwable {
            try {
                finished.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (throwable != null)
                    throw throwable;
            }
        }

    }
}