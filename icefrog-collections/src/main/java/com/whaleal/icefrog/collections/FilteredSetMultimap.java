

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;






/**
 * A supertype for filtered {@link SetMultimap} implementations.
 *
 * @author Louis Wasserman
 */


interface FilteredSetMultimap<K extends Object, V extends Object>
    extends FilteredMultimap<K, V>, SetMultimap<K, V> {
  @Override
  SetMultimap<K, V> unfiltered();
}
