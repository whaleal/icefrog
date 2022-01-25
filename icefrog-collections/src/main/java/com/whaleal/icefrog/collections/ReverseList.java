package com.whaleal.icefrog.collections;

import java.util.*;

import static com.whaleal.icefrog.core.collection.ListUtil.reverse;
import static com.whaleal.icefrog.core.lang.Precondition.*;
import static com.whaleal.icefrog.core.lang.Precondition.checkState;

/**
 * @author wh
 */
public  class ReverseList<T extends Object> extends AbstractList<T> {
    private final List<T> forwardList;

    ReverseList( List<T> forwardList ) {
        this.forwardList = checkNotNull(forwardList);
    }

    List<T> getForwardList() {
        return forwardList;
    }

    private int reverseIndex( int index ) {
        int size = size();
        checkElementIndex(index, size);
        return (size - 1) - index;
    }

    private int reversePosition( int index ) {
        int size = size();
        checkPositionIndex(index, size);
        return size - index;
    }

    @Override
    public void add( int index, @ParametricNullness T element ) {
        forwardList.add(reversePosition(index), element);
    }

    @Override
    public void clear() {
        forwardList.clear();
    }

    @Override
    @ParametricNullness
    public T remove( int index ) {
        return forwardList.remove(reverseIndex(index));
    }

    @Override
    protected void removeRange( int fromIndex, int toIndex ) {
        subList(fromIndex, toIndex).clear();
    }

    @Override
    @ParametricNullness
    public T set( int index, @ParametricNullness T element ) {
        return forwardList.set(reverseIndex(index), element);
    }

    @Override
    @ParametricNullness
    public T get( int index ) {
        return forwardList.get(reverseIndex(index));
    }

    @Override
    public int size() {
        return forwardList.size();
    }

    @Override
    public List<T> subList( int fromIndex, int toIndex ) {
        checkPositionIndexes(fromIndex, toIndex, size());
        return reverse(forwardList.subList(reversePosition(toIndex), reversePosition(fromIndex)));
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<T> listIterator( int index ) {
        int start = reversePosition(index);
        final ListIterator<T> forwardIterator = forwardList.listIterator(start);
        return new ListIterator<T>() {

            boolean canRemoveOrSet;

            @Override
            public void add( @ParametricNullness T e ) {
                forwardIterator.add(e);
                forwardIterator.previous();
                canRemoveOrSet = false;
            }

            @Override
            public boolean hasNext() {
                return forwardIterator.hasPrevious();
            }

            @Override
            public boolean hasPrevious() {
                return forwardIterator.hasNext();
            }

            @Override
            @ParametricNullness
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                canRemoveOrSet = true;
                return forwardIterator.previous();
            }

            @Override
            public int nextIndex() {
                return reversePosition(forwardIterator.nextIndex());
            }

            @Override
            @ParametricNullness
            public T previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                canRemoveOrSet = true;
                return forwardIterator.next();
            }

            @Override
            public int previousIndex() {
                return nextIndex() - 1;
            }

            @Override
            public void remove() {
                checkRemove(canRemoveOrSet);
                forwardIterator.remove();
                canRemoveOrSet = false;
            }

            @Override
            public void set( @ParametricNullness T e ) {
                checkState(canRemoveOrSet);
                forwardIterator.set(e);
            }
        };
    }
}
