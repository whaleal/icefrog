package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.*;

/**
 * @author wh
 */
// NavigableMap
abstract class DescendingMap<K extends Object, V extends Object>
        extends ForwardingMap<K, V> implements NavigableMap<K, V> {

    @CheckForNull
    private transient Comparator<? super K> comparator;
    @CheckForNull
    private transient Set<Entry<K, V>> entrySet;
    @CheckForNull
    private transient NavigableSet<K> navigableKeySet;

    // If we inline this, we get a javac error.
    private static <T extends Object> Ordering<T> reverse( Comparator<T> forward ) {
        return Ordering.from(forward).reverse();
    }

    abstract NavigableMap<K, V> forward();

    @Override
    protected final Map<K, V> delegate() {
        return forward();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Comparator<? super K> comparator() {
        Comparator<? super K> result = comparator;
        if (result == null) {
            Comparator<? super K> forwardCmp = forward().comparator();
            if (forwardCmp == null) {
                forwardCmp = (Comparator) Ordering.natural();
            }
            result = comparator = reverse(forwardCmp);
        }
        return result;
    }

    @Override

    public K firstKey() {
        return forward().lastKey();
    }

    @Override

    public K lastKey() {
        return forward().firstKey();
    }

    @Override
    @CheckForNull
    public Entry<K, V> lowerEntry( K key ) {
        return forward().higherEntry(key);
    }

    @Override
    @CheckForNull
    public K lowerKey( K key ) {
        return forward().higherKey(key);
    }

    @Override
    @CheckForNull
    public Entry<K, V> floorEntry( K key ) {
        return forward().ceilingEntry(key);
    }

    @Override
    @CheckForNull
    public K floorKey( K key ) {
        return forward().ceilingKey(key);
    }

    @Override
    @CheckForNull
    public Entry<K, V> ceilingEntry( K key ) {
        return forward().floorEntry(key);
    }

    @Override
    @CheckForNull
    public K ceilingKey( K key ) {
        return forward().floorKey(key);
    }

    @Override
    @CheckForNull
    public Entry<K, V> higherEntry( K key ) {
        return forward().lowerEntry(key);
    }

    @Override
    @CheckForNull
    public K higherKey( K key ) {
        return forward().lowerKey(key);
    }

    @Override
    @CheckForNull
    public Entry<K, V> firstEntry() {
        return forward().lastEntry();
    }

    @Override
    @CheckForNull
    public Entry<K, V> lastEntry() {
        return forward().firstEntry();
    }

    @Override
    @CheckForNull
    public Entry<K, V> pollFirstEntry() {
        return forward().pollLastEntry();
    }

    @Override
    @CheckForNull
    public Entry<K, V> pollLastEntry() {
        return forward().pollFirstEntry();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return forward();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> result = entrySet;
        return (result == null) ? entrySet = createEntrySet() : result;
    }

    abstract Iterator<Entry<K, V>> entryIterator();

    Set<Entry<K, V>> createEntrySet() {

        class AbsEntrySetImpl extends AbsEntrySet<K, V> {
            @Override
            Map<K, V> map() {
                return DescendingMap.this;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return entryIterator();
            }
        }
        return new AbsEntrySetImpl();
    }

    @Override
    public Set<K> keySet() {
        return navigableKeySet();
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        NavigableSet<K> result = navigableKeySet;
        return (result == null) ? navigableKeySet = new NavigableKeySet<>(this) : result;
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return forward().navigableKeySet();
    }

    @Override
    public NavigableMap<K, V> subMap(
            K fromKey,
            boolean fromInclusive,
            K toKey,
            boolean toInclusive ) {
        return forward().subMap(toKey, toInclusive, fromKey, fromInclusive).descendingMap();
    }

    @Override
    public SortedMap<K, V> subMap( K fromKey, K toKey ) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public NavigableMap<K, V> headMap( K toKey, boolean inclusive ) {
        return forward().tailMap(toKey, inclusive).descendingMap();
    }

    @Override
    public SortedMap<K, V> headMap( K toKey ) {
        return headMap(toKey, false);
    }

    @Override
    public NavigableMap<K, V> tailMap( K fromKey, boolean inclusive ) {
        return forward().headMap(fromKey, inclusive).descendingMap();
    }

    @Override
    public SortedMap<K, V> tailMap( K fromKey ) {
        return tailMap(fromKey, true);
    }

    @Override
    public Collection<V> values() {
        return new Values<>(this);
    }

    @Override
    public String toString() {
        return standardToString();
    }
}
