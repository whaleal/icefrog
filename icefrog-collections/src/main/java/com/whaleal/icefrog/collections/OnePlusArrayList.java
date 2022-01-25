package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.collections.ParametricNullness;
import com.whaleal.icefrog.core.util.NumberUtil;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.RandomAccess;

import static com.whaleal.icefrog.core.lang.Precondition.checkElementIndex;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
public  class OnePlusArrayList<E extends Object> extends AbstractList<E>
        implements Serializable, RandomAccess {
    private static final long serialVersionUID = 0;
    @ParametricNullness
    final E first;
    final E[] rest;

    OnePlusArrayList( @ParametricNullness E first, E[] rest ) {
        this.first = first;
        this.rest = checkNotNull(rest);
    }

    @Override
    public int size() {
        return (int) NumberUtil.saturatedAdd(rest.length, 1);
    }

    @Override
    @ParametricNullness
    public E get( int index ) {
        // check explicitly so the IOOBE will have the right message
        checkElementIndex(index, size());
        return (index == 0) ? first : rest[index - 1];
    }
}
