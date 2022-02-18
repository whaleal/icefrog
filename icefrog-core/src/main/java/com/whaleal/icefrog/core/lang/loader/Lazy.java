/**
 *    Copyright 2020-present  Shanghai Jinmu Information Technology Co., Ltd.
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the Server Side Public License, version 1,
 *    as published by Shanghai Jinmu Information Technology Co., Ltd.(The name of the development team is Whaleal.)
 *
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    Server Side Public License for more details.
 *
 *    You should have received a copy of the Server Side Public License
 *    along with this program. If not, see
 *    <http://www.whaleal.com/licensing/server-side-public-license>.
 *
 *    As a special exception, the copyright holders give permission to link the
 *    code of portions of this program with the OpenSSL library under certain
 *    conditions as described in each individual source file and distribute
 *    linked combinations including the program with the OpenSSL library. You
 *    must comply with the Server Side Public License in all respects for
 *    all of the code used other than as permitted herein. If you modify file(s)
 *    with this exception, you may extend this exception to your version of the
 *    file(s), but you are not obligated to do so. If you do not wish to do so,
 *    delete this exception statement from your version. If you delete this
 *    exception statement from all source files in the program, then also delete
 *    it in the license file.
 */
package com.whaleal.icefrog.core.lang.loader;



import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.util.ObjectUtil;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *  一种 来实现的Lazy 的实现
 *
 * @see LazyLoader
 * @see Supplier
 *
 * @author wh
 * @date 2021-12-01
 * @param <T>
 */
public class Lazy<T> extends LazyLoader<T> {

    private static final Lazy<?> EMPTY = new Lazy<>(() -> null, null, true);

    private final Supplier<? extends T> supplier;

    private T value;
    private volatile boolean resolved;

    /**
     * 私有的构造方法
     * 相关创建 全部通过of  来实现
     * @param supplier  supplier
     */
    private Lazy(Supplier<? extends T> supplier) {
        this(supplier, null, false);
    }

    /**
     * 为给定的 {@link Supplier} 创建一个新的 {@link Lazy}，值以及它是否已解决。
     *
     * @param supplier must not be {@literal null}.
     * @param value    can be {@literal null}.
     * @param resolved whether the value handed into the constructor represents a resolved value.
     */
    private Lazy( Supplier<? extends T> supplier, T value, boolean resolved ) {

        Precondition.checkNotNull(supplier);
        this.supplier = supplier;
        this.value = value;
        this.resolved = resolved;
    }

    /**
     *
     * 创建一个新的 {@link Lazy} 以延迟生成对象
     * @param <T>      the type of which to produce an object of eventually.
     * @param supplier the {@link Supplier} to create the object lazily.
     * @return
     */
    public static <T> Lazy<T> of(Supplier<? extends T> supplier) {
        return new Lazy<>(supplier);
    }

    /**
     *  通过传入的值 来生成一个新的Lazy
     *
     * @param <T>   the type of the value to return eventually.
     * @param value the value to return.
     * @return
     */
    public static <T> Lazy<T> of(T value) {

        Precondition.notNull(value, "Value must not be null!");

        return new Lazy<>(() -> value);
    }

    /**
     * 创建一个预解析的空的 {@link Lazy}。
     *
     * @return
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> Lazy<T> empty() {
        return (Lazy<T>) EMPTY;
    }



    @Override
    protected T init() {
        T value = this.getNullable();
        if(value == null){
            throw new IllegalStateException("Expected lazy evaluation to yield a non-null value but got null!");
        }

        return value ;

    }

    /**
     * 返回由配置的 {@link Supplier} 创建的 {@link Optional} 值，允许在
     * 与 {@link #get()} 形成对比。 将返回计算的实例以供后续查找。
     *
     * @return
     */
    public Optional<T> getOptional() {
        return Optional.ofNullable(getNullable());
    }

    /**
     *
     * 返回一个新的 Lazy，如果当前的Supplier 没有产生结果 将处理新的 supplier
     * @param supplier must not be {@literal null}.
     * @return
     */
    public Lazy<T> or(Supplier<? extends T> supplier) {

        Precondition.notNull(supplier, "Supplier must not be null!");

        return Lazy.of(() -> orElseGet(supplier));
    }

    /**
     * 返回一个新的 Lazy，如果当前的值没有在结果中产生，它将返回给定的值。
     *
     * @return
     */
    public Lazy<T> or(T value) {

        Precondition.notNull(value, "Value must not be null!");

        return Lazy.of(() -> orElse(value));
    }

    /**
     * 返回惰性计算的值或给定的默认值，以防计算产生
     * {@literal null}.
     *
     * @param value
     * @return
     */

    public T orElse( T value ) {

        T nullable = getNullable();

        return nullable == null ? value : nullable;
    }

    /**
     * 返回惰性计算的值或给定 {@link Supplier} 产生的值，以防原始异常
     * value is {@literal null}.
     *
     * @param supplier must not be {@literal null}.
     * @return
     */

    private T orElseGet(Supplier<? extends T> supplier) {

        Precondition.notNull(supplier, "Default value supplier must not be null!");

        T value = getNullable();

        return value == null ? supplier.get() : value;
    }

    /**
     * 创建一个新的 {@link Lazy} 并将给定的 {@link Function} 延迟应用于当前。
     *
     * @param function must not be {@literal null}.
     * @return
     */
    public <S> Lazy<S> map(Function<? super T, ? extends S> function) {

        Precondition.notNull(function, "Function must not be null!");

        return Lazy.of(() -> function.apply(get()));
    }

    /**
     * 创建一个新的 {@link Lazy} 并将给定的 {@link Function} 延迟应用于当前。
     *
     * @param function must not be {@literal null}.
     * @return
     */
    public <S> Lazy<S> flatMap(Function<? super T, Lazy<? extends S>> function) {

        Precondition.notNull(function, "Function must not be null!");

        return Lazy.of(() -> function.apply(get()).get());
    }

    /**
     * 返回惰性求值的值。
     *
     * @return
     *
     */

    public T getNullable() {

        if (resolved) {
            return value;
        }

        this.value = supplier.get();
        this.resolved = true;

        return value;
    }


    @Override
    public boolean equals( Object o ) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Lazy)) {
            return false;
        }

        Lazy<?> lazy = (Lazy<?>) o;

        if (resolved != lazy.resolved) {
            return false;
        }

        if (!ObjectUtil.nullSafeEquals(supplier, lazy.supplier)) {
            return false;
        }

        return ObjectUtil.nullSafeEquals(value, lazy.value);
    }


    @Override
    public int hashCode() {

        int result = ObjectUtil.nullSafeHashCode(supplier);

        result = 31 * result + ObjectUtil.nullSafeHashCode(value);
        result = 31 * result + (resolved ? 1 : 0);

        return result;
    }
}
