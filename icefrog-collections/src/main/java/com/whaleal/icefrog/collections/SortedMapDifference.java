

package com.whaleal.icefrog.collections;

import java.util.SortedMap;


/**
 * An object representing the differences between two sorted MapUtil.
 *
 * 
 * 
 */


public interface SortedMapDifference<K extends Object, V extends Object>
    extends MapDifference<K, V> {

  @Override
  SortedMap<K, V> entriesOnlyOnLeft();

  @Override
  SortedMap<K, V> entriesOnlyOnRight();

  @Override
  SortedMap<K, V> entriesInCommon();

  @Override
  SortedMap<K, ValueDifference<V>> entriesDiffering();
}
