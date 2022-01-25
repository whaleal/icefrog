package com.whaleal.icefrog.collections;


import javax.annotation.CheckForNull;
import java.io.Serializable;

/**
 * A mutable value of type {@code int}, for multisets to use in tracking counts of values.
 */


final class Count implements Serializable {
    private int value;

    Count( int value ) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    public void add( int delta ) {
        value += delta;
    }

    public int addAndGet( int delta ) {
        return value += delta;
    }

    public void set( int newValue ) {
        value = newValue;
    }

    public int getAndSet( int newValue ) {
        int result = value;
        value = newValue;
        return result;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public boolean equals( @CheckForNull Object obj ) {
        return obj instanceof Count && ((Count) obj).value == value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
