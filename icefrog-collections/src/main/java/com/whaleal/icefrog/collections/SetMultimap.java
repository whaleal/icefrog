package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * A {@code Multimap} that cannot hold duplicate key-value pairs. Adding a key-value pair that's
 * already in the multimap has no effect. See the {@link Multimap} documentation for information
 * common to all Multimaps.
 *
 * <p>The {@link #get}, {@link #removeAll}, and {@link #replaceValues} methods each return a {@link
 * Set} of values, while {@link #entries} returns a {@code Set} of map entries. Though the method
 * signature doesn't say so explicitly, the map returned by {@link #asMap} has {@code Set} values.
 *
 * <p>If the values corresponding to a single key should be ordered according to a {@link
 * java.util.Comparator} (or the natural order), see the {@link SortedSetMultimap} subinterface.
 *
 * <p>Since the value collections are sets, the behavior of a {@code SetMultimap} is not specified
 * if key <em>or value</em> objects already present in the multimap change in a manner that affects
 * {@code equals} comparisons. Use caution if mutable objects are used as keys or values in a {@code
 * SetMultimap}.
 *
 * <p><b>Warning:</b> Do not modify either a key <i>or a value</i> of a {@code SetMultimap} in a way
 * that affects its {@link Object#equals} behavior. Undefined behavior and bugs will result.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#multimap"> {@code
 * Multimap}</a>.
 */


public interface SetMultimap<K extends Object, V extends Object>
        extends Multimap<K, V> {
    /**
     * {@inheritDoc}
     *
     * <p>Because a {@code SetMultimap} has unique values for a given key, this method returns a
     * {@link Set}, instead of the {@link Collection} specified in the {@link Multimap}
     * interface.
     */
    @Override
    Set<V> get( @ParametricNullness K key );

    /**
     * {@inheritDoc}
     *
     * <p>Because a {@code SetMultimap} has unique values for a given key, this method returns a
     * {@link Set}, instead of the {@link Collection} specified in the {@link Multimap}
     * interface.
     */

    @Override
    Set<V> removeAll( @CheckForNull Object key );

    /**
     * {@inheritDoc}
     *
     * <p>Because a {@code SetMultimap} has unique values for a given key, this method returns a
     * {@link Set}, instead of the {@link Collection} specified in the {@link Multimap}
     * interface.
     *
     * <p>Any duplicates in {@code values} will be stored in the multimap once.
     */

    @Override
    Set<V> replaceValues( @ParametricNullness K key, Iterable<? extends V> values );

    /**
     * {@inheritDoc}
     *
     * <p>Because a {@code SetMultimap} has unique values for a given key, this method returns a
     * {@link Set}, instead of the {@link Collection} specified in the {@link Multimap}
     * interface.
     */
    @Override
    Set<Entry<K, V>> entries();

    /**
     * {@inheritDoc}
     *
     * <p><b>Note:</b> The returned map's values are guaranteed to be of type {@link Set}. To obtain
     * this map with the more specific generic type {@code Map<K, Set<V>>}, call {@link
     * MultimapUtil#asMap(SetMultimap)} instead.
     */
    @Override
    Map<K, Collection<V>> asMap();

    /**
     * Compares the specified object to this multimap for equality.
     *
     * <p>Two {@code SetMultimap} instances are equal if, for each key, they contain the same values.
     * Equality does not depend on the ordering of keys or values.
     *
     * <p>An empty {@code SetMultimap} is equal to any other empty {@code Multimap}, including an
     * empty {@code ListMultimap}.
     */
    @Override
    boolean equals( @CheckForNull Object obj );
}
