package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.collections.Multiset.Entry;
import com.whaleal.icefrog.core.collection.AbstractIterator;
import com.whaleal.icefrog.core.collection.SpliteratorUtil;
import com.whaleal.icefrog.core.lang.Predicate;
import com.whaleal.icefrog.core.util.NumberUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.util.PredicateUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import static com.whaleal.icefrog.core.lang.Precondition.*;
import static java.util.Objects.requireNonNull;


/**
 * Provides static utility methods for creating and working with {@link Multiset} instances.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#multisets"> {@code
 * Multisets}</a>.
 */


public final class Multisets {
    private Multisets() {
    }

    /**
     * Returns a {@code Collector} that accumulates elements into a multiset created via the specified
     * {@code Supplier}, whose elements are the result of applying {@code elementFunction} to the
     * inputs, with counts equal to the result of applying {@code countFunction} to the inputs.
     * Elements are added in encounter order.
     *
     * <p>If the mapped elements contain duplicates (according to {@link Object#equals}), the element
     * will be added more than once, with the count summed over all appearances of the element.
     *
     * <p>Note that {@code stream.collect(toMultiset(function, e -> 1, supplier))} is equivalent to
     * {@code stream.map(function).collect(Collectors.toCollection(supplier))}.
     *
     * <p>To collect to an {@link ImmutableMultiset}, use {@link
     * ImmutableMultiset#toImmutableMultiset}.
     */
    public static <T extends Object, E extends Object, M extends Multiset<E>>
    Collector<T, ?, M> toMultiset(
            Function<? super T, E> elementFunction,
            ToIntFunction<? super T> countFunction,
            Supplier<M> multisetSupplier ) {
        return CollectCollectors.toMultiset(elementFunction, countFunction, multisetSupplier);
    }

    /**
     * Returns an unmodifiable view of the specified multiset. Query operations on the returned
     * multiset "read through" to the specified multiset, and attempts to modify the returned multiset
     * result in an {@link UnsupportedOperationException}.
     *
     * <p>The returned multiset will be serializable if the specified multiset is serializable.
     *
     * @param multiset the multiset for which an unmodifiable view is to be generated
     * @return an unmodifiable view of the multiset
     */
    public static <E extends Object> Multiset<E> unmodifiableMultiset(
            Multiset<? extends E> multiset ) {
        if (multiset instanceof UnmodifiableMultiset || multiset instanceof ImmutableMultiset) {
            @SuppressWarnings("unchecked") // Since it's unmodifiable, the covariant cast is safe
            Multiset<E> result = (Multiset<E>) multiset;
            return result;
        }
        return new UnmodifiableMultiset<E>(checkNotNull(multiset));
    }

    /**
     * Simply returns its argument.
     *
     * @deprecated no need to use this
     */
    @Deprecated
    public static <E> Multiset<E> unmodifiableMultiset( ImmutableMultiset<E> multiset ) {
        return checkNotNull(multiset);
    }

    /**
     * Returns an unmodifiable view of the specified sorted multiset. Query operations on the returned
     * multiset "read through" to the specified multiset, and attempts to modify the returned multiset
     * result in an {@link UnsupportedOperationException}.
     *
     * <p>The returned multiset will be serializable if the specified multiset is serializable.
     *
     * @param sortedMultiset the sorted multiset for which an unmodifiable view is to be generated
     * @return an unmodifiable view of the multiset
     */

    public static <E extends Object> SortedMultiset<E> unmodifiableSortedMultiset(
            SortedMultiset<E> sortedMultiset ) {
        // it's in its own file so it can be emulated for GWT
        return new UnmodifiableSortedMultiset<E>(checkNotNull(sortedMultiset));
    }

    /**
     * Returns an immutable multiset entry with the specified element and count. The entry will be
     * serializable if {@code e} is.
     *
     * @param e the element to be associated with the returned entry
     * @param n the count to be associated with the returned entry
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public static <E extends Object> Multiset.Entry<E> immutableEntry(
            @ParametricNullness E e, int n ) {
        return new ImmutableEntry<E>(e, n);
    }

    /**
     * Returns a view of the elements of {@code unfiltered} that satisfy a predicate. The returned
     * multiset is a live view of {@code unfiltered}; changes to one affect the other.
     *
     * <p>The resulting multiset's iterators, and those of its {@code entrySet()} and {@code
     * elementSet()}, do not support {@code remove()}. However, all other multiset methods supported
     * by {@code unfiltered} are supported by the returned multiset. When given an element that
     * doesn't satisfy the predicate, the multiset's {@code add()} and {@code addAll()} methods throw
     * an {@link IllegalArgumentException}. When methods such as {@code removeAll()} and {@code
     * clear()} are called on the filtered multiset, only elements that satisfy the filter will be
     * removed from the underlying multiset.
     *
     * <p>The returned multiset isn't threadsafe or serializable, even if {@code unfiltered} is.
     *
     * <p>Many of the filtered multiset's methods, such as {@code size()}, iterate across every
     * element in the underlying multiset and determine which elements satisfy the filter. When a live
     * view is <i>not</i> needed, it may be faster to copy the returned multiset and use the copy.
     *
     * <p><b>Warning:</b> {@code predicate} must be <i>consistent with equals</i>, as documented at
     * {@link Predicate#apply}. Do not provide a predicate such as {@code
     * Predicates.instanceOf(ArrayList.class)}, which is inconsistent with equals. (See {@link
     * Iterables#filter(Iterable, Class)} for related functionality.)
     */

