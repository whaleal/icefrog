

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Map.Entry;
import java.util.Set;


/**
 * A set multimap which forwards all its method calls to another set multimap. Subclasses should
 * override one or more methods to modify the behavior of the backing multimap as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * <p><b>{@code default} method warning:</b> This class does <i>not</i> forward calls to {@code
 * default} methods. Instead, it inherits their default implementations. When those implementations
 * invoke methods, they invoke methods on the {@code ForwardingSetMultimap}.
 *
 * @author Kurt Alfred Kluever
 * 
 */


public abstract class ForwardingSetMultimap<K extends Object, V extends Object>
    extends ForwardingMultimap<K, V> implements SetMultimap<K, V> {

  @Override
  protected abstract SetMultimap<K, V> delegate();

  @Override
  public Set<Entry<K, V>> entries() {
    return delegate().entries();
  }

  @Override
  public Set<V> get(@ParametricNullness K key) {
    return delegate().get(key);
  }


  @Override
  public Set<V> removeAll(@CheckForNull Object key) {
    return delegate().removeAll(key);
  }


  @Override
  public Set<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return delegate().replaceValues(key, values);
  }
}
