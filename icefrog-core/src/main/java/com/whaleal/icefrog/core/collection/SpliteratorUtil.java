package com.whaleal.icefrog.core.collection;

import javax.annotation.CheckForNull;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.IntStream;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * {@link Spliterator}相关工具类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class SpliteratorUtil {

    /**
     * 使用给定的转换函数，转换源{@link Spliterator}为新类型的{@link Spliterator}
     *
     * @param <F>             源元素类型
     * @param <T>             目标元素类型
     * @param fromSpliterator 源{@link Spliterator}
     * @param function        转换函数
     * @return 新类型的{@link Spliterator}
     */
    public static <F, T> Spliterator<T> trans( Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function ) {
        return new TransSpliterator<>(fromSpliterator, function);
    }


    /**

     */


    /**
     * Returns a {@code Spliterator} over the elements of {@code fromSpliterator} mapped by {@code
     * function}.
     *
     * @param fromSpliterator from
     * @param function        fuc
     * @param <InElementT>    泛型 IN
     * @param <OutElementT>   泛型 OUT
     * @return 返回值
     */
    public static <InElementT extends Object, OutElementT extends Object>
    Spliterator<OutElementT> map(
            Spliterator<InElementT> fromSpliterator,
            Function<? super InElementT, ? extends OutElementT> function ) {
        checkNotNull(fromSpliterator);
        checkNotNull(function);
        return new Spliterator<OutElementT>() {

            @Override
            public boolean tryAdvance( Consumer<? super OutElementT> action ) {
                return fromSpliterator.tryAdvance(
                        fromElement -> action.accept(function.apply(fromElement)));
            }

            @Override
            public void forEachRemaining( Consumer<? super OutElementT> action ) {
                fromSpliterator.forEachRemaining(fromElement -> action.accept(function.apply(fromElement)));
            }

            @Override
            @CheckForNull
            public Spliterator<OutElementT> trySplit() {
                Spliterator<InElementT> fromSplit = fromSpliterator.trySplit();
                return (fromSplit != null) ? map(fromSplit, function) : null;
            }

            @Override
            public long estimateSize() {
                return fromSpliterator.estimateSize();
            }

            @Override
            public int characteristics() {
                return fromSpliterator.characteristics()
                        & ~(Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SORTED);
            }
        };
    }


    public static <T extends Object> Spliterator<T> indexed(
            int size, int extraCharacteristics, IntFunction<T> function ) {
        return indexed(size, extraCharacteristics, function, null);
    }

    public static <T extends Object> Spliterator<T> indexed(
            int size,
            int extraCharacteristics,
            IntFunction<T> function,
            @CheckForNull Comparator<? super T> comparator ) {
        if (comparator != null) {
            checkArgument((extraCharacteristics & Spliterator.SORTED) != 0);
        }
        class WithCharacteristics implements Spliterator<T> {
            private final Spliterator.OfInt delegate;

            WithCharacteristics( Spliterator.OfInt delegate ) {
                this.delegate = delegate;
            }

            @Override
            public boolean tryAdvance( Consumer<? super T> action ) {
                return delegate.tryAdvance((IntConsumer) i -> action.accept(function.apply(i)));
            }

            @Override
            public void forEachRemaining( Consumer<? super T> action ) {
                delegate.forEachRemaining((IntConsumer) i -> action.accept(function.apply(i)));
            }

            @Override
            @CheckForNull
            public Spliterator<T> trySplit() {
                Spliterator.OfInt split = delegate.trySplit();
                return (split == null) ? null : new WithCharacteristics(split);
            }

            @Override
            public long estimateSize() {
                return delegate.estimateSize();
            }

            @Override
            public int characteristics() {
                return Spliterator.ORDERED
                        | Spliterator.SIZED
                        | Spliterator.SUBSIZED
                        | extraCharacteristics;
            }

            @Override
            @CheckForNull
            public Comparator<? super T> getComparator() {
                if (hasCharacteristics(Spliterator.SORTED)) {
                    return comparator;
                } else {
                    throw new IllegalStateException();
                }
            }
        }
        return new WithCharacteristics(IntStream.range(0, size).spliterator());
    }


    /**
     * Returns a {@code Spliterator} filtered by the specified predicate.
     */
    public static <T extends Object> Spliterator<T> filter(
            Spliterator<T> fromSpliterator, Predicate<? super T> predicate ) {
        checkNotNull(fromSpliterator);
        checkNotNull(predicate);
        class Splitr implements Spliterator<T>, Consumer<T> {
            @CheckForNull
            T holder = null;

            @Override
            public void accept( T t ) {
                this.holder = t;
            }

            @Override
            public boolean tryAdvance( Consumer<? super T> action ) {
                while (fromSpliterator.tryAdvance(this)) {
                    try {
                        // The cast is safe because tryAdvance puts a T into `holder`.
                        T next = (holder);
                        if (predicate.test(next)) {
                            action.accept(next);
                            return true;
                        }
                    } finally {
                        holder = null;
                    }
                }
                return false;
            }

            @Override
            @CheckForNull
            public Spliterator<T> trySplit() {
                Spliterator<T> fromSplit = fromSpliterator.trySplit();
                return (fromSplit == null) ? null : filter(fromSplit, predicate);
            }

            @Override
            public long estimateSize() {
                return fromSpliterator.estimateSize() / 2;
            }

            @Override
            @CheckForNull
            public Comparator<? super T> getComparator() {
                return fromSpliterator.getComparator();
            }

            @Override
            public int characteristics() {
                return fromSpliterator.characteristics()
                        & (Spliterator.DISTINCT
                        | Spliterator.NONNULL
                        | Spliterator.ORDERED
                        | Spliterator.SORTED);
            }
        }
        return new Splitr();
    }

    /**
     * Returns a {@code Spliterator} that iterates over the elements of the spliterators generated by
     * applying {@code function} to the elements of {@code fromSpliterator}.
     */
    public static <InElementT extends Object, OutElementT extends Object>
    Spliterator<OutElementT> flatMap(
            Spliterator<InElementT> fromSpliterator,
            Function<? super InElementT, Spliterator<OutElementT>> function,
            int topCharacteristics,
            long topSize ) {
        checkArgument(
                (topCharacteristics & Spliterator.SUBSIZED) == 0,
                "flatMap does not support SUBSIZED characteristic");
        checkArgument(
                (topCharacteristics & Spliterator.SORTED) == 0,
                "flatMap does not support SORTED characteristic");
        checkNotNull(fromSpliterator);
        checkNotNull(function);
        return new FlatMapSpliteratorOfObject<InElementT, OutElementT>(
                null, fromSpliterator, function, topCharacteristics, topSize);
    }

    /**
     * Returns a {@code Spliterator.OfInt} that iterates over the elements of the spliterators
     * generated by applying {@code function} to the elements of {@code fromSpliterator}. (If {@code
     * function} returns {@code null} for an input, it is replaced with an empty stream.)
     */
    public static <InElementT extends Object> Spliterator.OfInt flatMapToInt(
            Spliterator<InElementT> fromSpliterator,
            Function<? super InElementT, Spliterator.OfInt> function,
            int topCharacteristics,
            long topSize ) {
        checkArgument(
                (topCharacteristics & Spliterator.SUBSIZED) == 0,
                "flatMap does not support SUBSIZED characteristic");
        checkArgument(
                (topCharacteristics & Spliterator.SORTED) == 0,
                "flatMap does not support SORTED characteristic");
        checkNotNull(fromSpliterator);
        checkNotNull(function);
        return new FlatMapSpliteratorOfInt<InElementT>(
                null, fromSpliterator, function, topCharacteristics, topSize);
    }

    /**
     * Returns a {@code Spliterator.OfLong} that iterates over the elements of the spliterators
     * generated by applying {@code function} to the elements of {@code fromSpliterator}. (If {@code
     * function} returns {@code null} for an input, it is replaced with an empty stream.)
     */
    public static <InElementT extends Object> Spliterator.OfLong flatMapToLong(
            Spliterator<InElementT> fromSpliterator,
            Function<? super InElementT, Spliterator.OfLong> function,
            int topCharacteristics,
            long topSize ) {
        checkArgument(
                (topCharacteristics & Spliterator.SUBSIZED) == 0,
                "flatMap does not support SUBSIZED characteristic");
        checkArgument(
                (topCharacteristics & Spliterator.SORTED) == 0,
                "flatMap does not support SORTED characteristic");
        checkNotNull(fromSpliterator);
        checkNotNull(function);
        return new FlatMapSpliteratorOfLong<InElementT>(
                null, fromSpliterator, function, topCharacteristics, topSize);
    }

    /**
     * Returns a {@code Spliterator.OfDouble} that iterates over the elements of the spliterators
     * generated by applying {@code function} to the elements of {@code fromSpliterator}. (If {@code
     * function} returns {@code null} for an input, it is replaced with an empty stream.)
     */
    public static <InElementT extends Object> Spliterator.OfDouble flatMapToDouble(
            Spliterator<InElementT> fromSpliterator,
            Function<? super InElementT, Spliterator.OfDouble> function,
            int topCharacteristics,
            long topSize ) {
        checkArgument(
                (topCharacteristics & Spliterator.SUBSIZED) == 0,
                "flatMap does not support SUBSIZED characteristic");
        checkArgument(
                (topCharacteristics & Spliterator.SORTED) == 0,
                "flatMap does not support SORTED characteristic");
        checkNotNull(fromSpliterator);
        checkNotNull(function);
        return new FlatMapSpliteratorOfDouble<InElementT>(
                null, fromSpliterator, function, topCharacteristics, topSize);
    }

    /**
     * Implements the {@code stream} operation on spliterators.
     *
     * @param <InElementT>      the element type of the input spliterator
     * @param <OutElementT>     the element type of the output spliterators
     * @param <OutSpliteratorT> the type of the output spliterators
     */
    abstract static class FlatMapSpliterator<
            InElementT extends Object,
            OutElementT extends Object,
            OutSpliteratorT extends Spliterator<OutElementT>>
            implements Spliterator<OutElementT> {
        final Spliterator<InElementT> from;
        final Function<? super InElementT, OutSpliteratorT> function;
        final Factory<InElementT, OutSpliteratorT> factory;
        @CheckForNull
        OutSpliteratorT prefix;
        int characteristics;
        long estimatedSize;

        FlatMapSpliterator(
                @CheckForNull OutSpliteratorT prefix,
                Spliterator<InElementT> from,
                Function<? super InElementT, OutSpliteratorT> function,
                Factory<InElementT, OutSpliteratorT> factory,
                int characteristics,
                long estimatedSize ) {
            this.prefix = prefix;
            this.from = from;
            this.function = function;
            this.factory = factory;
            this.characteristics = characteristics;
            this.estimatedSize = estimatedSize;
        }

        @Override
        public final boolean tryAdvance( Consumer<? super OutElementT> action ) {
            while (true) {
                if (prefix != null && prefix.tryAdvance(action)) {
                    if (estimatedSize != Long.MAX_VALUE) {
                        estimatedSize--;
                    }
                    return true;
                } else {
                    prefix = null;
                }
                if (!from.tryAdvance(fromElement -> prefix = function.apply(fromElement))) {
                    return false;
                }
            }
        }

        /*
         * The tryAdvance and forEachRemaining in FlatMapSpliteratorOfPrimitive are overloads of these
         * methods, not overrides. They are annotated @Override because they implement methods from
         * Spliterator.OfPrimitive (and override default implementations from Spliterator.OfPrimitive or
         * a subtype like Spliterator.OfInt).
         */

        @Override
        public final void forEachRemaining( Consumer<? super OutElementT> action ) {
            if (prefix != null) {
                prefix.forEachRemaining(action);
                prefix = null;
            }
            from.forEachRemaining(
                    fromElement -> {
                        Spliterator<OutElementT> elements = function.apply(fromElement);
                        if (elements != null) {
                            elements.forEachRemaining(action);
                        }
                    });
            estimatedSize = 0;
        }

        @Override
        @CheckForNull
        public final OutSpliteratorT trySplit() {
            Spliterator<InElementT> fromSplit = from.trySplit();
            if (fromSplit != null) {
                int splitCharacteristics = characteristics & ~Spliterator.SIZED;
                long estSplitSize = estimateSize();
                if (estSplitSize < Long.MAX_VALUE) {
                    estSplitSize /= 2;
                    this.estimatedSize -= estSplitSize;
                    this.characteristics = splitCharacteristics;
                }
                OutSpliteratorT result =
                        factory.newFlatMapSpliterator(
                                this.prefix, fromSplit, function, splitCharacteristics, estSplitSize);
                this.prefix = null;
                return result;
            } else if (prefix != null) {
                OutSpliteratorT result = prefix;
                this.prefix = null;
                return result;
            } else {
                return null;
            }
        }

        @Override
        public final long estimateSize() {
            if (prefix != null) {
                estimatedSize = Math.max(estimatedSize, prefix.estimateSize());
            }
            return Math.max(estimatedSize, 0);
        }

        @Override
        public final int characteristics() {
            return characteristics;
        }

        /**
         * Factory for constructing {@link FlatMapSpliterator} instances.
         */
        @FunctionalInterface
        interface Factory<InElementT extends Object, OutSpliteratorT extends Spliterator<?>> {
            OutSpliteratorT newFlatMapSpliterator(
                    @CheckForNull OutSpliteratorT prefix,
                    Spliterator<InElementT> fromSplit,
                    Function<? super InElementT, OutSpliteratorT> function,
                    int splitCharacteristics,
                    long estSplitSize );
        }
    }

    /**
     * Implementation of {@code Stream } with an object spliterator output type.
     *
     * <p>To avoid having this type, we could use {@code FlatMapSpliterator} directly. The main
     * advantages to having the type are the ability to use its constructor reference below and the
     * parallelism with the primitive version. In short, it makes its caller ({@code flatMap})
     * simpler.
     *
     * @param <InElementT>  the element type of the input spliterator
     * @param <OutElementT> the element type of the output spliterators
     */
    static final class FlatMapSpliteratorOfObject<
            InElementT extends Object, OutElementT extends Object>
            extends FlatMapSpliterator<InElementT, OutElementT, Spliterator<OutElementT>> {
        FlatMapSpliteratorOfObject(
                @CheckForNull Spliterator<OutElementT> prefix,
                Spliterator<InElementT> from,
                Function<? super InElementT, Spliterator<OutElementT>> function,
                int characteristics,
                long estimatedSize ) {
            super(
                    prefix, from, function, FlatMapSpliteratorOfObject::new, characteristics, estimatedSize);
        }
    }

    /**
     * Implementation of {@code Stream} with a primitive spliterator output type.
     *
     * @param <InElementT>      the element type of the input spliterator
     * @param <OutElementT>     the (boxed) element type of the output spliterators
     * @param <OutConsumerT>    the specialized consumer type for the primitive output type
     * @param <OutSpliteratorT> the primitive spliterator type associated with {@code OutElementT}
     */
    abstract static class FlatMapSpliteratorOfPrimitive<
            InElementT extends Object,
            OutElementT extends Object,
            OutConsumerT,
            OutSpliteratorT extends
                    Spliterator.OfPrimitive<OutElementT, OutConsumerT, OutSpliteratorT>>
            extends FlatMapSpliterator<InElementT, OutElementT, OutSpliteratorT>
            implements Spliterator.OfPrimitive<OutElementT, OutConsumerT, OutSpliteratorT> {

        FlatMapSpliteratorOfPrimitive(
                @CheckForNull OutSpliteratorT prefix,
                Spliterator<InElementT> from,
                Function<? super InElementT, OutSpliteratorT> function,
                Factory<InElementT, OutSpliteratorT> factory,
                int characteristics,
                long estimatedSize ) {
            super(prefix, from, function, factory, characteristics, estimatedSize);
        }

        @Override
        public final boolean tryAdvance( OutConsumerT action ) {
            while (true) {
                if (prefix != null && prefix.tryAdvance(action)) {
                    if (estimatedSize != Long.MAX_VALUE) {
                        estimatedSize--;
                    }
                    return true;
                } else {
                    prefix = null;
                }
                if (!from.tryAdvance(fromElement -> prefix = function.apply(fromElement))) {
                    return false;
                }
            }
        }

        @Override
        public final void forEachRemaining( OutConsumerT action ) {
            if (prefix != null) {
                prefix.forEachRemaining(action);
                prefix = null;
            }
            from.forEachRemaining(
                    fromElement -> {
                        OutSpliteratorT elements = function.apply(fromElement);
                        if (elements != null) {
                            elements.forEachRemaining(action);
                        }
                    });
            estimatedSize = 0;
        }
    }

    /**
     * Implementation of {@link #flatMapToInt}.
     */
    static final class FlatMapSpliteratorOfInt<InElementT extends Object>
            extends FlatMapSpliteratorOfPrimitive<InElementT, Integer, IntConsumer, Spliterator.OfInt>
            implements Spliterator.OfInt {
        FlatMapSpliteratorOfInt(
                @CheckForNull Spliterator.OfInt prefix,
                Spliterator<InElementT> from,
                Function<? super InElementT, Spliterator.OfInt> function,
                int characteristics,
                long estimatedSize ) {
            super(prefix, from, function, FlatMapSpliteratorOfInt::new, characteristics, estimatedSize);
        }
    }

    /**
     * Implementation of {@link #flatMapToLong}.
     */
    static final class FlatMapSpliteratorOfLong<InElementT extends Object>
            extends FlatMapSpliteratorOfPrimitive<InElementT, Long, LongConsumer, Spliterator.OfLong>
            implements Spliterator.OfLong {
        FlatMapSpliteratorOfLong(
                @CheckForNull Spliterator.OfLong prefix,
                Spliterator<InElementT> from,
                Function<? super InElementT, Spliterator.OfLong> function,
                int characteristics,
                long estimatedSize ) {
            super(prefix, from, function, FlatMapSpliteratorOfLong::new, characteristics, estimatedSize);
        }
    }

    /**
     * Implementation of {@link #flatMapToDouble}.
     */
    static final class FlatMapSpliteratorOfDouble<InElementT extends Object>
            extends FlatMapSpliteratorOfPrimitive<
            InElementT, Double, DoubleConsumer, Spliterator.OfDouble>
            implements Spliterator.OfDouble {
        FlatMapSpliteratorOfDouble(
                @CheckForNull Spliterator.OfDouble prefix,
                Spliterator<InElementT> from,
                Function<? super InElementT, Spliterator.OfDouble> function,
                int characteristics,
                long estimatedSize ) {
            super(
                    prefix, from, function, FlatMapSpliteratorOfDouble::new, characteristics, estimatedSize);
        }
    }

}
