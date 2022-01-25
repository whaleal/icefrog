package com.whaleal.icefrog.core.collection;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
public class TransRandomAccessList<
        F extends Object, T extends Object>
        extends AbstractList<T> implements RandomAccess, Serializable {
    private static final long serialVersionUID = 0;
    final List<F> fromList;
    final Function<? super F, ? extends T> function;

    TransRandomAccessList( List<F> fromList, Function<? super F, ? extends T> function ) {
        this.fromList = checkNotNull(fromList);
        this.function = checkNotNull(function);
    }

    @Override
    public void clear() {
        fromList.clear();
    }

    @Override
    public T get( int index ) {
        return function.apply(fromList.get(index));
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<T> listIterator( int index ) {
        return new TransListIter(fromList.listIterator(index),function) ;
    }

    @Override
    public boolean isEmpty() {
        return fromList.isEmpty();
    }

    @Override
    public boolean removeIf( Predicate<? super T> filter ) {
        checkNotNull(filter);
        return fromList.removeIf(element -> filter.test(function.apply(element)));
    }

    @Override
    public T remove( int index ) {
        return function.apply(fromList.remove(index));
    }

    @Override
    public int size() {
        return fromList.size();
    }
}
