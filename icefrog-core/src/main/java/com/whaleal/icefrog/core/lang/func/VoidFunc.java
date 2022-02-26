package com.whaleal.icefrog.core.lang.func;

import java.io.Serializable;

/**
 * 函数对象<br>
 * 接口灵感来自于<a href="http://actframework.org/">ActFramework</a><br>
 * 一个函数接口代表一个一个函数，用于包装一个函数为对象<br>
 * 在JDK8之前，Java的函数并不能作为参数传递，也不能作为返回值存在，此接口用于将一个函数包装成为一个对象，从而传递对象
 *
 * @param <P> 参数类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
@FunctionalInterface
public interface VoidFunc<P> extends Serializable {

    /**
     * 执行函数
     *
     * @param parameters 参数列表
     * @throws Exception 自定义异常
     */
    @SuppressWarnings("unchecked")
    void apply( P... parameters ) throws Exception;

    /**
     * 执行函数，异常包装为RuntimeException
     *
     * @param parameters 参数列表
     *                    @throws RuntimeException e 抛出的异常 
     */
    @SuppressWarnings("unchecked")
    default void applyWithRuntimeException( P... parameters ) {
        try {
            apply(parameters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
