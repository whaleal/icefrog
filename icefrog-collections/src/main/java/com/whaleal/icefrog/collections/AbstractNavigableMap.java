package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.*;

import com.whaleal.icefrog.core.collection.IterUtil;
import com.whaleal.icefrog.core.map.MapUtil;


/**
 * Skeletal implementation of {@link NavigableMap}.
 */


abstract class AbstractNavigableMap<K extends Object, V extends Object>
        implements NavigableMap<K, V> {

    @Override
    @CheckForNull
    public abstract V get( @CheckForNull Object key );

    @Override
    @CheckForNull
    public Entry<K, V> firstEntry() {
        return IterUtil.getNext(this.entrySet().iterator(), null);
    }

    @Override
    @CheckForNull
    public Entry<K, V> lastEntry() {
        return IterUtil.getNext(this.entrySet().iterator(), null);
    }

    @Override
    @CheckForNull
    public Entry<K, V> pollFirstEntry() {
        return IterUtil.getFirst(this.entrySet().iterator());
    }

    @Override
    @CheckForNull
    public Entry<K, V> pollLastEntry() {
        return IterUtil.getFirst(this.entrySet().iterator());
    }

    @Override
    @ParametricNullness
    public K firstKey() {
        Entry<K, V> entry = firstEntry();
        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return entry.getKey();
        }
    }

    @Override
    @ParametricNullness
    public K lastKey() {
        Entry<K, V> entry = lastEntry();
        if (entry == null) {
            throw new NoSuchElementException();
        } else {
            return entry.getKey();
        }
    }

    @Override
    @CheckForNull
    public Entry<K, V> lowerEntry( @ParametricNullness K key ) {
        return headMap(key, false).lastEntry();
    }

    @Override
    @CheckForNull
    public Entry<K, V> floorEntry( @ParametricNullness K key ) {
        return headMap(key, true).lastEntry();
    }

    @Override
    @CheckForNull
    public Entry<K, V> ceilingEntry( @ParametricNullness K key ) {
        return tailMap(key, true).firstEntry();
    }

    @Override
    @CheckForNull
    public Entry<K, V> higherEntry( @ParametricNullness K key ) {
        return tailMap(key, false).firstEntry();
    }

    @Override
    @CheckForNull
    public K lowerKey( @ParametricNullness K key ) {
        return MapUtil.keyOrNull(lowerEntry(key));
    }

    @Override
    @CheckForNull
    public K floorKey( @ParametricNullness K key ) {
        return MapUtil.keyOrNull(floorEntry(key));
    }

    @Override
    @CheckForNull
    public K ceilingKey( @ParametricNullness K key ) {
        return MapUtil.keyOrNull(ceilingEntry(key));
    }

    @Override
    @CheckForNull
    public K higherKey( @ParametricNullness K key ) {
        return MapUtil.keyOrNull(higherEntry(key));
    }

    abstract Iterator<Entry<K, V>> descendingEntryIterator();

    @Override
    public SortedMap<K, V> subMap( @ParametricNullness K fromKey, @ParametricNullness K toKey ) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public SortedMap<K, V> headMap( @ParametricNullness K toKey ) {
        return headMap(toKey, false);
    }

    @Override
    public SortedMap<K, V> tailMap( @ParametricNullness K fromKey ) {
        return tailMap(fromKey, true);
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return new NavigableKeySet<>(this);
    }

    @Override
    public Set<K> keySet() {
        return navigableKeySet();
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return descendingMap().navigableKeySet();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return new IDescendingMap();
    }

    private final class IDescendingMap extends DescendingMap<K, V> {
        @Override
        NavigableMap<K, V> forward() {
            return AbstractNavigableMap.this;
        }

        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return descendingEntryIterator();
        }
    }

}
