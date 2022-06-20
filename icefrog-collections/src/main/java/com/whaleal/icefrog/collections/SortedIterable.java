package com.whaleal.icefrog.collections;

import java.util.Comparator;
import java.util.Iterator;


/**
 * An {@code Iterable} whose elements are sorted relative to a {@code Comparator}, typically
 * provided at creation time.
 */


interface SortedIterable<T extends Object> extends Iterable<T> {
    /**
     * Returns the {@code Comparator} by which the elements of this iterable are ordered, or {@code
     * Ordering.natural()} if the elements are ordered by their natural ordering.
     */
    Comparator<? super T> comparator();

    /**
     * Returns an iterator over elements of type {@code T}. The elements are returned in nondecreasing
     * order according to the associated {@link #comparator}.
     */
    @Override
    Iterator<T> iterator();
}
