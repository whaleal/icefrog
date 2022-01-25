package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Set;

/**
 * @author wh
 */
abstract class ViewCachingAbstractMap<
        K extends Object, V extends Object>
        extends AbstractMap<K, V> {
    @CheckForNull
    private transient Set<Entry<K, V>> entrySet;
    @CheckForNull
    private transient Set<K> keySet;
    @CheckForNull
    private transient Collection<V> values;

    /**
     * Creates the entry set to be returned by {@link #entrySet()}. This method is invoked at most
     * once on a given map, at the time when {@code entrySet} is first called.
     */
    abstract Set<Entry<K, V>> createEntrySet();

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> result = entrySet;
        return (result == null) ? entrySet = createEntrySet() : result;
    }

    @Override
    public Set<K> keySet() {
        Set<K> result = keySet;
        return (result == null) ? keySet = createKeySet() : result;
    }

    Set<K> createKeySet() {
        return new CKeySet<>(this);
    }

    @Override
    public Collection<V> values() {
        Collection<V> result = values;
        return (result == null) ? values = createValues() : result;
    }

    Collection<V> createValues() {
        return new Values<>(this);
    }
}