package com.whaleal.icefrog.core.bean;

/**
 * 为了解决反射过程中,需要传递null参数,但是会丢失参数类型而设立的包装类
 *
 * @param <T> Null值对应的类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class NullWrapperBean<T> {

    private final Class<T> clazz;

    /**
     * @param clazz null的类型
     */
    public NullWrapperBean( Class<T> clazz ) {
        this.clazz = clazz;
    }

    /**
     * 获取null值对应的类型
     *
     * @return 类型
     */
    public Class<T> getWrappedClass() {
        return clazz;
    }
}
