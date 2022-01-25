package com.whaleal.icefrog.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @see Multimaps#unmodifiableEntries
 */
class UnmodifiableEntries< K extends Object, V extends Object >
        extends ForwardingCollection< Map.Entry< K, V > > {
    private final Collection< Map.Entry< K, V > > entries;

    UnmodifiableEntries( Collection< Map.Entry< K, V > > entries ) {
        this.entries = entries;
    }

    @Override
    protected Collection< Map.Entry< K, V > > delegate() {
        return entries;
    }

    @Override
    public Iterator< Map.Entry< K, V > > iterator() {
        return MapUtil.unmodifiableEntryIterator(entries.iterator());
    }

    // See java.util.Collections.UnmodifiableEntrySet for details on attacks.

    @Override
    public Object[] toArray() {
        /*
         * standardToArray returns `Object[]` rather than `Object[]` but only because it can
         * be used with collections that may contain null. This collection never contains nulls, so we
         * can treat it as a plain `Object[]`.
         */
        @SuppressWarnings("nullness")
        Object[] result = standardToArray();
        return result;
    }

    @Override
    @SuppressWarnings("nullness") // b/192354773 in our checker affects toArray declarations
    public < T extends Object > T[] toArray( T[] array ) {
        return standardToArray(array);
    }
}
