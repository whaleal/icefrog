

package com.whaleal.icefrog.collections;

import java.util.Spliterator;
import java.util.Spliterators;






/**
 * Implementation of {@link ImmutableList} backed by a simple array.
 *
 *
 */

@SuppressWarnings("serial") // uses writeReplace(), not default serialization

class RegularImmutableList<E> extends ImmutableList<E> {
  static final ImmutableList<Object> EMPTY = new RegularImmutableList<>(new Object[0]);

  final transient Object[] array;

  RegularImmutableList(Object[] array) {
    this.array = array;
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override
  Object[] internalArray() {
    return array;
  }

  @Override
  int internalArrayStart() {
    return 0;
  }

  @Override
  int internalArrayEnd() {
    return array.length;
  }

  @Override
  int copyIntoArray(Object[] dst, int dstOff) {
    System.arraycopy(array, 0, dst, dstOff, array.length);
    return dstOff + array.length;
  }

  // The fake cast to E is safe because the creation methods only allow E's
  @Override
  @SuppressWarnings("unchecked")
  public E get(int index) {
    return (E) array[index];
  }

  @SuppressWarnings("unchecked")
  @Override
  public UnmodifiableListIterator<E> listIterator(int index) {
    // for performance
    // The fake cast to E is safe because the creation methods only allow E's
    return (UnmodifiableListIterator<E>) Iterators.forArray(array, 0, array.length, index);
  }

  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(array, SPLITERATOR_CHARACTERISTICS);
  }

  // TODO(lowasser): benchmark optimizations for equals() and see if they're worthwhile
}
