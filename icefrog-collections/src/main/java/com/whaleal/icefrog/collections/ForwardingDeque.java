

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Deque;
import java.util.Iterator;


/**
 * A deque which forwards all its method calls to another deque. Subclasses should override one or
 * more methods to modify the behavior of the backing deque as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingDeque} forward <b>indiscriminately</b> to the
 * methods of the delegate. For example, overriding {@link #add} alone <b>will not</b> change the
 * behavior of {@link #offer} which can lead to unexpected behavior. In this case, you should
 * override {@code offer} as well.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingDeque}.
 *
 * @author Kurt Alfred Kluever
 * 
 */


public abstract class ForwardingDeque<E extends Object> extends ForwardingQueue<E>
    implements Deque<E> {

  /** Constructor for use by subclasses. */
  protected ForwardingDeque() {}

  @Override
  protected abstract Deque<E> delegate();

  @Override
  public void addFirst(@ParametricNullness E e) {
    delegate().addFirst(e);
  }

  @Override
  public void addLast(@ParametricNullness E e) {
    delegate().addLast(e);
  }

  @Override
  public Iterator<E> descendingIterator() {
    return delegate().descendingIterator();
  }

  @Override
  @ParametricNullness
  public E getFirst() {
    return delegate().getFirst();
  }

  @Override
  @ParametricNullness
  public E getLast() {
    return delegate().getLast();
  }

   // TODO(cpovirk): Consider removing this?
  @Override
  public boolean offerFirst(@ParametricNullness E e) {
    return delegate().offerFirst(e);
  }

   // TODO(cpovirk): Consider removing this?
  @Override
  public boolean offerLast(@ParametricNullness E e) {
    return delegate().offerLast(e);
  }

  @Override
  @CheckForNull
  public E peekFirst() {
    return delegate().peekFirst();
  }

  @Override
  @CheckForNull
  public E peekLast() {
    return delegate().peekLast();
  }

   // TODO(cpovirk): Consider removing this?
  @Override
  @CheckForNull
  public E pollFirst() {
    return delegate().pollFirst();
  }

   // TODO(cpovirk): Consider removing this?
  @Override
  @CheckForNull
  public E pollLast() {
    return delegate().pollLast();
  }


  @Override
  @ParametricNullness
  public E pop() {
    return delegate().pop();
  }

  @Override
  public void push(@ParametricNullness E e) {
    delegate().push(e);
  }


  @Override
  @ParametricNullness
  public E removeFirst() {
    return delegate().removeFirst();
  }


  @Override
  @ParametricNullness
  public E removeLast() {
    return delegate().removeLast();
  }


  @Override
  public boolean removeFirstOccurrence(@CheckForNull Object o) {
    return delegate().removeFirstOccurrence(o);
  }


  @Override
  public boolean removeLastOccurrence(@CheckForNull Object o) {
    return delegate().removeLastOccurrence(o);
  }
}
