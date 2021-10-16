

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import com.whaleal.icefrog.core.util.Predicate;

import javax.annotation.CheckForNull;
import java.util.List;






/**
 * Implementation of {@link Multimaps#filterKeys(ListMultimap, Predicate)}.
 *
 * @author Louis Wasserman
 */


final class FilteredKeyListMultimap<K extends Object, V extends Object>
    extends FilteredKeyMultimap<K, V> implements ListMultimap<K, V> {
  FilteredKeyListMultimap(ListMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    super(unfiltered, keyPredicate);
  }

  @Override
  public ListMultimap<K, V> unfiltered() {
    return (ListMultimap<K, V>) super.unfiltered();
  }

  @Override
  public List<V> get(@ParametricNullness K key) {
    return (List<V>) super.get(key);
  }

  @Override
  public List<V> removeAll(@CheckForNull Object key) {
    return (List<V>) super.removeAll(key);
  }

  @Override
  public List<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return (List<V>) super.replaceValues(key, values);
  }
}
