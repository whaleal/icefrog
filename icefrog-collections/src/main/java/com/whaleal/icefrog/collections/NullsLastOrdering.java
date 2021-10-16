

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.io.Serializable;


/** An ordering that treats {@code null} as greater than all other values. */


final class NullsLastOrdering<T extends Object> extends Ordering<T>
    implements Serializable {
  final Ordering<? super T> ordering;

  NullsLastOrdering(Ordering<? super T> ordering) {
    this.ordering = ordering;
  }

  @Override
  public int compare(@CheckForNull T left, @CheckForNull T right) {
    if (left == right) {
      return 0;
    }
    if (left == null) {
      return LEFT_IS_GREATER;
    }
    if (right == null) {
      return RIGHT_IS_GREATER;
    }
    return ordering.compare(left, right);
  }

  @Override
  @SuppressWarnings("nullness") // should be safe, but not sure if we can avoid the warning
  public <S extends T> Ordering<S> reverse() {
    // ordering.reverse() might be optimized, so let it do its thing
    return ordering.reverse().nullsFirst();
  }

  @Override
  public <S extends T> Ordering<S> nullsFirst() {
    return ordering.nullsFirst();
  }

  @SuppressWarnings("unchecked") // still need the right way to explain this
  @Override
  public <S extends T> Ordering<S> nullsLast() {
    return (Ordering<S>) this;
  }

  @Override
  public boolean equals(@CheckForNull Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof NullsLastOrdering) {
      NullsLastOrdering<?> that = (NullsLastOrdering<?>) object;
      return this.ordering.equals(that.ordering);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return ordering.hashCode() ^ -921210296; // meaningless
  }

  @Override
  public String toString() {
    return ordering + ".nullsLast()";
  }

  private static final long serialVersionUID = 0;
}
