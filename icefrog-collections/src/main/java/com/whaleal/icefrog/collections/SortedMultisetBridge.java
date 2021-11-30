package com.whaleal.icefrog.collections;

import java.util.SortedSet;


/**
 * Superinterface of {@link SortedMultiset} to introduce a bridge method for {@code elementSet()},
 * to ensure binary compatibility with older Guava versions that specified {@code elementSet()} to
 * return {@code SortedSet}.
 */


interface SortedMultisetBridge<E extends Object> extends Multiset<E> {
    @Override
    SortedSet<E> elementSet();
}
