package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.NavigableMap;
import java.util.NavigableSet;

/**
 * @author wh
 */

        // NavigableMap
class TransformedEntriesNavigableMap<
        K extends Object, V1 extends Object, V2 extends Object >
        extends TransformedEntriesSortedMap< K, V1, V2 > implements NavigableMap< K, V2 > {

    TransformedEntriesNavigableMap(
            NavigableMap< K, V1 > fromMap, MapUtil.EntryTransformer< ? super K, ? super V1, V2 > transformer ) {
        super(fromMap, transformer);
    }

    @Override
    @CheckForNull
    public Entry< K, V2 > ceilingEntry( @ParametricNullness K key ) {
        return transformEntry(fromMap().ceilingEntry(key));
    }

    @Override
    @CheckForNull
    public K ceilingKey( @ParametricNullness K key ) {
        return fromMap().ceilingKey(key);
    }

    @Override
    public NavigableSet< K > descendingKeySet() {
        return fromMap().descendingKeySet();
    }

    @Override
    public NavigableMap< K, V2 > descendingMap() {
        return transformEntries(fromMap().descendingMap(), transformer);
    }

    @Override
    @CheckForNull
    public Entry< K, V2 > firstEntry() {
        return transformEntry(fromMap().firstEntry());
    }

    @Override
    @CheckForNull
    public Entry< K, V2 > floorEntry( @ParametricNullness K key ) {
        return transformEntry(fromMap().floorEntry(key));
    }

    @Override
    @CheckForNull
    public K floorKey( @ParametricNullness K key ) {
        return fromMap().floorKey(key);
    }

    @Override
    public NavigableMap< K, V2 > headMap( @ParametricNullness K toKey ) {
        return headMap(toKey, false);
    }

    @Override
    public NavigableMap< K, V2 > headMap( @ParametricNullness K toKey, boolean inclusive ) {
        return transformEntries(fromMap().headMap(toKey, inclusive), transformer);
    }

    @Override
    @CheckForNull
    public Entry< K, V2 > higherEntry( @ParametricNullness K key ) {
        return transformEntry(fromMap().higherEntry(key));
    }

    @Override
    @CheckForNull
    public K higherKey( @ParametricNullness K key ) {
        return fromMap().higherKey(key);
    }

    @Override
    @CheckForNull
    public Entry< K, V2 > lastEntry() {
        return transformEntry(fromMap().lastEntry());
    }

    @Override
    @CheckForNull
    public Entry< K, V2 > lowerEntry( @ParametricNullness K key ) {
        return transformEntry(fromMap().lowerEntry(key));
    }

    @Override
    @CheckForNull
    public K lowerKey( @ParametricNullness K key ) {
        return fromMap().lowerKey(key);
    }

    @Override
    public NavigableSet< K > navigableKeySet() {
        return fromMap().navigableKeySet();
    }

    @Override
    @CheckForNull
    public Entry< K, V2 > pollFirstEntry() {
        return transformEntry(fromMap().pollFirstEntry());
    }

    @Override
    @CheckForNull
    public Entry< K, V2 > pollLastEntry() {
        return transformEntry(fromMap().pollLastEntry());
    }

    @Override
    public NavigableMap< K, V2 > subMap(
            @ParametricNullness K fromKey,
            boolean fromInclusive,
            @ParametricNullness K toKey,
            boolean toInclusive ) {
        return transformEntries(
                fromMap().subMap(fromKey, fromInclusive, toKey, toInclusive), transformer);
    }

    @Override
    public NavigableMap< K, V2 > subMap( @ParametricNullness K fromKey, @ParametricNullness K toKey ) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public NavigableMap< K, V2 > tailMap( @ParametricNullness K fromKey ) {
        return tailMap(fromKey, true);
    }

    @Override
    public NavigableMap< K, V2 > tailMap( @ParametricNullness K fromKey, boolean inclusive ) {
        return transformEntries(fromMap().tailMap(fromKey, inclusive), transformer);
    }

    @CheckForNull
    private Entry< K, V2 > transformEntry( @CheckForNull Entry< K, V1 > entry ) {
        return (entry == null) ? null : MapUtil.transformEntry(transformer, entry);
    }

    @Override
    protected NavigableMap< K, V1 > fromMap() {
        return (NavigableMap< K, V1 >) super.fromMap();
    }
}
