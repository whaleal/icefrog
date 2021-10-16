

package com.whaleal.icefrog.collections;


import java.util.ListIterator;

/**
 * A list iterator that does not support {@link #remove}, {@link #add}, or {@link #set}.
 *
 * @author Louis Wasserman
 */


public abstract class UnmodifiableListIterator<E extends Object>
        extends UnmodifiableIterator<E> implements ListIterator<E> {
  /** Constructor for use by subclasses. */
  protected UnmodifiableListIterator() {}

  /**
   * Guaranteed to throw an exception and leave the underlying data unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  @Deprecated
  @Override
  public final void add(@ParametricNullness E e) {
    throw new UnsupportedOperationException();
  }

  /**
   * Guaranteed to throw an exception and leave the underlying data unmodified.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Unsupported operation.
   */
  @Deprecated
  @Override
  public final void set(@ParametricNullness E e) {
    throw new UnsupportedOperationException();
  }
}
