package com.whaleal.icefrog.core.collection.set;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Set;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/**
 * A set which forwards all its method calls to another set. Subclasses should override one or more
 * methods to modify the behavior of the backing set as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingSet} forward <b>indiscriminately</b> to the
 * methods of the delegate. For example, overriding {@link #add} alone <b>will not</b> change the
 * behavior of {@link #addAll}, which can lead to unexpected behavior. In this case, you should
 * override {@code addAll} as well, either providing your own implementation, or delegating to the
 * provided {@code standardAddAll} method.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingSet}.
 *
 * <p>The {@code standard} methods are not guaranteed to be thread-safe, even when all of the
 * methods that they depend on are thread-safe.
 */


public abstract class ForwardingSet<E extends Object> extends ForwardingCollection<E>
        implements Set<E> {
    // TODO(lowasser): identify places where thread safety is actually lost

    /**
     * Constructor for use by subclasses.
     */
    protected ForwardingSet() {
    }

    @Override
    protected abstract Set<E> delegate();

    @Override
    public boolean equals( @CheckForNull Object object ) {
        return object == this || delegate().equals(object);
    }

    @Override
    public int hashCode() {
        return delegate().hashCode();
    }

    /**
     * A sensible definition of {@link #removeAll} in terms of {@link #iterator} and {@link #remove}.
     * If you override {@code iterator} or {@code remove}, you may wish to override {@link #removeAll}
     * to forward to this implementation.
     */
    @Override
    protected boolean standardRemoveAll( Collection<?> collection ) {
        return SetUtil.removeAllImpl(this, checkNotNull(collection)); // for GWT
    }

    /**
     * A sensible definition of {@link #equals} in terms of {@link #size} and {@link #containsAll}. If
     * you override either of those methods, you may wish to override {@link #equals} to forward to
     * this implementation.
     */
    protected boolean standardEquals( @CheckForNull Object object ) {
        return SetUtil.equalsImpl(this, object);
    }

    /**
     * A sensible definition of {@link #hashCode} in terms of {@link #iterator}. If you override
     * {@link #iterator}, you may wish to override {@link #equals} to forward to this implementation.
     */
    protected int standardHashCode() {
        return SetUtil.hashCodeImpl(this);
    }
}
