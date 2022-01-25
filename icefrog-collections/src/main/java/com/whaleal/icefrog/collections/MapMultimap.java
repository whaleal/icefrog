package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.*;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;
import static com.whaleal.icefrog.core.lang.Precondition.checkRemove;

/**
 * @see MultimapUtil#forMap
 */
class MapMultimap< K extends Object, V extends Object >
        extends AbstractMultimap< K, V > implements SetMultimap< K, V >, Serializable {
    private static final long serialVersionUID = 7845222491160860175L;
    final Map< K, V > map;

    MapMultimap( Map< K, V > map ) {
        this.map = checkNotNull(map);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean containsKey( @CheckForNull Object key ) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue( @CheckForNull Object value ) {
        return map.containsValue(value);
    }

    @Override
    public boolean
    containsEntry( @CheckForNull Object key, @CheckForNull Object value ) {
        return map.entrySet().contains(new ImmutableEntry(key, value));
    }

    @Override
    public Set< V > get( @ParametricNullness final K key ) {
        return new SetUtil.ImprovedAbstractSet< V >() {
            @Override
            public Iterator< V > iterator() {
                return new Iterator< V >() {
                    int i;

                    @Override
                    public boolean hasNext() {
                        return (i == 0) && map.containsKey(key);
                    }

                    @Override
                    @ParametricNullness
                    public V next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        i++;
                        /*
                         * The cast is safe because of the containsKey check in hasNext(). (That means it's
                         * unsafe under concurrent modification, but all bets are off then, anyway.)
                         */
                        return (map.get(key));
                    }

                    @Override
                    public void remove() {
                        checkRemove(i == 1);
                        i = -1;
                        map.remove(key);
                    }
                };
            }

            @Override
            public int size() {
                return map.containsKey(key) ? 1 : 0;
            }
        };
    }

    @Override
    public boolean put( @ParametricNullness K key, @ParametricNullness V value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean putAll( @ParametricNullness K key, Iterable< ? extends V > values ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean putAll( Multimap< ? extends K, ? extends V > multimap ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set< V > replaceValues( @ParametricNullness K key, Iterable< ? extends V > values ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove( @CheckForNull Object key, @CheckForNull Object value ) {
        return map.entrySet().remove(new ImmutableEntry(key, value));
    }

    @Override
    public Set< V > removeAll( @CheckForNull Object key ) {
        Set< V > values = new HashSet< V >(2);
        if (!map.containsKey(key)) {
            return values;
        }
        values.add(map.remove(key));
        return values;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    Set< K > createKeySet() {
        return map.keySet();
    }

    @Override
    Collection< V > createValues() {
        return map.values();
    }

    @Override
    public Set< Map.Entry< K, V > > entries() {
        return map.entrySet();
    }

    @Override
    Collection< Map.Entry< K, V > > createEntries() {
        throw new AssertionError("unreachable");
    }

    @Override
    Multiset< K > createKeys() {
        return new CKeys< K, V >(this);
    }

    @Override
    Iterator< Map.Entry< K, V > > entryIterator() {
        return map.entrySet().iterator();
    }

    @Override
    Map< K, Collection< V > > createAsMap() {
        return new AsMap<>(this);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
