

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.util.Spliterator;
import java.util.Spliterators;




/**
 * Implementation of {@link ImmutableSet} with two or more elements.
 *
 *
 */

@SuppressWarnings("serial") // uses writeReplace(), not default serialization

final class RegularImmutableSet<E> extends ImmutableSet.CachingAsList<E> {
  private static final Object[] EMPTY_ARRAY = new Object[0];
  static final RegularImmutableSet<Object> EMPTY =
      new RegularImmutableSet<>(EMPTY_ARRAY, 0, EMPTY_ARRAY, 0);

  private final transient Object[] elements;
  private final transient int hashCode;
  // the same values as `elements` in hashed positions (plus nulls)
  final transient Object[] table;
  // 'and' with an int to get a valid table index.
  private final transient int mask;

  RegularImmutableSet(Object[] elements, int hashCode, Object[] table, int mask) {
    this.elements = elements;
    this.hashCode = hashCode;
    this.table = table;
    this.mask = mask;
  }

  @Override
  public boolean contains(@CheckForNull Object target) {
    Object[] table = this.table;
    if (target == null || table.length == 0) {
      return false;
    }
    for (int i = ObjectUtil.hashCode(target); ; i++) {
      i &= mask;
      Object candidate = table[i];
      if (candidate == null) {
        return false;
      } else if (candidate.equals(target)) {
        return true;
      }
    }
  }

  @Override
  public int size() {
    return elements.length;
  }

  @Override
  public UnmodifiableIterator<E> iterator() {
    return (UnmodifiableIterator<E>) Iterators.forArray(elements);
  }

  @Override
  public Spliterator<E> spliterator() {
    return Spliterators.spliterator(elements, SPLITERATOR_CHARACTERISTICS);
  }

  @Override
  Object[] internalArray() {
    return elements;
  }

  @Override
  int internalArrayStart() {
    return 0;
  }

  @Override
  int internalArrayEnd() {
    return elements.length;
  }

  @Override
  int copyIntoArray(Object[] dst, int offset) {
    System.arraycopy(elements, 0, dst, offset, elements.length);
    return offset + elements.length;
  }

  @Override
  ImmutableList<E> createAsList() {
    return (table.length == 0)
        ? ImmutableList.of()
        : new RegularImmutableAsList<E>(this, elements);
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  boolean isHashCodeFast() {
    return true;
  }
}
