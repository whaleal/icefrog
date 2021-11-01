package com.whaleal.icefrog.core.lang;

/**
 * 片段默认实现
 *
 * @param <T> 数字类型，用于表示位置index
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class DefaultSegment<T extends Number> implements Segment<T> {

    protected T startIndex;
    protected T endIndex;

    /**
     * 构造
     *
     * @param startIndex 起始位置
     * @param endIndex   结束位置
     */
    public DefaultSegment( T startIndex, T endIndex ) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override
    public T getStartIndex() {
        return this.startIndex;
    }

    @Override
    public T getEndIndex() {
        return this.endIndex;
    }
}
