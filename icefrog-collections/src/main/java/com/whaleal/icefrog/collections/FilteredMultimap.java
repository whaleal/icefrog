

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.lang.Predicate;

import java.util.Map.Entry;




/**
 * An interface for all filtered multimap types.
 *
 * 
 */


interface FilteredMultimap<K extends Object, V extends Object>
    extends Multimap<K, V> {
  Multimap<K, V> unfiltered();

  Predicate<? super Entry<K, V>> entryPredicate();
}
