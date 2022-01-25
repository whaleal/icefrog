package com.whaleal.icefrog.collections;

/**
 * @author wh
 */

import javax.annotation.CheckForNull;
import java.util.Iterator;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;
import static com.whaleal.icefrog.core.lang.Precondition.checkState;

public class PeekingImpl<E extends Object> implements PeekingIterator<E> {

    private final Iterator<? extends E> iterator;
    private boolean hasPeeked;
    @CheckForNull
    private E peekedElement;

    public PeekingImpl( Iterator<? extends E> iterator ) {
        this.iterator = checkNotNull(iterator);
    }

    @Override
    public boolean hasNext() {
        return hasPeeked || iterator.hasNext();
    }

    @Override

    public E next() {
        if (!hasPeeked) {
            return iterator.next();
        }
        // The cast is safe because of the hasPeeked check.
        E result = (peekedElement);
        hasPeeked = false;
        peekedElement = null;
        return result;
    }

    @Override
    public void remove() {
        checkState(!hasPeeked, "Can't remove after you've peeked at next");
        iterator.remove();
    }

    @Override

    public E peek() {
        if (!hasPeeked) {
            peekedElement = iterator.next();
            hasPeeked = true;
        }
        // The cast is safe because of the hasPeeked check.
        return (peekedElement);
    }
}
