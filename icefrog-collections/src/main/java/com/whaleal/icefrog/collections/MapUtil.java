/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whaleal.icefrog.collections;



import com.whaleal.icefrog.core.collection.TransIter;
import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.map.BiMap;
import com.whaleal.icefrog.core.util.PredicateUtil;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;
import java.util.stream.Collector;
import  com.whaleal.icefrog.core.lang.Predicate;

import static com.whaleal.icefrog.core.lang.Precondition.*;
import static com.whaleal.icefrog.core.map.MapUtil.keyFunction;
import static com.whaleal.icefrog.core.map.MapUtil.valueFunction;
import static com.whaleal.icefrog.core.util.NumberUtil.MAX_POWER_OF_TWO;
import static com.whaleal.icefrog.core.util.PredicateUtil.compose;
import static java.util.Objects.requireNonNull;

@ElementTypesAreNonnullByDefault
public final class MapUtil {
  private MapUtil() {}





  static <K extends Object, V extends Object> Iterator<K> keyIterator(
      Iterator<Entry<K, V>> entryIterator) {

    return new TransIter<Entry<K, V>, K>(entryIterator ,keyFunction());
  }

  static <K extends Object, V extends Object> Iterator<V> valueIterator(
      Iterator<Entry<K, V>> entryIterator) {
    return new TransIter<Entry<K, V>, V>(entryIterator ,valueFunction());
    
  }

  /**
   * Returns an immutable map instance containing the given entries. Internally, the returned map
   * will be backed by an {@link EnumMap}.
   *
   * <p>The iteration order of the returned map follows the enum's iteration order, not the order in
   * which the elements appear in the given map.
   *
   * @param map the map to make an immutable copy of
   * @return an immutable map containing those entries
   * @since 14.0
   */

  public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(
      Map<K, ? extends V> map) {
    if (map instanceof ImmutableEnumMap) {
      @SuppressWarnings("unchecked") // safe covariant cast
      ImmutableEnumMap<K, V> result = (ImmutableEnumMap<K, V>) map;
      return result;
    }
    Iterator<? extends Entry<K, ? extends V>> entryItr = map.entrySet().iterator();
    if (!entryItr.hasNext()) {
      return ImmutableMap.of();
    }
    Entry<K, ? extends V> entry1 = entryItr.next();
    K key1 = entry1.getKey();
    V value1 = entry1.getValue();
    checkEntryNotNull(key1, value1);
    Class<K> clazz = key1.getDeclaringClass();
    EnumMap<K, V> enumMap = new EnumMap<>(clazz);
    enumMap.put(key1, value1);
    while (entryItr.hasNext()) {
      Entry<K, ? extends V> entry = entryItr.next();
      K key = entry.getKey();
      V value = entry.getValue();
      checkEntryNotNull(key, value);
      enumMap.put(key, value);
    }
    return ImmutableEnumMap.asImmutable(enumMap);
  }

