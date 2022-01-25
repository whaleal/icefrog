package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.AbstractCollection;
import java.util.Map;

/**
 * A skeleton implementation of {@link Multimap#entries()}.
 */
abstract class AbsEntries< K extends Object, V extends Object >
        extends AbstractCollection< Map.Entry< K, V > > {
    abstract Multimap< K, V > multimap();

    @Override
    public int size() {
        return multimap().size();
    }

    @Override
    public boolean contains( @CheckForNull Object o ) {
        if (o instanceof Map.Entry) {
            Map.Entry< ?, ? > entry = (Map.Entry< ?, ? >) o;
            return multimap().containsEntry(entry.getKey(), entry.getValue());
        }
        return false;
    }

    @Override
    public boolean remove( @CheckForNull Object o ) {
        if (o instanceof Map.Entry) {
            Map.Entry< ?, ? > entry = (Map.Entry< ?, ? >) o;
            return multimap().remove(entry.getKey(), entry.getValue());
        }
        return false;
    }

    @Override
    public void clear() {
        multimap().clear();
    }
}
