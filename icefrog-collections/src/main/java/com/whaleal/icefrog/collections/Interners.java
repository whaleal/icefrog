

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;











import javax.annotation.CheckForNull;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;

/**
 * Contains static methods pertaining to instances of {@link Interner}.
 *
 * @author Kevin Bourrillion
 * 
 */


public final class Interners {
  private Interners() {}

  /**
   * Builder for {@link Interner} instances.
   *
   * 
   */
  public static class InternerBuilder {
    private final MapMaker mapMaker = new MapMaker();
    private boolean strong = true;

    private InternerBuilder() {}

    /**
     * Instructs the {@link InternerBuilder} to build a strong interner.
     *
     * @see Interners#newStrongInterner()
     */
    public InternerBuilder strong() {
      this.strong = true;
      return this;
    }

    /**
     * Instructs the {@link InternerBuilder} to build a weak interner.
     *
     * @see Interners#newWeakInterner()
     */

    public InternerBuilder weak() {
      this.strong = false;
      return this;
    }

    /**
     * Sets the concurrency level that will be used by the to-be-built {@link Interner}.
     *
     * @see MapMaker#concurrencyLevel(int)
     */
    public InternerBuilder concurrencyLevel(int concurrencyLevel) {
      this.mapMaker.concurrencyLevel(concurrencyLevel);
      return this;
    }

    public <E> Interner<E> build() {
      if (!strong) {
        mapMaker.weakKeys();
      }
      return new InternerImpl<>(mapMaker);
    }
  }

  /** Returns a fresh {@link InternerBuilder} instance. */
  public static InternerBuilder newBuilder() {
    return new InternerBuilder();
  }

  /**
   * Returns a new thread-safe interner which retains a strong reference to each instance it has
   * interned, thus preventing these instances from being garbage-collected. If this retention is
   * acceptable, this implementation may perform better than {@link #newWeakInterner}.
   */
  public static <E> Interner<E> newStrongInterner() {
    return newBuilder().strong().build();
  }

  /**
   * Returns a new thread-safe interner which retains a weak reference to each instance it has
   * interned, and so does not prevent these instances from being garbage-collected. This most
   * likely does not perform as well as {@link #newStrongInterner}, but is the best alternative when
   * the memory usage of that implementation is unacceptable.
   */

  public static <E> Interner<E> newWeakInterner() {
    return newBuilder().weak().build();
  }


  static final class InternerImpl<E> implements Interner<E> {
    // MapMaker is our friend, we know about this type
    final MapMakerInternalMap<E, MapMaker.Dummy, ?, ?> map;

    private InternerImpl(MapMaker mapMaker) {
      this.map =
              MapMakerInternalMap.createWithDummyValues(mapMaker.keyEquivalence(Equivalence.equals()));
    }

    @Override
    public E intern(E sample) {
      while (true) {
        // trying to read the canonical...
        @SuppressWarnings("rawtypes") // using raw types to avoid a bug in our nullness checker :(
        MapMakerInternalMap.InternalEntry entry = map.getEntry(sample);
        if (entry != null) {
          Object canonical = entry.getKey();
          if (canonical != null) { // only matters if weak/soft keys are used
            // The compiler would know this is safe if not for our use of raw types (see above).
            @SuppressWarnings("unchecked")
            E result = (E) canonical;
            return result;
          }
        }

        // didn't see it, trying to put it instead...
        MapMaker.Dummy sneaky = map.putIfAbsent(sample, MapMaker.Dummy.VALUE);
        if (sneaky == null) {
          return sample;
        } else {
          /* Someone beat us to it! Trying again...
           *
           * Technically this loop not guaranteed to terminate, so theoretically (extremely
           * unlikely) this thread might starve, but even then, there is always going to be another
           * thread doing progress here.
           */
        }
      }
    }
  }

  /**
   * Returns a function that delegates to the {@link Interner#intern} method of the given interner.
   *
   * 
   */
  public static <E> Function<E, E> asFunction(Interner<E> interner) {
    return new InternerFunction<>(checkNotNull(interner));
  }

  private static class InternerFunction<E> implements Function<E, E> {

    private final Interner<E> interner;

    public InternerFunction(Interner<E> interner) {
      this.interner = interner;
    }

    @Override
    public E apply(E input) {
      return interner.intern(input);
    }

    @Override
    public int hashCode() {
      return interner.hashCode();
    }

    @Override
    public boolean equals(@CheckForNull Object other) {
      if (other instanceof InternerFunction) {
        InternerFunction<?> that = (InternerFunction<?>) other;
        return interner.equals(that.interner);
      }

      return false;
    }
  }
}