    public static <E extends Object> Multiset<E> filter(
            Multiset<E> unfiltered, Predicate<? super E> predicate ) {
        if (unfiltered instanceof FilteredMultiset) {
            // Support clear(), removeAll(), and retainAll() when filtering a filtered
            // collection.
            FilteredMultiset<E> filtered = (FilteredMultiset<E>) unfiltered;
            Predicate<E> combinedPredicate = PredicateUtil.and(filtered.predicate, predicate);
            return new FilteredMultiset<E>(filtered.unfiltered, combinedPredicate);
        }
        return new FilteredMultiset<E>(unfiltered, predicate);
    }

    /**
     * Returns the expected number of distinct elements given the specified elements. The number of
     * distinct elements is only computed if {@code elements} is an instance of {@code Multiset};
     * otherwise the default value of 11 is returned.
     */
    static int inferDistinctElements( Iterable<?> elements ) {
        if (elements instanceof Multiset) {
            return ((Multiset<?>) elements).elementSet().size();
        }
        return 11; // initial capacity will be rounded up to 16
    }

    /**
     * Returns an unmodifiable view of the union of two multisets. In the returned multiset, the count
     * of each element is the <i>maximum</i> of its counts in the two backing multisets. The iteration
     * order of the returned multiset matches that of the element set of {@code multiset1} followed by
     * the members of the element set of {@code multiset2} that are not contained in {@code
     * multiset1}, with repeated occurrences of the same element appearing consecutively.
     *
     * <p>Results are undefined if {@code multiset1} and {@code multiset2} are based on different
     * equivalence relations (as {@code HashMultiset} and {@code TreeMultiset} are).
     */

    public static <E extends Object> Multiset<E> union(
            final Multiset<? extends E> multiset1, final Multiset<? extends E> multiset2 ) {
        checkNotNull(multiset1);
        checkNotNull(multiset2);

        return new ViewMultiset<E>() {
            @Override
            public boolean contains( @CheckForNull Object element ) {
                return multiset1.contains(element) || multiset2.contains(element);
            }

            @Override
            public boolean isEmpty() {
                return multiset1.isEmpty() && multiset2.isEmpty();
            }

            @Override
            public int count( @CheckForNull Object element ) {
                return Math.max(multiset1.count(element), multiset2.count(element));
            }

            @Override
            Set<E> createElementSet() {
                return SetUtil.union(multiset1.elementSet(), multiset2.elementSet());
            }

            @Override
            Iterator<E> elementIterator() {
                throw new AssertionError("should never be called");
            }

            @Override
            Iterator<Entry<E>> entryIterator() {
                final Iterator<? extends Entry<? extends E>> iterator1 = multiset1.entrySet().iterator();
                final Iterator<? extends Entry<? extends E>> iterator2 = multiset2.entrySet().iterator();
                // TODO(lowasser): consider making the entries live views
                return new AbstractIterator<Entry<E>>() {
                    @Override
                    @CheckForNull
                    protected Entry<E> computeNext() {
                        if (iterator1.hasNext()) {
                            Entry<? extends E> entry1 = iterator1.next();
                            E element = entry1.getElement();
                            int count = Math.max(entry1.getCount(), multiset2.count(element));
                            return immutableEntry(element, count);
                        }
                        while (iterator2.hasNext()) {
                            Entry<? extends E> entry2 = iterator2.next();
                            E element = entry2.getElement();
                            if (!multiset1.contains(element)) {
                                return immutableEntry(element, entry2.getCount());
                            }
                        }
                        return endOfData();
                    }
                };
            }
        };
    }

