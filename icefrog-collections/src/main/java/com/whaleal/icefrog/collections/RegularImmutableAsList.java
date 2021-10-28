

package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.function.Consumer;




/**
 * An {@link ImmutableAsList} implementation specialized for when the delegate collection is already
 * backed by an {@code ImmutableList} or array.
 *
 * 
 */

@SuppressWarnings("serial") // uses writeReplace, not default serialization

class RegularImmutableAsList<E> extends ImmutableAsList<E> {
  private final ImmutableCollection<E> delegate;
  private final ImmutableList<? extends E> delegateList;

  RegularImmutableAsList(ImmutableCollection<E> delegate, ImmutableList<? extends E> delegateList) {
    this.delegate = delegate;
    this.delegateList = delegateList;
  }

  RegularImmutableAsList(ImmutableCollection<E> delegate, Object[] array) {
    this(delegate, ImmutableList.asImmutableList(array));
  }

  @Override
  ImmutableCollection<E> delegateCollection() {
    return delegate;
  }

  ImmutableList<? extends E> delegateList() {
    return delegateList;
  }

  @SuppressWarnings("unchecked") // safe covariant cast!
  @Override
  public UnmodifiableListIterator<E> listIterator(int index) {
    return (UnmodifiableListIterator<E>) delegateList.listIterator(index);
  }

 // not present in emulated superclass
  @Override
  public void forEach(Consumer<? super E> action) {
    delegateList.forEach(action);
  }

 // not present in emulated superclass
  @Override
  int copyIntoArray(Object[] dst, int offset) {
    return delegateList.copyIntoArray(dst, offset);
  }

  @Override
  @CheckForNull
  Object[] internalArray() {
    return delegateList.internalArray();
  }

  @Override
  int internalArrayStart() {
    return delegateList.internalArrayStart();
  }

  @Override
  int internalArrayEnd() {
    return delegateList.internalArrayEnd();
  }

  @Override
  public E get(int index) {
    return delegateList.get(index);
  }
}
