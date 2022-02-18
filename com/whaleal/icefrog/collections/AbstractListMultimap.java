package com.whaleal.icefrog.collections;


import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Basic implementation of the {@link ListMultimap} interface. It's a wrapper around {@link
 * AbstractMapBasedMultimap} that converts the returned collections into {@code Lists}. The {@link
 * #createCollection} method must return a {@code List}.
 */


abstract class AbstractListMultimap<K extends Object, V extends Object>
        extends AbstractMapBasedMultimap<K, V> implements ListMultimap<K, V> {
    private static final long serialVersionUID = 6588350623831699109L;

    /**
     * Creates a new multimap that uses the provided map.
     *
     * @param map place to store the mapping from each key to its corresponding values
     */
    protected AbstractListMultimap( Map<K, Collection<V>> map ) {
        super(map);
    }

    @Override
    abstract List<V> createCollection();

    @Override
    List<V> createUnmodifiableEmptyCollection() {
        return Collections.emptyList();
    }

    @Override
    <E extends Object> Collection<E> unmodifiableCollectionSubclass(
            Collection<E> collection ) {
        return Collections.unmodifiableList((List<E>) collection);
    }

    // Following Javadoc copied from ListMultimap.

    @Override
    Collection<V> wrapCollection( @ParametricNullness K key, Collection<V> collection ) {
        return wrapList(key, (List<V>) collection, null);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Because the values for a given key may have duplicates and follow the insertion ordering,
     * this method returns a {@link List}, instead of the {@link Collection} specified in the {@link
     * Multimap} interface.
     */
    @Override
    public List<V> get( @ParametricNullness K key ) {
        return (List<V>) super.get(key);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Because the values for a given key may have duplicates and follow the insertion ordering,
     * this method returns a {@link List}, instead of the {@link Collection} specified in the {@link
     * Multimap} interface.
     */

    @Override
    public List<V> removeAll( @CheckForNull Object key ) {
        return (List<V>) super.removeAll(key);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Because the values for a given key may have duplicates and follow the insertion ordering,
     * this method returns a {@link List}, instead of the {@link Collection} specified in the {@link
     * Multimap} interface.
     */

    @Override
    public List<V> replaceValues( @ParametricNullness K key, Iterable<? extends V> values ) {
        return (List<V>) super.replaceValues(key, values);
    }

    /**
     * Stores a key-value pair in the multimap.
     *
     * @param key   key to store in the multimap
     * @param value value to store in the multimap
     * @return {@code true} always
     */

    @Override
    public boolean put( @ParametricNullness K key, @ParametricNullness V value ) {
        return super.put(key, value);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Though the method signature doesn't say so explicitly, the returned map has {@link List}
     * values.
     */
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }

    /**
     * Compares the specified object to this multimap for equality.
     *
     * <p>Two {@code ListMultimap} instances are equal if, for each key, they contain the same values
     * in the same order. If the value orderings disagree, the Multimaps will not be considered equal.
     */
    @Override
    public boolean equals( @CheckForNull Object object ) {
        return super.equals(object);
    }
}
