package com.whaleal.icefrog.collections;

/**
 * @author wh
 */

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

public class MergingIterator<T extends Object> extends UnmodifiableIterator<T> {
    final Queue<PeekingIterator<T>> queue;

    public MergingIterator(
            Iterable<? extends Iterator<? extends T> > iterators, Comparator<? super T> itemComparator ) {
        // A comparator that's used by the heap, allowing the heap
        // to be sorted based on the top of each iterator.
        Comparator<PeekingIterator<T>> heapComparator =
                ( PeekingIterator<T> o1, PeekingIterator<T> o2 ) ->
                        itemComparator.compare(o1.peek(), o2.peek());

        queue = new PriorityQueue<>(2, heapComparator);

        for (Iterator<? extends T> iterator : iterators) {
            if (iterator.hasNext()) {
                queue.add(IterUtil.peekingIterator(iterator));
            }
        }
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override

    public T next() {
        PeekingIterator<T> nextIter = queue.remove();
        T next = nextIter.next();
        if (nextIter.hasNext()) {
            queue.add(nextIter);
        }
        return next;
    }
}
