package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.SortedMap;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;


/**
 * A sorted map which forwards all its method calls to another sorted map. Subclasses should
 * override one or more methods to modify the behavior of the backing sorted map as desired per the
 * <a href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingSortedMap} forward <i>indiscriminately</i> to
 * the methods of the delegate. For example, overriding {@link #put} alone <i>will not</i> change
 * the behavior of {@link #putAll}, which can lead to unexpected behavior. In this case, you should
 * override {@code putAll} as well, either providing your own implementation, or delegating to the
 * provided {@code standardPutAll} method.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingSortedMap}.
 *
 * <p>Each of the {@code standard} methods, where appropriate, use the comparator of the map to test
 * equality for both keys and values, unlike {@code ForwardingMap}.
 *
 * <p>The {@code standard} methods and the collection views they return are not guaranteed to be
 * thread-safe, even when all of the methods that they depend on are thread-safe.
 */


public abstract class ForwardingSortedMap<K extends Object, V extends Object>
        extends ForwardingMap<K, V> implements SortedMap<K, V> {
    // TODO(lowasser): identify places where thread safety is actually lost

    /**
     * Constructor for use by subclasses.
     */
    protected ForwardingSortedMap() {
    }

    // unsafe, but worst case is a CCE or NPE is thrown, which callers will be expecting
    @SuppressWarnings({"unchecked", "nullness"})
    static int unsafeCompare(
            @CheckForNull Comparator<?> comparator, @CheckForNull Object o1, @CheckForNull Object o2 ) {
        if (comparator == null) {
            return ((Comparable<Object>) o1).compareTo(o2);
        } else {
            return ((Comparator<Object>) comparator).compare(o1, o2);
        }
    }

    @Override
    protected abstract SortedMap<K, V> delegate();

    @Override
    @CheckForNull
    public Comparator<? super K> comparator() {
        return delegate().comparator();
    }

    @Override
    @ParametricNullness
    public K firstKey() {
        return delegate().firstKey();
    }

    @Override
    public SortedMap<K, V> headMap( @ParametricNullness K toKey ) {
        return delegate().headMap(toKey);
    }

    @Override
    @ParametricNullness
    public K lastKey() {
        return delegate().lastKey();
    }

    @Override
    public SortedMap<K, V> subMap( @ParametricNullness K fromKey, @ParametricNullness K toKey ) {
        return delegate().subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> tailMap( @ParametricNullness K fromKey ) {
        return delegate().tailMap(fromKey);
    }

    /**
     * A sensible definition of {@link #containsKey} in terms of the {@code firstKey()} method of
     * {@link #tailMap}. If you override {@link #tailMap}, you may wish to override {@link
     * #containsKey} to forward to this implementation.
     */
    @Override

    protected boolean standardContainsKey( @CheckForNull Object key ) {
        try {
            // any CCE or NPE will be caught
            @SuppressWarnings({"unchecked", "nullness"})
            SortedMap<Object, V> self = (SortedMap<Object, V>) this;
            Object ceilingKey = self.tailMap(key).firstKey();
            return unsafeCompare(comparator(), ceilingKey, key) == 0;
        } catch (ClassCastException | NoSuchElementException | NullPointerException e) {
            return false;
        }
    }

    /**
     * A sensible default implementation of {@link #subMap(Object, Object)} in terms of {@link
     * #headMap(Object)} and {@link #tailMap(Object)}. In some situations, you may wish to override
     * {@link #subMap(Object, Object)} to forward to this implementation.
     */

    protected SortedMap<K, V> standardSubMap( K fromKey, K toKey ) {
        checkArgument(unsafeCompare(comparator(), fromKey, toKey) <= 0, "fromKey must be <= toKey");
        return tailMap(fromKey).headMap(toKey);
    }

    /**
     * A sensible implementation of {@link SortedMap#keySet} in terms of the methods of {@code
     * ForwardingSortedMap}. In many cases, you may wish to override {@link
     * ForwardingSortedMap#keySet} to forward to this implementation or a subclass thereof.
     */

    protected class StandardKeySet extends Maps.SortedKeySet<K, V> {
        /**
         * Constructor for use by subclasses.
         */
        public StandardKeySet() {
            super(ForwardingSortedMap.this);
        }
    }
}
