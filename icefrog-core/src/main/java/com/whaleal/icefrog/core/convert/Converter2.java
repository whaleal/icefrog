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


    /**
     * 转换为指定类型<br>
     * 如果类型无法确定，将读取默认值的类型做为目标类型
     *
     * @param var1        原始值
     * @return 转换后的值
     * @throws IllegalArgumentException 无法确定目标类型，且默认值为{@code null}，无法确定类型
     */
    T convert(S var1)  throws IllegalArgumentException;

    default <U> Converter2<S, U> andThen(Converter2<? super T, ? extends U> after) {
        Precondition.notNull(after, "After Converter must not be null");
        return (s) -> {
            T initialResult = this.convert(s);
            return initialResult != null ? after.convert(initialResult) : null;
        };
    }
}
