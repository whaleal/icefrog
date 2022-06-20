package com.whaleal.icefrog.core.collection;

import javax.annotation.CheckForNull;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
public class AbstractListWrapper<E extends Object> extends AbstractList<E> {
    final List<E> backingList;

    public AbstractListWrapper( List<E> backingList ) {
        this.backingList = checkNotNull(backingList);
    }

    @Override
    public void add( int index,  E element ) {
        backingList.add(index, element);
    }

    @Override
    public boolean addAll( int index, Collection<? extends E> c ) {
        return backingList.addAll(index, c);
    }

    @Override
    public E get( int index ) {
        return backingList.get(index);
    }

    @Override
    public E remove( int index ) {
        return backingList.remove(index);
    }

    @Override
    public E set( int index,  E element ) {
        return backingList.set(index, element);
    }

    @Override
    public boolean contains( @CheckForNull Object o ) {
        return backingList.contains(o);
    }

    @Override
    public int size() {
        return backingList.size();
    }
}
