package com.whaleal.icefrog.core.convert;


import com.whaleal.icefrog.core.lang.Precondition;


/**
 * 双 泛型的转换器
 *
 * @author wh
 * @since 1.2
 *
 * @param <S>  源端类型
 * @param <T>  目标类型
 *
 */
@FunctionalInterface
public interface Converter2<S, T> {

    T convert(S var1);

    default <U> Converter2<S, U> andThen(Converter2<? super T, ? extends U> after) {
        Precondition.notNull(after, "After Converter must not be null");
        return (s) -> {
            T initialResult = this.convert(s);
            return initialResult != null ? after.convert(initialResult) : null;
        };
    }
}
