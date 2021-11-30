package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.lang.Pair;
import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.stream.StreamUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * Optional  相关的工具类
 *
 *
 * @author wh
 * @since 1.2.0
 *
 */
public class OptionalUtil {
    /**
     * Returns whether any of the given {@link Optional}s is present.
     *
     * @param optionals must not be {@literal null}.
     * @return
     */
    public static boolean isAnyPresent(Optional<?>... optionals) {

        Precondition.notNull(optionals, "Optionals must not be null!");

        return Arrays.stream(optionals).anyMatch(Optional::isPresent);
    }

    /**
     * Turns the given {@link Optional} into a one-element {@link Stream} or an empty one if not present.
     *
     * @param optionals must not be {@literal null}.
     * @return
     */
    @SafeVarargs
    public static <T> Stream<T> toStream(Optional<? extends T>... optionals) {

        Precondition.notNull(optionals, "Optional must not be null!");

        return Arrays.asList(optionals).stream().flatMap(it -> it.map(Stream::of).orElseGet(Stream::empty));
    }

    /**
     * Applies the given function to the elements of the source and returns the first non-empty result.
     *
     * @param source   must not be {@literal null}.
     * @param function must not be {@literal null}.
     * @return
     */
    public static <S, T> Optional<T> firstNonEmpty(Iterable<S> source, Function<S, Optional<T>> function) {

        Precondition.notNull(source, "Source must not be null!");
        Precondition.notNull(function, "Function must not be null!");

        return StreamUtil.of(source)//
                .map(function::apply)//
                .filter(Optional::isPresent)//
                .findFirst().orElseGet(Optional::empty);
    }

    /**
     * Applies the given function to the elements of the source and returns the first non-empty result.
     *
     * @param source   must not be {@literal null}.
     * @param function must not be {@literal null}.
     * @return
     */
    public static <S, T> T firstNonEmpty(Iterable<S> source, Function<S, T> function, T defaultValue) {

        Precondition.notNull(source, "Source must not be null!");
        Precondition.notNull(function, "Function must not be null!");

        return StreamUtil.of(source)//
                .map(function::apply)//
                .filter(it -> !it.equals(defaultValue))//
                .findFirst().orElse(defaultValue);
    }

    /**
     * Invokes the given {@link Supplier}s for {@link Optional} results one by one and returns the first non-empty one.
     *
     * @param suppliers must not be {@literal null}.
     * @return
     */
    @SafeVarargs
    public static <T> Optional<T> firstNonEmpty(Supplier<Optional<T>>... suppliers) {

        Precondition.notNull(suppliers, "Suppliers must not be null!");


        return firstNonEmpty(StreamUtil.of(suppliers).collect(Collectors.toList()));
    }

    /**
     * Invokes the given {@link Supplier}s for {@link Optional} results one by one and returns the first non-empty one.
     *
     * @param suppliers must not be {@literal null}.
     * @return
     */
    public static <T> Optional<T> firstNonEmpty(Iterable<Supplier<Optional<T>>> suppliers) {

        Precondition.notNull(suppliers, "Suppliers must not be null!");

        return StreamUtil.of(suppliers)//
                .map(Supplier::get)//
                .filter(Optional::isPresent)//
                .findFirst().orElse(Optional.empty());
    }

    /**
     * Returns the next element of the given {@link Iterator} or {@link Optional#empty()} in case there is no next
     * element.
     *
     * @param iterator must not be {@literal null}.
     * @return
     */
    public static <T> Optional<T> next(Iterator<T> iterator) {

        Precondition.notNull(iterator, "Iterator must not be null!");

        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }

    /**
     * Returns a {@link Pair} if both {@link Optional} instances have values or {@link Optional#empty()} if one or both
     * are missing.
     *
     * @param left
     * @param right
     * @return
     */
    public static <T, S> Optional<Pair<T, S>> withBoth( Optional<T> left, Optional<S> right) {
        return left.flatMap(l -> right.map(r -> Pair.of(l, r)));
    }

    /**
     * Invokes the given {@link BiConsumer} if all given {@link Optional} are present.
     *
     * @param left     must not be {@literal null}.
     * @param right    must not be {@literal null}.
     * @param consumer must not be {@literal null}.
     */
    public static <T, S> void ifAllPresent(Optional<T> left, Optional<S> right, BiConsumer<T, S> consumer) {

        Precondition.notNull(left, "Optional must not be null!");
        Precondition.notNull(right, "Optional must not be null!");
        Precondition.notNull(consumer, "Consumer must not be null!");

        mapIfAllPresent(left, right, (l, r) -> {
            consumer.accept(l, r);
            return null;
        });
    }

    /**
     * Maps the values contained in the given {@link Optional} if both of them are present.
     *
     * @param left     must not be {@literal null}.
     * @param right    must not be {@literal null}.
     * @param function must not be {@literal null}.
     * @return
     */
    public static <T, S, R> Optional<R> mapIfAllPresent(Optional<T> left, Optional<S> right,
                                                        BiFunction<T, S, R> function) {

        Precondition.notNull(left, "Optional must not be null!");
        Precondition.notNull(right, "Optional must not be null!");
        Precondition.notNull(function, "BiFunctionmust not be null!");

        return left.flatMap(l -> right.map(r -> function.apply(l, r)));
    }

    /**
     * Invokes the given {@link Consumer} if the {@link Optional} is present or the {@link Runnable} if not.
     *
     * @param optional must not be {@literal null}.
     * @param consumer must not be {@literal null}.
     * @param runnable must not be {@literal null}.
     */
    public static <T> void ifPresentOrElse(Optional<T> optional, Consumer<? super T> consumer, Runnable runnable) {

        Precondition.notNull(optional, "Optional must not be null!");
        Precondition.notNull(consumer, "Consumer must not be null!");
        Precondition.notNull(runnable, "Runnable must not be null!");

        if (optional.isPresent()) {
            optional.ifPresent(consumer);
        } else {
            runnable.run();
        }
    }
}
