package com.whaleal.icefrog.core.collection;

import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
public class TransSequentialList<
        F extends Object, T extends Object>
        extends AbstractSequentialList<T> implements Serializable {
    private static final long serialVersionUID = 0;
    final List<F> fromList;
    final Function<? super F, ? extends T> function;

    TransSequentialList( List<F> fromList, Function<? super F, ? extends T> function ) {
        this.fromList = checkNotNull(fromList);
        this.function = checkNotNull(function);
    }

    /**
     * The default implementation inherited is based on iteration and removal of each element which
     * can be overkill. That's why we forward this call directly to the backing list.
     */
    @Override
    public void clear() {
        fromList.clear();
    }

    @Override
    public int size() {
        return fromList.size();
    }

    @Override
    public ListIterator<T> listIterator( final int index ) {


        return new TransListIter(fromList.listIterator(index),function) ;
    }

    @Override
    public boolean removeIf( Predicate<? super T> filter ) {
        checkNotNull(filter);
        return fromList.removeIf(element -> filter.test(function.apply(element)));
    }
}