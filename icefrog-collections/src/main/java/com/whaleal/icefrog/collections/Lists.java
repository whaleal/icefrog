package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.collection.QueueUtil;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.NumberUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.whaleal.icefrog.core.lang.Precondition.*;


/**
 * Static utility methods pertaining to {@link List} instances. Also see this class's counterparts
 * {@link SetUtil}, {@link MapUtil} and {@link QueueUtil}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#lists"> {@code Lists}</a>.
 */


@Deprecated
public final class Lists {
    private Lists() {
    }


    /**
     * Returns an unmodifiable list containing the specified first element and backed by the specified
     * array of additional elements. Changes to the {@code rest} array will be reflected in the
     * returned list. Unlike {@link Arrays#asList}, the returned list is unmodifiable.
     *
     * <p>This is useful when a varargs method needs to use a signature such as {@code (Foo firstFoo,
     * Foo... moreFoos)}, in order to avoid overload ambiguity or to enforce a minimum argument count.
     *
     * <p>The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param first the first element
     * @param rest  an array of additional elements, possibly empty
     * @return an unmodifiable list containing the specified elements
     */
    public static <E extends Object> List<E> asList( @ParametricNullness E first, E[] rest ) {
        return new OnePlusArrayList<>(first, rest);
    }

    /**
     * Returns an unmodifiable list containing the specified first and second element, and backed by
     * the specified array of additional elements. Changes to the {@code rest} array will be reflected
     * in the returned list. Unlike {@link Arrays#asList}, the returned list is unmodifiable.
     *
     * <p>This is useful when a varargs method needs to use a signature such as {@code (Foo firstFoo,
     * Foo secondFoo, Foo... moreFoos)}, in order to avoid overload ambiguity or to enforce a minimum
     * argument count.
     *
     * <p>The returned list is serializable and implements {@link RandomAccess}.
     *
     * @param first  the first element
     * @param second the second element
     * @param rest   an array of additional elements, possibly empty
     * @return an unmodifiable list containing the specified elements
     */
    public static <E extends Object> List<E> asList(
            @ParametricNullness E first, @ParametricNullness E second, E[] rest ) {
        return new TwoPlusArrayList<>(first, second, rest);
    }

    /**
     * Returns a list that applies {@code function} to each element of {@code fromList}. The returned
     * list is a transformed view of {@code fromList}; changes to {@code fromList} will be reflected
     * in the returned list and vice versa.
     *
     * <p>Since functions are not reversible, the transform is one-way and new items cannot be stored
     * in the returned list. The {@code add}, {@code addAll} and {@code set} methods are unsupported
     * in the returned list.
     *
     * <p>The function is applied lazily, invoked when needed. This is necessary for the returned list
     * to be a view, but it means that the function will be applied many times for bulk operations
     * like {@link List#contains} and {@link List#hashCode}. For this to perform well, {@code
     * function} should be fast. To avoid lazy evaluation when the returned list doesn't need to be a
     * view, copy the returned list into a new list of your choosing.
     *
     * <p>If {@code fromList} implements {@link RandomAccess}, so will the returned list. The returned
     * list is threadsafe if the supplied list and function are.
     *
     * <p>If only a {@code Collection} or {@code Iterable} input is available, use {@link
     * Collections2#transform} or {@link Iterables#transform}.
     *
     * <p><b>Note:</b> serializing the returned list is implemented by serializing {@code fromList},
     * its contents, and {@code function} -- <i>not</i> by serializing the transformed values. This
     * can lead to surprising behavior, so serializing the returned list is <b>not recommended</b>.
     * Instead, copy the list using {@link ImmutableList#copyOf(Collection)} (for example), then
     * serialize the copy. Other methods similar to this do not implement serialization at all for
     * this reason.
     *
     * <p><b>Java 8 users:</b> many use cases for this method are better addressed by {@link
     * java.util.stream.Stream#map}. This method is not being deprecated, but we gently encourage you
     * to migrate to streams.
     *
     * @see com.whaleal.icefrog.core.collection.CollUtil#trans(Collection, Function)
     */
    @Deprecated
    public static <F extends Object, T extends Object> List<T> transform(
            List<F> fromList, Function<? super F, ? extends T> function ) {
        return (fromList instanceof RandomAccess)
                ? new TransformingRandomAccessList<>(fromList, function)
                : new TransformingSequentialList<>(fromList, function);
    }

