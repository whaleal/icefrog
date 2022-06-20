package com.whaleal.icefrog.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.whaleal.icefrog.core.lang.Precondition.checkRemove;

/**
 * @author wh
 */
public enum EmptyModifiableIterator implements Iterator<Object> {
    INSTANCE;

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        checkRemove(false);
    }
}
