package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;
import static java.util.Objects.requireNonNull;

/**
 * A skeleton implementation of {@link Multimap#asMap()}.
 */
final class AsMap< K extends Object, V extends Object >
        extends ViewCachingAbstractMap< K, Collection< V > > {
    private final Multimap< K, V > multimap;

    AsMap( Multimap< K, V > multimap ) {
        this.multimap = checkNotNull(multimap);
    }

    @Override
    public int size() {
        return multimap.keySet().size();
    }

    @Override
    protected Set< Entry< K, Collection< V > > > createEntrySet() {
        return new EntrySet();
    }

    void removeValuesForKey( @CheckForNull Object key ) {
        multimap.keySet().remove(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    @CheckForNull
    public Collection< V > get( @CheckForNull Object key ) {
        return containsKey(key) ? multimap.get((K) key) : null;
    }

    @Override
    @CheckForNull
    public Collection< V > remove( @CheckForNull Object key ) {
        return containsKey(key) ? multimap.removeAll(key) : null;
    }

    @Override
    public Set< K > keySet() {
        return multimap.keySet();
    }

    @Override
    public boolean isEmpty() {
        return multimap.isEmpty();
    }

    @Override
    public boolean containsKey( @CheckForNull Object key ) {
        return multimap.containsKey(key);
    }

    @Override
    public void clear() {
        multimap.clear();
    }

    class EntrySet extends MapUtil.EntrySet< K, Collection< V > > {
        @Override
        Map< K, Collection< V > > map() {
            return AsMap.this;
        }

        @Override
        public Iterator< Entry< K, Collection< V > > > iterator() {
            return MapUtil.asMapEntryIterator(
                    multimap.keySet(),
                    new Function< K, Collection< V > >() {
                        @Override
                        public Collection< V > apply( @ParametricNullness K key ) {
                            return multimap.get(key);
                        }
                    });
        }

        @Override
        public boolean remove( @CheckForNull Object o ) {
            if (!contains(o)) {
                return false;
            }
            // requireNonNull is safe because of the contains check.
            Entry< ?, ? > entry = requireNonNull((Entry< ?, ? >) o);
            removeValuesForKey(entry.getKey());
            return true;
        }
    }
}
