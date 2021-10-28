

package com.whaleal.icefrog.collections;

/**
 * A supertype for filtered {@link SetMultimap} implementations.
 *
 * 
 */


interface FilteredSetMultimap<K extends Object, V extends Object>
    extends FilteredMultimap<K, V>, SetMultimap<K, V> {
  @Override
  SetMultimap<K, V> unfiltered();
}
