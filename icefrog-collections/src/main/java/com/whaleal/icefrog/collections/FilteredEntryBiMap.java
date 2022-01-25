package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.BiMap;

import javax.annotation.CheckForNull;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;

/**
 * @author wh
 */
final class FilteredEntryBiMap< K extends Object, V extends Object >
        extends FilteredEntryMap< K, V > implements BiMap< K, V > {
    private final BiMap< V, K > inverse;

    private static < K extends Object, V extends Object >
    Predicate< Entry< V, K > > inversePredicate(
            final Predicate< ? super Entry< K, V > > forwardPredicate ) {
        return new Predicate< Entry< V, K > >() {
            @Override
            public boolean apply( Entry< V, K > input ) {
                return forwardPredicate.apply(MapUtil.immutableEntry(input.getValue(), input.getKey()));
            }
        };
    }

    FilteredEntryBiMap( BiMap< K, V > delegate, Predicate< ? super Entry< K, V > > predicate ) {
        super(delegate, predicate);
        this.inverse =
                new FilteredEntryBiMap<>(delegate.inverse(), inversePredicate(predicate), this);
    }

    private FilteredEntryBiMap(
            BiMap< K, V > delegate, Predicate< ? super Entry< K, V > > predicate, BiMap< V, K > inverse ) {
        super(delegate, predicate);
        this.inverse = inverse;
    }

    BiMap< K, V > unfiltered() {
        return (BiMap< K, V >) unfiltered;
    }

    @Override
    @CheckForNull
    public V forcePut( @ParametricNullness K key, @ParametricNullness V value ) {
        checkArgument(apply(key, value));
        return unfiltered().forcePut(key, value);
    }

    @Override
    public void replaceAll( BiFunction< ? super K, ? super V, ? extends V > function ) {
        unfiltered()
                .replaceAll(
                        ( key, value ) ->
                                predicate.apply(MapUtil.immutableEntry(key, value))
                                        ? function.apply(key, value)
                                        : value);
    }

    @Override
    public BiMap< V, K > inverse() {
        return inverse;
    }

    @Override
    public Set< V > values() {
        return inverse.keySet();
    }
}
