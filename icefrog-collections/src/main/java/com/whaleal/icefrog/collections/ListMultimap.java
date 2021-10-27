

package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * A {@code Multimap} that can hold duplicate key-value pairs and that maintains the insertion
 * ordering of values for a given key. See the {@link Multimap} documentation for information common
 * to all Multimaps.
 *
 * <p>The {@link #get}, {@link #removeAll}, and {@link #replaceValues} methods each return a {@link
 * List} of values. Though the method signature doesn't say so explicitly, the map returned by
 * {@link #asMap} has {@code List} values.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#multimap"> {@code
 * Multimap}</a>.
 *
 *
 * 
 */


public interface ListMultimap<K extends Object, V extends Object>
    extends Multimap<K, V> {
  /**
   * {@inheritDoc}
   *
   * <p>Because the values for a given key may have duplicates and follow the insertion ordering,
   * this method returns a {@link List}, instead of the {@link Collection} specified in
   * the {@link Multimap} interface.
   */
  @Override
  List<V> get(@ParametricNullness K key);

  /**
   * {@inheritDoc}
   *
   * <p>Because the values for a given key may have duplicates and follow the insertion ordering,
   * this method returns a {@link List}, instead of the {@link Collection} specified in
   * the {@link Multimap} interface.
   */
  
  @Override
  List<V> removeAll(@CheckForNull Object key);

  /**
   * {@inheritDoc}
   *
   * <p>Because the values for a given key may have duplicates and follow the insertion ordering,
   * this method returns a {@link List}, instead of the {@link Collection} specified in
   * the {@link Multimap} interface.
   */
  
  @Override
  List<V> replaceValues(@ParametricNullness K key, Iterable<? extends V> values);

  /**
   * {@inheritDoc}
   *
   * <p><b>Note:</b> The returned map's values are guaranteed to be of type {@link List}. To obtain
   * this map with the more specific generic type {@code Map<K, List<V>>}, call {@link
   * Multimaps#asMap(ListMultimap)} instead.
   */
  @Override
  Map<K, Collection<V>> asMap();

  /**
   * Compares the specified object to this multimap for equality.
   *
   * <p>Two {@code ListMultimap} instances are equal if, for each key, they contain the same values
   * in the same order. If the value orderings disagree, the Multimaps will not be considered equal.
   *
   * <p>An empty {@code ListMultimap} is equal to any other empty {@code Multimap}, including an
   * empty {@code SetMultimap}.
   */
  @Override
  boolean equals(@CheckForNull Object obj);
}
