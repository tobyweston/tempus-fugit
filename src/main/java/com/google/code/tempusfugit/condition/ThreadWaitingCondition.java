package com.google.code.tempusfugit.condition;

import com.google.code.tempusfugit.temporal.Condition;

import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;

public class ThreadWaitingCondition implements Condition {
    private final Thread thread;

    public ThreadWaitingCondition(Thread thread) {
        this.thread = thread;
    }

    public boolean isSatisfied() {
        return (thread.getState() == TIMED_WAITING) || (thread.getState() == WAITING);
    }
}
