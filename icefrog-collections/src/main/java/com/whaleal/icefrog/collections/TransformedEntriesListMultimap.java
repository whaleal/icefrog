package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.List;

/**
 * @author wh
 */
public final class TransformedEntriesListMultimap<
        K extends Object, V1 extends Object, V2 extends Object >
        extends TransformedEntriesMultimap< K, V1, V2 > implements ListMultimap< K, V2 > {

    TransformedEntriesListMultimap(
            ListMultimap< K, V1 > fromMultimap, MapUtil.EntryTransformer< ? super K, ? super V1, V2 > transformer ) {
        super(fromMultimap, transformer);
    }

    @Override
    List< V2 > transform( @ParametricNullness K key, Collection< V1 > values ) {
        return com.whaleal.icefrog.core.collection.CollUtil.list(false, com.whaleal.icefrog.core.collection.CollUtil.trans((List< V1 >) values, MapUtil.asValueToValueFunction(transformer, key)));
    }

    @Override
    public List< V2 > get( @ParametricNullness K key ) {
        return transform(key, fromMultimap.get(key));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List< V2 > removeAll( @CheckForNull Object key ) {
        return transform((K) key, fromMultimap.removeAll(key));
    }

    @Override
    public List< V2 > replaceValues( @ParametricNullness K key, Iterable< V2 > values ) {
        throw new UnsupportedOperationException();
    }
}
