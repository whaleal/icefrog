package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author wh
 */
public class UnmodifiableSetMultimap<
        K extends Object, V extends Object >
        extends UnmodifiableMultimap< K, V > implements SetMultimap< K, V > {
    private static final long serialVersionUID = 0;

    UnmodifiableSetMultimap( SetMultimap< K, V > delegate ) {
        super(delegate);
    }

    @Override
    public SetMultimap< K, V > delegate() {
        return (SetMultimap< K, V >) super.delegate();
    }

    @Override
    public Set< V > get( @ParametricNullness K key ) {
        /*
         * Note that this doesn't return a SortedSet when delegate is a
         * SortedSetMultiset, unlike (SortedSet<V>) super.get().
         */
        return Collections.unmodifiableSet(delegate().get(key));
    }

    @Override
    public Set< Map.Entry< K, V > > entries() {
        return MapUtil.unmodifiableEntrySet(delegate().entries());
    }

    @Override
    public Set< V > removeAll( @CheckForNull Object key ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set< V > replaceValues( @ParametricNullness K key, Iterable< ? extends V > values ) {
        throw new UnsupportedOperationException();
    }
}
