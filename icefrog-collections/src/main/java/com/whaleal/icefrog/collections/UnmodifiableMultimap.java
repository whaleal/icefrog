package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
public class UnmodifiableMultimap< K extends Object, V extends Object >
        extends ForwardingMultimap< K, V > implements Serializable {
    private static final long serialVersionUID = 0;
    final Multimap< K, V > delegate;
    @CheckForNull
    transient Collection< Map.Entry< K, V > > entries;
    @CheckForNull
    transient Multiset< K > keys;
    @CheckForNull
    transient Set< K > keySet;
    @CheckForNull
    transient Collection< V > values;
    @CheckForNull
    transient Map< K, Collection< V > > map;

    UnmodifiableMultimap( final Multimap< K, V > delegate ) {
        this.delegate = checkNotNull(delegate);
    }

    @Override
    protected Multimap< K, V > delegate() {
        return delegate;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map< K, Collection< V > > asMap() {
        Map< K, Collection< V > > result = map;
        if (result == null) {
            result =
                    map =
                            Collections.unmodifiableMap(
                                    MapUtil.transformValues(
                                            delegate.asMap(),
                                            new Function< Collection< V >, Collection< V > >() {
                                                @Override
                                                public Collection< V > apply( Collection< V > collection ) {
                                                    return MultimapUtil.unmodifiableValueCollection(collection);
                                                }
                                            }));
        }
        return result;
    }

    @Override
    public Collection< Map.Entry< K, V > > entries() {
        Collection< Map.Entry< K, V > > result = entries;
        if (result == null) {
            entries = result = MultimapUtil.unmodifiableEntries(delegate.entries());
        }
        return result;
    }

    @Override
    public void forEach( BiConsumer< ? super K, ? super V > consumer ) {
        delegate.forEach(checkNotNull(consumer));
    }

    @Override
    public Collection< V > get( @ParametricNullness K key ) {
        return MultimapUtil.unmodifiableValueCollection(delegate.get(key));
    }

    @Override
    public Multiset< K > keys() {
        Multiset< K > result = keys;
        if (result == null) {
            keys = result = Multisets.unmodifiableMultiset(delegate.keys());
        }
        return result;
    }

    @Override
    public Set< K > keySet() {
        Set< K > result = keySet;
        if (result == null) {
            keySet = result = Collections.unmodifiableSet(delegate.keySet());
        }
        return result;
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
    public boolean remove( @CheckForNull Object key, @CheckForNull Object value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection< V > removeAll( @CheckForNull Object key ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection< V > replaceValues( @ParametricNullness K key, Iterable< ? extends V > values ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection< V > values() {
        Collection< V > result = values;
        if (result == null) {
            values = result = Collections.unmodifiableCollection(delegate.values());
        }
        return result;
    }
}
