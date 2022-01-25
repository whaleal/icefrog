package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.SortedMap;

import static com.whaleal.icefrog.collections.MapUtil.transformEntries;

/**
 * @author wh
 */
class TransformedEntriesSortedMap<
        K extends Object, V1 extends Object, V2 extends Object >
        extends TransformedEntriesMap< K, V1, V2 > implements SortedMap< K, V2 > {

    TransformedEntriesSortedMap(
            SortedMap< K, V1 > fromMap, EntryTransformer< ? super K, ? super V1, V2 > transformer ) {
        super(fromMap, transformer);
    }

    protected SortedMap< K, V1 > fromMap() {
        return (SortedMap< K, V1 >) fromMap;
    }

    @Override
    @CheckForNull
    public Comparator< ? super K > comparator() {
        return fromMap().comparator();
    }

    @Override

    public K firstKey() {
        return fromMap().firstKey();
    }

    @Override
    public SortedMap< K, V2 > headMap( K toKey ) {
        return transformEntries(fromMap().headMap(toKey), transformer);
    }

    @Override

    public K lastKey() {
        return fromMap().lastKey();
    }

    @Override
    public SortedMap< K, V2 > subMap( K fromKey, K toKey ) {
        return transformEntries(fromMap().subMap(fromKey, toKey), transformer);
    }

    @Override
    public SortedMap< K, V2 > tailMap( K fromKey ) {
        return transformEntries(fromMap().tailMap(fromKey), transformer);
    }
}
