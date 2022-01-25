package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.collection.IterUtil;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
public class TransformedEntriesMultimap<
        K extends Object, V1 extends Object, V2 extends Object >
        extends AbstractMultimap< K, V2 > {
    final Multimap< K, V1 > fromMultimap;
    final EntryTransformer< ? super K, ? super V1, V2 > transformer;

    TransformedEntriesMultimap(
            Multimap< K, V1 > fromMultimap,
            final EntryTransformer< ? super K, ? super V1, V2 > transformer ) {
        this.fromMultimap = checkNotNull(fromMultimap);
        this.transformer = checkNotNull(transformer);
    }

    Collection< V2 > transform( @ParametricNullness K key, Collection< V1 > values ) {
        Function< ? super V1, V2 > function = MapUtil.asValueToValueFunction(transformer, key);
        if (values instanceof List) {
            return com.whaleal.icefrog.core.collection.CollUtil.trans((List< V1 >) values, function);
        } else {
            return com.whaleal.icefrog.core.collection.CollUtil.trans(values, function);
        }
    }

    @Override
    Map< K, Collection< V2 > > createAsMap() {
        return MapUtil.transformEntries(
                fromMultimap.asMap(),
                new EntryTransformer< K, Collection< V1 >, Collection< V2 > >() {
                    @Override
                    public Collection< V2 > transformEntry( @ParametricNullness K key, Collection< V1 > value ) {
                        return transform(key, value);
                    }
                });
    }

    @Override
    public void clear() {
        fromMultimap.clear();
    }

    @Override
    public boolean containsKey( @CheckForNull Object key ) {
        return fromMultimap.containsKey(key);
    }

    @Override
    Collection< Map.Entry< K, V2 > > createEntries()

    @Override
    Iterator< Map.Entry< K, V2 > > entryIterator() {
        return IterUtil.trans(
                fromMultimap.entries().iterator(), MapUtil.asEntryToEntryFunction(transformer));
    }

    @Override
    public Collection< V2 > get( @ParametricNullness final K key ) {
        return transform(key, fromMultimap.get(key));
    }

    @Override
    public boolean isEmpty() {
        return fromMultimap.isEmpty();
    }

    @Override
    Set< K > createKeySet() {
        return fromMultimap.keySet();
    }

    @Override
    Multiset< K > createKeys() {
        return fromMultimap.keys();
    }

    @Override
    public boolean put( @ParametricNullness K key, @ParametricNullness V2 value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean putAll( @ParametricNullness K key, Iterable< ? extends V2 > values ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean putAll( Multimap< ? extends K, ? extends V2 > multimap ) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove( @CheckForNull Object key, @CheckForNull Object value ) {
        return get((K) key).remove(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection< V2 > removeAll( @CheckForNull Object key ) {
        return transform((K) key, fromMultimap.removeAll(key));
    }

    @Override
    public Collection< V2 > replaceValues( @ParametricNullness K key, Iterable< ? extends V2 > values ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return fromMultimap.size();
    }

    @Override
    Collection< V2 > createValues() {
        return CollUtil.trans(
                fromMultimap.entries(), MapUtil.asEntryToValueFunction(transformer));
    }
}
