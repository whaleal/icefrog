package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author wh
 */

        // NavigableMap
class FilteredEntryNavigableMap<
        K extends Object, V extends Object >
        extends AbstractNavigableMap< K, V > {
    /*
     * It's less code to extend AbstractNavigableMap and forward the filtering logic to
     * FilteredEntryMap than to extend FilteredEntrySortedMap and reimplement all the NavigableMap
     * methods.
     */

    private final MapUtil mapUtil;
    private final NavigableMap< K, V > unfiltered;
    private final Predicate< ? super Entry< K, V > > entryPredicate;
    private final Map< K, V > filteredDelegate;

    FilteredEntryNavigableMap( MapUtil mapUtil,
                               NavigableMap< K, V > unfiltered, Predicate< ? super Entry< K, V > > entryPredicate ) {
        this.mapUtil = mapUtil;
        this.unfiltered = checkNotNull(unfiltered);
        this.entryPredicate = entryPredicate;
        this.filteredDelegate = new FilteredEntryMap<>(unfiltered, entryPredicate);
    }

    @Override
    @CheckForNull
    public Comparator< ? super K > comparator() {
        return unfiltered.comparator();
    }

    @Override
    public NavigableSet< K > navigableKeySet() {
        return new MapUtil.NavigableKeySet< K, V >(this) {
            @Override
            public boolean removeAll( Collection< ? > collection ) {
                return FilteredEntryMap.removeAllKeys(unfiltered, entryPredicate, collection);
            }

            @Override
            public boolean retainAll( Collection< ? > collection ) {
                return FilteredEntryMap.retainAllKeys(unfiltered, entryPredicate, collection);
            }
        };
    }

    @Override
    public Collection< V > values() {
        return new FilteredMapValues<>(this, unfiltered, entryPredicate);
    }

    @Override
    Iterator< Entry< K, V > > entryIterator() {
        return IterUtil.filter(unfiltered.entrySet().iterator(), entryPredicate);
    }

    @Override
    Iterator< Entry< K, V > > descendingEntryIterator() {
        return IterUtil.filter(unfiltered.descendingMap().entrySet().iterator(), entryPredicate);
    }

    @Override
    public int size() {
        return filteredDelegate.size();
    }

    @Override
    public boolean isEmpty() {
        return !Iterables.any(unfiltered.entrySet(), entryPredicate);
    }

    @Override
    @CheckForNull
    public V get( @CheckForNull Object key ) {
        return filteredDelegate.get(key);
    }

    @Override
    public boolean containsKey( @CheckForNull Object key ) {
        return filteredDelegate.containsKey(key);
    }

    @Override
    @CheckForNull
    public V put( @ParametricNullness K key, @ParametricNullness V value ) {
        return filteredDelegate.put(key, value);
    }

    @Override
    @CheckForNull
    public V remove( @CheckForNull Object key ) {
        return filteredDelegate.remove(key);
    }

    @Override
    public void putAll( Map< ? extends K, ? extends V > m ) {
        filteredDelegate.putAll(m);
    }

    @Override
    public void clear() {
        filteredDelegate.clear();
    }

    @Override
    public Set< Entry< K, V > > entrySet() {
        return filteredDelegate.entrySet();
    }

    @Override
    @CheckForNull
    public Entry< K, V > pollFirstEntry() {
        return Iterables.removeFirstMatching(unfiltered.entrySet(), entryPredicate);
    }

    @Override
    @CheckForNull
    public Entry< K, V > pollLastEntry() {
        return Iterables.removeFirstMatching(unfiltered.descendingMap().entrySet(), entryPredicate);
    }

    @Override
    public NavigableMap< K, V > descendingMap() {
        return MapUtil.filterEntries(unfiltered.descendingMap(), entryPredicate);
    }

    @Override
    public NavigableMap< K, V > subMap(
            @ParametricNullness K fromKey,
            boolean fromInclusive,
            @ParametricNullness K toKey,
            boolean toInclusive ) {
        return MapUtil.filterEntries(
                unfiltered.subMap(fromKey, fromInclusive, toKey, toInclusive), entryPredicate);
    }

    @Override
    public NavigableMap< K, V > headMap( @ParametricNullness K toKey, boolean inclusive ) {
        return MapUtil.filterEntries(unfiltered.headMap(toKey, inclusive), entryPredicate);
    }

    @Override
    public NavigableMap< K, V > tailMap( @ParametricNullness K fromKey, boolean inclusive ) {
        return MapUtil.filterEntries(unfiltered.tailMap(fromKey, inclusive), entryPredicate);
    }
}
