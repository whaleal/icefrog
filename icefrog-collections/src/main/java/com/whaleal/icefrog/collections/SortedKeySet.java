package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * @author wh
 */
class SortedKeySet<K extends Object, V extends Object>
        extends CKeySet<K, V> implements SortedSet<K> {
    SortedKeySet( SortedMap<K, V> map ) {
        super(map);
    }

    @Override
    SortedMap<K, V> map() {
        return (SortedMap<K, V>) super.map();
    }

    @Override
    @CheckForNull
    public Comparator<? super K> comparator() {
        return map().comparator();
    }

    @Override
    public SortedSet<K> subSet( K fromElement, K toElement ) {
        return new MapUtil.SortedKeySet<>(map().subMap(fromElement, toElement));
    }

    @Override
    public SortedSet<K> headSet( K toElement ) {
        return new MapUtil.SortedKeySet<>(map().headMap(toElement));
    }

    @Override
    public SortedSet<K> tailSet( K fromElement ) {
        return new MapUtil.SortedKeySet<>(map().tailMap(fromElement));
    }

    @Override

    public K first() {
        return map().firstKey();
    }

    @Override

    public K last() {
        return map().lastKey();
    }
}