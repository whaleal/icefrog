

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import static com.whaleal.icefrog.collections.ForwardingSortedMap.unsafeCompare;


/**
 * A sorted set which forwards all its method calls to another sorted set. Subclasses should
 * override one or more methods to modify the behavior of the backing sorted set as desired per the
 * <a href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingSortedSet} forward <i>indiscriminately</i> to
 * the methods of the delegate. For example, overriding {@link #add} alone <i>will not</i> change
 * the behavior of {@link #addAll}, which can lead to unexpected behavior. In this case, you should
 * override {@code addAll} as well, either providing your own implementation, or delegating to the
 * provided {@code standardAddAll} method.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingSortedSet}.
 *
 * <p>Each of the {@code standard} methods, where appropriate, uses the set's comparator (or the
 * natural ordering of the elements, if there is no comparator) to test element equality. As a
 * result, if the comparator is not consistent with equals, some of the standard implementations may
 * violate the {@code Set} contract.
 *
 * <p>The {@code standard} methods and the collection views they return are not guaranteed to be
 * thread-safe, even when all of the methods that they depend on are thread-safe.
 *
 * @author Mike Bostock
 * @author Louis Wasserman
 * 
 */


public abstract class ForwardingSortedSet<E extends Object> extends ForwardingSet<E>
    implements SortedSet<E> {

  /** Constructor for use by subclasses. */
  protected ForwardingSortedSet() {}

  @Override
  protected abstract SortedSet<E> delegate();

  @Override
  @CheckForNull
  public Comparator<? super E> comparator() {
    return delegate().comparator();
  }

  @Override
  @ParametricNullness
  public E first() {
    return delegate().first();
  }

  @Override
  public SortedSet<E> headSet(@ParametricNullness E toElement) {
    return delegate().headSet(toElement);
  }

  @Override
  @ParametricNullness
  public E last() {
    return delegate().last();
  }

  @Override
  public SortedSet<E> subSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
    return delegate().subSet(fromElement, toElement);
  }

  @Override
  public SortedSet<E> tailSet(@ParametricNullness E fromElement) {
    return delegate().tailSet(fromElement);
  }

  /**
   * A sensible definition of {@link #contains} in terms of the {@code first()} method of {@link
   * #tailSet}. If you override {@link #tailSet}, you may wish to override {@link #contains} to
   * forward to this implementation.
   *
   * 
   */
  @Override

  protected boolean standardContains(@CheckForNull Object object) {
    try {
      // any ClassCastExceptions and NullPointerExceptions are caught
      @SuppressWarnings({"unchecked", "nullness"})
      SortedSet<Object> self = (SortedSet<Object>) this;
      Object ceiling = self.tailSet(object).first();
      return unsafeCompare(comparator(), ceiling, object) == 0;
    } catch (ClassCastException | NoSuchElementException | NullPointerException e) {
      return false;
    }
  }

  /**
   * A sensible definition of {@link #remove} in terms of the {@code iterator()} method of {@link
   * #tailSet}. If you override {@link #tailSet}, you may wish to override {@link #remove} to
   * forward to this implementation.
   *
   * 
   */
  @Override

  protected boolean standardRemove(@CheckForNull Object object) {
    try {
      // any ClassCastExceptions and NullPointerExceptions are caught
      @SuppressWarnings({"unchecked", "nullness"})
      SortedSet<Object> self = (SortedSet<Object>) this;
      Iterator<?> iterator = self.tailSet(object).iterator();
      if (iterator.hasNext()) {
        Object ceiling = iterator.next();
        if (unsafeCompare(comparator(), ceiling, object) == 0) {
          iterator.remove();
          return true;
        }
      }
    } catch (ClassCastException | NullPointerException e) {
      return false;
    }
    return false;
  }

  /**
   * A sensible default implementation of {@link #subSet(Object, Object)} in terms of {@link
   * #headSet(Object)} and {@link #tailSet(Object)}. In some situations, you may wish to override
   * {@link #subSet(Object, Object)} to forward to this implementation.
   *
   * 
   */

  protected SortedSet<E> standardSubSet(
      @ParametricNullness E fromElement, @ParametricNullness E toElement) {
    return tailSet(fromElement).headSet(toElement);
  }
}
