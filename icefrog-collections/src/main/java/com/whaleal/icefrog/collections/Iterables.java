package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.collection.IterUtil;
import com.whaleal.icefrog.core.collection.ListUtil;
import com.whaleal.icefrog.core.collection.SpliteratorUtil;
import com.whaleal.icefrog.core.lang.Predicate;
import com.whaleal.icefrog.core.util.PredicateUtil;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.whaleal.icefrog.core.lang.Precondition.*;


/**
 * An assortment of mainly legacy static utility methods that operate on or return objects of type
 * {@code Iterable}. Except as noted, each method has a corresponding {@link Iterator}-based method
 * in the {@link Iterators} class.
 *
 * <p><b>Java 8 users:</b> several common uses for this class are now more comprehensively addressed
 * by the new {@link Stream} library. Read the method documentation below for
 * comparisons. This class is not being deprecated, but we gently encourage you to migrate to
 * streams.
 *
 * <p><i>Performance notes:</i> Unless otherwise noted, all of the iterables produced in this class
 * are <i>lazy</i>, which means that their iterators only advance the backing iteration when
 * absolutely necessary.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#iterables"> {@code
 * Iterables}</a>.
 */


@Deprecated
public final class Iterables {
    private Iterables() {
    }

    /**
     * Returns the single element contained in {@code iterable}.
     *
     * <p><b>Java 8 users:</b> the {@code Stream} equivalent to this method is {@code
     * stream.collect(MoreCollectors.onlyElement())}.
     *
     * @throws NoSuchElementException   if the iterable is empty
     * @throws IllegalArgumentException if the iterable contains multiple elements
     */
    @ParametricNullness
    public static <T extends Object> T getOnlyElement( Iterable<T> iterable ) {
        return Iterators.getOnlyElement(iterable.iterator());
    }

    /**
     * Returns the single element contained in {@code iterable}, or {@code defaultValue} if the
     * iterable is empty.
     *
     * <p><b>Java 8 users:</b> the {@code Stream} equivalent to this method is {@code
     * stream.collect(MoreCollectors.toOptional()).orElse(defaultValue)}.
     *
     * @throws IllegalArgumentException if the iterator contains multiple elements
     */
    @ParametricNullness
    @Deprecated
    public static <T extends Object> T getOnlyElement(
            Iterable<? extends T> iterable, @ParametricNullness T defaultValue ) {
        return Iterators.getOnlyElement(iterable.iterator(), defaultValue);
    }







    /**
     * Returns an iterable whose iterators cycle indefinitely over the elements of {@code iterable}.
     *
     * <p>That iterator supports {@code remove()} if {@code iterable.iterator()} does. After {@code
     * remove()} is called, subsequent cycles omit the removed element, which is no longer in {@code
     * iterable}. The iterator's {@code hasNext()} method returns {@code true} until {@code iterable}
     * is empty.
     *
     * <p><b>Warning:</b> Typical uses of the resulting iterator may produce an infinite loop. You
     * should use an explicit {@code break} or be certain that you will eventually remove all the
     * elements.
     *
     * <p>To cycle over the iterable {@code n} times, use the following: {@code
     * Iterables.concat(Collections.nCopies(n, iterable))}
     *
     * <p><b>Java 8 users:</b> The {@code Stream} equivalent of this method is {@code
     * Stream.generate(() -> iterable).flatMap(Streams::stream)}.
     */
    public static <T extends Object> Iterable<T> cycle( final Iterable<T> iterable ) {
        checkNotNull(iterable);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.cycle(iterable);
            }

            @Override
            public Spliterator<T> spliterator() {
                return Stream.generate(() -> iterable).flatMap(Streams::stream).spliterator();
            }

            @Override
            public String toString() {
                return iterable.toString() + " (cycled)";
            }
        };
    }





    /**
     * Returns a view containing the result of applying {@code function} to each element of {@code
     * fromIterable}.
     *
     * <p>The returned iterable's iterator supports {@code remove()} if {@code fromIterable}'s
     * iterator does. After a successful {@code remove()} call, {@code fromIterable} no longer
     * contains the corresponding element.
     *
     * <p>If the input {@code Iterable} is known to be a {@code List} or other {@code Collection},
     * consider {@link Lists#transform} and {@link Collections2#transform}.
     *
     * <p><b>{@code Stream} equivalent:</b> {@link Stream#map}
     */
    @Deprecated

    public static <F extends Object, T extends Object> Iterable<T> transform(
            final Iterable<F> fromIterable, final Function<? super F, ? extends T> function ) {
        checkNotNull(fromIterable);
        checkNotNull(function);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.transform(fromIterable.iterator(), function);
            }

            @Override
            public void forEach( Consumer<? super T> action ) {
                checkNotNull(action);
                fromIterable.forEach(( F f ) -> action.accept(function.apply(f)));
            }

            @Override
            public Spliterator<T> spliterator() {
                return SpliteratorUtil.map(fromIterable.spliterator(), function);
            }
        };
    }











    private static final class UnmodifiableIterable<T extends Object>
            extends FluentIterable<T> {
        private final Iterable<? extends T> iterable;

        private UnmodifiableIterable( Iterable<? extends T> iterable ) {
            this.iterable = iterable;
        }

        @Override
        public Iterator<T> iterator() {
            return Iterators.unmodifiableIterator(iterable.iterator());
        }

        @Override
        public void forEach( Consumer<? super T> action ) {
            iterable.forEach(action);
        }

        @SuppressWarnings("unchecked") // safe upcast, assuming no one has a crazy Spliterator subclass
        @Override
        public Spliterator<T> spliterator() {
            return (Spliterator<T>) iterable.spliterator();
        }

        @Override
        public String toString() {
            return iterable.toString();
        }
        // no equals and hashCode; it would break the contract!
    }
}
