package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.temporal.Condition;

public class ThreadAliveCondition implements Condition {
    private final Thread thread;

    public ThreadAliveCondition(Thread thread) {
        this.thread = thread;
    }

    public boolean isSatisfied() {
        return thread.isAlive();
    }
}
