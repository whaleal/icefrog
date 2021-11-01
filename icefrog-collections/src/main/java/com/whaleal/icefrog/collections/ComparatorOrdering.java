

package com.whaleal.icefrog.collections;



import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Comparator;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/** An ordering for a pre-existing comparator. */


final class ComparatorOrdering<T extends Object> extends Ordering<T>
    implements Serializable {
  final Comparator<T> comparator;

  ComparatorOrdering(Comparator<T> comparator) {
    this.comparator = checkNotNull(comparator);
  }

  @Override
  public int compare(@ParametricNullness T a, @ParametricNullness T b) {
    return comparator.compare(a, b);
  }

  @Override
  public boolean equals(@CheckForNull Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof ComparatorOrdering) {
      ComparatorOrdering<?> that = (ComparatorOrdering<?>) object;
      return this.comparator.equals(that.comparator);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return comparator.hashCode();
  }

  @Override
  public String toString() {
    return comparator.toString();
  }

  private static final long serialVersionUID = 0;
}
