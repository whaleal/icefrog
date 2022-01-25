package com.whaleal.icefrog.collections;

import java.util.Iterator;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/**
 * An iterator that transforms a backing iterator; for internal use. This avoids the object overhead
 * of constructing a {@link Function Function} for internal methods.
 */


abstract class TransIter<F extends Object, T extends Object>
        implements Iterator<T> {
    final Iterator<? extends F> backingIterator;

    TransIter( Iterator<? extends F> backingIterator ) {
        this.backingIterator = checkNotNull(backingIterator);
    }

    @ParametricNullness
    abstract T transform( @ParametricNullness F from );

    @Override
    public final boolean hasNext() {
        return backingIterator.hasNext();
    }

    @Override
    @ParametricNullness
    public final T next() {
        return transform(backingIterator.next());
    }

    @Override
    public final void remove() {
        backingIterator.remove();
    }
}
