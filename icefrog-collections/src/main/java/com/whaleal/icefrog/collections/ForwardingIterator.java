

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;





import java.util.Iterator;


/**
 * An iterator which forwards all its method calls to another iterator. Subclasses should override
 * one or more methods to modify the behavior of the backing iterator as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>{@code default} method warning:</b> This class forwards calls to <i>only some</i> {@code
 * default} methods. Specifically, it forwards calls only for methods that existed <a
 * href="https://docs.oracle.com/javase/7/docs/api/java/util/Iterator.html">before {@code default}
 * methods were introduced</a>. For newer methods, like {@code forEachRemaining}, it inherits their
 * default implementations. When those implementations invoke methods, they invoke methods on the
 * {@code ForwardingIterator}.
 *
 *
 * 
 */


public abstract class ForwardingIterator<T extends Object> extends ForwardingObject
    implements Iterator<T> {

  /** Constructor for use by subclasses. */
  protected ForwardingIterator() {}

  @Override
  protected abstract Iterator<T> delegate();

  @Override
  public boolean hasNext() {
    return delegate().hasNext();
  }

  
  @Override
  @ParametricNullness
  public T next() {
    return delegate().next();
  }

  @Override
  public void remove() {
    delegate().remove();
  }
}
