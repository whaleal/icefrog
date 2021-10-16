

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;



import com.whaleal.icefrog.core.util.Predicate;

import java.util.Map.Entry;




/**
 * An interface for all filtered multimap types.
 *
 * @author Louis Wasserman
 */


interface FilteredMultimap<K extends Object, V extends Object>
    extends Multimap<K, V> {
  Multimap<K, V> unfiltered();

  Predicate<? super Entry<K, V>> entryPredicate();
}
