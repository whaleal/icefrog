

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.*;




/**
 * A {@link Multiset} which maintains the ordering of its elements, according to either their
 * natural order or an explicit {@link Comparator}. This order is reflected when iterating over the
 * sorted multiset, either directly, or through its {@code elementSet} or {@code entrySet} views. In
 * all cases, this implementation uses {@link Comparable#compareTo} or {@link Comparator#compare}
 * instead of {@link Object#equals} to determine equivalence of instances.
 *
 * <p><b>Warning:</b> The comparison must be <i>consistent with equals</i> as explained by the
 * {@link Comparable} class specification. Otherwise, the resulting multiset will violate the {@link
 * Collection} contract, which is specified in terms of {@link Object#equals}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#multiset"> {@code
 * Multiset}</a>.
 *
 * 
 * 
 */


public interface SortedMultiset<E extends Object>
    extends SortedMultisetBridge<E>, SortedIterable<E> {
  /**
   * Returns the comparator that orders this multiset, or {@link Ordering#natural()} if the natural
   * ordering of the elements is used.
   */
  @Override
  Comparator<? super E> comparator();

  /**
   * Returns the entry of the first element in this multiset, or {@code null} if this multiset is
   * empty.
   */
  @CheckForNull
  Entry<E> firstEntry();

  /**
   * Returns the entry of the last element in this multiset, or {@code null} if this multiset is
   * empty.
   */
  @CheckForNull
  Entry<E> lastEntry();

  /**
   * Returns and removes the entry associated with the lowest element in this multiset, or returns
   * {@code null} if this multiset is empty.
   */
  @CheckForNull
  Entry<E> pollFirstEntry();

  /**
   * Returns and removes the entry associated with the greatest element in this multiset, or returns
   * {@code null} if this multiset is empty.
   */
  @CheckForNull
  Entry<E> pollLastEntry();

  /**
   * Returns a {@link NavigableSet} view of the distinct elements in this multiset.
   *
   * 
   */
  @Override
  NavigableSet<E> elementSet();

  /**
   * {@inheritDoc}
   *
   * <p>The {@code entrySet}'s iterator returns entries in ascending element order according to the
   * this multiset's comparator.
   */
  @Override
  Set<Entry<E>> entrySet();

  /**
   * {@inheritDoc}
   *
   * <p>The iterator returns the elements in ascending order according to this multiset's
   * comparator.
   */
  @Override
  Iterator<E> iterator();

  /**
   * Returns a descending view of this multiset. Modifications made to either map will be reflected
   * in the other.
   */
  SortedMultiset<E> descendingMultiset();

  /**
   * Returns a view of this multiset restricted to the elements less than {@code upperBound},
   * optionally including {@code upperBound} itself. The returned multiset is a view of this
   * multiset, so changes to one will be reflected in the other. The returned multiset supports all
   * operations that this multiset supports.
   *
   * <p>The returned multiset will throw an {@link IllegalArgumentException} on attempts to add
   * elements outside its range.
   */
  SortedMultiset<E> headMultiset(@ParametricNullness E upperBound, BoundType boundType);

  /**
   * Returns a view of this multiset restricted to the range between {@code lowerBound} and {@code
   * upperBound}. The returned multiset is a view of this multiset, so changes to one will be
   * reflected in the other. The returned multiset supports all operations that this multiset
   * supports.
   *
   * <p>The returned multiset will throw an {@link IllegalArgumentException} on attempts to add
   * elements outside its range.
   *
   * <p>This method is equivalent to {@code tailMultiset(lowerBound,
   * lowerBoundType).headMultiset(upperBound, upperBoundType)}.
   */
  SortedMultiset<E> subMultiset(
      @ParametricNullness E lowerBound,
      BoundType lowerBoundType,
      @ParametricNullness E upperBound,
      BoundType upperBoundType);

  /**
   * Returns a view of this multiset restricted to the elements greater than {@code lowerBound},
   * optionally including {@code lowerBound} itself. The returned multiset is a view of this
   * multiset, so changes to one will be reflected in the other. The returned multiset supports all
   * operations that this multiset supports.
   *
   * <p>The returned multiset will throw an {@link IllegalArgumentException} on attempts to add
   * elements outside its range.
   */
  SortedMultiset<E> tailMultiset(@ParametricNullness E lowerBound, BoundType boundType);
}
