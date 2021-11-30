package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.lang.Predicate;

import javax.annotation.CheckForNull;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Implementation of {@link Multimaps#filterEntries(SetMultimap, Predicate)}.
 */


final class FilteredEntrySetMultimap<K extends Object, V extends Object>
        extends FilteredEntryMultimap<K, V> implements FilteredSetMultimap<K, V> {

    FilteredEntrySetMultimap( SetMultimap<K, V> unfiltered, Predicate<? super Entry<K, V>> predicate ) {
        super(unfiltered, predicate);
    }

    @Override
    public SetMultimap<K, V> unfiltered() {
        return (SetMultimap<K, V>) unfiltered;
    }

    @Override
    public Set<V> get( @ParametricNullness K key ) {
        return (Set<V>) super.get(key);
    }

    @Override
    public Set<V> removeAll( @CheckForNull Object key ) {
        return (Set<V>) super.removeAll(key);
    }

    @Override
    public Set<V> replaceValues( @ParametricNullness K key, Iterable<? extends V> values ) {
        return (Set<V>) super.replaceValues(key, values);
    }

    @Override
    Set<Entry<K, V>> createEntries() {
        return SetUtil.filter(unfiltered().entries(), entryPredicate());
    }

    @Override
    public Set<Entry<K, V>> entries() {
        return (Set<Entry<K, V>>) super.entries();
    }
}
