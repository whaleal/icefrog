

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.lang.Preconditions;

import java.util.Collections;
import java.util.Spliterator;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;

/**
 * Implementation of {@link ImmutableList} with exactly one element.
 *
 * @author Hayward Chan
 */

@SuppressWarnings("serial") // uses writeReplace(), not default serialization

final class SingletonImmutableList<E> extends ImmutableList<E> {

  final transient E element;

  SingletonImmutableList(E element) {
    this.element = checkNotNull(element);
  }

  @Override
  public E get(int index) {
    Preconditions.checkElementIndex(index, 1);
    return element;
  }

  @Override
  public UnmodifiableIterator<E> iterator() {
    return Iterators.singletonIterator(element);
  }

  @Override
  public Spliterator<E> spliterator() {
    return Collections.singleton(element).spliterator();
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public ImmutableList<E> subList(int fromIndex, int toIndex) {
    Preconditions.checkPositionIndexes(fromIndex, toIndex, 1);
    return (fromIndex == toIndex) ? ImmutableList.of() : this;
  }

  @Override
  public String toString() {
    return '[' + element.toString() + ']';
  }

  @Override
  boolean isPartialView() {
    return false;
  }
}