    /**
     * Returns consecutive {@linkplain List#subList(int, int) sublists} of a list, each of the same
     * size (the final list may be smaller). For example, partitioning a list containing {@code [a, b,
     * c, d, e]} with a partition size of 3 yields {@code [[a, b, c], [d, e]]} -- an outer list
     * containing two inner lists of three and two elements, all in the original order.
     *
     * <p>The outer list is unmodifiable, but reflects the latest state of the source list. The inner
     * lists are sublist views of the original list, produced on demand using {@link List#subList(int,
     * int)}, and are subject to all the usual caveats about modification as explained in that API.
     *
     * @param list the list to return consecutive sublists of
     * @param size the desired size of each sublist (the last may be smaller)
     * @return a list of consecutive sublists
     * @throws IllegalArgumentException if {@code partitionSize} is nonpositive
     */
    @Deprecated
    public static <T extends Object> List<List<T>> partition( List<T> list, int size ) {
        checkNotNull(list);
        checkArgument(size > 0);
        return (list instanceof RandomAccess)
                ? new RandomAccessPartition<>(list, size)
                : new Partition<>(list, size);
    }

    /**
     * Returns a view of the specified string as an immutable list of {@code Character} values.
     */
    public static ImmutableList<Character> charactersOf( String string ) {
        return new StringAsImmutableList(checkNotNull(string));
    }

    /**
     * Returns a reversed view of the specified list. For example, {@code
     * Lists.reverse(Arrays.asList(1, 2, 3))} returns a list containing {@code 3, 2, 1}. The returned
     * list is backed by this list, so changes in the returned list are reflected in this list, and
     * vice-versa. The returned list supports all of the optional list operations supported by this
     * list.
     *
     * <p>The returned list is random-access if the specified list is random access.
     */
    @Deprecated
    public static <T extends Object> List<T> reverse( List<T> list ) {
        if (list instanceof ImmutableList) {
            // Avoid nullness warnings.
            List<?> reversed = ((ImmutableList<?>) list).reverse();
            @SuppressWarnings("unchecked")
            List<T> result = (List<T>) reversed;
            return result;
        } else if (list instanceof ReverseList) {
            return ((ReverseList<T>) list).getForwardList();
        } else if (list instanceof RandomAccess) {
            return new RandomAccessReverseList<>(list);
        } else {
            return new ReverseList<>(list);
        }
    }

