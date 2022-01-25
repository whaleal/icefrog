package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.CollUtil;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
private static class AsMapView< K extends Object, V extends Object >
        extends ViewCachingAbstractMap< K, V > {

    final Function< ? super K, V > function;
    private final Set< K > set;

    AsMapView( Set< K > set, Function< ? super K, V > function ) {
        this.set = checkNotNull(set);
        this.function = checkNotNull(function);
    }

    Set< K > backingSet() {
        return set;
    }

    @Override
    public Set< K > createKeySet() {
        return removeOnlySet(backingSet());
    }

    @Override
    Collection< V > createValues() {
        return CollUtil.trans(set, function);
    }

    @Override
    public int size() {
        return backingSet().size();
    }

    @Override
    public boolean containsKey( @CheckForNull Object key ) {
        return backingSet().contains(key);
    }

    @Override
    @CheckForNull
    public V get( @CheckForNull Object key ) {
        return getOrDefault(key, null);
    }

    @Override
    @CheckForNull
    public V getOrDefault( @CheckForNull Object key, @CheckForNull V defaultValue ) {
        if (CollUtil.safeContains(backingSet(), key)) {
            @SuppressWarnings("unchecked") // unsafe, but Javadoc warns about it
            K k = (K) key;
            return function.apply(k);
        } else {
            return defaultValue;
        }
    }

    @Override
    @CheckForNull
    public V remove( @CheckForNull Object key ) {
        if (backingSet().remove(key)) {
            @SuppressWarnings("unchecked") // unsafe, but Javadoc warns about it
            K k = (K) key;
            return function.apply(k);
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        backingSet().clear();
    }

    @Override
    protected Set< Entry< K, V > > createEntrySet() {

        class AbsEntrySetImpl extends AbsEntrySet< K, V > {
            @Override
            Map< K, V > map() {
                return AsMapView.this;
            }

            @Override
            public Iterator< Entry< K, V > > iterator() {
                return asMapEntryIterator(backingSet(), function);
            }
        }
        return new AbsEntrySetImpl();
    }

    @Override
    public void forEach( BiConsumer< ? super K, ? super V > action ) {
        checkNotNull(action);
        // avoids allocation of entries
        backingSet().forEach(k -> action.accept(k, function.apply(k)));
    }
}
