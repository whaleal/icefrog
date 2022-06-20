package com.whaleal.icefrog.core.collection;

import com.whaleal.icefrog.core.lang.Precondition;

import java.util.Iterator;
import java.util.function.Function;

/**
 * 使用给定的转换函数，转换源{@link Iterator}为新类型的{@link Iterator}
 *
 * @param <F> 源元素类型
 * @param <T> 目标元素类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class TransIter<F, T> implements Iterator<T> {

    protected final Iterator<? extends F> backingIterator;
    protected final Function<? super F, ? extends T> func;

    /**
     * 构造
     *
     * @param backingIterator 源{@link Iterator}
     * @param func            转换函数
     */
    public TransIter( Iterator<? extends F> backingIterator, Function<? super F, ? extends T> func ) {
        this.backingIterator = Precondition.notNull(backingIterator);
        this.func = Precondition.notNull(func);
    }

    @Override
    public final boolean hasNext() {
        return backingIterator.hasNext();
    }

    @Override
    public final T next() {
        return func.apply(backingIterator.next());
    }

    @Override
    public final void remove() {
        backingIterator.remove();
    }
}