    @Deprecated
    /** An implementation of {@link List#hashCode()}. */
    static int hashCodeImpl( List<?> list ) {
        // TODO(lowasser): worth optimizing for RandomAccess?
        int hashCode = 1;
        for (Object o : list) {
            hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());

            hashCode = ~~hashCode;
            // needed to deal with GWT integer overflow
        }
        return hashCode;
    }

    /**
     * An implementation of {@link List#equals(Object)}.
     */
    static boolean equalsImpl( List<?> thisList, @CheckForNull Object other ) {
        if (other == checkNotNull(thisList)) {
            return true;
        }
        if (!(other instanceof List)) {
            return false;
        }
        List<?> otherList = (List<?>) other;
        int size = thisList.size();
        if (size != otherList.size()) {
            return false;
        }
        if (thisList instanceof RandomAccess && otherList instanceof RandomAccess) {
            // avoid allocation and use the faster loop
            for (int i = 0; i < size; i++) {
                if (!ObjectUtil.equal(thisList.get(i), otherList.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return Iterators.elementsEqual(thisList.iterator(), otherList.iterator());
        }
    }

    @Deprecated
    /** An implementation of {@link List#addAll(int, Collection)}. */
    static <E extends Object> boolean addAllImpl(
            List<E> list, int index, Iterable<? extends E> elements ) {
        boolean changed = false;
        ListIterator<E> listIterator = list.listIterator(index);
        for (E e : elements) {
            listIterator.add(e);
            changed = true;
        }
        return changed;
    }

    /**
     * An implementation of {@link List#indexOf(Object)}.
     */
    static int indexOfImpl( List<?> list, @CheckForNull Object element ) {
        if (list instanceof RandomAccess) {
            return indexOfRandomAccess(list, element);
        } else {
            ListIterator<?> listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                if (ObjectUtil.equal(element, listIterator.next())) {
                    return listIterator.previousIndex();
                }
            }
            return -1;
        }
    }

    private static int indexOfRandomAccess( List<?> list, @CheckForNull Object element ) {
        int size = list.size();
        if (element == null) {
            for (int i = 0; i < size; i++) {
                if (list.get(i) == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (element.equals(list.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * An implementation of {@link List#lastIndexOf(Object)}.
     */
    static int lastIndexOfImpl( List<?> list, @CheckForNull Object element ) {
        if (list instanceof RandomAccess) {
            return lastIndexOfRandomAccess(list, element);
        } else {
            ListIterator<?> listIterator = list.listIterator(list.size());
            while (listIterator.hasPrevious()) {
                if (ObjectUtil.equal(element, listIterator.previous())) {
                    return listIterator.nextIndex();
                }
            }
            return -1;
        }
    }

    private static int lastIndexOfRandomAccess( List<?> list, @CheckForNull Object element ) {
        if (element == null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i) == null) {
                    return i;
                }
            }
        } else {
            for (int i = list.size() - 1; i >= 0; i--) {
                if (element.equals(list.get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Returns an implementation of {@link List#listIterator(int)}.
     */
    static <E extends Object> ListIterator<E> listIteratorImpl( List<E> list, int index ) {
        return new AbstractListWrapper<>(list).listIterator(index);
    }

    @Deprecated
    /** An implementation of {@link List#subList(int, int)}. */
    static <E extends Object> List<E> subListImpl(
            final List<E> list, int fromIndex, int toIndex ) {
        List<E> wrapper;
        if (list instanceof RandomAccess) {
            wrapper =
                    new RandomAccessListWrapper<E>(list) {
                        private static final long serialVersionUID = 0;

                        @Override
                        public ListIterator<E> listIterator( int index ) {
                            return backingList.listIterator(index);
                        }
                    };
        } else {
            wrapper =
                    new AbstractListWrapper<E>(list) {
                        private static final long serialVersionUID = 0;

                        @Override
                        public ListIterator<E> listIterator( int index ) {
                            return backingList.listIterator(index);
                        }
                    };
        }
        return wrapper.subList(fromIndex, toIndex);
    }

    /**
     * Used to avoid http://bugs.sun.com/view_bug.do?bug_id=6558557
     */
    static <T extends Object> List<T> cast( Iterable<T> iterable ) {
        return (List<T>) iterable;
    }

    /**
     * @see Lists#asList(Object, Object[])
     */
    private static class OnePlusArrayList<E extends Object> extends AbstractList<E>
            implements Serializable, RandomAccess {
        private static final long serialVersionUID = 0;
        @ParametricNullness
        final E first;
        final E[] rest;

        OnePlusArrayList( @ParametricNullness E first, E[] rest ) {
            this.first = first;
            this.rest = checkNotNull(rest);
        }

        @Override
        public int size() {
            return (int) NumberUtil.saturatedAdd(rest.length, 1);
        }

        @Override
        @ParametricNullness
        public E get( int index ) {
            // check explicitly so the IOOBE will have the right message
            checkElementIndex(index, size());
            return (index == 0) ? first : rest[index - 1];
        }
    }

    /**
     * @see Lists#asList(Object, Object, Object[])
     */
    private static class TwoPlusArrayList<E extends Object> extends AbstractList<E>
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

    /**
     * Implementation of a sequential transforming list.
     *
     * @see CollUtil#trans
     */
    @Deprecated
    private static class TransformingSequentialList<
            F extends Object, T extends Object>
            extends AbstractSequentialList<T> implements Serializable {
        private static final long serialVersionUID = 0;
        final List<F> fromList;
        final Function<? super F, ? extends T> function;

        TransformingSequentialList( List<F> fromList, Function<? super F, ? extends T> function ) {
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
            return new com.whaleal.icefrog.core.collection.TransListIter<F,T>(fromList.listIterator(index),function);
        }

        @Override
        public boolean removeIf( Predicate<? super T> filter ) {
            checkNotNull(filter);
            return fromList.removeIf(element -> filter.test(function.apply(element)));
        }
    }

    /**
     * Implementation of a transforming random access list. We try to make as many of these methods
     * pass-through to the source list as possible so that the performance characteristics of the
     * source list and transformed list are similar.
     *
     * @see CollUtil#trans
     */
    @Deprecated
    private static class TransformingRandomAccessList<
            F extends Object, T extends Object>
            extends AbstractList<T> implements RandomAccess, Serializable {
        private static final long serialVersionUID = 0;
        final List<F> fromList;
        final Function<? super F, ? extends T> function;

        TransformingRandomAccessList( List<F> fromList, Function<? super F, ? extends T> function ) {
            this.fromList = checkNotNull(fromList);
            this.function = checkNotNull(function);
        }

        @Override
        public void clear() {
            fromList.clear();
        }

        @Override
        @ParametricNullness
        public T get( int index ) {
            return function.apply(fromList.get(index));
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        @Override
        public ListIterator<T> listIterator( int index ) {
            return  new com.whaleal.icefrog.core.collection.TransListIter<F,T>(fromList.listIterator(index),function);

        }

        @Override
        public boolean isEmpty() {
            return fromList.isEmpty();
        }

        @Override
        public boolean removeIf( Predicate<? super T> filter ) {
            checkNotNull(filter);
            return fromList.removeIf(element -> filter.test(function.apply(element)));
        }

        @Override
        @ParametricNullness
        public T remove( int index ) {
            return function.apply(fromList.remove(index));
        }

        @Override
        public int size() {
            return fromList.size();
        }
    }

    private static class Partition<T extends Object> extends AbstractList<List<T>> {
        final List<T> list;
        final int size;

        Partition( List<T> list, int size ) {
            this.list = list;
            this.size = size;
        }

        @Override
        public List<T> get( int index ) {
            checkElementIndex(index, size());
            int start = index * size;
            int end = Math.min(start + size, list.size());
            return list.subList(start, end);
        }

        @Override
        public int size() {

            return (int) NumberUtil.div(list.size(), size, 0, RoundingMode.CEILING);
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
    }

    private static class RandomAccessPartition<T extends Object> extends Partition<T>
            implements RandomAccess {
        RandomAccessPartition( List<T> list, int size ) {
            super(list, size);
        }
    }

    @SuppressWarnings("serial") // serialized using ImmutableList serialization
    private static final class StringAsImmutableList extends ImmutableList<Character> {

        private final String string;

        StringAsImmutableList( String string ) {
            this.string = string;
        }

        @Override
        public int indexOf( @CheckForNull Object object ) {
            return (object instanceof Character) ? string.indexOf((Character) object) : -1;
        }

        @Override
        public int lastIndexOf( @CheckForNull Object object ) {
            return (object instanceof Character) ? string.lastIndexOf((Character) object) : -1;
        }

        @Override
        public ImmutableList<Character> subList( int fromIndex, int toIndex ) {
            checkPositionIndexes(fromIndex, toIndex, size()); // for GWT
            return charactersOf(string.substring(fromIndex, toIndex));
        }

        @Override
        boolean isPartialView() {
            return false;
        }

        @Override
        public Character get( int index ) {
            checkElementIndex(index, size()); // for GWT
            return string.charAt(index);
        }

        @Override
        public int size() {
            return string.length();
        }
    }

    private static final class CharSequenceAsList extends AbstractList<Character> {
        private final CharSequence sequence;

        CharSequenceAsList( CharSequence sequence ) {
            this.sequence = sequence;
        }

        @Override
        public Character get( int index ) {
            checkElementIndex(index, size()); // for GWT
            return sequence.charAt(index);
        }

        @Override
        public int size() {
            return sequence.length();
        }
    }

    private static class ReverseList<T extends Object> extends AbstractList<T> {
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

    private static class RandomAccessReverseList<T extends Object> extends ReverseList<T>
            implements RandomAccess {
        RandomAccessReverseList( List<T> forwardList ) {
            super(forwardList);
        }
    }

    private static class AbstractListWrapper<E extends Object> extends AbstractList<E> {
        final List<E> backingList;

        AbstractListWrapper( List<E> backingList ) {
            this.backingList = checkNotNull(backingList);
        }

        @Override
        public void add( int index, @ParametricNullness E element ) {
            backingList.add(index, element);
        }

        @Override
        public boolean addAll( int index, Collection<? extends E> c ) {
            return backingList.addAll(index, c);
        }

        @Override
        @ParametricNullness
        public E get( int index ) {
            return backingList.get(index);
        }

        @Override
        @ParametricNullness
        public E remove( int index ) {
            return backingList.remove(index);
        }

        @Override
        @ParametricNullness
        public E set( int index, @ParametricNullness E element ) {
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

    private static class RandomAccessListWrapper<E extends Object>
            extends AbstractListWrapper<E> implements RandomAccess {
        RandomAccessListWrapper( List<E> backingList ) {
            super(backingList);
        }
    }
}