    /**
     * Returns an unmodifiable view of the intersection of two multisets. In the returned multiset,
     * the count of each element is the <i>minimum</i> of its counts in the two backing multisets,
     * with elements that would have a count of 0 not included. The iteration order of the returned
     * multiset matches that of the element set of {@code multiset1}, with repeated occurrences of the
     * same element appearing consecutively.
     *
     * <p>Results are undefined if {@code multiset1} and {@code multiset2} are based on different
     * equivalence relations (as {@code HashMultiset} and {@code TreeMultiset} are).
     */
    @Deprecated
    public static <E extends Object> Multiset<E> intersection(
            final Multiset<E> multiset1, final Multiset<?> multiset2 ) {
        checkNotNull(multiset1);
        checkNotNull(multiset2);

        return new ViewMultiset<E>() {
            @Override
            public int count( @CheckForNull Object element ) {
                int count1 = multiset1.count(element);
                return (count1 == 0) ? 0 : Math.min(count1, multiset2.count(element));
            }

            @Override
            Set<E> createElementSet() {
                return SetUtil.intersection(multiset1.elementSet(), multiset2.elementSet());
            }

            @Override
            Iterator<E> elementIterator() {
                throw new AssertionError("should never be called");
            }

            @Override
            Iterator<Entry<E>> entryIterator() {
                final Iterator<Entry<E>> iterator1 = multiset1.entrySet().iterator();
                // TODO(lowasser): consider making the entries live views
                return new AbstractIterator<Entry<E>>() {
                    @Override
                    @CheckForNull
                    protected Entry<E> computeNext() {
                        while (iterator1.hasNext()) {
                            Entry<E> entry1 = iterator1.next();
                            E element = entry1.getElement();
                            int count = Math.min(entry1.getCount(), multiset2.count(element));
                            if (count > 0) {
                                return immutableEntry(element, count);
                            }
                        }
                        return endOfData();
                    }
                };
            }
        };
    }

    /**
     * Returns an unmodifiable view of the sum of two multisets. In the returned multiset, the count
     * of each element is the <i>sum</i> of its counts in the two backing multisets. The iteration
     * order of the returned multiset matches that of the element set of {@code multiset1} followed by
     * the members of the element set of {@code multiset2} that are not contained in {@code
     * multiset1}, with repeated occurrences of the same element appearing consecutively.
     *
     * <p>Results are undefined if {@code multiset1} and {@code multiset2} are based on different
     * equivalence relations (as {@code HashMultiset} and {@code TreeMultiset} are).
     */

    public static <E extends Object> Multiset<E> sum(
            final Multiset<? extends E> multiset1, final Multiset<? extends E> multiset2 ) {
        checkNotNull(multiset1);
        checkNotNull(multiset2);

        // TODO(lowasser): consider making the entries live views
        return new ViewMultiset<E>() {
            @Override
            public boolean contains( @CheckForNull Object element ) {
                return multiset1.contains(element) || multiset2.contains(element);
            }

            @Override
            public boolean isEmpty() {
                return multiset1.isEmpty() && multiset2.isEmpty();
            }

            @Override
            public int size() {
                return (int) NumberUtil.saturatedCast((long) (multiset1.size() + multiset2.size()), Integer.class);
            }

            @Override
            public int count( @CheckForNull Object element ) {
                return multiset1.count(element) + multiset2.count(element);
            }

            @Override
            Set<E> createElementSet() {
                return SetUtil.union(multiset1.elementSet(), multiset2.elementSet());
            }

            @Override
            Iterator<E> elementIterator() {
                throw new AssertionError("should never be called");
            }

            @Override
            Iterator<Entry<E>> entryIterator() {
                final Iterator<? extends Entry<? extends E>> iterator1 = multiset1.entrySet().iterator();
                final Iterator<? extends Entry<? extends E>> iterator2 = multiset2.entrySet().iterator();
                return new AbstractIterator<Entry<E>>() {
                    @Override
                    @CheckForNull
                    protected Entry<E> computeNext() {
                        if (iterator1.hasNext()) {
                            Entry<? extends E> entry1 = iterator1.next();
                            E element = entry1.getElement();
                            int count = entry1.getCount() + multiset2.count(element);
                            return immutableEntry(element, count);
                        }
                        while (iterator2.hasNext()) {
                            Entry<? extends E> entry2 = iterator2.next();
                            E element = entry2.getElement();
                            if (!multiset1.contains(element)) {
                                return immutableEntry(element, entry2.getCount());
                            }
                        }
                        return endOfData();
                    }
                };
            }
        };
    }

    /**
     * Returns an unmodifiable view of the difference of two multisets. In the returned multiset, the
     * count of each element is the result of the <i>zero-truncated subtraction</i> of its count in
     * the second multiset from its count in the first multiset, with elements that would have a count
     * of 0 not included. The iteration order of the returned multiset matches that of the element set
     * of {@code multiset1}, with repeated occurrences of the same element appearing consecutively.
     *
     * <p>Results are undefined if {@code multiset1} and {@code multiset2} are based on different
     * equivalence relations (as {@code HashMultiset} and {@code TreeMultiset} are).
     */

