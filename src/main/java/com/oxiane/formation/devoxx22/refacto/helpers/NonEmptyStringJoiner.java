package com.oxiane.formation.devoxx22.refacto.helpers;

import java.util.StringJoiner;

/**
 * This class has same functionalities than java.util.StringJoiner, excepts
 * that empty or null values are ignored.
 *
 * Sample :
 *   {@code NonEmptyStringJoiner nesj;}
 *   {@code StringJoiner sj;}
 *   {@code nesj.add("George")} is identical to {@code sj.add("George")}
 *   {@code nesj.add(null)} does nothing
 *   {@code nesj.add("")} does nothing
 */
public class NonEmptyStringJoiner {
    private final StringJoiner joiner;

    public NonEmptyStringJoiner(CharSequence delimiter) {
        joiner = new StringJoiner(delimiter);
    }
    public NonEmptyStringJoiner(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        joiner = new StringJoiner(delimiter, prefix, suffix);
    }

    public NonEmptyStringJoiner add(CharSequence newElement) {
        if(newElement!=null && !newElement.isEmpty()) joiner.add(newElement);
        return this;
    }

    public int length() { return joiner.length(); }
    public NonEmptyStringJoiner merge(NonEmptyStringJoiner other) {
        return add(other.toString());
    }
    public NonEmptyStringJoiner merge(StringJoiner other) {
        return add(other.toString());
    }
    public NonEmptyStringJoiner setEmptyValue(CharSequence emptyValue) {
        joiner.setEmptyValue(emptyValue);
        return this;
    }
    public String toString() { return joiner.toString(); }
}
