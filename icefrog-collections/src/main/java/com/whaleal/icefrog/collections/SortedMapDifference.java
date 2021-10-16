

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import java.util.SortedMap;


/**
 * An object representing the differences between two sorted MapUtil.
 *
 * @author Louis Wasserman
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
