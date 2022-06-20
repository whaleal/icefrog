package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.*;

import static com.whaleal.icefrog.collections.BoundType.CLOSED;
import static com.whaleal.icefrog.collections.BoundType.OPEN;


/**
 * Provides static utility methods for creating and working with {@link SortedMultiset} instances.
 */


final class SortedMultisets {
    private SortedMultisets() {
    }

    private static <E extends Object> E getElementOrThrow( @CheckForNull Multiset.Entry<E> entry ) {
        if (entry == null) {
            throw new NoSuchElementException();
        }
        return entry.getElement();
    }

    @CheckForNull
    private static <E extends Object> E getElementOrNull( @CheckForNull Multiset.Entry<E> entry ) {
        return (entry == null) ? null : entry.getElement();
    }

    /**
     * A skeleton implementation for {@link SortedMultiset#elementSet}.
     */
    @SuppressWarnings("JdkObsolete") // TODO(b/6160855): Switch GWT emulations to NavigableSet.
    static class ElementSet<E extends Object> extends Multisets.ElementSet<E>
            implements SortedSet<E> {
        private final SortedMultiset<E> multiset;

        ElementSet( SortedMultiset<E> multiset ) {
            this.multiset = multiset;
        }

        @Override
        final SortedMultiset<E> multiset() {
            return multiset;
        }

        @Override
        public Iterator<E> iterator() {
            return Multisets.elementIterator(multiset().entrySet().iterator());
        }

        @Override
        public Comparator<? super E> comparator() {
            return multiset().comparator();
        }

        @Override
        public SortedSet<E> subSet( @ParametricNullness E fromElement, @ParametricNullness E toElement ) {
            return multiset().subMultiset(fromElement, CLOSED, toElement, OPEN).elementSet();
        }

        @Override
        public SortedSet<E> headSet( @ParametricNullness E toElement ) {
            return multiset().headMultiset(toElement, OPEN).elementSet();
        }

        @Override
        public SortedSet<E> tailSet( @ParametricNullness E fromElement ) {
            return multiset().tailMultiset(fromElement, CLOSED).elementSet();
        }

        @Override
        @ParametricNullness
        public E first() {
            return getElementOrThrow(multiset().firstEntry());
        }

        @Override
        @ParametricNullness
        public E last() {
            return getElementOrThrow(multiset().lastEntry());
        }
    }

    /**
     * A skeleton navigable implementation for {@link SortedMultiset#elementSet}.
     */
    // Navigable
    static class NavigableElementSet<E extends Object> extends ElementSet<E>
            implements NavigableSet<E> {
        NavigableElementSet( SortedMultiset<E> multiset ) {
            super(multiset);
        }

        @Override
        @CheckForNull
        public E lower( @ParametricNullness E e ) {
            return getElementOrNull(multiset().headMultiset(e, OPEN).lastEntry());
        }

        @Override
        @CheckForNull
        public E floor( @ParametricNullness E e ) {
            return getElementOrNull(multiset().headMultiset(e, CLOSED).lastEntry());
        }

        @Override
        @CheckForNull
        public E ceiling( @ParametricNullness E e ) {
            return getElementOrNull(multiset().tailMultiset(e, CLOSED).firstEntry());
        }

        @Override
        @CheckForNull
        public E higher( @ParametricNullness E e ) {
            return getElementOrNull(multiset().tailMultiset(e, OPEN).firstEntry());
        }

        @Override
        public NavigableSet<E> descendingSet() {
            return new NavigableElementSet<>(multiset().descendingMultiset());
        }

        @Override
        public Iterator<E> descendingIterator() {
            return descendingSet().iterator();
        }

        @Override
        @CheckForNull
        public E pollFirst() {
            return getElementOrNull(multiset().pollFirstEntry());
        }

        @Override
        @CheckForNull
        public E pollLast() {
            return getElementOrNull(multiset().pollLastEntry());
        }

        @Override
        public NavigableSet<E> subSet(
                @ParametricNullness E fromElement,
                boolean fromInclusive,
                @ParametricNullness E toElement,
                boolean toInclusive ) {
            return new NavigableElementSet<>(
                    multiset()
                            .subMultiset(
                                    fromElement, BoundType.forBoolean(fromInclusive),
                                    toElement, BoundType.forBoolean(toInclusive)));
        }

        @Override
        public NavigableSet<E> headSet( @ParametricNullness E toElement, boolean inclusive ) {
            return new NavigableElementSet<>(
                    multiset().headMultiset(toElement, BoundType.forBoolean(inclusive)));
        }

        @Override
        public NavigableSet<E> tailSet( @ParametricNullness E fromElement, boolean inclusive ) {
            return new NavigableElementSet<>(
                    multiset().tailMultiset(fromElement, BoundType.forBoolean(inclusive)));
        }
    }
}
