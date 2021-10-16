

package com.whaleal.icefrog.collections;


import javax.annotation.CheckForNull;
import java.util.Iterator;

/**
 * Skeletal implementation of {@link ImmutableSortedSet#descendingSet()}.
 *
 * @author Louis Wasserman
 */


final class DescendingImmutableSortedSet<E> extends ImmutableSortedSet<E> {
  private final ImmutableSortedSet<E> forward;

  DescendingImmutableSortedSet(ImmutableSortedSet<E> forward) {
    super(Ordering.from(forward.comparator()).reverse());
    this.forward = forward;
  }

  @Override
  public boolean contains(@CheckForNull Object object) {
    return forward.contains(object);
  }

  @Override
  public int size() {
    return forward.size();
  }

  @Override
  public Iterator<E> iterator() {
    return forward.descendingIterator();
  }

  @Override
  ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive) {
    return forward.tailSet(toElement, inclusive).descendingSet();
  }

  @Override
  ImmutableSortedSet<E> subSetImpl(
      E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
    return forward.subSet(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
  }

  @Override
  ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive) {
    return forward.headSet(fromElement, inclusive).descendingSet();
  }

  @Override

  public ImmutableSortedSet<E> descendingSet() {
    return forward;
  }

  @Override

  public Iterator<E> descendingIterator() {
    return forward.iterator();
  }

  @Override

  ImmutableSortedSet<E> createDescendingSet() {
    throw new AssertionError("should never be called");
  }

  @Override
  @CheckForNull
  public E lower(E element) {
    return forward.higher(element);
  }

  @Override
  @CheckForNull
  public E floor(E element) {
    return forward.ceiling(element);
  }

  @Override
  @CheckForNull
  public E ceiling(E element) {
    return forward.floor(element);
  }

  @Override
  @CheckForNull
  public E higher(E element) {
    return forward.lower(element);
  }

  @Override
  int indexOf(@CheckForNull Object target) {
    int index = forward.indexOf(target);
    if (index == -1) {
      return index;
    } else {
      return size() - 1 - index;
    }
  }

  @Override
  boolean isPartialView() {
    return forward.isPartialView();
  }
}
