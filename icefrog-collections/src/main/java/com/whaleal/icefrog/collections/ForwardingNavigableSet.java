package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;


/**
 * A navigable set which forwards all its method calls to another navigable set. Subclasses should
 * override one or more methods to modify the behavior of the backing set as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingNavigableSet} forward <i>indiscriminately</i>
 * to the methods of the delegate. For example, overriding {@link #add} alone <i>will not</i> change
 * the behavior of {@link #addAll}, which can lead to unexpected behavior. In this case, you should
 * override {@code addAll} as well, either providing your own implementation, or delegating to the
 * provided {@code standardAddAll} method.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingNavigableSet}.
 *
 * <p>Each of the {@code standard} methods uses the set's comparator (or the natural ordering of the
 * elements, if there is no comparator) to test element equality. As a result, if the comparator is
 * not consistent with equals, some of the standard implementations may violate the {@code Set}
 * contract.
 *
 * <p>The {@code standard} methods and the collection views they return are not guaranteed to be
 * thread-safe, even when all of the methods that they depend on are thread-safe.
 */


public abstract class ForwardingNavigableSet<E extends Object>
        extends ForwardingSortedSet<E> implements NavigableSet<E> {

    /**
     * Constructor for use by subclasses.
     */
    protected ForwardingNavigableSet() {
    }

    @Override
    protected abstract NavigableSet<E> delegate();

    @Override
    @CheckForNull
    public E lower( @ParametricNullness E e ) {
        return delegate().lower(e);
    }

    /**
     * A sensible definition of {@link #lower} in terms of the {@code descendingIterator} method of
     * {@link #headSet(Object, boolean)}. If you override {@link #headSet(Object, boolean)}, you may
     * wish to override {@link #lower} to forward to this implementation.
     */
    @CheckForNull
    protected E standardLower( @ParametricNullness E e ) {
        return IterUtil.getNext(headSet(e, false).descendingIterator(), null);
    }

    @Override
    @CheckForNull
    public E floor( @ParametricNullness E e ) {
        return delegate().floor(e);
    }

    /**
     * A sensible definition of {@link #floor} in terms of the {@code descendingIterator} method of
     * {@link #headSet(Object, boolean)}. If you override {@link #headSet(Object, boolean)}, you may
     * wish to override {@link #floor} to forward to this implementation.
     */
    @CheckForNull
    protected E standardFloor( @ParametricNullness E e ) {
        return IterUtil.getNext(headSet(e, true).descendingIterator(), null);
    }

    @Override
    @CheckForNull
    public E ceiling( @ParametricNullness E e ) {
        return delegate().ceiling(e);
    }

    /**
     * A sensible definition of {@link #ceiling} in terms of the {@code iterator} method of {@link
     * #tailSet(Object, boolean)}. If you override {@link #tailSet(Object, boolean)}, you may wish to
     * override {@link #ceiling} to forward to this implementation.
     */
    @CheckForNull
    protected E standardCeiling( @ParametricNullness E e ) {
        return IterUtil.getNext(tailSet(e, true).iterator(), null);
    }

    @Override
    @CheckForNull
    public E higher( @ParametricNullness E e ) {
        return delegate().higher(e);
    }

    /**
     * A sensible definition of {@link #higher} in terms of the {@code iterator} method of {@link
     * #tailSet(Object, boolean)}. If you override {@link #tailSet(Object, boolean)}, you may wish to
     * override {@link #higher} to forward to this implementation.
     */
    @CheckForNull
    protected E standardHigher( @ParametricNullness E e ) {
        return IterUtil.getNext(tailSet(e, false).iterator(), null);
    }

    @Override
    @CheckForNull
    public E pollFirst() {
        return delegate().pollFirst();
    }

    /**
     * A sensible definition of {@link #pollFirst} in terms of the {@code iterator} method. If you
     * override {@link #iterator} you may wish to override {@link #pollFirst} to forward to this
     * implementation.
     */
    @CheckForNull
    protected E standardPollFirst() {
        return IterUtil.pollNext(iterator());
    }

    @Override
    @CheckForNull
    public E pollLast() {
        return delegate().pollLast();
    }

    /**
     * A sensible definition of {@link #pollLast} in terms of the {@code descendingIterator} method.
     * If you override {@link #descendingIterator} you may wish to override {@link #pollLast} to
     * forward to this implementation.
     */
    @CheckForNull
    protected E standardPollLast() {
        return IterUtil.pollNext(descendingIterator());
    }

    @ParametricNullness
    protected E standardFirst() {
        return iterator().next();
    }

    @ParametricNullness
    protected E standardLast() {
        return descendingIterator().next();
    }

    @Override
    public NavigableSet<E> descendingSet() {
        return delegate().descendingSet();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return delegate().descendingIterator();
    }

    @Override
    public NavigableSet<E> subSet(
            @ParametricNullness E fromElement,
            boolean fromInclusive,
            @ParametricNullness E toElement,
            boolean toInclusive ) {
        return delegate().subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    /**
     * A sensible definition of {@link #subSet(Object, boolean, Object, boolean)} in terms of the
     * {@code headSet} and {@code tailSet} methods. In many cases, you may wish to override {@link
     * #subSet(Object, boolean, Object, boolean)} to forward to this implementation.
     */

    protected NavigableSet<E> standardSubSet(
            @ParametricNullness E fromElement,
            boolean fromInclusive,
            @ParametricNullness E toElement,
            boolean toInclusive ) {
        return tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
    }

    /**
     * A sensible definition of {@link #subSet(Object, Object)} in terms of the {@link #subSet(Object,
     * boolean, Object, boolean)} method. If you override {@link #subSet(Object, boolean, Object,
     * boolean)}, you may wish to override {@link #subSet(Object, Object)} to forward to this
     * implementation.
     */
    @Override
    protected SortedSet<E> standardSubSet(
            @ParametricNullness E fromElement, @ParametricNullness E toElement ) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public NavigableSet<E> headSet( @ParametricNullness E toElement, boolean inclusive ) {
        return delegate().headSet(toElement, inclusive);
    }

    /**
     * A sensible definition of {@link #headSet(Object)} in terms of the {@link #headSet(Object,
     * boolean)} method. If you override {@link #headSet(Object, boolean)}, you may wish to override
     * {@link #headSet(Object)} to forward to this implementation.
     */
    protected SortedSet<E> standardHeadSet( @ParametricNullness E toElement ) {
        return headSet(toElement, false);
    }

    @Override
    public NavigableSet<E> tailSet( @ParametricNullness E fromElement, boolean inclusive ) {
        return delegate().tailSet(fromElement, inclusive);
    }

    /**
     * A sensible definition of {@link #tailSet(Object)} in terms of the {@link #tailSet(Object,
     * boolean)} method. If you override {@link #tailSet(Object, boolean)}, you may wish to override
     * {@link #tailSet(Object)} to forward to this implementation.
     */
    protected SortedSet<E> standardTailSet( @ParametricNullness E fromElement ) {
        return tailSet(fromElement, true);
    }

    /**
     * A sensible implementation of {@link NavigableSet#descendingSet} in terms of the other methods
     * of {@link NavigableSet}, notably including {@link NavigableSet#descendingIterator}.
     *
     * <p>In many cases, you may wish to override {@link ForwardingNavigableSet#descendingSet} to
     * forward to this implementation or a subclass thereof.
     */

    protected class StandardDescendingSet extends SetUtil.DescendingSet<E> {
        /**
         * Constructor for use by subclasses.
         */
        public StandardDescendingSet() {
            super(ForwardingNavigableSet.this);
        }
    }
}