    public static <E extends Object> Multiset<E> difference(
            final Multiset<E> multiset1, final Multiset<?> multiset2 ) {
        checkNotNull(multiset1);
        checkNotNull(multiset2);

        // TODO(lowasser): consider making the entries live views
        return new ViewMultiset<E>() {
            @Override
            public int count( @CheckForNull Object element ) {
                int count1 = multiset1.count(element);
                return (count1 == 0) ? 0 : Math.max(0, count1 - multiset2.count(element));
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

            @Override
            Iterator<E> elementIterator() {
                final Iterator<Entry<E>> iterator1 = multiset1.entrySet().iterator();
                return new AbstractIterator<E>() {
                    @Override
                    @CheckForNull
                    protected E computeNext() {
                        while (iterator1.hasNext()) {
                            Entry<E> entry1 = iterator1.next();
                            E element = entry1.getElement();
                            if (entry1.getCount() > multiset2.count(element)) {
                                return element;
                            }
                        }
                        return endOfData();
                    }
                };
            }

            @Override
            Iterator<Entry<E>> entryIterator() {
                final Iterator<Entry<E>> iterator1 = multiset1.entrySet().iterator();
                return new AbstractIterator<Entry<E>>() {
                    @Override
                    @CheckForNull
                    protected Entry<E> computeNext() {
                        while (iterator1.hasNext()) {
                            Entry<E> entry1 = iterator1.next();
                            E element = entry1.getElement();
                            int count = entry1.getCount() - multiset2.count(element);
                            if (count > 0) {
                                return immutableEntry(element, count);
                            }
                        }
                        return endOfData();
                    }
                };
            }

            @Override
            int distinctElements() {
                return Iterators.size(entryIterator());
            }
        };
    }

    /**
     * Returns {@code true} if {@code subMultiset.count(o) <= superMultiset.count(o)} for all {@code
     * o}.
     */

    public static boolean containsOccurrences( Multiset<?> superMultiset, Multiset<?> subMultiset ) {
        checkNotNull(superMultiset);
        checkNotNull(subMultiset);
        for (Multiset.Entry<?> entry : subMultiset.entrySet()) {
            int superCount = superMultiset.count(entry.getElement());
            if (superCount < entry.getCount()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Modifies {@code multisetToModify} so that its count for an element {@code e} is at most {@code
     * multisetToRetain.count(e)}.
     *
     * <p>To be precise, {@code multisetToModify.count(e)} is set to {@code
     * Math.min(multisetToModify.count(e), multisetToRetain.count(e))}. This is similar to {@link
     * #intersection(Multiset, Multiset) intersection} {@code (multisetToModify, multisetToRetain)},
     * but mutates {@code multisetToModify} instead of returning a view.
     *
     * <p>In contrast, {@code multisetToModify.retainAll(multisetToRetain)} keeps all occurrences of
     * elements that appear at all in {@code multisetToRetain}, and deletes all occurrences of all
     * other elements.
     *
     * @return {@code true} if {@code multisetToModify} was changed as a result of this operation
     */

    public static boolean retainOccurrences(
            Multiset<?> multisetToModify, Multiset<?> multisetToRetain ) {
        return retainOccurrencesImpl(multisetToModify, multisetToRetain);
    }

    /**
     * Delegate implementation which cares about the element type.
     */
    private static <E extends Object> boolean retainOccurrencesImpl(
            Multiset<E> multisetToModify, Multiset<?> occurrencesToRetain ) {
        checkNotNull(multisetToModify);
        checkNotNull(occurrencesToRetain);
        // Avoiding ConcurrentModificationExceptions is tricky.
        Iterator<Entry<E>> entryIterator = multisetToModify.entrySet().iterator();
        boolean changed = false;
        while (entryIterator.hasNext()) {
            Entry<E> entry = entryIterator.next();
            int retainCount = occurrencesToRetain.count(entry.getElement());
            if (retainCount == 0) {
                entryIterator.remove();
                changed = true;
            } else if (retainCount < entry.getCount()) {
                multisetToModify.setCount(entry.getElement(), retainCount);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * For each occurrence of an element {@code e} in {@code occurrencesToRemove}, removes one
     * occurrence of {@code e} in {@code multisetToModify}.
     *
     * <p>Equivalently, this method modifies {@code multisetToModify} so that {@code
     * multisetToModify.count(e)} is set to {@code Math.max(0, multisetToModify.count(e) -
     * Iterables.frequency(occurrencesToRemove, e))}.
     *
     * <p>This is <i>not</i> the same as {@code multisetToModify.} {@link Multiset#removeAll
     * removeAll}{@code (occurrencesToRemove)}, which removes all occurrences of elements that appear
     * in {@code occurrencesToRemove}. However, this operation <i>is</i> equivalent to, albeit
     * sometimes more efficient than, the following:
     *
     * <pre>{@code
     * for (E e : occurrencesToRemove) {
     *   multisetToModify.remove(e);
     * }
     * }</pre>
     *
     * @return {@code true} if {@code multisetToModify} was changed as a result of this operation
     * <p>
     * Multiset})
     */

    public static boolean removeOccurrences(
            Multiset<?> multisetToModify, Iterable<?> occurrencesToRemove ) {
        if (occurrencesToRemove instanceof Multiset) {
            return removeOccurrences(multisetToModify, (Multiset<?>) occurrencesToRemove);
        } else {
            checkNotNull(multisetToModify);
            checkNotNull(occurrencesToRemove);
            boolean changed = false;
            for (Object o : occurrencesToRemove) {
                changed |= multisetToModify.remove(o);
            }
            return changed;
        }
    }

    /**
     * For each occurrence of an element {@code e} in {@code occurrencesToRemove}, removes one
     * occurrence of {@code e} in {@code multisetToModify}.
     *
     * <p>Equivalently, this method modifies {@code multisetToModify} so that {@code
     * multisetToModify.count(e)} is set to {@code Math.max(0, multisetToModify.count(e) -
     * occurrencesToRemove.count(e))}.
     *
     * <p>This is <i>not</i> the same as {@code multisetToModify.} {@link Multiset#removeAll
     * removeAll}{@code (occurrencesToRemove)}, which removes all occurrences of elements that appear
     * in {@code occurrencesToRemove}. However, this operation <i>is</i> equivalent to, albeit
     * sometimes more efficient than, the following:
     *
     * <pre>{@code
     * for (E e : occurrencesToRemove) {
     *   multisetToModify.remove(e);
     * }
     * }</pre>
     *
     * @return {@code true} if {@code multisetToModify} was changed as a result of this operation
     */

    public static boolean removeOccurrences(
            Multiset<?> multisetToModify, Multiset<?> occurrencesToRemove ) {
        checkNotNull(multisetToModify);
        checkNotNull(occurrencesToRemove);

        boolean changed = false;
        Iterator<? extends Entry<?>> entryIterator = multisetToModify.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Entry<?> entry = entryIterator.next();
            int removeCount = occurrencesToRemove.count(entry.getElement());
            if (removeCount >= entry.getCount()) {
                entryIterator.remove();
                changed = true;
            } else if (removeCount > 0) {
                multisetToModify.remove(entry.getElement(), removeCount);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * An implementation of {@link Multiset#equals}.
     */
    static boolean equalsImpl( Multiset<?> multiset, @CheckForNull Object object ) {
        if (object == multiset) {
            return true;
        }
        if (object instanceof Multiset) {
            Multiset<?> that = (Multiset<?>) object;
            /*
             * We can't simply check whether the entry sets are equal, since that
             * approach fails when a TreeMultiset has a comparator that returns 0
             * when passed unequal elements.
             */

            if (multiset.size() != that.size() || multiset.entrySet().size() != that.entrySet().size()) {
                return false;
            }
            for (Entry<?> entry : that.entrySet()) {
                if (multiset.count(entry.getElement()) != entry.getCount()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * An implementation of {@link Multiset#addAll}.
     */
    static <E extends Object> boolean addAllImpl(
            Multiset<E> self, Collection<? extends E> elements ) {
        checkNotNull(self);
        checkNotNull(elements);
        if (elements instanceof Multiset) {
            return addAllImpl(self, cast(elements));
        } else if (elements.isEmpty()) {
            return false;
        } else {
            return Iterators.addAll(self, elements.iterator());
        }
    }

    /**
     * A specialization of {@code addAllImpl} for when {@code elements} is itself a Multiset.
     */
    private static <E extends Object> boolean addAllImpl(
            Multiset<E> self, Multiset<? extends E> elements ) {
        if (elements.isEmpty()) {
            return false;
        }
        elements.forEachEntry(self::add);
        return true;
    }

    /**
     * An implementation of {@link Multiset#removeAll}.
     */
    static boolean removeAllImpl( Multiset<?> self, Collection<?> elementsToRemove ) {
        Collection<?> collection =
                (elementsToRemove instanceof Multiset)
                        ? ((Multiset<?>) elementsToRemove).elementSet()
                        : elementsToRemove;

        return self.elementSet().removeAll(collection);
    }

    /**
     * An implementation of {@link Multiset#retainAll}.
     */
    static boolean retainAllImpl( Multiset<?> self, Collection<?> elementsToRetain ) {
        checkNotNull(elementsToRetain);
        Collection<?> collection =
                (elementsToRetain instanceof Multiset)
                        ? ((Multiset<?>) elementsToRetain).elementSet()
                        : elementsToRetain;

        return self.elementSet().retainAll(collection);
    }

    /**
     * An implementation of {@link Multiset#setCount(Object, int)}.
     */
    static <E extends Object> int setCountImpl(
            Multiset<E> self, @ParametricNullness E element, int count ) {
        checkNonnegative(count, "count");

        int oldCount = self.count(element);

        int delta = count - oldCount;
        if (delta > 0) {
            self.add(element, delta);
        } else if (delta < 0) {
            self.remove(element, -delta);
        }

        return oldCount;
    }

    /**
     * An implementation of {@link Multiset#setCount(Object, int, int)}.
     */
    static <E extends Object> boolean setCountImpl(
            Multiset<E> self, @ParametricNullness E element, int oldCount, int newCount ) {
        checkNonnegative(oldCount, "oldCount");
        checkNonnegative(newCount, "newCount");

        if (self.count(element) == oldCount) {
            self.setCount(element, newCount);
            return true;
        } else {
            return false;
        }
    }

    static <E extends Object> Iterator<E> elementIterator(
            Iterator<Entry<E>> entryIterator ) {
        return new TransformedIterator<Entry<E>, E>(entryIterator) {
            @Override
            @ParametricNullness
            E transform( Entry<E> entry ) {
                return entry.getElement();
            }
        };
    }

    /**
     * An implementation of {@link Multiset#iterator}.
     */
    static <E extends Object> Iterator<E> iteratorImpl( Multiset<E> multiset ) {
        return new MultisetIteratorImpl<E>(multiset, multiset.entrySet().iterator());
    }

    static <E extends Object> Spliterator<E> spliteratorImpl( Multiset<E> multiset ) {
        Spliterator<Entry<E>> entrySpliterator = multiset.entrySet().spliterator();
        return SpliteratorUtil.flatMap(
                entrySpliterator,
                entry -> Collections.nCopies(entry.getCount(), entry.getElement()).spliterator(),
                Spliterator.SIZED
                        | (entrySpliterator.characteristics()
                        & (Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE)),
                multiset.size());
    }

    /**
     * An implementation of {@link Multiset#size}.
     */
    static int linearTimeSizeImpl( Multiset<?> multiset ) {
        long size = 0;
        for (Entry<?> entry : multiset.entrySet()) {
            size += entry.getCount();
        }
        return (int) NumberUtil.saturatedCast(size, Integer.class);
    }

    /**
     * Used to avoid http://bugs.sun.com/view_bug.do?bug_id=6558557
     */
    static <T extends Object> Multiset<T> cast( Iterable<T> iterable ) {
        return (Multiset<T>) iterable;
    }

    /**
     * Returns a copy of {@code multiset} as an {@link ImmutableMultiset} whose iteration order is
     * highest count first, with ties broken by the iteration order of the original multiset.
     */

    public static <E> ImmutableMultiset<E> copyHighestCountFirst( Multiset<E> multiset ) {
        Entry<E>[] entries = (Entry<E>[]) multiset.entrySet().toArray(new Entry[0]);
        Arrays.sort(entries, DecreasingCount.INSTANCE);
        return ImmutableMultiset.copyFromEntries(Arrays.asList(entries));
    }

    static class UnmodifiableMultiset<E extends Object> extends ForwardingMultiset<E>
            implements Serializable {
        private static final long serialVersionUID = 0;
        final Multiset<? extends E> delegate;
        @CheckForNull
        transient Set<E> elementSet;
        @CheckForNull
        transient Set<Entry<E>> entrySet;

        UnmodifiableMultiset( Multiset<? extends E> delegate ) {
            this.delegate = delegate;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Multiset<E> delegate() {
            // This is safe because all non-covariant methods are overridden
            return (Multiset<E>) delegate;
        }

        Set<E> createElementSet() {
            return Collections.unmodifiableSet(delegate.elementSet());
        }

        @Override
        public Set<E> elementSet() {
            Set<E> es = elementSet;
            return (es == null) ? elementSet = createElementSet() : es;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Set<Entry<E>> entrySet() {
            Set<Entry<E>> es = entrySet;
            return (es == null)
                    // Safe because the returned set is made unmodifiable and Entry
                    // itself is readonly
                    ? entrySet = (Set) Collections.unmodifiableSet(delegate.entrySet())
                    : es;
        }

        @Override
        public Iterator<E> iterator() {
            return Iterators.<E>unmodifiableIterator(delegate.iterator());
        }

        @Override
        public boolean add( @ParametricNullness E element ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int add( @ParametricNullness E element, int occurrences ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll( Collection<? extends E> elementsToAdd ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove( @CheckForNull Object element ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int remove( @CheckForNull Object element, int occurrences ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll( Collection<?> elementsToRemove ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll( Collection<?> elementsToRetain ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int setCount( @ParametricNullness E element, int count ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setCount( @ParametricNullness E element, int oldCount, int newCount ) {
            throw new UnsupportedOperationException();
        }
    }

    static class ImmutableEntry<E extends Object> extends AbstractEntry<E>
            implements Serializable {
        private static final long serialVersionUID = 0;
        @ParametricNullness
        private final E element;
        private final int count;

        ImmutableEntry( @ParametricNullness E element, int count ) {
            this.element = element;
            this.count = count;
            checkNonnegative(count, "count");
        }

        @Override
        @ParametricNullness
        public final E getElement() {
            return element;
        }

        @Override
        public final int getCount() {
            return count;
        }

        @CheckForNull
        public ImmutableEntry<E> nextInBucket() {
            return null;
        }
    }

    private static final class FilteredMultiset<E extends Object> extends ViewMultiset<E> {
        final Multiset<E> unfiltered;
        final Predicate<? super E> predicate;

        FilteredMultiset( Multiset<E> unfiltered, Predicate<? super E> predicate ) {
            this.unfiltered = checkNotNull(unfiltered);
            this.predicate = checkNotNull(predicate);
        }

        @Override
        public Iterator<E> iterator() {
            return Iterators.filter(unfiltered.iterator(), predicate);
        }

        @Override
        Set<E> createElementSet() {
            return SetUtil.filter(unfiltered.elementSet(), predicate);
        }

        @Override
        Iterator<E> elementIterator() {
            throw new AssertionError("should never be called");
        }

        @Override
        Set<Entry<E>> createEntrySet() {
            return SetUtil.filter(
                    unfiltered.entrySet(),
                    new Predicate<Entry<E>>() {
                        @Override
                        public boolean apply( Entry<E> entry ) {
                            return predicate.apply(entry.getElement());
                        }
                    });
        }

        @Override
        Iterator<Entry<E>> entryIterator() {
            throw new AssertionError("should never be called");
        }

        @Override
        public int count( @CheckForNull Object element ) {
            int count = unfiltered.count(element);
            if (count > 0) {
                @SuppressWarnings("unchecked") // element is equal to an E
                E e = (E) element;
                return predicate.apply(e) ? count : 0;
            }
            return 0;
        }

        @Override
        public int add( @ParametricNullness E element, int occurrences ) {
            checkArgument(
                    predicate.apply(element), "Element %s does not match predicate %s", element, predicate);
            return unfiltered.add(element, occurrences);
        }

        @Override
        public int remove( @CheckForNull Object element, int occurrences ) {
            checkNonnegative(occurrences, "occurrences");
            if (occurrences == 0) {
                return count(element);
            } else {
                return contains(element) ? unfiltered.remove(element, occurrences) : 0;
            }
        }
    }

    /**
     * Implementation of the {@code equals}, {@code hashCode}, and {@code toString} methods of {@link
     * Entry}.
     */
    abstract static class AbstractEntry<E extends Object> implements Entry<E> {
        /**
         * Indicates whether an object equals this entry, following the behavior specified in {@link
         * Entry#equals}.
         */
        @Override
        public boolean equals( @CheckForNull Object object ) {
            if (object instanceof Multiset.Entry) {
                Entry<?> that = (Entry<?>) object;
                return this.getCount() == that.getCount()
                        && ObjectUtil.equal(this.getElement(), that.getElement());
            }
            return false;
        }

        /**
         * Return this entry's hash code, following the behavior specified in {@link
         * Entry#hashCode}.
         */
        @Override
        public int hashCode() {
            E e = getElement();
            return ((e == null) ? 0 : e.hashCode()) ^ getCount();
        }

        /**
         * Returns a string representation of this multiset entry. The string representation consists of
         * the associated element if the associated count is one, and otherwise the associated element
         * followed by the characters " x " (space, x and space) followed by the count. Elements and
         * counts are converted to strings as by {@code String.valueOf}.
         */
        @Override
        public String toString() {
            String text = String.valueOf(getElement());
            int n = getCount();
            return (n == 1) ? text : (text + " x " + n);
        }
    }

    abstract static class ElementSet<E extends Object> extends SetUtil.ImprovedAbstractSet<E> {
        abstract Multiset<E> multiset();

        @Override
        public void clear() {
            multiset().clear();
        }

        @Override
        public boolean contains( @CheckForNull Object o ) {
            return multiset().contains(o);
        }

        @Override
        public boolean containsAll( Collection<?> c ) {
            return multiset().containsAll(c);
        }

        @Override
        public boolean isEmpty() {
            return multiset().isEmpty();
        }

        @Override
        public abstract Iterator<E> iterator();

        @Override
        public boolean remove( @CheckForNull Object o ) {
            return multiset().remove(o, Integer.MAX_VALUE) > 0;
        }

        @Override
        public int size() {
            return multiset().entrySet().size();
        }
    }

    abstract static class EntrySet<E extends Object>
            extends SetUtil.ImprovedAbstractSet<Entry<E>> {
        abstract Multiset<E> multiset();

        @Override
        public boolean contains( @CheckForNull Object o ) {
            if (o instanceof Entry) {
                /*
                 * The GWT compiler wrongly issues a warning here.
                 */
                @SuppressWarnings("cast")
                Entry<?> entry = (Entry<?>) o;
                if (entry.getCount() <= 0) {
                    return false;
                }
                int count = multiset().count(entry.getElement());
                return count == entry.getCount();
            }
            return false;
        }

        // GWT compiler warning; see contains().
        @SuppressWarnings("cast")
        @Override
        public boolean remove( @CheckForNull Object object ) {
            if (object instanceof Multiset.Entry) {
                Entry<?> entry = (Entry<?>) object;
                Object element = entry.getElement();
                int entryCount = entry.getCount();
                if (entryCount != 0) {
                    // Safe as long as we never add a new entry, which we won't.
                    // (Presumably it can still throw CCE/NPE but only if the underlying Multiset does.)
                    @SuppressWarnings({"unchecked", "nullness"})
                    Multiset<Object> multiset = (Multiset<Object>) multiset();
                    return multiset.setCount(element, entryCount, 0);
                }
            }
            return false;
        }

        @Override
        public void clear() {
            multiset().clear();
        }
    }

    static final class MultisetIteratorImpl<E extends Object> implements Iterator<E> {
        private final Multiset<E> multiset;
        private final Iterator<Entry<E>> entryIterator;
        @CheckForNull
        private Entry<E> currentEntry;

        /**
         * Count of subsequent elements equal to current element
         */
        private int laterCount;

        /**
         * Count of all elements equal to current element
         */
        private int totalCount;

        private boolean canRemove;

        MultisetIteratorImpl( Multiset<E> multiset, Iterator<Entry<E>> entryIterator ) {
            this.multiset = multiset;
            this.entryIterator = entryIterator;
        }

        @Override
        public boolean hasNext() {
            return laterCount > 0 || entryIterator.hasNext();
        }

        @Override
        @ParametricNullness
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (laterCount == 0) {
                currentEntry = entryIterator.next();
                totalCount = laterCount = currentEntry.getCount();
            }
            laterCount--;
            canRemove = true;
            /*
             * requireNonNull is safe because laterCount starts at 0, forcing us to initialize
             * currentEntry above. After that, we never clear it.
             */
            return requireNonNull(currentEntry).getElement();
        }

        @Override
        public void remove() {
            checkRemove(canRemove);
            if (totalCount == 1) {
                entryIterator.remove();
            } else {
                /*
                 * requireNonNull is safe because canRemove is set to true only after we initialize
                 * currentEntry (which we never subsequently clear).
                 */
                multiset.remove(requireNonNull(currentEntry).getElement());
            }
            totalCount--;
            canRemove = false;
        }
    }

    private static final class DecreasingCount implements Comparator<Entry<?>> {
        static final DecreasingCount INSTANCE = new DecreasingCount();

        @Override
        public int compare( Entry<?> entry1, Entry<?> entry2 ) {
            return entry2.getCount() - entry1.getCount(); // subtracting two nonnegative integers
        }
    }

    /**
     * An {@link AbstractMultiset} with additional default implementations, some of them linear-time
     * implementations in terms of {@code elementSet} and {@code entrySet}.
     */
    private abstract static class ViewMultiset<E extends Object>
            extends AbstractMultiset<E> {
        @Override
        public int size() {
            return linearTimeSizeImpl(this);
        }

        @Override
        public void clear() {
            elementSet().clear();
        }

        @Override
        public Iterator<E> iterator() {
            return iteratorImpl(this);
        }

        @Override
        int distinctElements() {
            return elementSet().size();
        }
    }
}