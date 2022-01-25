package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.*;

/**
 * @author wh
 */ // NavigableMap
class UnmodifiableNavigableMap< K extends Object, V extends Object >
        extends ForwardingSortedMap< K, V > implements NavigableMap< K, V >, Serializable {
    private final NavigableMap< K, ? extends V > delegate;
    @CheckForNull
    private transient UnmodifiableNavigableMap< K, V > descendingMap;

    UnmodifiableNavigableMap( NavigableMap< K, ? extends V > delegate ) {
        this.delegate = delegate;
    }

    UnmodifiableNavigableMap(
            NavigableMap< K, ? extends V > delegate, UnmodifiableNavigableMap< K, V > descendingMap ) {
        this.delegate = delegate;
        this.descendingMap = descendingMap;
    }

    @Override
    protected SortedMap< K, V > delegate() {
        return Collections.unmodifiableSortedMap(delegate);
    }

    @Override
    @CheckForNull
    public Entry< K, V > lowerEntry( K key ) {
        return unmodifiableOrNull(delegate.lowerEntry(key));
    }

    @Override
    @CheckForNull
    public K lowerKey( K key ) {
        return delegate.lowerKey(key);
    }

    @Override
    @CheckForNull
    public Entry< K, V > floorEntry( K key ) {
        return unmodifiableOrNull(delegate.floorEntry(key));
    }

    @Override
    @CheckForNull
    public K floorKey( K key ) {
        return delegate.floorKey(key);
    }

    @Override
    @CheckForNull
    public Entry< K, V > ceilingEntry( K key ) {
        return unmodifiableOrNull(delegate.ceilingEntry(key));
    }

    @Override
    @CheckForNull
    public K ceilingKey( K key ) {
        return delegate.ceilingKey(key);
    }

    @Override
    @CheckForNull
    public Entry< K, V > higherEntry( K key ) {
        return unmodifiableOrNull(delegate.higherEntry(key));
    }

    @Override
    @CheckForNull
    public K higherKey( K key ) {
        return delegate.higherKey(key);
    }

    @Override
    @CheckForNull
    public Entry< K, V > firstEntry() {
        return unmodifiableOrNull(delegate.firstEntry());
    }

    @Override
    @CheckForNull
    public Entry< K, V > lastEntry() {
        return unmodifiableOrNull(delegate.lastEntry());
    }

    @Override
    @CheckForNull
    public final Entry< K, V > pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    @CheckForNull
    public final Entry< K, V > pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableMap< K, V > descendingMap() {
        UnmodifiableNavigableMap< K, V > result = descendingMap;
        return (result == null)
                ? descendingMap = new UnmodifiableNavigableMap<>(delegate.descendingMap(), this)
                : result;
    }

    @Override
    public Set< K > keySet() {
        return navigableKeySet();
    }

    @Override
    public NavigableSet< K > navigableKeySet() {
        return SetUtil.unmodifiableNavigableSet(delegate.navigableKeySet());
    }

    @Override
    public NavigableSet< K > descendingKeySet() {
        return SetUtil.unmodifiableNavigableSet(delegate.descendingKeySet());
    }

    @Override
    public SortedMap< K, V > subMap( K fromKey, K toKey ) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public NavigableMap< K, V > subMap(
            K fromKey,
            boolean fromInclusive,
            K toKey,
            boolean toInclusive ) {
        return MapUtil.unmodifiableNavigableMap(
                delegate.subMap(fromKey, fromInclusive, toKey, toInclusive));
    }

    @Override
    public SortedMap< K, V > headMap( K toKey ) {
        return headMap(toKey, false);
    }

    @Override
    public NavigableMap< K, V > headMap( K toKey, boolean inclusive ) {
        return MapUtil.unmodifiableNavigableMap(delegate.headMap(toKey, inclusive));
    }

    @Override
    public SortedMap< K, V > tailMap( K fromKey ) {
        return tailMap(fromKey, true);
    }

    @Override
    public NavigableMap< K, V > tailMap( K fromKey, boolean inclusive ) {
        return MapUtil.unmodifiableNavigableMap(delegate.tailMap(fromKey, inclusive));
    }
}
