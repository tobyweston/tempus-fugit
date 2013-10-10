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

package com.google.code.tempusfugit.concurrency;

import com.google.code.tempusfugit.temporal.Condition;
import java.util.concurrent.Callable;

public class CallableAdapter {

    public static Runnable runnable(final Callable callable) {
        return new Runnable() {
            public void run() {
                try {
                    callable.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

	/**
	 * Converts a callable to a Condition.
	 * <p/>
	 * If the callable throws an Exception, the Condition is assumed to not have been
	 * satisfied.
	 *
	 * @param callable to be converted
	 * @return Condition wrapping a Callable
	 */
	public static Condition condition( final Callable<Boolean> callable ) {
		return new Condition() {
			@Override
			public boolean isSatisfied() {
				try {
					return callable.call();
				} catch ( Exception e ) {
					return false;
				}
			}
		};
	}

}
