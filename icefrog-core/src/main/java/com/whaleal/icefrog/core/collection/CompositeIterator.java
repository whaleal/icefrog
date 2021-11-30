package com.whaleal.icefrog.core.collection;

/**
 * @author wh
 */

import com.whaleal.icefrog.core.lang.Precondition;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Composite iterator that combines multiple other iterators,
 * as registered via {@link #add(Iterator)}.
 *
 * <p>This implementation maintains a linked set of iterators
 * which are invoked in sequence until all iterators are exhausted.
 * 组合多个迭代器的复合迭代器，
 * 通过 {@link #add(Iterator)} 注册。
 *
 * <p>这个实现维护了一组链接的迭代器
 * 依次调用，直到所有迭代器都用完为止。
 *
 * 混合 迭代器
 *
 * @author wh
 * @date 2021-11-29
 */
public class CompositeIterator<E> implements Iterator<E> {

    private final Set<Iterator<E>> iterators = new LinkedHashSet<>();

    //  当开始迭代时 不能再添加新的迭代器。这里当做一个锁来使用
    private boolean inUse = false;


    /**
     * Add given iterator to this composite.
     */
    public void add(Iterator<E> iterator) {
        Precondition.state(!this.inUse, "You can no longer add iterators to a composite iterator that's already in use");
        if (this.iterators.contains(iterator)) {
            throw new IllegalArgumentException("You cannot add the same iterator twice");
        }
        this.iterators.add(iterator);
    }

    @Override
    public boolean hasNext() {
        this.inUse = true;
        for (Iterator<E> iterator : this.iterators) {
            if (iterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public E next() {
        this.inUse = true;
        for (Iterator<E> iterator : this.iterators) {
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        throw new NoSuchElementException("All iterators exhausted");
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("CompositeIterator does not support remove()");
    }

}
