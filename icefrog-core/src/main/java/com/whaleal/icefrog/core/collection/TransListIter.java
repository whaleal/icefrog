package com.whaleal.icefrog.core.collection;

import java.util.ListIterator;
import java.util.function.Function;

/**
 * @author wh
 */
public class TransListIter<F extends Object, T extends Object> extends TransIter<F, T> implements ListIterator<T> {

    TransListIter( ListIterator<? extends F> backingIterator , Function<? super F, ? extends T> func ) {
        super(backingIterator,func);
    }

    private ListIterator<? extends F> backingIterator() {
        return (ListIterator<? extends F>)backingIterator;
    }

    @Override
    public final boolean hasPrevious() {
        return backingIterator().hasPrevious();
    }

    @Override
    public final T previous() {
        return func.apply(backingIterator().previous());
    }

    @Override
    public final int nextIndex() {
        return backingIterator().nextIndex();
    }

    @Override
    public final int previousIndex() {
        return backingIterator().previousIndex();
    }

    @Override
    public void set( T element ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(  T element ) {
        throw new UnsupportedOperationException();
    }
}
