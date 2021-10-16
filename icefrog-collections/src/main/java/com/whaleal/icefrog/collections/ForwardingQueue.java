

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.NoSuchElementException;
import java.util.Queue;


/**
 * A queue which forwards all its method calls to another queue. Subclasses should override one or
 * more methods to modify the behavior of the backing queue as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingQueue} forward <b>indiscriminately</b> to the
 * methods of the delegate. For example, overriding {@link #add} alone <b>will not</b> change the
 * behavior of {@link #offer} which can lead to unexpected behavior. In this case, you should
 * override {@code offer} as well, either providing your own implementation, or delegating to the
 * provided {@code standardOffer} method.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingQueue}.
 *
 * <p>The {@code standard} methods are not guaranteed to be thread-safe, even when all of the
 * methods that they depend on are thread-safe.
 *
 * @author Mike Bostock
 * @author Louis Wasserman
 * 
 */


public abstract class ForwardingQueue<E extends Object> extends ForwardingCollection<E>
    implements Queue<E> {

  /** Constructor for use by subclasses. */
  protected ForwardingQueue() {}

  @Override
  protected abstract Queue<E> delegate();

   // TODO(cpovirk): Consider removing this?
  @Override
  public boolean offer(@ParametricNullness E o) {
    return delegate().offer(o);
  }

   // TODO(cpovirk): Consider removing this?
  @Override
  @CheckForNull
  public E poll() {
    return delegate().poll();
  }

  
  @Override
  @ParametricNullness
  public E remove() {
    return delegate().remove();
  }

  @Override
  @CheckForNull
  public E peek() {
    return delegate().peek();
  }

  @Override
  @ParametricNullness
  public E element() {
    return delegate().element();
  }

  /**
   * A sensible definition of {@link #offer} in terms of {@link #add}. If you override {@link #add},
   * you may wish to override {@link #offer} to forward to this implementation.
   *
   * 
   */
  protected boolean standardOffer(@ParametricNullness E e) {
    try {
      return add(e);
    } catch (IllegalStateException caught) {
      return false;
    }
  }

  /**
   * A sensible definition of {@link #peek} in terms of {@link #element}. If you override {@link
   * #element}, you may wish to override {@link #peek} to forward to this implementation.
   *
   * 
   */
  @CheckForNull
  protected E standardPeek() {
    try {
      return element();
    } catch (NoSuchElementException caught) {
      return null;
    }
  }

  /**
   * A sensible definition of {@link #poll} in terms of {@link #remove}. If you override {@link
   * #remove}, you may wish to override {@link #poll} to forward to this implementation.
   *
   * 
   */
  @CheckForNull
  protected E standardPoll() {
    try {
      return remove();
    } catch (NoSuchElementException caught) {
      return null;
    }
  }
}
