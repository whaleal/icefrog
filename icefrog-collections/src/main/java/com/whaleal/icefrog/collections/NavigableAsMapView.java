package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author wh
 */

final class NavigableAsMapView<
        K extends Object, V extends Object >
        extends AbstractNavigableMap< K, V > {
    /*
     * Using AbstractNavigableMap is simpler than extending SortedAsMapView and rewriting all the
     * NavigableMap methods.
     */

    private final NavigableSet< K > set;
    private final Function< ? super K, V > function;

    NavigableAsMapView( NavigableSet< K > ks, Function< ? super K, V > vFunction ) {
        this.set = checkNotNull(ks);
        this.function = checkNotNull(vFunction);
    }

    @Override
    public NavigableMap< K, V > subMap(
            @ParametricNullness K fromKey,
            boolean fromInclusive,
            @ParametricNullness K toKey,
            boolean toInclusive ) {
        return MapUtil.asMap(set.subSet(fromKey, fromInclusive, toKey, toInclusive), function);
    }

    @Override
    public NavigableMap< K, V > headMap( @ParametricNullness K toKey, boolean inclusive ) {
        return MapUtil.asMap(set.headSet(toKey, inclusive), function);
    }

    @Override
    public NavigableMap< K, V > tailMap( @ParametricNullness K fromKey, boolean inclusive ) {
        return MapUtil.asMap(set.tailSet(fromKey, inclusive), function);
    }

    @Override
    @CheckForNull
    public Comparator< ? super K > comparator() {
        return set.comparator();
    }

    @Override
    @CheckForNull
    public V get( @CheckForNull Object key ) {
        return getOrDefault(key, null);
    }

    @Override
    @CheckForNull
    public V getOrDefault( @CheckForNull Object key, @CheckForNull V defaultValue ) {
        if (CollUtil.safeContains(set, key)) {
            @SuppressWarnings("unchecked") // unsafe, but Javadoc warns about it
            K k = (K) key;
            return function.apply(k);
        } else {
            return defaultValue;
        }
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    Iterator< Entry< K, V > > entryIterator() {
        return MapUtil.asMapEntryIterator(set, function);
    }

    @Override
    Spliterator< Entry< K, V > > entrySpliterator() {
        return SpliteratorUtil.map(set.spliterator(), e -> MapUtil.immutableEntry(e, function.apply(e)));
    }

    @Override
    public void forEach( BiConsumer< ? super K, ? super V > action ) {
        set.forEach(k -> action.accept(k, function.apply(k)));
    }

    @Override
    Iterator< Entry< K, V > > descendingEntryIterator() {
        return descendingMap().entrySet().iterator();
    }

    @Override
    public NavigableSet< K > navigableKeySet() {
        return MapUtil.removeOnlyNavigableSet(set);
    }

    @Override
    public int size() {
        return set.size();
    }

    @Override
    public NavigableMap< K, V > descendingMap() {
        return MapUtil.asMap(set.descendingSet(), function);
    }
}
