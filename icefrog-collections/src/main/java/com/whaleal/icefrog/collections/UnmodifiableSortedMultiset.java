package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.NavigableSet;


/**
 * Implementation of {@link Multisets#unmodifiableSortedMultiset(SortedMultiset)}, split out into
 * its own file so it can be GWT emulated (to deal with the differing elementSet() types in GWT and
 * non-GWT).
 */


final class UnmodifiableSortedMultiset<E extends Object> extends Multisets.UnmodifiableMultiset<E>
        implements SortedMultiset<E> {
    private static final long serialVersionUID = 0;
    @CheckForNull
    private transient UnmodifiableSortedMultiset<E> descendingMultiset;

    UnmodifiableSortedMultiset( SortedMultiset<E> delegate ) {
        super(delegate);
    }

    @Override
    protected SortedMultiset<E> delegate() {
        return (SortedMultiset<E>) super.delegate();
    }

    @Override
    public Comparator<? super E> comparator() {
        return delegate().comparator();
    }

    @Override
    NavigableSet<E> createElementSet() {
        return SetUtil.unmodifiableNavigableSet(delegate().elementSet());
    }

    @Override
    public NavigableSet<E> elementSet() {
        return (NavigableSet<E>) super.elementSet();
    }

    @Override
    public SortedMultiset<E> descendingMultiset() {
        UnmodifiableSortedMultiset<E> result = descendingMultiset;
        if (result == null) {
            result = new UnmodifiableSortedMultiset<>(delegate().descendingMultiset());
            result.descendingMultiset = this;
            return descendingMultiset = result;
        }
        return result;
    }

    @Override
    @CheckForNull
    public Multiset.Entry<E> firstEntry() {
        return delegate().firstEntry();
    }

    @Override
    @CheckForNull
    public Multiset.Entry<E> lastEntry() {
        return delegate().lastEntry();
    }

    @Override
    @CheckForNull
    public Multiset.Entry<E> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    @CheckForNull
    public Multiset.Entry<E> pollLastEntry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SortedMultiset<E> headMultiset( @ParametricNullness E upperBound, BoundType boundType ) {
        return Multisets.unmodifiableSortedMultiset(delegate().headMultiset(upperBound, boundType));
    }

    @Override
    public SortedMultiset<E> subMultiset(
            @ParametricNullness E lowerBound,
            BoundType lowerBoundType,
            @ParametricNullness E upperBound,
            BoundType upperBoundType ) {
        return Multisets.unmodifiableSortedMultiset(
                delegate().subMultiset(lowerBound, lowerBoundType, upperBound, upperBoundType));
    }

    @Override
    public SortedMultiset<E> tailMultiset( @ParametricNullness E lowerBound, BoundType boundType ) {
        return Multisets.unmodifiableSortedMultiset(delegate().tailMultiset(lowerBound, boundType));
    }
}
