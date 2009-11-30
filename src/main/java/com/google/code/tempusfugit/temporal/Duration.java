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

import java.util.concurrent.TimeUnit;

public class Duration {
    private final Long value;
    private final TimeUnit unit;

    private Duration(Long value, TimeUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public static Duration seconds(long seconds) {
        validate(seconds, TimeUnit.SECONDS);
        return new Duration(seconds, TimeUnit.SECONDS);
    }

    public static Duration millis(long millis) {
        validate(millis, TimeUnit.MILLISECONDS);
        return new Duration(millis, TimeUnit.MILLISECONDS);
    }

    public static Duration minutes(long minutes) {
        long seconds = minutes * 60;
        validate(seconds,  TimeUnit.SECONDS);
        return new Duration(seconds, TimeUnit.SECONDS);
    }

    public static Duration hours(long hours) {
        return minutes(hours * 60);
    }

    public static Duration days(long days) {
        return hours(days * 24);
    }

    private static void validate(long value, TimeUnit unit) {
        Duration duration = new Duration(value, unit);
        if (duration.inMillis() == Long.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
    }

    public long inMillis() {
        return unit.toMillis(value);
    }

    public long inSeconds() {
        return unit.toSeconds(value);
    }

    public long inMinutes() {
        return unit.toSeconds(value) / 60;
    }

    public long inHours() {
        return inMinutes() / 60;
    }

    public long inDays() {
        return inHours() / 24;
    }

    public Duration plus(Duration duration) {
        return millis(duration.inMillis() + this.inMillis());
    }

    public Boolean greaterThan(Duration comparator) {
        return this.inMillis() > comparator.inMillis();
    }

    @Override
    public int hashCode() {
        return new Long(unit.toMillis(value)).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o.getClass().getName().equals(Duration.class.getName())))
            return false;
        Duration other = (Duration) o;
        return other.unit.toMillis(other.value) == this.unit.toMillis(this.value);
    }

    public String toString() {
        return "Duration " + value + " " + unit;
    }
}
