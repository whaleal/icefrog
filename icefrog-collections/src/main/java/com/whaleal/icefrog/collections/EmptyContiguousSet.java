package com.whaleal.icefrog.collections;


import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An empty contiguous set.
 */

@SuppressWarnings("rawtypes") // allow ungenerified Comparable types

final class EmptyContiguousSet<C extends Comparable> extends ContiguousSet<C> {
    EmptyContiguousSet( DiscreteDomain<C> domain ) {
        super(domain);
    }

    @Override
    public C first() {
        throw new NoSuchElementException();
    }

    @Override
    public C last() {
        throw new NoSuchElementException();
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public ContiguousSet<C> intersection( ContiguousSet<C> other ) {
        return this;
    }

    @Override
    public Range<C> range() {
        throw new NoSuchElementException();
    }

    @Override
    public Range<C> range( BoundType lowerBoundType, BoundType upperBoundType ) {
        throw new NoSuchElementException();
    }

    @Override
    ContiguousSet<C> headSetImpl( C toElement, boolean inclusive ) {
        return this;
    }

    @Override
    ContiguousSet<C> subSetImpl(
            C fromElement, boolean fromInclusive, C toElement, boolean toInclusive ) {
        return this;
    }

    @Override
    ContiguousSet<C> tailSetImpl( C fromElement, boolean fromInclusive ) {
        return this;
    }

    @Override
    public boolean contains( @CheckForNull Object object ) {
        return false;
    }

    // not used by GWT emulation
    @Override
    int indexOf( @CheckForNull Object target ) {
        return -1;
    }

    @Override
    public UnmodifiableIterator<C> iterator() {
        return IterUtil.emptyIterator();
    }

    // NavigableSet
    @Override
    public Iterator<C> descendingIterator() {
        return IterUtil.emptyIterator();
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ImmutableList<C> asList() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return "[]";
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object instanceof Set) {
            Set<?> that = (Set<?>) object;
            return that.isEmpty();
        }
        return false;
    }

    // not used in GWT
    @Override
    boolean isHashCodeFast() {
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    // serialization
    @Override
    Object writeReplace() {
        return new SerializedForm<>(domain);
    }

    // NavigableSet
    @Override
    ImmutableSortedSet<C> createDescendingSet() {
        return ImmutableSortedSet.emptySet(Ordering.natural().reverse());
    }

    // serialization
    private static final class SerializedForm<C extends Comparable> implements Serializable {
        private static final long serialVersionUID = 0;
        private final DiscreteDomain<C> domain;

        private SerializedForm( DiscreteDomain<C> domain ) {
            this.domain = domain;
        }

        private Object readResolve() {
            return new EmptyContiguousSet<>(domain);
        }
    }
}
