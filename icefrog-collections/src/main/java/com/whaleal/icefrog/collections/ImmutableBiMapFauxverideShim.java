

package com.whaleal.icefrog.collections;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;


/**
 * "Overrides" the {@link ImmutableMap} static methods that lack {@link ImmutableBiMap} equivalents
 * with deprecated, exception-throwing versions. See {@link ImmutableSortedSetFauxverideShim} for
 * details.
 *
 * 
 */


abstract class ImmutableBiMapFauxverideShim<K, V> extends ImmutableMap<K, V> {
  /**
   * Not supported. Use {@link ImmutableBiMap#toImmutableBiMap} instead. This method exists only to
   * hide {@link ImmutableMap#toImmutableMap(Function, Function)} from consumers of {@code
   * ImmutableBiMap}.
   *
   * @throws UnsupportedOperationException always
   * @deprecated Use {@link ImmutableBiMap#toImmutableBiMap}.
   */
  @Deprecated

  public static <T extends Object, K, V>
      Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    throw new UnsupportedOperationException();
  }

  /**
   * Not supported. This method does not make sense for {@code BiMap}. This method exists only to
   * hide {@link ImmutableMap#toImmutableMap(Function, Function, BinaryOperator)} from consumers of
   * {@code ImmutableBiMap}.
   *
   * @throws UnsupportedOperationException always
   * @deprecated
   */
  @Deprecated

  public static <T extends Object, K, V>
      Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction,
          BinaryOperator<V> mergeFunction) {
    throw new UnsupportedOperationException();
  }
}
