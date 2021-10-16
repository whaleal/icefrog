

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;






/**
 * Provides similar behavior to {@link String#intern} for any immutable type. Common implementations
 * are available from the {@link Interners} class.
 *
 * <p>Note that {@code String.intern()} has some well-known performance limitations, and should
 * generally be avoided. Prefer {@link Interners#newWeakInterner} or another {@code Interner}
 * implementation even for {@code String} interning.
 *
 * @author Kevin Bourrillion
 * 
 */



public interface Interner<E> {
  /**
   * Chooses and returns the representative instance for any of a collection of instances that are
   * equal to each other. If two {@linkplain Object#equals equal} inputs are given to this method,
   * both calls will return the same instance. That is, {@code intern(a).equals(a)} always holds,
   * and {@code intern(a) == intern(b)} if and only if {@code a.equals(b)}. Note that {@code
   * intern(a)} is permitted to return one instance now and a different instance later if the
   * original interned instance was garbage-collected.
   *
   * <p><b>Warning:</b> do not use with mutable objects.
   *
   * @throws NullPointerException if {@code sample} is null
   */
   // TODO(cpovirk): Consider removing this?
  E intern(E sample);
}
