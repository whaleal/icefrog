

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;





import javax.annotation.CheckForNull;

/**
 * A descending wrapper around an {@code ImmutableSortedMultiset}
 *
 * 
 */
@SuppressWarnings("serial") // uses writeReplace, not default serialization


final class DescendingImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {
  private final transient ImmutableSortedMultiset<E> forward;

  DescendingImmutableSortedMultiset(ImmutableSortedMultiset<E> forward) {
    this.forward = forward;
  }

  @Override
  public int count(@CheckForNull Object element) {
    return forward.count(element);
  }

  @Override
  @CheckForNull
  public Multiset.Entry<E> firstEntry() {
    return forward.lastEntry();
  }

  @Override
  @CheckForNull
  public Multiset.Entry<E> lastEntry() {
    return forward.firstEntry();
  }

  @Override
  public int size() {
    return forward.size();
  }

  @Override
  public ImmutableSortedSet<E> elementSet() {
    return forward.elementSet().descendingSet();
  }

  @Override
  Multiset.Entry<E> getEntry(int index) {
    return forward.entrySet().asList().reverse().get(index);
  }

  @Override
  public ImmutableSortedMultiset<E> descendingMultiset() {
    return forward;
  }

  @Override
  public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType) {
    return forward.tailMultiset(upperBound, boundType).descendingMultiset();
  }

  @Override
  public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType) {
    return forward.headMultiset(lowerBound, boundType).descendingMultiset();
  }

  @Override
  boolean isPartialView() {
    return forward.isPartialView();
  }
}
