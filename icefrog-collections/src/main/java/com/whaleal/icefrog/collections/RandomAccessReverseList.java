package com.whaleal.icefrog.collections;

import java.util.List;
import java.util.RandomAccess;

/**
 * @author wh
 */
public  class RandomAccessReverseList<T extends Object> extends ReverseList<T>
        implements RandomAccess {
    RandomAccessReverseList( List<T> forwardList ) {
        super(forwardList);
    }
}
