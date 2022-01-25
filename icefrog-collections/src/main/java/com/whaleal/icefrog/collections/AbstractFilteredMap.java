package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Map;
import com.whaleal.icefrog.core.lang.Predicate;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;

/**
 * @author wh
 */
abstract class AbstractFilteredMap<
        K extends Object, V extends Object >
        extends ViewCachingAbstractMap< K, V > {
    final Map< K, V > unfiltered;
    final Predicate< ? super Entry< K, V > > predicate;

    AbstractFilteredMap( Map< K, V > unfiltered, Predicate< ? super Entry< K, V > > predicate ) {
        this.unfiltered = unfiltered;
        this.predicate = predicate;
    }

    boolean apply( @CheckForNull Object key, @ParametricNullness V value ) {
        // This method is called only when the key is in the map (or about to be added to the map),
        // implying that key is a K.
        @SuppressWarnings({"unchecked", "nullness"})
        K k = (K) key;
        return predicate.apply(MapUtil.immutableEntry(k, value));
    }

    @Override
    @CheckForNull
    public V put( @ParametricNullness K key, @ParametricNullness V value ) {
        checkArgument(apply(key, value));
        return unfiltered.put(key, value);
    }

    @Override
    public void putAll( Map< ? extends K, ? extends V > map ) {
        for (Entry< ? extends K, ? extends V > entry : map.entrySet()) {
            checkArgument(apply(entry.getKey(), entry.getValue()));
        }
        unfiltered.putAll(map);
    }

    @Override
    public boolean containsKey( @CheckForNull Object key ) {
        return unfiltered.containsKey(key) && apply(key, unfiltered.get(key));
    }

    @Override
    @CheckForNull
    public V get( @CheckForNull Object key ) {
        V value = unfiltered.get(key);
        return ((value != null) && apply(key, value)) ? value : null;
    }

    @Override
    public boolean isEmpty() {
        return entrySet().isEmpty();
    }

    @Override
    @CheckForNull
    public V remove( @CheckForNull Object key ) {
        return containsKey(key) ? unfiltered.remove(key) : null;
    }

    @Override
    Collection< V > createValues() {
        return new FilteredMapValues<>(this, unfiltered, predicate);
    }
}
