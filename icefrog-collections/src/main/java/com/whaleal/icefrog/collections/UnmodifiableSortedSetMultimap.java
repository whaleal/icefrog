package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;

/**
 * @author wh
 */
public class UnmodifiableSortedSetMultimap<
        K extends Object, V extends Object >
        extends UnmodifiableSetMultimap< K, V > implements SortedSetMultimap< K, V > {
    private static final long serialVersionUID = 0;

    UnmodifiableSortedSetMultimap( SortedSetMultimap< K, V > delegate ) {
        super(delegate);
    }

    @Override
    public SortedSetMultimap< K, V > delegate() {
        return (SortedSetMultimap< K, V >) super.delegate();
    }

    @Override
    public SortedSet< V > get( @ParametricNullness K key ) {
        return Collections.unmodifiableSortedSet(delegate().get(key));
    }

    @Override
    public SortedSet< V > removeAll( @CheckForNull Object key ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedSet< V > replaceValues( @ParametricNullness K key, Iterable< ? extends V > values ) {
        throw new UnsupportedOperationException();
    }

    @Override
    @CheckForNull
    public Comparator< ? super V > valueComparator() {
        return delegate().valueComparator();
    }
}
