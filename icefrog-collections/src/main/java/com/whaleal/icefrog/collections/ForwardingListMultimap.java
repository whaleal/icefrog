package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.List;


/**
 * A list multimap which forwards all its method calls to another list multimap. Subclasses should
 * override one or more methods to modify the behavior of the backing multimap as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingListMultimap}.
 *
 * @author Kurt Alfred Kluever
 */


public abstract class ForwardingListMultimap<K extends Object, V extends Object>
        extends ForwardingMultimap<K, V> implements ListMultimap<K, V> {

    /**
     * Constructor for use by subclasses.
     */
    protected ForwardingListMultimap() {
    }

    @Override
    protected abstract ListMultimap<K, V> delegate();

    @Override
    public List<V> get( @ParametricNullness K key ) {
        return delegate().get(key);
    }


    @Override
    public List<V> removeAll( @CheckForNull Object key ) {
        return delegate().removeAll(key);
    }


    @Override
    public List<V> replaceValues( @ParametricNullness K key, Iterable< V > values ) {
        return delegate().replaceValues(key, values);
    }
}
