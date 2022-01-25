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
public   class TwoPlusArrayList<E extends Object> extends AbstractList<E>
        implements Serializable, RandomAccess {
    private static final long serialVersionUID = 0;
    @ParametricNullness
    final E first;
    @ParametricNullness
    final E second;
    final E[] rest;

    TwoPlusArrayList( @ParametricNullness E first, @ParametricNullness E second, E[] rest ) {
        this.first = first;
        this.second = second;
        this.rest = checkNotNull(rest);
    }

    @Override
    public int size() {
        return (int) NumberUtil.saturatedAdd(rest.length, 2);
    }

    @Override
    @ParametricNullness
    public E get( int index ) {
        switch (index) {
            case 0:
                return first;
            case 1:
                return second;
            default:
                // check explicitly so the IOOBE will have the right message
                checkElementIndex(index, size());
                return rest[index - 2];
        }
    }
}