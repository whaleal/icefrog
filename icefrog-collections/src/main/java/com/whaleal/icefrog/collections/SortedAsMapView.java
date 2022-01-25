package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Function;

/**
 * @author wh
 */
private static class SortedAsMapView< K extends Object, V extends Object >
        extends AsMapView< K, V > implements SortedMap< K, V > {

    SortedAsMapView( SortedSet< K > set, Function< ? super K, V > function ) {
        super(set, function);
    }

    @Override
    SortedSet< K > backingSet() {
        return (SortedSet< K >) super.backingSet();
    }

    @Override
    @CheckForNull
    public Comparator< ? super K > comparator() {
        return backingSet().comparator();
    }

    @Override
    public Set< K > keySet() {
        return removeOnlySortedSet(backingSet());
    }

    @Override
    public SortedMap< K, V > subMap( K fromKey, K toKey ) {
        return asMap(backingSet().subSet(fromKey, toKey), function);
    }

    @Override
    public SortedMap< K, V > headMap( K toKey ) {
        return asMap(backingSet().headSet(toKey), function);
    }

    @Override
    public SortedMap< K, V > tailMap( K fromKey ) {
        return asMap(backingSet().tailSet(fromKey), function);
    }

    @Override

    public K firstKey() {
        return backingSet().first();
    }

    @Override

    public K lastKey() {
        return backingSet().last();
    }
}
