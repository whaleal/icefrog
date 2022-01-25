package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.IterUtil;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author wh
 */
abstract class IteratorBasedAbstractMap<
        K extends Object, V extends Object>
        extends AbstractMap<K, V> {
    @Override
    public abstract int size();

    abstract Iterator<Entry<K, V>> entryIterator();

    Spliterator<Entry<K, V>> entrySpliterator() {
        return Spliterators.spliterator(
                entryIterator(), size(), Spliterator.SIZED | Spliterator.DISTINCT);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbsEntrySet<K, V>() {
            @Override
            Map<K, V> map() {
                return MapUtil.IteratorBasedAbstractMap.this;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return entryIterator();
            }

            @Override
            public Spliterator<Entry<K, V>> spliterator() {
                return entrySpliterator();
            }

            @Override
            public void forEach( Consumer<? super Entry<K, V>> action ) {
                forEachEntry(action);
            }
        };
    }

    void forEachEntry( Consumer<? super Entry<K, V>> action ) {
        entryIterator().forEachRemaining(action);
    }

    @Override
    public void clear() {
        IterUtil.clear(entryIterator());
    }
}


