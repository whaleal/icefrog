package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.ListUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.List;


/**
 * An ordering that treats all references as equals, even nulls.
 *
 * @author Emily Soldal
 */


final class AllEqualOrdering extends Ordering<Object> implements Serializable {
    static final AllEqualOrdering INSTANCE = new AllEqualOrdering();
    private static final long serialVersionUID = 0;

    @Override
    public int compare( @CheckForNull Object left, @CheckForNull Object right ) {
        return 0;
    }

    @Override
    public <E extends Object> List<E> sortedCopy( Iterable<E> iterable ) {
        return ListUtil.list(false, iterable);
    }

    @Override
    @SuppressWarnings("nullness") // unsafe: see supertype
    public <E extends Object> ImmutableList<E> immutableSortedCopy( Iterable<E> iterable ) {
        return ImmutableList.copyOf(iterable);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S extends Object> Ordering<S> reverse() {
        return (Ordering<S>) this;
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return "Ordering.allEqual()";
    }
}
