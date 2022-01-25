package com.whaleal.icefrog.core.stream;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.io.IORuntimeException;
import com.whaleal.icefrog.core.lang.Pair;
import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.util.CharsetUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * {@link Stream} 工具类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class StreamUtil {

    @SafeVarargs
    public static <T> Stream<T> of( T... array ) {
        Precondition.notNull(array, "Array must be not null!");
        return Stream.of(array);
    }

    /**
     * 集合类转 Stream
     * @param collection 集合类
     * @param <T> 集合元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of( Collection<T> collection){
        return collection.stream();
    }
    /**
     * {@link Iterable}转换为{@link Stream}，默认非并行
     *
     * @param iterable 集合
     * @param <T>      集合元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of( Iterable<T> iterable ) {
        return of(iterable, false);
    }

    /**
     * {@link Iterable}转换为{@link Stream}
     *
     * @param iterable 集合
     * @param parallel 是否并行
     * @param <T>      集合元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of( Iterable<T> iterable, boolean parallel ) {
        Precondition.notNull(iterable, "Iterable must be not null!");
        return StreamSupport.stream(
                Spliterators.spliterator(CollUtil.toCollection(iterable), 0),
                parallel);
    }

    /**
     * 按行读取文件为{@link Stream}
     *
     * @param file 文件
     * @return {@link Stream}
     */
    public static Stream<String> of( File file ) {
        return of(file, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 按行读取文件为{@link Stream}
     *
     * @param path 路径
     * @return {@link Stream}
     */
    public static Stream<String> of( Path path ) {
        return of(path, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 按行读取文件为{@link Stream}
     *
     * @param file    文件
     * @param charset 编码
     * @return {@link Stream}
     */
    public static Stream<String> of( File file, Charset charset ) {
        Precondition.notNull(file, "File must be not null!");
        return of(file.toPath(), charset);
    }

    /**
     * 按行读取文件为{@link Stream}
     *
     * @param path    路径
     * @param charset 编码
     * @return {@link Stream}
     */
    public static Stream<String> of( Path path, Charset charset ) {
        try {
            return Files.lines(path, charset);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 通过函数创建Stream
     *
     * @param seed           初始值
     * @param elementCreator 递进函数，每次调用此函数获取下一个值
     * @param limit          限制个数
     * @param <T>            创建元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of( T seed, UnaryOperator<T> elementCreator, int limit ) {
        return Stream.iterate(seed, elementCreator).limit(limit);
    }

    /**
     * 将Stream中所有元素以指定分隔符，合并为一个字符串，对象默认调用toString方法
     *
     * @param stream    {@link Stream}
     * @param delimiter 分隔符
     * @param <T>       元素类型
     * @return 字符串
     */
    public static <T> String join( Stream<T> stream, CharSequence delimiter ) {
        return stream.collect(CollectorUtil.joining(delimiter));
    }

    /**
     * 将Stream中所有元素以指定分隔符，合并为一个字符串
     *
     * @param stream       {@link Stream}
     * @param delimiter    分隔符
     * @param toStringFunc 元素转换为字符串的函数
     * @param <T>          元素类型
     * @return 字符串
     */
    public static <T> String join( Stream<T> stream, CharSequence delimiter,
                                   Function<T, ? extends CharSequence> toStringFunc ) {
        return stream.collect(CollectorUtil.joining(delimiter, toStringFunc));
    }

    /**
     *
     * 如果存在 该元素 返回该元素的流，
     * 否则返回一个空的流
     * @param optional  java 原生的 Optional 类型
     * @param <T>  元素类型
     * @return {@link Stream}
     */
    public static <T> Stream<T> of( java.util.Optional<T> optional ) {
        return optional.isPresent() ? Stream.of(optional.get()) : Stream.empty();
    }


    /**
     * 这里主要为并行流调用 
     * 
     * 为 {@code streamA} 中的每对 <i>corresponding</i> 元素调用一次 {@code consumer}
     * 和{@code streamB}。 如果一个流比另一个长，则额外的元素会
     * 忽略。 传递给消费者的元素保证来自它们的相同位置
     * 各自的源流。 例如：
     *
     * <pre>{@code
     * Streams.forEachPair(
     *   Stream.of("foo1", "foo2", "foo3"),
     *   Stream.of("bar1", "bar2"),
     *   (arg1, arg2) -> System.out.println(arg1 + ":" + arg2)
     * }</pre>
     *
     * <p>will print:
     *
     * <pre>{@code
     * foo1:bar1
     * foo2:bar2
     * }</pre>
     *
     * <p><b>警告：</b> 如果任一提供的流是并行流，则相同的对应关系
     * 将在元素之间进行，并将这些元素对传递给 消费者{@link BiConsumer}
     * 。
     *
     * <p>请注意，此方法的许多用法可以替换为对 {@link #zip} 的更简单调用。
     * 此方法的行为等同于 {@linkplain #zip zipping} 将流元素放入
     * 临时{@link Pair}对象，然后在该流上使用 {@link Stream#forEach}。
     *
     * @param streamA  流 A
     * @param streamB  流B
     * @param function  消费者
     * @param <A>  元素泛型A
     * @param <B>  元素泛型B
     *
     */

    public static <A extends Object, B extends Object, R extends Object>
    Stream<R> zip(
            Stream<A> streamA, Stream<B> streamB, BiFunction<? super A, ? super B, R> function ) {
        checkNotNull(streamA);
        checkNotNull(streamB);
        checkNotNull(function);
        boolean isParallel = streamA.isParallel() || streamB.isParallel(); // same as Stream.concat
        Spliterator<A> splitrA = streamA.spliterator();
        Spliterator<B> splitrB = streamB.spliterator();
        int characteristics =
                splitrA.characteristics()
                        & splitrB.characteristics()
                        & (Spliterator.SIZED | Spliterator.ORDERED);
        Iterator<A> itrA = Spliterators.iterator(splitrA);
        Iterator<B> itrB = Spliterators.iterator(splitrB);
        return StreamSupport.stream(
                new Spliterators.AbstractSpliterator<R>(
                        Math.min(splitrA.estimateSize(), splitrB.estimateSize()), characteristics) {
                    @Override
                    public boolean tryAdvance( Consumer<? super R> action ) {
                        if (itrA.hasNext() && itrB.hasNext()) {
                            action.accept(function.apply(itrA.next(), itrB.next()));
                            return true;
                        }
                        return false;
                    }
                },
                isParallel)
                .onClose(streamA::close)
                .onClose(streamB::close);
    }

    /**
     * 当遇到并行流时会调用 zip 方法
     * 否则迭代并封装为一对  {A ，B} 同时消费 
     * 
     * 为 {@code streamA} 中的每对 <i>corresponding</i> 元素调用一次 {@code consumer}
     * 和{@code streamB}。 如果一个流比另一个长，则额外的元素会
     * 忽略。 传递给消费者的元素保证来自它们的相同位置
     * 各自的源流。 例如：
     *
     * <pre>{@code
     * Streams.forEachPair(
     *   Stream.of("foo1", "foo2", "foo3"),
     *   Stream.of("bar1", "bar2"),
     *   (arg1, arg2) -> System.out.println(arg1 + ":" + arg2)
     * }</pre>
     *
     * <p>will print:
     *
     * <pre>{@code
     * foo1:bar1
     * foo2:bar2
     * }</pre>
     *
     * <p><b>警告：</b> 如果任一提供的流是并行流，则相同的对应关系
     * 将在元素之间进行，并将这些元素对传递给 消费者{@link BiConsumer}
     * 。
     *
     * <p>请注意，此方法的许多用法可以替换为对 {@link #zip} 的更简单调用。
     * 此方法的行为等同于 {@linkplain #zip zipping} 将流元素放入
     * 临时{@link Pair}对象，然后在该流上使用 {@link Stream#forEach}。
     *
     * @param streamA  流 A
     * @param streamB  流B
     * @param consumer  消费者
     * @param <A>  元素泛型A
     * @param <B>  元素泛型B
     * 
     */
    
    public static <A extends Object, B extends Object> void forEachPair(
            Stream<A> streamA, Stream<B> streamB, BiConsumer<? super A, ? super B> consumer ) {
        checkNotNull(consumer);

        if (streamA.isParallel() || streamB.isParallel()) {
            zip(streamA, streamB, Pair::new).forEach(pair -> consumer.accept(pair.left(), pair.right()));
        } else {
            Iterator<A> iterA = streamA.iterator();
            Iterator<B> iterB = streamB.iterator();
            while (iterA.hasNext() && iterB.hasNext()) {
                consumer.accept(iterA.next(), iterB.next());
            }
        }
    }





}
