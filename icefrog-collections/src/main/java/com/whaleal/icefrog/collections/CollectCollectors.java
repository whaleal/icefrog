

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.lang.Preconditions;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;


/** Collectors utilities for {@code common.collect} internals. */


final class CollectCollectors {

  private static final Collector<Object, ?, ImmutableList<Object>> TO_IMMUTABLE_LIST =
      Collector.of(
          ImmutableList::builder,
          ImmutableList.Builder::add,
          ImmutableList.Builder::combine,
          ImmutableList.Builder::build);

  private static final Collector<Object, ?, ImmutableSet<Object>> TO_IMMUTABLE_SET =
      Collector.of(
          ImmutableSet::builder,
          ImmutableSet.Builder::add,
          ImmutableSet.Builder::combine,
          ImmutableSet.Builder::build);


  private static final Collector<Range<Comparable<?>>, ?, ImmutableRangeSet<Comparable<?>>>
      TO_IMMUTABLE_RANGE_SET =
          Collector.of(
              ImmutableRangeSet::builder,
              ImmutableRangeSet.Builder::add,
              ImmutableRangeSet.Builder::combine,
              ImmutableRangeSet.Builder::build);

  // Lists

  @SuppressWarnings({"rawtypes", "unchecked"})
  static <E> Collector<E, ?, ImmutableList<E>> toImmutableList() {
    return (Collector) TO_IMMUTABLE_LIST;
  }

  // Sets