  /**
   * Returns a {@link Collector} that accumulates elements into an {@code ImmutableMap} whose keys
   * and values are the result of applying the provided mapping functions to the input elements. The
   * resulting implementation is specialized for enum key types. The returned map and its views will
   * iterate over keys in their enum definition order, not encounter order.
   *
   * <p>If the mapped keys contain duplicates, an {@code IllegalArgumentException} is thrown when
   * the collection operation is performed. (This differs from the {@code Collector} returned by
   * {@link java.util.stream.Collectors#toMap(java.util.function.Function,
   * java.util.function.Function) Collectors.toMap(Function, Function)}, which throws an {@code
   * IllegalStateException}.)
   *
   * @since 21.0
   */
  public static <T extends Object, K extends Enum<K>, V>
      Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
          java.util.function.Function<? super T, ? extends K> keyFunction,
          java.util.function.Function<? super T, ? extends V> valueFunction) {
    return CollectCollectors.toImmutableEnumMap(keyFunction, valueFunction);
  }

  /**
   * Returns a {@link Collector} that accumulates elements into an {@code ImmutableMap} whose keys
   * and values are the result of applying the provided mapping functions to the input elements. The
   * resulting implementation is specialized for enum key types. The returned map and its views will
   * iterate over keys in their enum definition order, not encounter order.
   *
   * <p>If the mapped keys contain duplicates, the values are merged using the specified merging
   * function.
   *
   * @since 21.0
   */
  public static <T extends Object, K extends Enum<K>, V>
      Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
          java.util.function.Function<? super T, ? extends K> keyFunction,
          java.util.function.Function<? super T, ? extends V> valueFunction,
          BinaryOperator<V> mergeFunction) {
    return CollectCollectors.toImmutableEnumMap(keyFunction, valueFunction, mergeFunction);
  }

  /**
   * Creates a <i>mutable</i>, empty {@code HashMap} instance.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link ImmutableMap#of()} instead.
   *
   * <p><b>Note:</b> if {@code K} is an {@code enum} type, use {@link #newEnumMap} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code HashMap} constructor directly, taking advantage of the new
   * <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @return a new, empty {@code HashMap}
   */
  public static <K extends Object, V extends Object>
      HashMap<K, V> newHashMap() {
    return new HashMap<>();
  }

  /**
   * Creates a <i>mutable</i> {@code HashMap} instance with the same mappings as the specified map.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link ImmutableMap#copyOf(Map)} instead.
   *
   * <p><b>Note:</b> if {@code K} is an {@link Enum} type, use {@link #newEnumMap} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code HashMap} constructor directly, taking advantage of the new
   * <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @param map the mappings to be placed in the new map
   * @return a new {@code HashMap} initialized with the mappings from {@code map}
   */
  public static <K extends Object, V extends Object> HashMap<K, V> newHashMap(
      Map<? extends K, ? extends V> map) {
    return new HashMap<>(map);
  }

  /**
   * Creates a {@code HashMap} instance, with a high enough "initial capacity" that it <i>should</i>
   * hold {@code expectedSize} elements without growth. This behavior cannot be broadly guaranteed,
   * but it is observed to be true for OpenJDK 1.7. It also can't be guaranteed that the method
   * isn't inadvertently <i>oversizing</i> the returned map.
   *
   * @param expectedSize the number of entries you expect to add to the returned map
   * @return a new, empty {@code HashMap} with enough capacity to hold {@code expectedSize} entries
   *     without resizing
   * @throws IllegalArgumentException if {@code expectedSize} is negative
   */
  public static <K extends Object, V extends Object>
      HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
    return new HashMap<>(capacity(expectedSize));
  }

  /**
   * Returns a capacity that is sufficient to keep the map from being resized as long as it grows no
   * larger than expectedSize and the load factor is ≥ its default (0.75).
   */
  static int capacity(int expectedSize) {
    if (expectedSize < 3) {
      checkNonnegative(expectedSize, "expectedSize");
      return expectedSize + 1;
    }
    if (expectedSize < MAX_POWER_OF_TWO) {
      // This is the calculation used in JDK8 to resize when a putAll
      // happens; it seems to be the most conservative calculation we
      // can make.  0.75 is the default load factor.
      return (int) ((float) expectedSize / 0.75F + 1.0F);
    }
    return Integer.MAX_VALUE; // any large value
  }

  /**
   * Creates a <i>mutable</i>, empty, insertion-ordered {@code LinkedHashMap} instance.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link ImmutableMap#of()} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code LinkedHashMap} constructor directly, taking advantage of
   * the new <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @return a new, empty {@code LinkedHashMap}
   */
  public static <K extends Object, V extends Object>
      LinkedHashMap<K, V> newLinkedHashMap() {
    return new LinkedHashMap<>();
  }

  /**
   * Creates a <i>mutable</i>, insertion-ordered {@code LinkedHashMap} instance with the same
   * mappings as the specified map.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link ImmutableMap#copyOf(Map)} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code LinkedHashMap} constructor directly, taking advantage of
   * the new <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @param map the mappings to be placed in the new map
   * @return a new, {@code LinkedHashMap} initialized with the mappings from {@code map}
   */
  public static <K extends Object, V extends Object>
      LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map) {
    return new LinkedHashMap<>(map);
  }

  /**
   * Creates a {@code LinkedHashMap} instance, with a high enough "initial capacity" that it
   * <i>should</i> hold {@code expectedSize} elements without growth. This behavior cannot be
   * broadly guaranteed, but it is observed to be true for OpenJDK 1.7. It also can't be guaranteed
   * that the method isn't inadvertently <i>oversizing</i> the returned map.
   *
   * @param expectedSize the number of entries you expect to add to the returned map
   * @return a new, empty {@code LinkedHashMap} with enough capacity to hold {@code expectedSize}
   *     entries without resizing
   * @throws IllegalArgumentException if {@code expectedSize} is negative
   * @since 19.0
   */
  public static <K extends Object, V extends Object>
      LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) {
    return new LinkedHashMap<>(capacity(expectedSize));
  }

  /**
   * Creates a new empty {@link ConcurrentHashMap} instance.
   *
   * @since 3.0
   */
  public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
    return new ConcurrentHashMap<>();
  }

  /**
   * Creates a <i>mutable</i>, empty {@code TreeMap} instance using the natural ordering of its
   * elements.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link ImmutableSortedMap#of()} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code TreeMap} constructor directly, taking advantage of the new
   * <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @return a new, empty {@code TreeMap}
   */
  public static <K extends Comparable, V extends Object> TreeMap<K, V> newTreeMap() {
    return new TreeMap<>();
  }

  /**
   * Creates a <i>mutable</i> {@code TreeMap} instance with the same mappings as the specified map
   * and using the same ordering as the specified map.
   *
   * <p><b>Note:</b> if mutability is not required, use {@link
   * ImmutableSortedMap#copyOfSorted(SortedMap)} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code TreeMap} constructor directly, taking advantage of the new
   * <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @param map the sorted map whose mappings are to be placed in the new map and whose comparator
   *     is to be used to sort the new map
   * @return a new {@code TreeMap} initialized with the mappings from {@code map} and using the
   *     comparator of {@code map}
   */
  public static <K extends Object, V extends Object> TreeMap<K, V> newTreeMap(
      SortedMap<K, ? extends V> map) {
    return new TreeMap<>(map);
  }

  /**
   * Creates a <i>mutable</i>, empty {@code TreeMap} instance using the given comparator.
   *
   * <p><b>Note:</b> if mutability is not required, use {@code
   * ImmutableSortedMap.orderedBy(comparator).build()} instead.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code TreeMap} constructor directly, taking advantage of the new
   * <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @param comparator the comparator to sort the keys with
   * @return a new, empty {@code TreeMap}
   */
  public static <C extends Object, K extends C, V extends Object>
      TreeMap<K, V> newTreeMap(@CheckForNull Comparator<C> comparator) {
    // Ideally, the extra type parameter "C" shouldn't be necessary. It is a
    // work-around of a compiler type inference quirk that prevents the
    // following code from being compiled:
    // Comparator<Class<?>> comparator = null;
    // Map<Class<? extends Throwable>, String> map = newTreeMap(comparator);
    return new TreeMap<>(comparator);
  }

  /**
   * Creates an {@code EnumMap} instance.
   *
   * @param type the key type for this map
   * @return a new, empty {@code EnumMap}
   */
  public static <K extends Enum<K>, V extends Object> EnumMap<K, V> newEnumMap(
      Class<K> type) {
    return new EnumMap<>(checkNotNull(type));
  }

  /**
   * Creates an {@code EnumMap} with the same mappings as the specified map.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code EnumMap} constructor directly, taking advantage of the new
   * <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @param map the map from which to initialize this {@code EnumMap}
   * @return a new {@code EnumMap} initialized with the mappings from {@code map}
   * @throws IllegalArgumentException if {@code m} is not an {@code EnumMap} instance and contains
   *     no mappings
   */
  public static <K extends Enum<K>, V extends Object> EnumMap<K, V> newEnumMap(
      Map<K, ? extends V> map) {
    return new EnumMap<>(map);
  }

  /**
   * Creates an {@code IdentityHashMap} instance.
   *
   * <p><b>Note for Java 7 and later:</b> this method is now unnecessary and should be treated as
   * deprecated. Instead, use the {@code IdentityHashMap} constructor directly, taking advantage of
   * the new <a href="http://goo.gl/iz2Wi">"diamond" syntax</a>.
   *
   * @return a new, empty {@code IdentityHashMap}
   */
  public static <K extends Object, V extends Object>
      IdentityHashMap<K, V> newIdentityHashMap() {
    return new IdentityHashMap<>();
  }

  /**
   * Computes the difference between two maps. This difference is an immutable snapshot of the state
   * of the maps at the time this method is called. It will never change, even if the maps change at
   * a later time.
   *
   * <p>Since this method uses {@code HashMap} instances internally, the keys of the supplied maps
   * must be well-behaved with respect to {@link Object#equals} and {@link Object#hashCode}.
   *
   * <p><b>Note:</b>If you only need to know whether two maps have the same mappings, call {@code
   * left.equals(right)} instead of this method.
   *
   * @param left the map to treat as the "left" map for purposes of comparison
   * @param right the map to treat as the "right" map for purposes of comparison
   * @return the difference between the two maps
   */
  @SuppressWarnings("unchecked")
  public static <K extends Object, V extends Object>
      MapDifference<K, V> difference(
          Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
    if (left instanceof SortedMap) {
      SortedMap<K, ? extends V> sortedLeft = (SortedMap<K, ? extends V>) left;
      return difference(sortedLeft, right);
    }
    /*
     * This cast is safe: The Equivalence-accepting overload of difference() (which we call below)
     * has a weird signature because Equivalence is itself a little weird. Still, we know that
     * Equivalence.equals() can handle all inputs, and we know that the resulting MapDifference will
     * contain only Ks and Vs (as opposed to possibly containing objects even when K and V
     * are *not* @Nullable).
     *
     * An alternative to suppressing the warning would be to inline the body of the other
     * difference() method into this one.
     */
    @SuppressWarnings("nullness")
    MapDifference<K, V> result =
        (MapDifference<K, V>) difference(left, right, Equivalence.equals());
    return result;
  }

  /**
   * Computes the difference between two maps. This difference is an immutable snapshot of the state
   * of the maps at the time this method is called. It will never change, even if the maps change at
   * a later time.
   *
   * <p>Since this method uses {@code HashMap} instances internally, the keys of the supplied maps
   * must be well-behaved with respect to {@link Object#equals} and {@link Object#hashCode}.
   *
   * @param left the map to treat as the "left" map for purposes of comparison
   * @param right the map to treat as the "right" map for purposes of comparison
   * @param valueEquivalence the equivalence relationship to use to compare values
   * @return the difference between the two maps
   * @since 10.0
   */
  /*
   * This method should really be annotated to accept maps with value types. Fortunately,
   * no existing Google callers appear to pass null values (much less pass null values *and* run a
   * nullness checker).
   *
   * Still, if we decide that we want to make that work, we'd need to introduce a new type parameter
   * for the Equivalence input type:
   *
   * <E, K extends Object, V extends E> ... difference(..., Equivalence<E> ...)
   *
   * Maybe we should, even though it will break source compatibility.
   *
   * Alternatively, this is a case in which it would be useful to be able to express Equivalence<?
   * super @Nonnull T>).
   *
   * As things stand now, though, we have to either:
   *
   * - require non-null inputs so that we can guarantee non-null outputs
   *
   * - accept nullable inputs but force users to cope with nullable outputs
   *
   * And the non-null option is far more useful to existing users.
   *
   * (Vaguely related: Another thing we could consider is an overload that accepts a BiPredicate:
   * https://github.com/google/guava/issues/3913)
   */
  public static <K extends Object, V> MapDifference<K, V> difference(
      Map<? extends K, ? extends V> left,
      Map<? extends K, ? extends V> right,
      Equivalence<? super V> valueEquivalence) {
    Precondition.checkNotNull(valueEquivalence);

    Map<K, V> onlyOnLeft = newLinkedHashMap();
    Map<K, V> onlyOnRight = new LinkedHashMap<>(right); // will whittle it down
    Map<K, V> onBoth = newLinkedHashMap();
    Map<K, MapDifference.ValueDifference<V>> differences = newLinkedHashMap();
    doDifference(left, right, valueEquivalence, onlyOnLeft, onlyOnRight, onBoth, differences);
    return new MapDifferenceImpl<>(onlyOnLeft, onlyOnRight, onBoth, differences);
  }

  /**
   * Computes the difference between two sorted maps, using the comparator of the left map, or
   * {@code Ordering.natural()} if the left map uses the natural ordering of its elements. This
   * difference is an immutable snapshot of the state of the maps at the time this method is called.
   * It will never change, even if the maps change at a later time.
   *
   * <p>Since this method uses {@code TreeMap} instances internally, the keys of the right map must
   * all compare as distinct according to the comparator of the left map.
   *
   * <p><b>Note:</b>If you only need to know whether two sorted maps have the same mappings, call
   * {@code left.equals(right)} instead of this method.
   *
   * @param left the map to treat as the "left" map for purposes of comparison
   * @param right the map to treat as the "right" map for purposes of comparison
   * @return the difference between the two maps
   * @since 11.0
   */
  public static <K extends Object, V extends Object>
      SortedMapDifference<K, V> difference(
          SortedMap<K, ? extends V> left, Map<? extends K, ? extends V> right) {
    checkNotNull(left);
    checkNotNull(right);
    Comparator<? super K> comparator = orNaturalOrder(left.comparator());
    SortedMap<K, V> onlyOnLeft = MapUtil.newTreeMap(comparator);
    SortedMap<K, V> onlyOnRight = MapUtil.newTreeMap(comparator);
    onlyOnRight.putAll(right); // will whittle it down
    SortedMap<K, V> onBoth = MapUtil.newTreeMap(comparator);
    SortedMap<K, MapDifference.ValueDifference<V>> differences = MapUtil.newTreeMap(comparator);
    doDifference(left, right, Equivalence.equals(), onlyOnLeft, onlyOnRight, onBoth, differences);
    return new SortedMapDifferenceImpl<>(onlyOnLeft, onlyOnRight, onBoth, differences);
  }

  private static <K extends Object, V extends Object> void doDifference(
      Map<? extends K, ? extends V> left,
      Map<? extends K, ? extends V> right,
      Equivalence<? super V> valueEquivalence,
      Map<K, V> onlyOnLeft,
      Map<K, V> onlyOnRight,
      Map<K, V> onBoth,
      Map<K, MapDifference.ValueDifference<V>> differences) {
    for (Entry<? extends K, ? extends V> entry : left.entrySet()) {
      K leftKey = entry.getKey();
      V leftValue = entry.getValue();
      if (right.containsKey(leftKey)) {
        /*
         * The cast is safe because onlyOnRight contains all the keys of right.
         *
         * TODO(cpovirk): Consider checking onlyOnRight.containsKey instead of right.containsKey.
         * That could change behavior if the input maps use different equivalence relations (and so
         * a key that appears once in `right` might appear multiple times in `left`). We don't
         * guarantee behavior in that case, anyway, and the current behavior is likely undesirable.
         * So that's either a reason to feel free to change it or a reason to not bother thinking
         * further about this.
         */
        V rightValue = uncheckedCastNullableTToT(onlyOnRight.remove(leftKey));
        if (valueEquivalence.equivalent(leftValue, rightValue)) {
          onBoth.put(leftKey, leftValue);
        } else {
          differences.put(leftKey, ValueDifferenceImpl.create(leftValue, rightValue));
        }
      } else {
        onlyOnLeft.put(leftKey, leftValue);
      }
    }
  }

  private static <K extends Object, V extends Object> Map<K, V> unmodifiableMap(
      Map<K, ? extends V> map) {
    if (map instanceof SortedMap) {
      return Collections.unmodifiableSortedMap((SortedMap<K, ? extends V>) map);
    } else {
      return Collections.unmodifiableMap(map);
    }
  }


 
  @SuppressWarnings("unchecked")
  static <E extends Object> Comparator<? super E> orNaturalOrder(
      @CheckForNull Comparator<? super E> comparator) {
    if (comparator != null) { // can't use ? : because of javac bug 5080917
      return comparator;
    }
    return (Comparator<E>) Ordering.natural();
  }

  /**
   * Returns a live {@link Map} view whose keys are the contents of {@code set} and whose values are
   * computed on demand using {@code function}. To get an immutable <i>copy</i> instead, use {@link
   * #toMap(Iterable, Function)}.
   *
   * <p>Specifically, for each {@code k} in the backing set, the returned map has an entry mapping
   * {@code k} to {@code function.apply(k)}. The {@code keySet}, {@code values}, and {@code
   * entrySet} views of the returned map iterate in the same order as the backing set.
   *
   * <p>Modifications to the backing set are read through to the returned map. The returned map
   * supports removal operations if the backing set does. Removal operations write through to the
   * backing set. The returned map does not support put operations.
   *
   * <p><b>Warning:</b> If the function rejects {@code null}, caution is required to make sure the
   * set does not contain {@code null}, because the view cannot stop {@code null} from being added
   * to the set.
   *
   * <p><b>Warning:</b> This method assumes that for any instance {@code k} of key type {@code K},
   * {@code k.equals(k2)} implies that {@code k2} is also of type {@code K}. Using a key type for
   * which this may not hold, such as {@code ArrayList}, may risk a {@code ClassCastException} when
   * calling methods on the resulting map view.
   *
   * @since 14.0
   */
  public static <K extends Object, V extends Object> Map<K, V> asMap(
      Set<K> set, Function<? super K, V> function) {
    return new AsMapView<>(set, function);
  }

  /**
   * Returns a view of the sorted set as a map, mapping keys from the set according to the specified
   * function.
   *
   * <p>Specifically, for each {@code k} in the backing set, the returned map has an entry mapping
   * {@code k} to {@code function.apply(k)}. The {@code keySet}, {@code values}, and {@code
   * entrySet} views of the returned map iterate in the same order as the backing set.
   *
   * <p>Modifications to the backing set are read through to the returned map. The returned map
   * supports removal operations if the backing set does. Removal operations write through to the
   * backing set. The returned map does not support put operations.
   *
   * <p><b>Warning:</b> If the function rejects {@code null}, caution is required to make sure the
   * set does not contain {@code null}, because the view cannot stop {@code null} from being added
   * to the set.
   *
   * <p><b>Warning:</b> This method assumes that for any instance {@code k} of key type {@code K},
   * {@code k.equals(k2)} implies that {@code k2} is also of type {@code K}. Using a key type for
   * which this may not hold, such as {@code ArrayList}, may risk a {@code ClassCastException} when
   * calling methods on the resulting map view.
   *
   * @since 14.0
   */
  public static <K extends Object, V extends Object> SortedMap<K, V> asMap(
      SortedSet<K> set, Function<? super K, V> function) {
    return new SortedAsMapView<>(set, function);
  }

  /**
   * Returns a view of the navigable set as a map, mapping keys from the set according to the
   * specified function.
   *
   * <p>Specifically, for each {@code k} in the backing set, the returned map has an entry mapping
   * {@code k} to {@code function.apply(k)}. The {@code keySet}, {@code values}, and {@code
   * entrySet} views of the returned map iterate in the same order as the backing set.
   *
   * <p>Modifications to the backing set are read through to the returned map. The returned map
   * supports removal operations if the backing set does. Removal operations write through to the
   * backing set. The returned map does not support put operations.
   *
   * <p><b>Warning:</b> If the function rejects {@code null}, caution is required to make sure the
   * set does not contain {@code null}, because the view cannot stop {@code null} from being added
   * to the set.
   *
   * <p><b>Warning:</b> This method assumes that for any instance {@code k} of key type {@code K},
   * {@code k.equals(k2)} implies that {@code k2} is also of type {@code K}. Using a key type for
   * which this may not hold, such as {@code ArrayList}, may risk a {@code ClassCastException} when
   * calling methods on the resulting map view.
   *
   * @since 14.0
   */
   // NavigableMap
  public static <K extends Object, V extends Object> NavigableMap<K, V> asMap(
      NavigableSet<K> set, Function<? super K, V> function) {
    return new NavigableAsMapView<>(set, function);
  }

  static <K extends Object, V extends Object>
      Iterator<Entry<K, V>> asMapEntryIterator(Set<K> set, final Function<? super K, V> function) {
    return new TransformedIterator<K, Entry<K, V>>(set.iterator()) {
      @Override
      Entry<K, V> transform(@ParametricNullness final K key) {
        return immutableEntry(key, function.apply(key));
      }
    };
  }

  private static <E extends Object> Set<E> removeOnlySet(final Set<E> set) {
    return new ForwardingSet<E>() {
      @Override
      protected Set<E> delegate() {
        return set;
      }

      @Override
      public boolean add(@ParametricNullness E element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends E> es) {
        throw new UnsupportedOperationException();
      }
    };
  }

  private static <E extends Object> SortedSet<E> removeOnlySortedSet(
      final SortedSet<E> set) {
    return new ForwardingSortedSet<E>() {
      @Override
      protected SortedSet<E> delegate() {
        return set;
      }

      @Override
      public boolean add(@ParametricNullness E element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends E> es) {
        throw new UnsupportedOperationException();
      }

      @Override
      public SortedSet<E> headSet(@ParametricNullness E toElement) {
        return removeOnlySortedSet(super.headSet(toElement));
      }

      @Override
      public SortedSet<E> subSet(
          @ParametricNullness E fromElement, @ParametricNullness E toElement) {
        return removeOnlySortedSet(super.subSet(fromElement, toElement));
      }

      @Override
      public SortedSet<E> tailSet(@ParametricNullness E fromElement) {
        return removeOnlySortedSet(super.tailSet(fromElement));
      }
    };
  }

   // NavigableSet
  private static <E extends Object> NavigableSet<E> removeOnlyNavigableSet(
      final NavigableSet<E> set) {
    return new ForwardingNavigableSet<E>() {
      @Override
      protected NavigableSet<E> delegate() {
        return set;
      }

      @Override
      public boolean add(@ParametricNullness E element) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean addAll(Collection<? extends E> es) {
        throw new UnsupportedOperationException();
      }

      @Override
      public SortedSet<E> headSet(@ParametricNullness E toElement) {
        return removeOnlySortedSet(super.headSet(toElement));
      }

      @Override
      public NavigableSet<E> headSet(@ParametricNullness E toElement, boolean inclusive) {
        return removeOnlyNavigableSet(super.headSet(toElement, inclusive));
      }

      @Override
      public SortedSet<E> subSet(
          @ParametricNullness E fromElement, @ParametricNullness E toElement) {
        return removeOnlySortedSet(super.subSet(fromElement, toElement));
      }

      @Override
      public NavigableSet<E> subSet(
          @ParametricNullness E fromElement,
          boolean fromInclusive,
          @ParametricNullness E toElement,
          boolean toInclusive) {
        return removeOnlyNavigableSet(
            super.subSet(fromElement, fromInclusive, toElement, toInclusive));
      }

      @Override
      public SortedSet<E> tailSet(@ParametricNullness E fromElement) {
        return removeOnlySortedSet(super.tailSet(fromElement));
      }

      @Override
      public NavigableSet<E> tailSet(@ParametricNullness E fromElement, boolean inclusive) {
        return removeOnlyNavigableSet(super.tailSet(fromElement, inclusive));
      }

      @Override
      public NavigableSet<E> descendingSet() {
        return removeOnlyNavigableSet(super.descendingSet());
      }
    };
  }

  /**
   * Returns an immutable map whose keys are the distinct elements of {@code keys} and whose value
   * for each key was computed by {@code valueFunction}. The map's iteration order is the order of
   * the first appearance of each key in {@code keys}.
   *
   * <p>When there are multiple instances of a key in {@code keys}, it is unspecified whether {@code
   * valueFunction} will be applied to more than one instance of that key and, if it is, which
   * result will be mapped to that key in the returned map.
   *
   * <p>If {@code keys} is a {@link Set}, a live view can be obtained instead of a copy using {@link
   * MapUtil#asMap(Set, Function)}.
   *
   * @throws NullPointerException if any element of {@code keys} is {@code null}, or if {@code
   *     valueFunction} produces {@code null} for any key
   * @since 14.0
   */
  public static <K, V> ImmutableMap<K, V> toMap(
      Iterable<K> keys, Function<? super K, V> valueFunction) {
    return toMap(keys.iterator(), valueFunction);
  }

  /**
   * Returns an immutable map whose keys are the distinct elements of {@code keys} and whose value
   * for each key was computed by {@code valueFunction}. The map's iteration order is the order of
   * the first appearance of each key in {@code keys}.
   *
   * <p>When there are multiple instances of a key in {@code keys}, it is unspecified whether {@code
   * valueFunction} will be applied to more than one instance of that key and, if it is, which
   * result will be mapped to that key in the returned map.
   *
   * @throws NullPointerException if any element of {@code keys} is {@code null}, or if {@code
   *     valueFunction} produces {@code null} for any key
   * @since 14.0
   */
  public static <K, V> ImmutableMap<K, V> toMap(
      Iterator<K> keys, Function<? super K, V> valueFunction) {
    checkNotNull(valueFunction);
    // Using LHM instead of a builder so as not to fail on duplicate keys
    Map<K, V> builder = newLinkedHashMap();
    while (keys.hasNext()) {
      K key = keys.next();
      builder.put(key, valueFunction.apply(key));
    }
    return ImmutableMap.copyOf(builder);
  }

  /**
   * Returns a map with the given {@code values}, indexed by keys derived from those values. In
   * other words, each input value produces an entry in the map whose key is the result of applying
   * {@code keyFunction} to that value. These entries appear in the same order as the input values.
   * Example usage:
   *
   * <pre>{@code
   * Color red = new Color("red", 255, 0, 0);
   * ...
   * ImmutableSet<Color> allColors = ImmutableSet.of(red, green, blue);
   *
   * Map<String, Color> colorForName =
   *     uniqueIndex(allColors, toStringFunction());
   * assertThat(colorForName).containsEntry("red", red);
   * }</pre>
   *
   * <p>If your index may associate multiple values with each key, use {@link
   * Multimaps#index(Iterable, Function) Multimaps.index}.
   *
   * @param values the values to use when constructing the {@code Map}
   * @param keyFunction the function used to produce the key for each value
   * @return a map mapping the result of evaluating the function {@code keyFunction} on each value
   *     in the input collection to that value
   * @throws IllegalArgumentException if {@code keyFunction} produces the same key for more than one
   *     value in the input collection
   * @throws NullPointerException if any element of {@code values} is {@code null}, or if {@code
   *     keyFunction} produces {@code null} for any value
   */
  @CanIgnoreReturnValue
  public static <K, V> ImmutableMap<K, V> uniqueIndex(
      Iterable<V> values, Function<? super V, K> keyFunction) {
    // TODO(lowasser): consider presizing the builder if values is a Collection
    return uniqueIndex(values.iterator(), keyFunction);
  }

  /**
   * Returns a map with the given {@code values}, indexed by keys derived from those values. In
   * other words, each input value produces an entry in the map whose key is the result of applying
   * {@code keyFunction} to that value. These entries appear in the same order as the input values.
   * Example usage:
   *
   * <pre>{@code
   * Color red = new Color("red", 255, 0, 0);
   * ...
   * Iterator<Color> allColors = ImmutableSet.of(red, green, blue).iterator();
   *
   * Map<String, Color> colorForName =
   *     uniqueIndex(allColors, toStringFunction());
   * assertThat(colorForName).containsEntry("red", red);
   * }</pre>
   *
   * <p>If your index may associate multiple values with each key, use {@link
   * Multimaps#index(Iterator, Function) Multimaps.index}.
   *
   * @param values the values to use when constructing the {@code Map}
   * @param keyFunction the function used to produce the key for each value
   * @return a map mapping the result of evaluating the function {@code keyFunction} on each value
   *     in the input collection to that value
   * @throws IllegalArgumentException if {@code keyFunction} produces the same key for more than one
   *     value in the input collection
   * @throws NullPointerException if any element of {@code values} is {@code null}, or if {@code
   *     keyFunction} produces {@code null} for any value
   * @since 10.0
   */
  @CanIgnoreReturnValue
  public static <K, V> ImmutableMap<K, V> uniqueIndex(
      Iterator<V> values, Function<? super V, K> keyFunction) {
    checkNotNull(keyFunction);
    ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
    while (values.hasNext()) {
      V value = values.next();
      builder.put(keyFunction.apply(value), value);
    }
    try {
      return builder.build();
    } catch (IllegalArgumentException duplicateKeys) {
      throw new IllegalArgumentException(
          duplicateKeys.getMessage()
              + ". To index multiple values under a key, use Multimaps.index.");
    }
  }

  /**
   * Creates an {@code ImmutableMap<String, String>} from a {@code Properties} instance. Properties
   * normally derive from {@code Map<Object, Object>}, but they typically contain strings, which is
   * awkward. This method lets you get a plain-old-{@code Map} out of a {@code Properties}.
   *
   * @param properties a {@code Properties} object to be converted
   * @return an immutable map containing all the entries in {@code properties}
   * @throws ClassCastException if any key in {@code properties} is not a {@code String}
   * @throws NullPointerException if any key or value in {@code properties} is null
   */
   // java.util.Properties
  public static ImmutableMap<String, String> fromProperties(Properties properties) {
    ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

    for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
      /*
       * requireNonNull is safe because propertyNames contains only non-null elements.
       *
       * Accordingly, we have it annotated as returning `Enumeration<? extends Object>` in our
       * prototype checker's JDK. However, the checker still sees the return type as plain
       * `Enumeration<?>`, probably because of one of the following two bugs (and maybe those two
       * bugs are themselves just symptoms of the same underlying problem):
       *
       * https://github.com/typetools/checker-framework/issues/3030
       *
       * https://github.com/typetools/checker-framework/issues/3236
       */
      String key = (String) requireNonNull(e.nextElement());
      /*
       * requireNonNull is safe because the key came from propertyNames...
       *
       * ...except that it's possible for users to insert a string key with a non-string value, and
       * in that case, getProperty *will* return null.
       *
       * TODO(b/192002623): Handle that case: Either:
       *
       * - Skip non-string keys and values entirely, as proposed in the linked bug.
       *
       * - Throw ClassCastException instead of NullPointerException, as documented in the current
       *   Javadoc. (Note that we can't necessarily "just" change our call to `getProperty` to `get`
       *   because `get` does not consult the default properties.)
       */
      builder.put(key, requireNonNull(properties.getProperty(key)));
    }

    return builder.build();
  }

  /**
   * Returns an immutable map entry with the specified key and value. The {@link Entry#setValue}
   * operation throws an {@link UnsupportedOperationException}.
   *
   * <p>The returned entry is serializable.
   *
   * <p><b>Java 9 users:</b> consider using {@code java.util.Map.entry(key, value)} if the key and
   * value are non-null and the entry does not need to be serializable.
   *
   * @param key the key to be associated with the returned entry
   * @param value the value to be associated with the returned entry
   */

  public static <K extends Object, V extends Object> Entry<K, V> immutableEntry(
      @ParametricNullness K key, @ParametricNullness V value) {
    return new ImmutableEntry<>(key, value);
  }

  /**
   * Returns an unmodifiable view of the specified set of entries. The {@link Entry#setValue}
   * operation throws an {@link UnsupportedOperationException}, as do any operations that would
   * modify the returned set.
   *
   * @param entrySet the entries for which to return an unmodifiable view
   * @return an unmodifiable view of the entries
   */
  static <K extends Object, V extends Object>
      Set<Entry<K, V>> unmodifiableEntrySet(Set<Entry<K, V>> entrySet) {
    return new UnmodifiableEntrySet<>(Collections.unmodifiableSet(entrySet));
  }

  /**
   * Returns an unmodifiable view of the specified map entry. The {@link Entry#setValue} operation
   * throws an {@link UnsupportedOperationException}. This also has the side-effect of redefining
   * {@code equals} to comply with the Entry contract, to avoid a possible nefarious implementation
   * of equals.
   *
   * @param entry the entry for which to return an unmodifiable view
   * @return an unmodifiable view of the entry
   */
  static <K extends Object, V extends Object> Entry<K, V> unmodifiableEntry(
      final Entry<? extends K, ? extends V> entry) {
    checkNotNull(entry);
    return new AbstractMapEntry<K, V>() {
      @Override
      @ParametricNullness
      public K getKey() {
        return entry.getKey();
      }

      @Override
      @ParametricNullness
      public V getValue() {
        return entry.getValue();
      }
    };
  }

  static <K extends Object, V extends Object>
      UnmodifiableIterator<Entry<K, V>> unmodifiableEntryIterator(
          final Iterator<Entry<K, V>> entryIterator) {
    return new UnmodifiableIterator<Entry<K, V>>() {
      @Override
      public boolean hasNext() {
        return entryIterator.hasNext();
      }

      @Override
      public Entry<K, V> next() {
        return unmodifiableEntry(entryIterator.next());
      }
    };
  }

  /**
   * Returns a {@link Converter} that converts values using {@link BiMap#get bimap.get()}, and whose
   * inverse view converts values using {@link BiMap#inverse bimap.inverse()}{@code .get()}.
   *
   * <p>To use a plain {@link Map} as a {@link Function}, see {@link
   * com.google.common.base.Functions#forMap(Map)} or {@link
   * com.google.common.base.Functions#forMap(Map, Object)}.
   *
   * @since 16.0
   */
  public static <A, B> Converter<A, B> asConverter(final BiMap<A, B> bimap) {
    return new BiMapConverter<>(bimap);
  }

  /**
   * Returns a synchronized (thread-safe) bimap backed by the specified bimap. In order to guarantee
   * serial access, it is critical that <b>all</b> access to the backing bimap is accomplished
   * through the returned bimap.
   *
   * <p>It is imperative that the user manually synchronize on the returned map when accessing any
   * of its collection views:
   *
   * <pre>{@code
   * BiMap<Long, String> map = Maps.synchronizedBiMap(
   *     HashBiMap.<Long, String>create());
   * ...
   * Set<Long> set = map.keySet();  // Needn't be in synchronized block
   * ...
   * synchronized (map) {  // Synchronizing on map, not set!
   *   Iterator<Long> it = set.iterator(); // Must be in synchronized block
   *   while (it.hasNext()) {
   *     foo(it.next());
   *   }
   * }
   * }</pre>
   *
   * <p>Failure to follow this advice may result in non-deterministic behavior.
   *
   * <p>The returned bimap will be serializable if the specified bimap is serializable.
   *
   * @param bimap the bimap to be wrapped in a synchronized view
   * @return a synchronized view of the specified bimap
   */
  public static <K extends Object, V extends Object>
      BiMap<K, V> synchronizedBiMap(BiMap<K, V> bimap) {
    return Synchronized.biMap(bimap, null);
  }

  /**
   * Returns an unmodifiable view of the specified bimap. This method allows modules to provide
   * users with "read-only" access to internal bimaps. Query operations on the returned bimap "read
   * through" to the specified bimap, and attempts to modify the returned map, whether direct or via
   * its collection views, result in an {@code UnsupportedOperationException}.
   *
   * <p>The returned bimap will be serializable if the specified bimap is serializable.
   *
   * @param bimap the bimap for which an unmodifiable view is to be returned
   * @return an unmodifiable view of the specified bimap
   */
  public static <K extends Object, V extends Object>
      BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> bimap) {
    return new UnmodifiableBiMap<>(bimap, null);
  }

  /**
   * Returns a view of a map where each value is transformed by a function. All other properties of
   * the map, such as iteration order, are left intact. For example, the code:
   *
   * <pre>{@code
   * Map<String, Integer> map = ImmutableMap.of("a", 4, "b", 9);
   * Function<Integer, Double> sqrt =
   *     new Function<Integer, Double>() {
   *       public Double apply(Integer in) {
   *         return Math.sqrt((int) in);
   *       }
   *     };
   * Map<String, Double> transformed = Maps.transformValues(map, sqrt);
   * System.out.println(transformed);
   * }</pre>
   *
   * ... prints {@code {a=2.0, b=3.0}}.
   *
   * <p>Changes in the underlying map are reflected in this view. Conversely, this view supports
   * removal operations, and these are reflected in the underlying map.
   *
   * <p>It's acceptable for the underlying map to contain null keys, and even null values provided
   * that the function is capable of accepting null input. The transformed map might contain null
   * values, if the function sometimes gives a null result.
   *
   * <p>The returned map is not thread-safe or serializable, even if the underlying map is.
   *
   * <p>The function is applied lazily, invoked when needed. This is necessary for the returned map
   * to be a view, but it means that the function will be applied many times for bulk operations
   * like {@link Map#containsValue} and {@code Map.toString()}. For this to perform well, {@code
   * function} should be fast. To avoid lazy evaluation when the returned map doesn't need to be a
   * view, copy the returned map into a new map of your choosing.
   */
  public static <
          K extends Object, V1 extends Object, V2 extends Object>
      Map<K, V2> transformValues(Map<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }

  /**
   * Returns a view of a sorted map where each value is transformed by a function. All other
   * properties of the map, such as iteration order, are left intact. For example, the code:
   *
   * <pre>{@code
   * SortedMap<String, Integer> map = ImmutableSortedMap.of("a", 4, "b", 9);
   * Function<Integer, Double> sqrt =
   *     new Function<Integer, Double>() {
   *       public Double apply(Integer in) {
   *         return Math.sqrt((int) in);
   *       }
   *     };
   * SortedMap<String, Double> transformed =
   *      Maps.transformValues(map, sqrt);
   * System.out.println(transformed);
   * }</pre>
   *
   * ... prints {@code {a=2.0, b=3.0}}.
   *
   * <p>Changes in the underlying map are reflected in this view. Conversely, this view supports
   * removal operations, and these are reflected in the underlying map.
   *
   * <p>It's acceptable for the underlying map to contain null keys, and even null values provided
   * that the function is capable of accepting null input. The transformed map might contain null
   * values, if the function sometimes gives a null result.
   *
   * <p>The returned map is not thread-safe or serializable, even if the underlying map is.
   *
   * <p>The function is applied lazily, invoked when needed. This is necessary for the returned map
   * to be a view, but it means that the function will be applied many times for bulk operations
   * like {@link Map#containsValue} and {@code Map.toString()}. For this to perform well, {@code
   * function} should be fast. To avoid lazy evaluation when the returned map doesn't need to be a
   * view, copy the returned map into a new map of your choosing.
   *
   * @since 11.0
   */
  public static <
          K extends Object, V1 extends Object, V2 extends Object>
      SortedMap<K, V2> transformValues(
          SortedMap<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }

  /**
   * Returns a view of a navigable map where each value is transformed by a function. All other
   * properties of the map, such as iteration order, are left intact. For example, the code:
   *
   * <pre>{@code
   * NavigableMap<String, Integer> map = Maps.newTreeMap();
   * map.put("a", 4);
   * map.put("b", 9);
   * Function<Integer, Double> sqrt =
   *     new Function<Integer, Double>() {
   *       public Double apply(Integer in) {
   *         return Math.sqrt((int) in);
   *       }
   *     };
   * NavigableMap<String, Double> transformed =
   *      Maps.transformNavigableValues(map, sqrt);
   * System.out.println(transformed);
   * }</pre>
   *
   * ... prints {@code {a=2.0, b=3.0}}.
   *
   * <p>Changes in the underlying map are reflected in this view. Conversely, this view supports
   * removal operations, and these are reflected in the underlying map.
   *
   * <p>It's acceptable for the underlying map to contain null keys, and even null values provided
   * that the function is capable of accepting null input. The transformed map might contain null
   * values, if the function sometimes gives a null result.
   *
   * <p>The returned map is not thread-safe or serializable, even if the underlying map is.
   *
   * <p>The function is applied lazily, invoked when needed. This is necessary for the returned map
   * to be a view, but it means that the function will be applied many times for bulk operations
   * like {@link Map#containsValue} and {@code Map.toString()}. For this to perform well, {@code
   * function} should be fast. To avoid lazy evaluation when the returned map doesn't need to be a
   * view, copy the returned map into a new map of your choosing.
   *
   * @since 13.0
   */
   // NavigableMap
  public static <
          K extends Object, V1 extends Object, V2 extends Object>
      NavigableMap<K, V2> transformValues(
          NavigableMap<K, V1> fromMap, Function<? super V1, V2> function) {
    return transformEntries(fromMap, asEntryTransformer(function));
  }

  /**
   * Returns a view of a map whose values are derived from the original map's entries. In contrast
   * to {@link #transformValues}, this method's entry-transformation logic may depend on the key as
   * well as the value.
   *
   * <p>All other properties of the transformed map, such as iteration order, are left intact. For
   * example, the code:
   *
   * <pre>{@code
   * Map<String, Boolean> options =
   *     ImmutableMap.of("verbose", true, "sort", false);
   * EntryTransformer<String, Boolean, String> flagPrefixer =
   *     new EntryTransformer<String, Boolean, String>() {
   *       public String transformEntry(String key, Boolean value) {
   *         return value ? key : "no" + key;
   *       }
   *     };
   * Map<String, String> transformed =
   *     Maps.transformEntries(options, flagPrefixer);
   * System.out.println(transformed);
   * }</pre>
   *
   * ... prints {@code {verbose=verbose, sort=nosort}}.
   *
   * <p>Changes in the underlying map are reflected in this view. Conversely, this view supports
   * removal operations, and these are reflected in the underlying map.
   *
   * <p>It's acceptable for the underlying map to contain null keys and null values provided that
   * the transformer is capable of accepting null inputs. The transformed map might contain null
   * values if the transformer sometimes gives a null result.
   *
   * <p>The returned map is not thread-safe or serializable, even if the underlying map is.
   *
   * <p>The transformer is applied lazily, invoked when needed. This is necessary for the returned
   * map to be a view, but it means that the transformer will be applied many times for bulk
   * operations like {@link Map#containsValue} and {@link Object#toString}. For this to perform
   * well, {@code transformer} should be fast. To avoid lazy evaluation when the returned map
   * doesn't need to be a view, copy the returned map into a new map of your choosing.
   *
   * <p><b>Warning:</b> This method assumes that for any instance {@code k} of {@code
   * EntryTransformer} key type {@code K}, {@code k.equals(k2)} implies that {@code k2} is also of
   * type {@code K}. Using an {@code EntryTransformer} key type for which this may not hold, such as
   * {@code ArrayList}, may risk a {@code ClassCastException} when calling methods on the
   * transformed map.
   *
   * @since 7.0
   */
  public static <
          K extends Object, V1 extends Object, V2 extends Object>
      Map<K, V2> transformEntries(
          Map<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return new TransformedEntriesMap<>(fromMap, transformer);
  }

  /**
   * Returns a view of a sorted map whose values are derived from the original sorted map's entries.
   * In contrast to {@link #transformValues}, this method's entry-transformation logic may depend on
   * the key as well as the value.
   *
   * <p>All other properties of the transformed map, such as iteration order, are left intact. For
   * example, the code:
   *
   * <pre>{@code
   * Map<String, Boolean> options =
   *     ImmutableSortedMap.of("verbose", true, "sort", false);
   * EntryTransformer<String, Boolean, String> flagPrefixer =
   *     new EntryTransformer<String, Boolean, String>() {
   *       public String transformEntry(String key, Boolean value) {
   *         return value ? key : "yes" + key;
   *       }
   *     };
   * SortedMap<String, String> transformed =
   *     Maps.transformEntries(options, flagPrefixer);
   * System.out.println(transformed);
   * }</pre>
   *
   * ... prints {@code {sort=yessort, verbose=verbose}}.
   *
   * <p>Changes in the underlying map are reflected in this view. Conversely, this view supports
   * removal operations, and these are reflected in the underlying map.
   *
   * <p>It's acceptable for the underlying map to contain null keys and null values provided that
   * the transformer is capable of accepting null inputs. The transformed map might contain null
   * values if the transformer sometimes gives a null result.
   *
   * <p>The returned map is not thread-safe or serializable, even if the underlying map is.
   *
   * <p>The transformer is applied lazily, invoked when needed. This is necessary for the returned
   * map to be a view, but it means that the transformer will be applied many times for bulk
   * operations like {@link Map#containsValue} and {@link Object#toString}. For this to perform
   * well, {@code transformer} should be fast. To avoid lazy evaluation when the returned map
   * doesn't need to be a view, copy the returned map into a new map of your choosing.
   *
   * <p><b>Warning:</b> This method assumes that for any instance {@code k} of {@code
   * EntryTransformer} key type {@code K}, {@code k.equals(k2)} implies that {@code k2} is also of
   * type {@code K}. Using an {@code EntryTransformer} key type for which this may not hold, such as
   * {@code ArrayList}, may risk a {@code ClassCastException} when calling methods on the
   * transformed map.
   *
   * @since 11.0
   */
  public static <
          K extends Object, V1 extends Object, V2 extends Object>
      SortedMap<K, V2> transformEntries(
          SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return new TransformedEntriesSortedMap<K,V2>(fromMap, transformer);
  }

  /**
   * Returns a view of a navigable map whose values are derived from the original navigable map's
   * entries. In contrast to {@link #transformValues}, this method's entry-transformation logic may
   * depend on the key as well as the value.
   *
   * <p>All other properties of the transformed map, such as iteration order, are left intact. For
   * example, the code:
   *
   * <pre>{@code
   * NavigableMap<String, Boolean> options = Maps.newTreeMap();
   * options.put("verbose", false);
   * options.put("sort", true);
   * EntryTransformer<String, Boolean, String> flagPrefixer =
   *     new EntryTransformer<String, Boolean, String>() {
   *       public String transformEntry(String key, Boolean value) {
   *         return value ? key : ("yes" + key);
   *       }
   *     };
   * NavigableMap<String, String> transformed =
   *     LabsMaps.transformNavigableEntries(options, flagPrefixer);
   * System.out.println(transformed);
   * }</pre>
   *
   * ... prints {@code {sort=yessort, verbose=verbose}}.
   *
   * <p>Changes in the underlying map are reflected in this view. Conversely, this view supports
   * removal operations, and these are reflected in the underlying map.
   *
   * <p>It's acceptable for the underlying map to contain null keys and null values provided that
   * the transformer is capable of accepting null inputs. The transformed map might contain null
   * values if the transformer sometimes gives a null result.
   *
   * <p>The returned map is not thread-safe or serializable, even if the underlying map is.
   *
   * <p>The transformer is applied lazily, invoked when needed. This is necessary for the returned
   * map to be a view, but it means that the transformer will be applied many times for bulk
   * operations like {@link Map#containsValue} and {@link Object#toString}. For this to perform
   * well, {@code transformer} should be fast. To avoid lazy evaluation when the returned map
   * doesn't need to be a view, copy the returned map into a new map of your choosing.
   *
   * <p><b>Warning:</b> This method assumes that for any instance {@code k} of {@code
   * EntryTransformer} key type {@code K}, {@code k.equals(k2)} implies that {@code k2} is also of
   * type {@code K}. Using an {@code EntryTransformer} key type for which this may not hold, such as
   * {@code ArrayList}, may risk a {@code ClassCastException} when calling methods on the
   * transformed map.
   *
   * @since 13.0
   */
   // NavigableMap
  public static <
          K extends Object, V1 extends Object, V2 extends Object>
      NavigableMap<K, V2> transformEntries(
          NavigableMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer) {
    return new TransformedEntriesNavigableMap<>(fromMap, transformer);
  }

  /**
   * A transformation of the value of a key-value pair, using both key and value as inputs. To apply
   * the transformation to a map, use {@link MapUtil#transformEntries(Map, EntryTransformer)}.
   *
   * @param <K> the key type of the input and output entries
   * @param <V1> the value type of the input entry
   * @param <V2> the value type of the output entry
   * @since 7.0
   */
  @FunctionalInterface
  public interface EntryTransformer<
      K extends Object, V1 extends Object, V2 extends Object> {
    /**
     * Determines an output value based on a key-value pair. This method is <i>generally
     * expected</i>, but not absolutely required, to have the following properties:
     *
     * <ul>
     *   <li>Its execution does not cause any observable side effects.
     *   <li>The computation is <i>consistent with equals</i>; that is, {@link Objects#equal
     *       Objects.equal}{@code (k1, k2) &&} {@link Objects#equal}{@code (v1, v2)} implies that
     *       {@code Objects.equal(transformer.transform(k1, v1), transformer.transform(k2, v2))}.
     * </ul>
     *
     * @throws NullPointerException if the key or value is null and this transformer does not accept
     *     null arguments
     */
    V2 transformEntry(@ParametricNullness K key, @ParametricNullness V1 value);
  }

  /** Views a function as an entry transformer that ignores the entry key. */
  static <K extends Object, V1 extends Object, V2 extends Object>
      EntryTransformer<K, V1, V2> asEntryTransformer(final Function<? super V1, V2> function) {
    checkNotNull(function);
    return new EntryTransformer<K, V1, V2>() {
      @Override
      @ParametricNullness
      public V2 transformEntry(@ParametricNullness K key, @ParametricNullness V1 value) {
        return function.apply(value);
      }
    };
  }

  static <K extends Object, V1 extends Object, V2 extends Object>
      Function<V1, V2> asValueToValueFunction(
          final EntryTransformer<? super K, V1, V2> transformer, @ParametricNullness final K key) {
    checkNotNull(transformer);
    return new Function<V1, V2>() {
      @Override
      @ParametricNullness
      public V2 apply(@ParametricNullness V1 v1) {
        return transformer.transformEntry(key, v1);
      }
    };
  }

  /** Views an entry transformer as a function from {@code Entry} to values. */
  static <K extends Object, V1 extends Object, V2 extends Object>
      Function<Entry<K, V1>, V2> asEntryToValueFunction(
          final EntryTransformer<? super K, ? super V1, V2> transformer) {
    checkNotNull(transformer);
    return new Function<Entry<K, V1>, V2>() {
      @Override
      @ParametricNullness
      public V2 apply(Entry<K, V1> entry) {
        return transformer.transformEntry(entry.getKey(), entry.getValue());
      }
    };
  }

  /** Returns a view of an entry transformed by the specified transformer. */
  static <V2 extends Object, K extends Object, V1 extends Object>
      Entry<K, V2> transformEntry(
          final EntryTransformer<? super K, ? super V1, V2> transformer, final Entry<K, V1> entry) {
    checkNotNull(transformer);
    checkNotNull(entry);
    return new AbstractMapEntry<K, V2>() {
      @Override
      @ParametricNullness
      public K getKey() {
        return entry.getKey();
      }

      @Override
      @ParametricNullness
      public V2 getValue() {
        return transformer.transformEntry(entry.getKey(), entry.getValue());
      }
    };
  }

  /** Views an entry transformer as a function from entries to entries. */
  static <K extends Object, V1 extends Object, V2 extends Object>
      Function<Entry<K, V1>, Entry<K, V2>> asEntryToEntryFunction(
          final EntryTransformer<? super K, ? super V1, V2> transformer) {
    checkNotNull(transformer);
    return new Function<Entry<K, V1>, Entry<K, V2>>() {
      @Override
      public Entry<K, V2> apply(final Entry<K, V1> entry) {
        return transformEntry(transformer, entry);
      }
    };
  }


  static <K extends Object> Predicate<Entry<K, ?>> keyPredicateOnEntries(
      Predicate<? super K> keyPredicate) {
    return compose(keyPredicate, com.whaleal.icefrog.core.map.MapUtil.<K>keyFunction());
  }

  static <V extends Object> Predicate<Entry<?, V>> valuePredicateOnEntries(
      Predicate<? super V> valuePredicate) {
    return compose(valuePredicate, MapUtil.<V>valueFunction());
  }

  /**
   * Returns a map containing the mappings in {@code unfiltered} whose keys satisfy a predicate. The
   * returned map is a live view of {@code unfiltered}; changes to one affect the other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a key that doesn't satisfy the predicate, the map's {@code put()} and
   * {@code putAll()} methods throw an {@link IllegalArgumentException}.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings whose keys satisfy the filter will be removed from the underlying
   * map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code keyPredicate} must be <i>consistent with equals</i>, as documented at
   * {@link Predicate#apply}. Do not provide a predicate such as {@code
   * Predicates.instanceOf(ArrayList.class)}, which is inconsistent with equals.
   */
  public static <K extends Object, V extends Object> Map<K, V> filterKeys(
      Map<K, V> unfiltered, final Predicate<? super K> keyPredicate) {
    checkNotNull(keyPredicate);
    Predicate<Entry<K, ?>> entryPredicate = keyPredicateOnEntries(keyPredicate);
    return (unfiltered instanceof AbstractFilteredMap)
        ? filterFiltered((AbstractFilteredMap<K, V>) unfiltered, entryPredicate)
        : new FilteredKeyMap<K, V>(checkNotNull(unfiltered), keyPredicate, entryPredicate);
  }

  /**
   * Returns a sorted map containing the mappings in {@code unfiltered} whose keys satisfy a
   * predicate. The returned map is a live view of {@code unfiltered}; changes to one affect the
   * other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a key that doesn't satisfy the predicate, the map's {@code put()} and
   * {@code putAll()} methods throw an {@link IllegalArgumentException}.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings whose keys satisfy the filter will be removed from the underlying
   * map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code keyPredicate} must be <i>consistent with equals</i>, as documented at
   * {@link Predicate#apply}. Do not provide a predicate such as {@code
   * Predicates.instanceOf(ArrayList.class)}, which is inconsistent with equals.
   *
   * @since 11.0
   */
  public static <K extends Object, V extends Object> SortedMap<K, V> filterKeys(
      SortedMap<K, V> unfiltered, final Predicate<? super K> keyPredicate) {
    // TODO(lowasser): Return a subclass of Maps.FilteredKeyMap for slightly better
    // performance.
    return filterEntries(unfiltered, MapUtil.<K>keyPredicateOnEntries(keyPredicate));
  }

  /**
   * Returns a navigable map containing the mappings in {@code unfiltered} whose keys satisfy a
   * predicate. The returned map is a live view of {@code unfiltered}; changes to one affect the
   * other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a key that doesn't satisfy the predicate, the map's {@code put()} and
   * {@code putAll()} methods throw an {@link IllegalArgumentException}.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings whose keys satisfy the filter will be removed from the underlying
   * map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code keyPredicate} must be <i>consistent with equals</i>, as documented at
   * {@link Predicate#apply}. Do not provide a predicate such as {@code
   * Predicates.instanceOf(ArrayList.class)}, which is inconsistent with equals.
   *
   * @since 14.0
   */
   // NavigableMap
  public static <K extends Object, V extends Object>
      NavigableMap<K, V> filterKeys(
          NavigableMap<K, V> unfiltered, final Predicate<? super K> keyPredicate) {
    // TODO(lowasser): Return a subclass of Maps.FilteredKeyMap for slightly better
    // performance.
    return filterEntries(unfiltered, MapUtil.<K>keyPredicateOnEntries(keyPredicate));
  }

  /**
   * Returns a bimap containing the mappings in {@code unfiltered} whose keys satisfy a predicate.
   * The returned bimap is a live view of {@code unfiltered}; changes to one affect the other.
   *
   * <p>The resulting bimap's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the bimap
   * and its views. When given a key that doesn't satisfy the predicate, the bimap's {@code put()},
   * {@code forcePut()} and {@code putAll()} methods throw an {@link IllegalArgumentException}.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered
   * bimap or its views, only mappings that satisfy the filter will be removed from the underlying
   * bimap.
   *
   * <p>The returned bimap isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered bimap's methods, such as {@code size()}, iterate across every key in
   * the underlying bimap and determine which satisfy the filter. When a live view is <i>not</i>
   * needed, it may be faster to copy the filtered bimap and use the copy.
   *
   * <p><b>Warning:</b> {@code entryPredicate} must be <i>consistent with equals </i>, as documented
   * at {@link Predicate#apply}.
   *
   * @since 14.0
   */
  public static <K extends Object, V extends Object> BiMap<K, V> filterKeys(
      BiMap<K, V> unfiltered, final Predicate<? super K> keyPredicate) {
    checkNotNull(keyPredicate);
    return filterEntries(unfiltered, MapUtil.<K>keyPredicateOnEntries(keyPredicate));
  }

  /**
   * Returns a map containing the mappings in {@code unfiltered} whose values satisfy a predicate.
   * The returned map is a live view of {@code unfiltered}; changes to one affect the other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a value that doesn't satisfy the predicate, the map's {@code put()},
   * {@code putAll()}, and {@link Entry#setValue} methods throw an {@link IllegalArgumentException}.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings whose values satisfy the filter will be removed from the underlying
   * map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code valuePredicate} must be <i>consistent with equals</i>, as documented
   * at {@link Predicate#apply}. Do not provide a predicate such as {@code
   * Predicates.instanceOf(ArrayList.class)}, which is inconsistent with equals.
   */
  public static <K extends Object, V extends Object> Map<K, V> filterValues(
      Map<K, V> unfiltered, final Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, MapUtil.<V>valuePredicateOnEntries(valuePredicate));
  }

  /**
   * Returns a sorted map containing the mappings in {@code unfiltered} whose values satisfy a
   * predicate. The returned map is a live view of {@code unfiltered}; changes to one affect the
   * other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a value that doesn't satisfy the predicate, the map's {@code put()},
   * {@code putAll()}, and {@link Entry#setValue} methods throw an {@link IllegalArgumentException}.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings whose values satisfy the filter will be removed from the underlying
   * map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code valuePredicate} must be <i>consistent with equals</i>, as documented
   * at {@link Predicate#apply}. Do not provide a predicate such as {@code
   * Predicates.instanceOf(ArrayList.class)}, which is inconsistent with equals.
   *
   * @since 11.0
   */
  public static <K extends Object, V extends Object>
      SortedMap<K, V> filterValues(
          SortedMap<K, V> unfiltered, final Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, MapUtil.<V>valuePredicateOnEntries(valuePredicate));
  }

  /**
   * Returns a navigable map containing the mappings in {@code unfiltered} whose values satisfy a
   * predicate. The returned map is a live view of {@code unfiltered}; changes to one affect the
   * other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a value that doesn't satisfy the predicate, the map's {@code put()},
   * {@code putAll()}, and {@link Entry#setValue} methods throw an {@link IllegalArgumentException}.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings whose values satisfy the filter will be removed from the underlying
   * map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code valuePredicate} must be <i>consistent with equals</i>, as documented
   * at {@link Predicate#apply}. Do not provide a predicate such as {@code
   * Predicates.instanceOf(ArrayList.class)}, which is inconsistent with equals.
   *
   * @since 14.0
   */
   // NavigableMap
  public static <K extends Object, V extends Object>
      NavigableMap<K, V> filterValues(
          NavigableMap<K, V> unfiltered, final Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, MapUtil.<V>valuePredicateOnEntries(valuePredicate));
  }

  /**
   * Returns a bimap containing the mappings in {@code unfiltered} whose values satisfy a predicate.
   * The returned bimap is a live view of {@code unfiltered}; changes to one affect the other.
   *
   * <p>The resulting bimap's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the bimap
   * and its views. When given a value that doesn't satisfy the predicate, the bimap's {@code
   * put()}, {@code forcePut()} and {@code putAll()} methods throw an {@link
   * IllegalArgumentException}. Similarly, the map's entries have a {@link Entry#setValue} method
   * that throws an {@link IllegalArgumentException} when the provided value doesn't satisfy the
   * predicate.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered
   * bimap or its views, only mappings that satisfy the filter will be removed from the underlying
   * bimap.
   *
   * <p>The returned bimap isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered bimap's methods, such as {@code size()}, iterate across every value in
   * the underlying bimap and determine which satisfy the filter. When a live view is <i>not</i>
   * needed, it may be faster to copy the filtered bimap and use the copy.
   *
   * <p><b>Warning:</b> {@code entryPredicate} must be <i>consistent with equals </i>, as documented
   * at {@link Predicate#apply}.
   *
   * @since 14.0
   */
  public static <K extends Object, V extends Object> BiMap<K, V> filterValues(
      BiMap<K, V> unfiltered, final Predicate<? super V> valuePredicate) {
    return filterEntries(unfiltered, MapUtil.<V>valuePredicateOnEntries(valuePredicate));
  }

  /**
   * Returns a map containing the mappings in {@code unfiltered} that satisfy a predicate. The
   * returned map is a live view of {@code unfiltered}; changes to one affect the other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a key/value pair that doesn't satisfy the predicate, the map's {@code
   * put()} and {@code putAll()} methods throw an {@link IllegalArgumentException}. Similarly, the
   * map's entries have a {@link Entry#setValue} method that throws an {@link
   * IllegalArgumentException} when the existing key and the provided value don't satisfy the
   * predicate.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings that satisfy the filter will be removed from the underlying map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code entryPredicate} must be <i>consistent with equals</i>, as documented
   * at {@link Predicate#apply}.
   */
  public static <K extends Object, V extends Object> Map<K, V> filterEntries(
      Map<K, V> unfiltered, Predicate<? super Entry<K, V>> entryPredicate) {
    checkNotNull(entryPredicate);
    return (unfiltered instanceof AbstractFilteredMap)
        ? filterFiltered((AbstractFilteredMap<K, V>) unfiltered, entryPredicate)
        : new FilteredEntryMap<K, V>(checkNotNull(unfiltered), entryPredicate);
  }

  /**
   * Returns a sorted map containing the mappings in {@code unfiltered} that satisfy a predicate.
   * The returned map is a live view of {@code unfiltered}; changes to one affect the other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a key/value pair that doesn't satisfy the predicate, the map's {@code
   * put()} and {@code putAll()} methods throw an {@link IllegalArgumentException}. Similarly, the
   * map's entries have a {@link Entry#setValue} method that throws an {@link
   * IllegalArgumentException} when the existing key and the provided value don't satisfy the
   * predicate.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings that satisfy the filter will be removed from the underlying map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code entryPredicate} must be <i>consistent with equals</i>, as documented
   * at {@link Predicate#apply}.
   *
   * @since 11.0
   */
  public static <K extends Object, V extends Object>
      SortedMap<K, V> filterEntries(
          SortedMap<K, V> unfiltered, Predicate<? super Entry<K, V>> entryPredicate) {
    checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntrySortedMap)
        ? filterFiltered((FilteredEntrySortedMap<K, V>) unfiltered, entryPredicate)
        : new FilteredEntrySortedMap<K, V>(checkNotNull(unfiltered), entryPredicate);
  }

  /**
   * Returns a sorted map containing the mappings in {@code unfiltered} that satisfy a predicate.
   * The returned map is a live view of {@code unfiltered}; changes to one affect the other.
   *
   * <p>The resulting map's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the map
   * and its views. When given a key/value pair that doesn't satisfy the predicate, the map's {@code
   * put()} and {@code putAll()} methods throw an {@link IllegalArgumentException}. Similarly, the
   * map's entries have a {@link Entry#setValue} method that throws an {@link
   * IllegalArgumentException} when the existing key and the provided value don't satisfy the
   * predicate.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered map
   * or its views, only mappings that satisfy the filter will be removed from the underlying map.
   *
   * <p>The returned map isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered map's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying map and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered map and use the copy.
   *
   * <p><b>Warning:</b> {@code entryPredicate} must be <i>consistent with equals</i>, as documented
   * at {@link Predicate#apply}.
   *
   * @since 14.0
   */
   // NavigableMap
  public static <K extends Object, V extends Object>
      NavigableMap<K, V> filterEntries(
          NavigableMap<K, V> unfiltered, Predicate<? super Entry<K, V>> entryPredicate) {
    checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntryNavigableMap)
        ? filterFiltered((FilteredEntryNavigableMap<K, V>) unfiltered, entryPredicate)
        : new FilteredEntryNavigableMap<K, V>(this, checkNotNull(unfiltered), entryPredicate);
  }

  /**
   * Returns a bimap containing the mappings in {@code unfiltered} that satisfy a predicate. The
   * returned bimap is a live view of {@code unfiltered}; changes to one affect the other.
   *
   * <p>The resulting bimap's {@code keySet()}, {@code entrySet()}, and {@code values()} views have
   * iterators that don't support {@code remove()}, but all other methods are supported by the bimap
   * and its views. When given a key/value pair that doesn't satisfy the predicate, the bimap's
   * {@code put()}, {@code forcePut()} and {@code putAll()} methods throw an {@link
   * IllegalArgumentException}. Similarly, the map's entries have an {@link Entry#setValue} method
   * that throws an {@link IllegalArgumentException} when the existing key and the provided value
   * don't satisfy the predicate.
   *
   * <p>When methods such as {@code removeAll()} and {@code clear()} are called on the filtered
   * bimap or its views, only mappings that satisfy the filter will be removed from the underlying
   * bimap.
   *
   * <p>The returned bimap isn't threadsafe or serializable, even if {@code unfiltered} is.
   *
   * <p>Many of the filtered bimap's methods, such as {@code size()}, iterate across every key/value
   * mapping in the underlying bimap and determine which satisfy the filter. When a live view is
   * <i>not</i> needed, it may be faster to copy the filtered bimap and use the copy.
   *
   * <p><b>Warning:</b> {@code entryPredicate} must be <i>consistent with equals </i>, as documented
   * at {@link Predicate#apply}.
   *
   * @since 14.0
   */
  public static <K extends Object, V extends Object> BiMap<K, V> filterEntries(
      BiMap<K, V> unfiltered, Predicate<? super Entry<K, V>> entryPredicate) {
    checkNotNull(unfiltered);
    checkNotNull(entryPredicate);
    return (unfiltered instanceof FilteredEntryBiMap)
        ? filterFiltered((FilteredEntryBiMap<K, V>) unfiltered, entryPredicate)
        : new FilteredEntryBiMap<K, V>(unfiltered, entryPredicate);
  }

  /**
   * Support {@code clear()}, {@code removeAll()}, and {@code retainAll()} when filtering a filtered
   * map.
   */
  private static <K extends Object, V extends Object> Map<K, V> filterFiltered(
      AbstractFilteredMap<K, V> map, Predicate<? super Entry<K, V>> entryPredicate) {
    return new FilteredEntryMap<>(
        map.unfiltered, Predicates.<Entry<K, V>>and(map.predicate, entryPredicate));
  }

  /**
   * Support {@code clear()}, {@code removeAll()}, and {@code retainAll()} when filtering a filtered
   * sorted map.
   */
  private static <K extends Object, V extends Object>
      SortedMap<K, V> filterFiltered(
          FilteredEntrySortedMap<K, V> map, Predicate<? super Entry<K, V>> entryPredicate) {
    Predicate<Entry<K, V>> predicate = Predicates.<Entry<K, V>>and(map.predicate, entryPredicate);
    return new FilteredEntrySortedMap<>(map.sortedMap(), predicate);
  }

  /**
   * Support {@code clear()}, {@code removeAll()}, and {@code retainAll()} when filtering a filtered
   * navigable map.
   */
   // NavigableMap
  private static <K extends Object, V extends Object>
      NavigableMap<K, V> filterFiltered(
          FilteredEntryNavigableMap<K, V> map, Predicate<? super Entry<K, V>> entryPredicate) {
    Predicate<Entry<K, V>> predicate =
        Predicates.<Entry<K, V>>and(map.entryPredicate, entryPredicate);
    return new FilteredEntryNavigableMap<>(this, map.unfiltered, predicate);
  }

  /**
   * Support {@code clear()}, {@code removeAll()}, and {@code retainAll()} when filtering a filtered
   * map.
   */
  private static <K extends Object, V extends Object>
      BiMap<K, V> filterFiltered(
          FilteredEntryBiMap<K, V> map, Predicate<? super Entry<K, V>> entryPredicate) {
    Predicate<Entry<K, V>> predicate = PredicateUtil.<Entry<K, V>>and(map.predicate, entryPredicate);
    return new FilteredEntryBiMap<>(map.unfiltered(), predicate);
  }

  /**
   * Returns an unmodifiable view of the specified navigable map. Query operations on the returned
   * map read through to the specified map, and attempts to modify the returned map, whether direct
   * or via its views, result in an {@code UnsupportedOperationException}.
   *
   * <p>The returned navigable map will be serializable if the specified navigable map is
   * serializable.
   *
   * <p>This method's signature will not permit you to convert a {@code NavigableMap<? extends K,
   * V>} to a {@code NavigableMap<K, V>}. If it permitted this, the returned map's {@code
   * comparator()} method might return a {@code Comparator<? extends K>}, which works only on a
   * particular subtype of {@code K}, but promise that it's a {@code Comparator<? super K>}, which
   * must work on any type of {@code K}.
   *
   * @param map the navigable map for which an unmodifiable view is to be returned
   * @return an unmodifiable view of the specified navigable map
   * @since 12.0
   */
   // NavigableMap
  public static <K extends Object, V extends Object>
      NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, ? extends V> map) {
    checkNotNull(map);
    if (map instanceof UnmodifiableNavigableMap) {
      @SuppressWarnings("unchecked") // covariant
      NavigableMap<K, V> result = (NavigableMap<K, V>) map;
      return result;
    } else {
      return new UnmodifiableNavigableMap<>(map);
    }
  }

  @CheckForNull
  private static <K extends Object, V extends Object>
      Entry<K, V> unmodifiableOrNull(@CheckForNull Entry<K, ? extends V> entry) {
    return (entry == null) ? null : MapUtil.unmodifiableEntry(entry);
  }

  /**
   * Returns a synchronized (thread-safe) navigable map backed by the specified navigable map. In
   * order to guarantee serial access, it is critical that <b>all</b> access to the backing
   * navigable map is accomplished through the returned navigable map (or its views).
   *
   * <p>It is imperative that the user manually synchronize on the returned navigable map when
   * iterating over any of its collection views, or the collections views of any of its {@code
   * descendingMap}, {@code subMap}, {@code headMap} or {@code tailMap} views.
   *
   * <pre>{@code
   * NavigableMap<K, V> map = synchronizedNavigableMap(new TreeMap<K, V>());
   *
   * // Needn't be in synchronized block
   * NavigableSet<K> set = map.navigableKeySet();
   *
   * synchronized (map) { // Synchronizing on map, not set!
   *   Iterator<K> it = set.iterator(); // Must be in synchronized block
   *   while (it.hasNext()) {
   *     foo(it.next());
   *   }
   * }
   * }</pre>
   *
   * <p>or:
   *
   * <pre>{@code
   * NavigableMap<K, V> map = synchronizedNavigableMap(new TreeMap<K, V>());
   * NavigableMap<K, V> map2 = map.subMap(foo, false, bar, true);
   *
   * // Needn't be in synchronized block
   * NavigableSet<K> set2 = map2.descendingKeySet();
   *
   * synchronized (map) { // Synchronizing on map, not map2 or set2!
   *   Iterator<K> it = set2.iterator(); // Must be in synchronized block
   *   while (it.hasNext()) {
   *     foo(it.next());
   *   }
   * }
   * }</pre>
   *
   * <p>Failure to follow this advice may result in non-deterministic behavior.
   *
   * <p>The returned navigable map will be serializable if the specified navigable map is
   * serializable.
   *
   * @param navigableMap the navigable map to be "wrapped" in a synchronized navigable map.
   * @return a synchronized view of the specified navigable map.
   * @since 13.0
   */
   // NavigableMap
  public static <K extends Object, V extends Object>
      NavigableMap<K, V> synchronizedNavigableMap(NavigableMap<K, V> navigableMap) {
    return Synchronized.navigableMap(navigableMap);
  }

  /**
   * {@code AbstractMap} extension that makes it easy to cache customized keySet, values, and
   * entrySet views.
   */

  /**
   * Delegates to {@link Map#get}. Returns {@code null} on {@code ClassCastException} and {@code
   * NullPointerException}.
   */
  @CheckForNull
  static <V extends Object> V safeGet(Map<?, V> map, @CheckForNull Object key) {
    checkNotNull(map);
    try {
      return map.get(key);
    } catch (ClassCastException | NullPointerException e) {
      return null;
    }
  }

  /**
   * Delegates to {@link Map#containsKey}. Returns {@code false} on {@code ClassCastException} and
   * {@code NullPointerException}.
   */
  static boolean safeContainsKey(Map<?, ?> map, @CheckForNull Object key) {
    checkNotNull(map);
    try {
      return map.containsKey(key);
    } catch (ClassCastException | NullPointerException e) {
      return false;
    }
  }

  /**
   * Delegates to {@link Map#remove}. Returns {@code null} on {@code ClassCastException} and {@code
   * NullPointerException}.
   */
  @CheckForNull
  static <V extends Object> V safeRemove(Map<?, V> map, @CheckForNull Object key) {
    checkNotNull(map);
    try {
      return map.remove(key);
    } catch (ClassCastException | NullPointerException e) {
      return null;
    }
  }

  /** An admittedly inefficient implementation of {@link Map#containsKey}. */
  static boolean containsKeyImpl(Map<?, ?> map, @CheckForNull Object key) {
    return IterUtil.contains(keyIterator(map.entrySet().iterator()), key);
  }

  /** An implementation of {@link Map#containsValue}. */
  static boolean containsValueImpl(Map<?, ?> map, @CheckForNull Object value) {
    return IterUtil.contains(valueIterator(map.entrySet().iterator()), value);
  }

  /**
   * Implements {@code Collection.contains} safely for forwarding collections of map entries. If
   * {@code o} is an instance of {@code Entry}, it is wrapped using {@link #unmodifiableEntry} to
   * protect against a possible nefarious equals method.
   *
   * <p>Note that {@code c} is the backing (delegate) collection, rather than the forwarding
   * collection.
   *
   * @param c the delegate (unwrapped) collection of map entries
   * @param o the object that might be contained in {@code c}
   * @return {@code true} if {@code c} contains {@code o}
   */
  static <K extends Object, V extends Object> boolean containsEntryImpl(
      Collection<Entry<K, V>> c, @CheckForNull Object o) {
    if (!(o instanceof Entry)) {
      return false;
    }
    return c.contains(unmodifiableEntry((Entry<?, ?>) o));
  }

  /**
   * Implements {@code Collection.remove} safely for forwarding collections of map entries. If
   * {@code o} is an instance of {@code Entry}, it is wrapped using {@link #unmodifiableEntry} to
   * protect against a possible nefarious equals method.
   *
   * <p>Note that {@code c} is backing (delegate) collection, rather than the forwarding collection.
   *
   * @param c the delegate (unwrapped) collection of map entries
   * @param o the object to remove from {@code c}
   * @return {@code true} if {@code c} was changed
   */
  static <K extends Object, V extends Object> boolean removeEntryImpl(
      Collection<Entry<K, V>> c, @CheckForNull Object o) {
    if (!(o instanceof Entry)) {
      return false;
    }
    return c.remove(unmodifiableEntry((Entry<?, ?>) o));
  }

  /** An implementation of {@link Map#equals}. */
  static boolean equalsImpl(Map<?, ?> map, @CheckForNull Object object) {
    if (map == object) {
      return true;
    } else if (object instanceof Map) {
      Map<?, ?> o = (Map<?, ?>) object;
      return map.entrySet().equals(o.entrySet());
    }
    return false;
  }

  /** An implementation of {@link Map#toString}. */
  static String toStringImpl(Map<?, ?> map) {
    StringBuilder sb = CollUtil.newStringBuilderForCollection(map.size()).append('{');
    boolean first = true;
    for (Entry<?, ?> entry : map.entrySet()) {
      if (!first) {
        sb.append(", ");
      }
      first = false;
      sb.append(entry.getKey()).append('=').append(entry.getValue());
    }
    return sb.append('}').toString();
  }

  /** An implementation of {@link Map#putAll}. */
  static <K extends Object, V extends Object> void putAllImpl(
      Map<K, V> self, Map<? extends K, ? extends V> map) {
    for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
      self.put(entry.getKey(), entry.getValue());
    }
  }


  @CheckForNull
  static <K extends Object> K keyOrNull(@CheckForNull Entry<K, ?> entry) {
    return (entry == null) ? null : entry.getKey();
  }

  @CheckForNull
  static <V extends Object> V valueOrNull(@CheckForNull Entry<?, V> entry) {
    return (entry == null) ? null : entry.getValue();
  }

  /** Returns a map from the ith element of list to i. */
  static <E> ImmutableMap<E, Integer> indexMap(Collection<E> list) {
    ImmutableMap.Builder<E, Integer> builder = new ImmutableMap.Builder<>(list.size());
    int i = 0;
    for (E e : list) {
      builder.put(e, i++);
    }
    return builder.build();
  }

  /**
   * Returns a view of the portion of {@code map} whose keys are contained by {@code range}.
   *
   * <p>This method delegates to the appropriate methods of {@link NavigableMap} (namely {@link
   * NavigableMap#subMap(Object, boolean, Object, boolean) subMap()}, {@link
   * NavigableMap#tailMap(Object, boolean) tailMap()}, and {@link NavigableMap#headMap(Object,
   * boolean) headMap()}) to actually construct the view. Consult these methods for a full
   * description of the returned view's behavior.
   *
   * <p><b>Warning:</b> {@code Range}s always represent a range of values using the values' natural
   * ordering. {@code NavigableMap} on the other hand can specify a custom ordering via a {@link
   * Comparator}, which can violate the natural ordering. Using this method (or in general using
   * {@code Range}) with unnaturally-ordered maps can lead to unexpected and undefined behavior.
   *
   * @since 20.0
   */
  @Beta
   // NavigableMap
  public static <K extends Comparable<? super K>, V extends Object>
      NavigableMap<K, V> subMap(NavigableMap<K, V> map, Range<K> range) {
    if (map.comparator() != null
        && map.comparator() != Ordering.natural()
        && range.hasLowerBound()
        && range.hasUpperBound()) {
      checkArgument(
          map.comparator().compare(range.lowerEndpoint(), range.upperEndpoint()) <= 0,
          "map is using a custom comparator which is inconsistent with the natural ordering.");
    }
    if (range.hasLowerBound() && range.hasUpperBound()) {
      return map.subMap(
          range.lowerEndpoint(),
          range.lowerBoundType() == BoundType.CLOSED,
          range.upperEndpoint(),
          range.upperBoundType() == BoundType.CLOSED);
    } else if (range.hasLowerBound()) {
      return map.tailMap(range.lowerEndpoint(), range.lowerBoundType() == BoundType.CLOSED);
    } else if (range.hasUpperBound()) {
      return map.headMap(range.upperEndpoint(), range.upperBoundType() == BoundType.CLOSED);
    }
    return checkNotNull(map);
  }
}
