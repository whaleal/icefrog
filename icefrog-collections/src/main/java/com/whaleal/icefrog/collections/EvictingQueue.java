package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.IterUtil;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * A non-blocking queue which automatically evicts elements from the head of the queue when
 * attempting to add new elements onto the queue and it is full. This queue orders elements FIFO
 * (first-in-first-out). This data structure is logically equivalent to a circular buffer (i.e.,
 * cyclic buffer or ring buffer).
 *
 * <p>An evicting queue must be configured with a maximum size. Each time an element is added to a
 * full queue, the queue automatically removes its head element. This is different from conventional
 * bounded queues, which either block or reject new elements when full.
 *
 * <p>This class is not thread-safe, and does not accept null elements.
 *
 * @author Kurt Alfred Kluever
 */


public final class EvictingQueue<E> extends ForwardingQueue<E> implements Serializable {

    private static final long serialVersionUID = 0L;
    final int maxSize;
    private final Queue<E> delegate;

    private EvictingQueue( int maxSize ) {
        checkArgument(maxSize >= 0, "maxSize (%s) must >= 0", maxSize);
        this.delegate = new ArrayDeque<>(maxSize);
        this.maxSize = maxSize;
    }

    /**
     * Creates and returns a new evicting queue that will hold up to {@code maxSize} elements.
     *
     * <p>When {@code maxSize} is zero, elements will be evicted immediately after being added to the
     * queue.
     */
    public static <E> EvictingQueue<E> create( int maxSize ) {
        return new EvictingQueue<>(maxSize);
    }

    /**
     * Returns the number of additional elements that this queue can accept without evicting; zero if
     * the queue is currently full.
     */
    public int remainingCapacity() {
        return maxSize - size();
    }

    @Override
    protected Queue<E> delegate() {
        return delegate;
    }

    /**
     * Adds the given element to this queue. If the queue is currently full, the element at the head
     * of the queue is evicted to make room.
     *
     * @return {@code true} always
     */
    @Override

    public boolean offer( E e ) {
        return add(e);
    }

    /**
     * Adds the given element to this queue. If the queue is currently full, the element at the head
     * of the queue is evicted to make room.
     *
     * @return {@code true} always
     */
    @Override

    public boolean add( E e ) {
        checkNotNull(e); // check before removing
        if (maxSize == 0) {
            return true;
        }
        if (size() == maxSize) {
            delegate.remove();
        }
        delegate.add(e);
        return true;
    }

    @Override

    public boolean addAll( Collection<? extends E> collection ) {
        int size = collection.size();
        if (size >= maxSize) {
            clear();
            return com.whaleal.icefrog.core.collection.IterUtil.addAll(this, IterUtil.skip(collection, size - maxSize));
        }
        return standardAddAll(collection);
    }

    @Override
    public Object[] toArray() {
        /*
         * If we could, we'd declare the no-arg `Collection.toArray()` to return "Object[] but elements
         * have the same nullness as E." Since we can't, we declare it to return nullable elements, and
         * we can override it in our non-null-guaranteeing subtypes to present a better signature to
         * their users.
         *
         * However, the checker *we* use has this special knowledge about `Collection.toArray()` anyway,
         * so in our implementation code, we can rely on that. That's why the expression below
         * type-checks.
         */
        return super.toArray();
    }
}
