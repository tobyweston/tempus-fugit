package com.google.code.tempusfugit.temporal;

import com.google.code.tempusfugit.concurrency.Callable;
import org.hamcrest.SelfDescribing;

/**
 * A lazy evaluation probe for specific value.
 * <p></p>
 * Implementations should implement {@link #describeTo(org.hamcrest.Description)} to describe the specific probe, for example, a probe to check 'the time on a clock'.
 * <p></p>
 * @param <T> the value to lazily probe for
 */
public interface ProbeFor<T> extends Callable<T, RuntimeException>, SelfDescribing {

}
