

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;



import java.util.ListIterator;


/**
 * A list iterator which forwards all its method calls to another list iterator. Subclasses should
 * override one or more methods to modify the behavior of the backing iterator as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>{@code default} method warning:</b> This class forwards calls to <i>only some</i> {@code
 * default} methods. Specifically, it forwards calls only for methods that existed <a
 * href="https://docs.oracle.com/javase/7/docs/api/java/util/ListIterator.html">before {@code
 * default} methods were introduced</a>. For newer methods, like {@code forEachRemaining}, it
 * inherits their default implementations. When those implementations invoke methods, they invoke
 * methods on the {@code ForwardingListIterator}.
 *
 * @author Mike Bostock
 *
 */


public abstract class ForwardingListIterator<E extends Object>
    extends ForwardingIterator<E> implements ListIterator<E> {

  /** Constructor for use by subclasses. */
  protected ForwardingListIterator() {}

  @Override
  protected abstract ListIterator<E> delegate();

  @Override
  public void add(@ParametricNullness E element) {
    delegate().add(element);
  }

  @Override
  public boolean hasPrevious() {
    return delegate().hasPrevious();
  }

  @Override
  public int nextIndex() {
    return delegate().nextIndex();
  }

  
  @Override
  @ParametricNullness
  public E previous() {
    return delegate().previous();
  }

  @Override
  public int previousIndex() {
    return delegate().previousIndex();
  }

  @Override
  public void set(@ParametricNullness E element) {
    delegate().set(element);
  }
}
