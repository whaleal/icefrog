package com.whaleal.icefrog.collections;

import java.util.Comparator;
import java.util.SortedSet;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/**
 * Utilities for dealing with sorted collections of all types.
 */


final class SortedIterables {
    private SortedIterables() {
    }

    /**
     * Returns {@code true} if {@code elements} is a sorted collection using an ordering equivalent to
     * {@code comparator}.
     */
    public static boolean hasSameComparator( Comparator<?> comparator, Iterable<?> elements ) {
        checkNotNull(comparator);
        checkNotNull(elements);
        Comparator<?> comparator2;
        if (elements instanceof SortedSet) {
            comparator2 = comparator((SortedSet<?>) elements);
        } else if (elements instanceof SortedIterable) {
            comparator2 = ((SortedIterable<?>) elements).comparator();
        } else {
            return false;
        }
        return comparator.equals(comparator2);
    }

    @SuppressWarnings("unchecked")
    // if sortedSet.comparator() is null, the set must be naturally ordered
    public static <E extends Object> Comparator<? super E> comparator(
            SortedSet<E> sortedSet ) {
        Comparator<? super E> result = sortedSet.comparator();
        if (result == null) {
            result = (Comparator<? super E>) Ordering.natural();
        }
        return result;
    }
}