  @SuppressWarnings({"rawtypes", "unchecked"})
  static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
    return (Collector) TO_IMMUTABLE_SET;
  }

  static <E> Collector<E, ?, ImmutableSortedSet<E>> toImmutableSortedSet(
      Comparator<? super E> comparator) {
    checkNotNull(comparator);
    return Collector.of(
        () -> new ImmutableSortedSet.Builder<E>(comparator),
        ImmutableSortedSet.Builder::add,
        ImmutableSortedSet.Builder::combine,
        ImmutableSortedSet.Builder::build);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  static <E extends Enum<E>> Collector<E, ?, ImmutableSet<E>> toImmutableEnumSet() {
    return (Collector) EnumSetAccumulator.TO_IMMUTABLE_ENUM_SET;
  }

  private static final class EnumSetAccumulator<E extends Enum<E>> {
    @SuppressWarnings({"rawtypes", "unchecked"})
    static final Collector<Enum<?>, ?, ImmutableSet<? extends Enum<?>>> TO_IMMUTABLE_ENUM_SET =
        (Collector)
            Collector.<Enum, EnumSetAccumulator, ImmutableSet<?>>of(
                EnumSetAccumulator::new,
                EnumSetAccumulator::add,
                EnumSetAccumulator::combine,
                EnumSetAccumulator::toImmutableSet,
                Collector.Characteristics.UNORDERED);

    @CheckForNull private EnumSet<E> set;

    void add(E e) {
      if (set == null) {
        set = EnumSet.of(e);
      } else {
        set.add(e);
      }
    }

    EnumSetAccumulator<E> combine(EnumSetAccumulator<E> other) {
      if (this.set == null) {
        return other;
      } else if (other.set == null) {
        return this;
      } else {
        this.set.addAll(other.set);
        return this;
      }
    }

    ImmutableSet<E> toImmutableSet() {
      return (set == null) ? ImmutableSet.of() : ImmutableEnumSet.asImmutable(set);
    }
  }


  @SuppressWarnings({"rawtypes", "unchecked"})
  static <E extends Comparable<? super E>>
      Collector<Range<E>, ?, ImmutableRangeSet<E>> toImmutableRangeSet() {
    return (Collector) TO_IMMUTABLE_RANGE_SET;
  }

  // Multisets

  static <T extends Object, E> Collector<T, ?, ImmutableMultiset<E>> toImmutableMultiset(
      Function<? super T, ? extends E> elementFunction, ToIntFunction<? super T> countFunction) {
    checkNotNull(elementFunction);
    checkNotNull(countFunction);
    return Collector.of(
        LinkedHashMultiset::create,
        (multiset, t) ->
            multiset.add(checkNotNull(elementFunction.apply(t)), countFunction.applyAsInt(t)),
        (multiset1, multiset2) -> {
          multiset1.addAll(multiset2);
          return multiset1;
        },
        (Multiset<E> multiset) -> ImmutableMultiset.copyFromEntries(multiset.entrySet()));
  }

  static <T extends Object, E extends Object, M extends Multiset<E>>
      Collector<T, ?, M> toMultiset(
          Function<? super T, E> elementFunction,
          ToIntFunction<? super T> countFunction,
          Supplier<M> multisetSupplier) {
    checkNotNull(elementFunction);
    checkNotNull(countFunction);
    checkNotNull(multisetSupplier);
    return Collector.of(
        multisetSupplier,
        (ms, t) -> ms.add(elementFunction.apply(t), countFunction.applyAsInt(t)),
        (ms1, ms2) -> {
          ms1.addAll(ms2);
          return ms1;
        });
  }

  // MapUtil

  static <T extends Object, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
      Function<? super T, ? extends K> keyFunction,
      Function<? super T, ? extends V> valueFunction) {
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    return Collector.of(
        ImmutableMap.Builder<K, V>::new,
        (builder, input) -> builder.put(keyFunction.apply(input), valueFunction.apply(input)),
        ImmutableMap.Builder::combine,
        ImmutableMap.Builder::build);
  }

  public static <T extends Object, K, V>
      Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction,
          BinaryOperator<V> mergeFunction) {
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    checkNotNull(mergeFunction);
    return Collectors.collectingAndThen(
        Collectors.toMap(keyFunction, valueFunction, mergeFunction, LinkedHashMap::new),
        ImmutableMap::copyOf);
  }

  static <T extends Object, K, V>
      Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
          Comparator<? super K> comparator,
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    checkNotNull(comparator);
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    /*
     * We will always fail if there are duplicate keys, and the keys are always sorted by
     * the Comparator, so the entries can come in an arbitrary order -- so we report UNORDERED.
     */
    return Collector.of(
        () -> new ImmutableSortedMap.Builder<K, V>(comparator),
        (builder, input) -> builder.put(keyFunction.apply(input), valueFunction.apply(input)),
        ImmutableSortedMap.Builder::combine,
        ImmutableSortedMap.Builder::build,
        Collector.Characteristics.UNORDERED);
  }

  static <T extends Object, K, V>
      Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(
          Comparator<? super K> comparator,
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction,
          BinaryOperator<V> mergeFunction) {
    checkNotNull(comparator);
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    checkNotNull(mergeFunction);
    return Collectors.collectingAndThen(
        Collectors.toMap(
            keyFunction, valueFunction, mergeFunction, () -> new TreeMap<K, V>(comparator)),
        ImmutableSortedMap::copyOfSorted);
  }



  static <T extends Object, K extends Enum<K>, V>
      Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    return Collector.of(
        () ->
            new EnumMapAccumulator<K, V>(
                (v1, v2) -> {
                  throw new IllegalArgumentException("Multiple values for key: " + v1 + ", " + v2);
                }),
        (accum, t) -> {
          /*
           * We assign these to variables before calling checkNotNull to work around a bug in our
           * nullness checker.
           */
          K key = keyFunction.apply(t);
          V newValue = valueFunction.apply(t);
          accum.put(
              checkNotNull(key, "Null key for input %s", t),
              checkNotNull(newValue, "Null value for input %s", t));
        },
        EnumMapAccumulator::combine,
        EnumMapAccumulator::toImmutableMap,
        Collector.Characteristics.UNORDERED);
  }

  static <T extends Object, K extends Enum<K>, V>
      Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction,
          BinaryOperator<V> mergeFunction) {
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    checkNotNull(mergeFunction);
    // not UNORDERED because we don't know if mergeFunction is commutative
    return Collector.of(
        () -> new EnumMapAccumulator<K, V>(mergeFunction),
        (accum, t) -> {
          /*
           * We assign these to variables before calling checkNotNull to work around a bug in our
           * nullness checker.
           */
          K key = keyFunction.apply(t);
          V newValue = valueFunction.apply(t);
          accum.put(
              checkNotNull(key, "Null key for input %s", t),
              checkNotNull(newValue, "Null value for input %s", t));
        },
        EnumMapAccumulator::combine,
        EnumMapAccumulator::toImmutableMap);
  }

  private static class EnumMapAccumulator<K extends Enum<K>, V> {
    private final BinaryOperator<V> mergeFunction;
    @CheckForNull private EnumMap<K, V> map = null;

    EnumMapAccumulator(BinaryOperator<V> mergeFunction) {
      this.mergeFunction = mergeFunction;
    }

    void put(K key, V value) {
      if (map == null) {
        map = new EnumMap<>(key.getDeclaringClass());
      }
      map.merge(key, value, mergeFunction);
    }

    EnumMapAccumulator<K, V> combine(EnumMapAccumulator<K, V> other) {
      if (this.map == null) {
        return other;
      } else if (other.map == null) {
        return this;
      } else {
        other.map.forEach(this::put);
        return this;
      }
    }

    ImmutableMap<K, V> toImmutableMap() {
      //return (map == null) ? ImmutableMap.of() : ImmutableEnumMap.asImmutable(map);
      return null ;
    }
  }


  static <T extends Object, K extends Comparable<? super K>, V>
      Collector<T, ?, ImmutableRangeMap<K, V>> toImmutableRangeMap(
          Function<? super T, Range<K>> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    return Collector.of(
        ImmutableRangeMap::<K, V>builder,
        (builder, input) -> builder.put(keyFunction.apply(input), valueFunction.apply(input)),
        ImmutableRangeMap.Builder::combine,
        ImmutableRangeMap.Builder::build);
  }

  // Multimaps

  static <T extends Object, K, V>
      Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultimap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    checkNotNull(keyFunction, "keyFunction");
    checkNotNull(valueFunction, "valueFunction");
    return Collector.of(
        ImmutableListMultimap::<K, V>builder,
        (builder, t) -> builder.put(keyFunction.apply(t), valueFunction.apply(t)),
        ImmutableListMultimap.Builder::combine,
        ImmutableListMultimap.Builder::build);
  }

  static <T extends Object, K, V>
      Collector<T, ?, ImmutableListMultimap<K, V>> flatteningToImmutableListMultimap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends Stream<? extends V>> valuesFunction) {
    checkNotNull(keyFunction);
    checkNotNull(valuesFunction);
    return Collectors.collectingAndThen(
        flatteningToMultimap(
            input -> checkNotNull(keyFunction.apply(input)),
            input -> valuesFunction.apply(input).peek(Preconditions::checkNotNull),
            MultimapBuilder.linkedHashKeys().arrayListValues()::<K, V>build),
        ImmutableListMultimap::copyOf);
  }

  static <T extends Object, K, V>
      Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction) {
    checkNotNull(keyFunction, "keyFunction");
    checkNotNull(valueFunction, "valueFunction");
    return Collector.of(
        ImmutableSetMultimap::<K, V>builder,
        (builder, t) -> builder.put(keyFunction.apply(t), valueFunction.apply(t)),
        ImmutableSetMultimap.Builder::combine,
        ImmutableSetMultimap.Builder::build);
  }

  static <T extends Object, K, V>
      Collector<T, ?, ImmutableSetMultimap<K, V>> flatteningToImmutableSetMultimap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends Stream<? extends V>> valuesFunction) {
    checkNotNull(keyFunction);
    checkNotNull(valuesFunction);
    return Collectors.collectingAndThen(
        flatteningToMultimap(
            input -> checkNotNull(keyFunction.apply(input)),
            input -> valuesFunction.apply(input).peek(Preconditions::checkNotNull),
            MultimapBuilder.linkedHashKeys().linkedHashSetValues()::<K, V>build),
        ImmutableSetMultimap::copyOf);
  }

  static <
          T extends Object,
          K extends Object,
          V extends Object,
          M extends Multimap<K, V>>
      Collector<T, ?, M> toMultimap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends V> valueFunction,
          Supplier<M> Multimapsupplier) {
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    checkNotNull(Multimapsupplier);
    return Collector.of(
        Multimapsupplier,
        (multimap, input) -> multimap.put(keyFunction.apply(input), valueFunction.apply(input)),
        (multimap1, multimap2) -> {
          multimap1.putAll(multimap2);
          return multimap1;
        });
  }

  static <
          T extends Object,
          K extends Object,
          V extends Object,
          M extends Multimap<K, V>>
      Collector<T, ?, M> flatteningToMultimap(
          Function<? super T, ? extends K> keyFunction,
          Function<? super T, ? extends Stream<? extends V>> valueFunction,
          Supplier<M> Multimapsupplier) {
    checkNotNull(keyFunction);
    checkNotNull(valueFunction);
    checkNotNull(Multimapsupplier);
    return Collector.of(
        Multimapsupplier,
        (multimap, input) -> {
          K key = keyFunction.apply(input);
          Collection<V> valuesForKey = multimap.get(key);
          valueFunction.apply(input).forEachOrdered(valuesForKey::add);
        },
        (multimap1, multimap2) -> {
          multimap1.putAll(multimap2);
          return multimap1;
        });
  }
}
