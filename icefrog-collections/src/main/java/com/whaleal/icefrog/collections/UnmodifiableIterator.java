package com.whaleal.icefrog.collections;


import java.util.Iterator;

/**
 * An iterator that does not support {@link #remove}.
 *
 * <p>{@code AbstractIterator} is used primarily in conjunction with implementations of {@link
 * ImmutableCollection}, such as {@link ImmutableList}. You can, however, convert an existing
 * iterator to an {@code AbstractIterator} using.
 */


public abstract class UnmodifiableIterator<E extends Object> implements Iterator<E> {
    /**
     * Constructor for use by subclasses.
     */
    protected UnmodifiableIterator() {
    }

    /**
     * Guaranteed to throw an exception and leave the underlying data unmodified.
     *
     * @throws UnsupportedOperationException always
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override

    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
