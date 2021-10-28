

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.NumberUtil;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.function.ObjIntConsumer;

import static com.whaleal.icefrog.collections.BoundType.CLOSED;
import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;
import static com.whaleal.icefrog.core.lang.Preconditions.checkPositionIndexes;

/**
 * An immutable sorted multiset with one or more distinct elements.
 *
 * 
 */
@SuppressWarnings("serial") // uses writeReplace, not default serialization
final class RegularImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {
  private static final long[] ZERO_CUMULATIVE_COUNTS = {0};

  static final ImmutableSortedMultiset<Comparable> NATURAL_EMPTY_MULTISET =
      new RegularImmutableSortedMultiset<>(Ordering.natural());

  final transient RegularImmutableSortedSet<E> elementSet;
  private final transient long[] cumulativeCounts;
  private final transient int offset;
  private final transient int length;

  RegularImmutableSortedMultiset(Comparator<? super E> comparator) {
    this.elementSet = ImmutableSortedSet.emptySet(comparator);
    this.cumulativeCounts = ZERO_CUMULATIVE_COUNTS;
    this.offset = 0;
    this.length = 0;
  }

  RegularImmutableSortedMultiset(
      RegularImmutableSortedSet<E> elementSet, long[] cumulativeCounts, int offset, int length) {
    this.elementSet = elementSet;
    this.cumulativeCounts = cumulativeCounts;
    this.offset = offset;
    this.length = length;
  }

  private int getCount(int index) {
    return (int) (cumulativeCounts[offset + index + 1] - cumulativeCounts[offset + index]);
  }

  @Override
  Multiset.Entry<E> getEntry(int index) {
    return Multisets.immutableEntry(elementSet.asList().get(index), getCount(index));
  }

  @Override
  public void forEachEntry(ObjIntConsumer<? super E> action) {
    checkNotNull(action);
    for (int i = 0; i < length; i++) {
      action.accept(elementSet.asList().get(i), getCount(i));
    }
  }

  @Override
  @CheckForNull
  public Multiset.Entry<E> firstEntry() {
    return isEmpty() ? null : getEntry(0);
  }

  @Override
  @CheckForNull
  public Multiset.Entry<E> lastEntry() {
    return isEmpty() ? null : getEntry(length - 1);
  }

  @Override
  public int count(@CheckForNull Object element) {
    int index = elementSet.indexOf(element);
    return (index >= 0) ? getCount(index) : 0;
  }

  @Override
  public int size() {
    long size = cumulativeCounts[offset + length] - cumulativeCounts[offset];
    return (int)NumberUtil.saturatedCast(size,Integer.class);
  }

  @Override
  public ImmutableSortedSet<E> elementSet() {
    return elementSet;
  }

  @Override
  public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType) {
    return getSubMultiset(0, elementSet.headIndex(upperBound, checkNotNull(boundType) == CLOSED));
  }

  @Override
  public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType) {
    return getSubMultiset(
        elementSet.tailIndex(lowerBound, checkNotNull(boundType) == CLOSED), length);
  }

  ImmutableSortedMultiset<E> getSubMultiset(int from, int to) {
    checkPositionIndexes(from, to, length);
    if (from == to) {
      return emptyMultiset(comparator());
    } else if (from == 0 && to == length) {
      return this;
    } else {
      RegularImmutableSortedSet<E> subElementSet = elementSet.getSubSet(from, to);
      return new RegularImmutableSortedMultiset<E>(
          subElementSet, cumulativeCounts, offset + from, to - from);
    }
  }

  @Override
  boolean isPartialView() {
    return offset > 0 || length < cumulativeCounts.length - 1;
  }
}
