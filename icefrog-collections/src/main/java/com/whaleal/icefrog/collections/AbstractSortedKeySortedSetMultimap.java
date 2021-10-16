

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import java.util.Collection;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;





/**
 * Basic implementation of a {@link SortedSetMultimap} with a sorted key set.
 *
 * <p>This superclass allows {@code TreeMultimap} to override methods to return navigable set and
 * map types in non-GWT only, while GWT code will inherit the SortedMap/SortedSet overrides.
 *
 * @author Louis Wasserman
 */


abstract class AbstractSortedKeySortedSetMultimap<
        K extends Object, V extends Object>
    extends AbstractSortedSetMultimap<K, V> {

  AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> map) {
    super(map);
  }

  @Override
  public SortedMap<K, Collection<V>> asMap() {
    return (SortedMap<K, Collection<V>>) super.asMap();
  }

  @Override
  SortedMap<K, Collection<V>> backingMap() {
    return (SortedMap<K, Collection<V>>) super.backingMap();
  }

  @Override
  public SortedSet<K> keySet() {
    return (SortedSet<K>) super.keySet();
  }

  @Override
  Set<K> createKeySet() {
    return createMaybeNavigableKeySet();
  }
}
