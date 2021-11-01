
package com.whaleal.icefrog.collections;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;

/**
 * Abstract superclass for tests that hash flooding a collection has controlled worst-case
 * performance.
 */

public abstract class AbstractHashFloodingTest<T> extends TestCase {
  @Test
  public void test(){
  }
  private final List<Construction<T>> constructions;
  private final IntToDoubleFunction constructionAsymptotics;
  private final List<QueryOp<T>> queries;

  AbstractHashFloodingTest(
      List<Construction<T>> constructions,
      IntToDoubleFunction constructionAsymptotics,
      List<QueryOp<T>> queries) {
    this.constructions = constructions;
    this.constructionAsymptotics = constructionAsymptotics;
    this.queries = queries;
  }

  /**
   * A Comparable wrapper around a String which executes callbacks on calls to hashCode, equals, and
   * compareTo.
   */
  private static class CountsHashCodeAndEquals implements Comparable<CountsHashCodeAndEquals> {
    private final String delegateString;
    private final Runnable onHashCode;
    private final Runnable onEquals;
    private final Runnable onCompareTo;

    CountsHashCodeAndEquals(
        String delegateString, Runnable onHashCode, Runnable onEquals, Runnable onCompareTo) {
      this.delegateString = delegateString;
      this.onHashCode = onHashCode;
      this.onEquals = onEquals;
      this.onCompareTo = onCompareTo;
    }

    @Override
    public int hashCode() {
      onHashCode.run();
      return delegateString.hashCode();
    }

    @Override
    public boolean equals(Object other) {
      onEquals.run();
      return other instanceof CountsHashCodeAndEquals
          && delegateString.equals(((CountsHashCodeAndEquals) other).delegateString);
    }

    @Override
    public int compareTo(CountsHashCodeAndEquals o) {
      onCompareTo.run();
      return delegateString.compareTo(o.delegateString);
    }
  }

  /** A holder of counters for calls to hashCode, equals, and compareTo. */
  private static final class CallsCounter {
    long hashCode;
    long equals;
    long compareTo;

    long total() {
      return hashCode + equals + compareTo;
    }

    void zero() {
      hashCode = 0;
      equals = 0;
      compareTo = 0;
    }
  }

  @FunctionalInterface
  interface Construction<T> {

    abstract T create(List<?> keys);

    static Construction<Map<Object, Object>> mapFromKeys(
        Supplier<Map<Object, Object>> mutableSupplier) {
      return keys -> {
        Map<Object, Object> map = mutableSupplier.get();
        for (Object key : keys) {
          map.put(key, new Object());
        }
        return map;
      };
    }

    static Construction<Set<Object>> setFromElements(Supplier<Set<Object>> mutableSupplier) {
      return elements -> {
        Set<Object> set = mutableSupplier.get();
        set.addAll(elements);
        return set;
      };
    }
  }

  abstract static class QueryOp<T> {
    static <T> QueryOp<T> create(
        String name, BiConsumer<T, Object> queryLambda, IntToDoubleFunction asymptotic) {
      return new QueryOp<T>() {
        @Override
        void apply(T collection, Object query) {
          queryLambda.accept(collection, query);
        }

        @Override
        double expectedAsymptotic(int n) {
          return asymptotic.applyAsDouble(n);
        }

        @Override
        public String toString() {
          return name;
        }
      };
    }

    static final QueryOp<Map<Object, Object>> MAP_GET =
        QueryOp.create("Map.get", Map::get, Math::log);

    @SuppressWarnings("ReturnValueIgnored")
    static final QueryOp<Set<Object>> SET_CONTAINS =
        QueryOp.create("Set.contains", Set::contains, Math::log);

    abstract void apply(T collection, Object query);

    abstract double expectedAsymptotic(int n);
  }







  private long getWorstCaseOps(
      CallsCounter counter,
      List<CountsHashCodeAndEquals> haveSameHashes,
      QueryOp<T> query,
      Construction<T> pathway) {
    T collection = pathway.create(haveSameHashes);
    long worstOps = 0;
    for (Object o : haveSameHashes) {
      counter.zero();
      query.apply(collection, o);
      worstOps = Math.max(worstOps, counter.total());
    }
    return worstOps;
  }
}
