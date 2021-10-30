

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.lang.Predicate;

import javax.annotation.CheckForNull;
import java.util.Map.Entry;
import java.util.Set;






/**
 * Implementation of {@link Multimaps#filterKeys(SetMultimap, Predicate)}.
 *
 * 
 */


final class FilteredKeySetMultimap<K extends Object, V extends Object>
    extends FilteredKeyMultimap<K, V> implements FilteredSetMultimap<K, V> {

  FilteredKeySetMultimap(SetMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate) {
    super(unfiltered, keyPredicate);
  }

  @Override
  public SetMultimap<K, V> unfiltered() {
    return (SetMultimap<K, V>) unfiltered;
  }

  @Override
  public Set<V> get(@ParametricNullness K key) {
    return (Set<V>) super.get(key);
  }

  @Override
  public Set<V> removeAll(@CheckForNull Object key) {
    return (Set<V>) super.removeAll(key);
  }

  @Override
  public Set<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values) {
    return (Set<V>) super.replaceValues(key, values);
  }

  @Override
  public Set<Entry<K, V>> entries() {
    return (Set<Entry<K, V>>) super.entries();
  }

  @Override
  Set<Entry<K, V>> createEntries() {
    return new EntrySet();
  }

  class EntrySet extends Entries implements Set<Entry<K, V>> {
    @Override
    public int hashCode() {
      return Sets.hashCodeImpl(this);
    }

    @Override
    public boolean equals(@CheckForNull Object o) {
      return Sets.equalsImpl(this, o);
    }
  }
}
