package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.*;

/**
 * @author wh
 */
class NavigableKeySet<K extends Object, V extends Object>
        extends MapUtil.SortedKeySet<K, V> implements NavigableSet<K> {
    NavigableKeySet( NavigableMap<K, V> map ) {
        super(map);
    }

    @Override
    NavigableMap<K, V> map() {
        return (NavigableMap<K, V>) map;
    }

    @Override
    @CheckForNull
    public K lower( K e ) {
        return map().lowerKey(e);
    }

    @Override
    @CheckForNull
    public K floor( K e ) {
        return map().floorKey(e);
    }

    @Override
    @CheckForNull
    public K ceiling( K e ) {
        return map().ceilingKey(e);
    }

    @Override
    @CheckForNull
    public K higher( K e ) {
        return map().higherKey(e);
    }

    @Override
    @CheckForNull
    public K pollFirst() {
        Map.Entry< K, V > entry = map().pollFirstEntry();
        return entry ==null? null:entry.getKey();
    }

    @Override
    @CheckForNull
    public K pollLast() {
        Map.Entry< K, V > entry = map().pollLastEntry();
        return entry ==null? null:entry.getKey();

    }

    @Override
    public NavigableSet<K> descendingSet() {
        return map().descendingKeySet();
    }

    @Override
    public Iterator<K> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public NavigableSet<K> subSet(
            K fromElement,
            boolean fromInclusive,
            K toElement,
            boolean toInclusive ) {
        return map().subMap(fromElement, fromInclusive, toElement, toInclusive).navigableKeySet();
    }

    @Override
    public SortedSet<K> subSet( K fromElement, K toElement ) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public NavigableSet<K> headSet( K toElement, boolean inclusive ) {
        return map().headMap(toElement, inclusive).navigableKeySet();
    }

    @Override
    public SortedSet<K> headSet( K toElement ) {
        return headSet(toElement, false);
    }

    @Override
    public NavigableSet<K> tailSet( K fromElement, boolean inclusive ) {
        return map().tailMap(fromElement, inclusive).navigableKeySet();
    }

    @Override
    public SortedSet<K> tailSet( K fromElement ) {
        return tailSet(fromElement, true);
    }
}