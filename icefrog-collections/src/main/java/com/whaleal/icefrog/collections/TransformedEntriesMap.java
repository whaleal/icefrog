package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.IterUtil;
import com.whaleal.icefrog.core.collection.SpliteratorUtil;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.BiConsumer;

import static com.whaleal.icefrog.collections.NullnessCasts.uncheckedCastNullableTToT;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
class TransformedEntriesMap<
        K extends Object, V1 extends Object, V2 extends Object >
        extends IteratorBasedAbstractMap< K, V2 > {
    final Map< K, V1 > fromMap;
    final EntryTransformer< ? super K, ? super V1, V2 > transformer;

    TransformedEntriesMap(
            Map< K, V1 > fromMap, EntryTransformer< ? super K, ? super V1, V2 > transformer ) {
        this.fromMap = checkNotNull(fromMap);
        this.transformer = checkNotNull(transformer);
    }

    @Override
    public int size() {
        return fromMap.size();
    }

    @Override
    public boolean containsKey( @CheckForNull Object key ) {
        return fromMap.containsKey(key);
    }

    @Override
    @CheckForNull
    public V2 get( @CheckForNull Object key ) {
        return getOrDefault(key, null);
    }

    // safe as long as the user followed the <b>Warning</b> in the javadoc
    @SuppressWarnings("unchecked")
    @Override
    @CheckForNull
    public V2 getOrDefault( @CheckForNull Object key, @CheckForNull V2 defaultValue ) {
        V1 value = fromMap.get(key);
        if (value != null || fromMap.containsKey(key)) {
            // The cast is safe because of the containsKey check.
            return transformer.transformEntry((K) key, uncheckedCastNullableTToT(value));
        }
        return defaultValue;
    }

    // safe as long as the user followed the <b>Warning</b> in the javadoc
    @SuppressWarnings("unchecked")
    @Override
    @CheckForNull
    public V2 remove( @CheckForNull Object key ) {
        return fromMap.containsKey(key)
                // The cast is safe because of the containsKey check.
                ? transformer.transformEntry((K) key, uncheckedCastNullableTToT(fromMap.remove(key)))
                : null;
    }

    @Override
    public void clear() {
        fromMap.clear();
    }

    @Override
    public Set< K > keySet() {
        return fromMap.keySet();
    }

    @Override
    Iterator< Entry< K, V2 > > entryIterator() {
        return IterUtil.trans(
                fromMap.entrySet().iterator(), MapUtil.asEntryToEntryFunction(transformer));
    }

    @Override
    Spliterator< Entry< K, V2 > > entrySpliterator() {
        return SpliteratorUtil.map(
                fromMap.entrySet().spliterator(), MapUtil.asEntryToEntryFunction(transformer));
    }

    @Override
    public void forEach( BiConsumer< ? super K, ? super V2 > action ) {
        checkNotNull(action);
        // avoids creating new Entry<K, V2> objects
        fromMap.forEach(( k, v1 ) -> action.accept(k, transformer.transformEntry(k, v1)));
    }

    @Override
    public Collection< V2 > values() {
        return new Values<>(this);
    }
}
