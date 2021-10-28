

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.util.Map.Entry;




/**
 * A map entry which forwards all its method calls to another map entry. Subclasses should override
 * one or more methods to modify the behavior of the backing map entry as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>Warning:</b> The methods of {@code ForwardingMapEntry} forward <i>indiscriminately</i> to
 * the methods of the delegate. For example, overriding {@link #getValue} alone <i>will not</i>
 * change the behavior of {@link #equals}, which can lead to unexpected behavior. In this case, you
 * should override {@code equals} as well, either providing your own implementation, or delegating
 * to the provided {@code standardEquals} method.
 *
 * <p>Each of the {@code standard} methods, where appropriate, use {@link ObjectUtil#equal} to test
 * equality for both keys and values. This may not be the desired behavior for map implementations
 * that use non-standard notions of key equality, such as the entry of a {@code SortedMap} whose
 * comparator is not consistent with {@code equals}.
 *
 * <p>The {@code standard} methods are not guaranteed to be thread-safe, even when all of the
 * methods that they depend on are thread-safe.
 *
 *
 * 
 * 
 */


public abstract class ForwardingMapEntry<K extends Object, V extends Object>
    extends ForwardingObject implements Entry<K, V> {
  // TODO(lowasser): identify places where thread safety is actually lost

  /** Constructor for use by subclasses. */
  protected ForwardingMapEntry() {}

  @Override
  protected abstract Entry<K, V> delegate();

  @Override
  @ParametricNullness
  public K getKey() {
    return delegate().getKey();
  }

  @Override
  @ParametricNullness
  public V getValue() {
    return delegate().getValue();
  }

  @Override
  @ParametricNullness
  public V setValue(@ParametricNullness V value) {
    return delegate().setValue(value);
  }

  @Override
  public boolean equals(@CheckForNull Object object) {
    return delegate().equals(object);
  }

  @Override
  public int hashCode() {
    return delegate().hashCode();
  }

  /**
   * A sensible definition of {@link #equals(Object)} in terms of {@link #getKey()} and {@link
   * #getValue()}. If you override either of these methods, you may wish to override {@link
   * #equals(Object)} to forward to this implementation.
   *
   * 
   */
  protected boolean standardEquals(@CheckForNull Object object) {
    if (object instanceof Entry) {
      Entry<?, ?> that = (Entry<?, ?>) object;
      return ObjectUtil.equal(this.getKey(), that.getKey())
          && ObjectUtil.equal(this.getValue(), that.getValue());
    }
    return false;
  }

  /**
   * A sensible definition of {@link #hashCode()} in terms of {@link #getKey()} and {@link
   * #getValue()}. If you override either of these methods, you may wish to override {@link
   * #hashCode()} to forward to this implementation.
   *
   * 
   */
  protected int standardHashCode() {
    K k = getKey();
    V v = getValue();
    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
  }

  /**
   * A sensible definition of {@link #toString} in terms of {@link #getKey} and {@link #getValue}.
   * If you override either of these methods, you may wish to override {@link #equals} to forward to
   * this implementation.
   *
   * 
   */

  protected String standardToString() {
    return getKey() + "=" + getValue();
  }
}
