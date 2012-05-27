/*
 * Copyright (c) 2009-2011, tempus-fugit committers
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

package com.google.code.tempusfugit.concurrency.annotations;

/**
 * Basic example of an AspectJ aspect that will declare a compilation error for @Immutable classes that provide
 * obvious mutators.
 * <p/>
 * Currently doesn't ensure that all members are themselves @Immutable or that constructors / getters against collections
 * ensure immutability.
 * <p/>
 */
public abstract aspect DeclareImmutableError {

	pointcut testCase() : within(TestCase+);

	pointcut mutators() : call(*.new()) || call(* *.set*(..)) || call(* *.add*(..));

	pointcut immutable() : @within(Immutable);

	declare error : mutators() && immutable() && !testCase() : "Immutable objects should not be mutated";
	
}