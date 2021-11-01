

package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Iterator;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/** An ordering that uses the reverse of a given order. */


final class ReverseOrdering<T extends Object> extends Ordering<T>
    implements Serializable {
  final Ordering<? super T> forwardOrder;

  ReverseOrdering(Ordering<? super T> forwardOrder) {
    this.forwardOrder = checkNotNull(forwardOrder);
  }

  @Override
  public int compare(@ParametricNullness T a, @ParametricNullness T b) {
    return forwardOrder.compare(b, a);
  }

  @SuppressWarnings("unchecked") // how to explain?
  @Override
  public <S extends T> Ordering<S> reverse() {
    return (Ordering<S>) forwardOrder;
  }

  // Override the min/max methods to "hoist" delegation outside loops

  @Override
  public <E extends T> E min(@ParametricNullness E a, @ParametricNullness E b) {
    return forwardOrder.max(a, b);
  }

  @Override
  public <E extends T> E min(
      @ParametricNullness E a, @ParametricNullness E b, @ParametricNullness E c, E... rest) {
    return forwardOrder.max(a, b, c, rest);
  }

  @Override
  public <E extends T> E min(Iterator<E> iterator) {
    return forwardOrder.max(iterator);
  }

  @Override
  public <E extends T> E min(Iterable<E> iterable) {
    return forwardOrder.max(iterable);
  }

  @Override
  public <E extends T> E max(@ParametricNullness E a, @ParametricNullness E b) {
    return forwardOrder.min(a, b);
  }

  @Override
  public <E extends T> E max(
      @ParametricNullness E a, @ParametricNullness E b, @ParametricNullness E c, E... rest) {
    return forwardOrder.min(a, b, c, rest);
  }

  @Override
  public <E extends T> E max(Iterator<E> iterator) {
    return forwardOrder.min(iterator);
  }

  @Override
  public <E extends T> E max(Iterable<E> iterable) {
    return forwardOrder.min(iterable);
  }

  @Override
  public int hashCode() {
    return -forwardOrder.hashCode();
  }

  @Override
  public boolean equals(@CheckForNull Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof ReverseOrdering) {
      ReverseOrdering<?> that = (ReverseOrdering<?>) object;
      return this.forwardOrder.equals(that.forwardOrder);
    }
    return false;
  }

  @Override
  public String toString() {
    return forwardOrder + ".reverse()";
  }

  private static final long serialVersionUID = 0;
}
