package com.whaleal.icefrog.core.thread.threadlocal;

/**
 * 带有Name标识的 {@link ThreadLocal}，调用toString返回name
 *
 * @param <T> 值类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class NamedThreadLocal<T> extends ThreadLocal<T> {

    private final String name;

    /**
     * 构造
     *
     * @param name 名字
     */
    public NamedThreadLocal( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
