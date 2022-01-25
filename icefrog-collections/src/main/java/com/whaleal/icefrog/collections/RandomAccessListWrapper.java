package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.AbstractListWrapper;

import java.util.List;
import java.util.RandomAccess;

/**
 * @author wh
 */
public  class RandomAccessListWrapper<E extends Object>
        extends AbstractListWrapper<E> implements RandomAccess {
    RandomAccessListWrapper( List<E> backingList ) {
        super(backingList);
    }
}
