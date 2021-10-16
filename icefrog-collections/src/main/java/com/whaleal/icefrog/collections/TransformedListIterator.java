

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;



import java.util.ListIterator;
import java.util.function.Function;


/**
 * An iterator that transforms a backing list iterator; for internal use. This avoids the object
 * overhead of constructing a {@link Function} for internal methods.
 *
 * @author Louis Wasserman
 */


abstract class TransformedListIterator<F extends Object, T extends Object>
    extends TransformedIterator<F, T> implements ListIterator<T> {
  TransformedListIterator(ListIterator<? extends F> backingIterator) {
    super(backingIterator);
  }

  private ListIterator<? extends F> backingIterator() {
    return Iterators.cast(backingIterator);
  }

  @Override
  public final boolean hasPrevious() {
    return backingIterator().hasPrevious();
  }

  @Override
  @ParametricNullness
  public final T previous() {
    return transform(backingIterator().previous());
  }

  @Override
  public final int nextIndex() {
    return backingIterator().nextIndex();
  }

  @Override
  public final int previousIndex() {
    return backingIterator().previousIndex();
  }

  @Override
  public void set(@ParametricNullness T element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(@ParametricNullness T element) {
    throw new UnsupportedOperationException();
  }
}
