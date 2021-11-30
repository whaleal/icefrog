package com.whaleal.icefrog.core.lang.tuple;

/**
 * 表示有1个元素的元组类型
 * 可迭代
 * 不可变，线程安全
 * @author wh
 *
 */
public final class Tuple1<A> extends Tuple {

    public final A first;

    private Tuple1(final A first) {
        super(first);
        this.first = first;
    }

    /**
     * 创建一个包含1个元素的元组
     *
     * @param first 第一个元素
     * @param <A>   元素类型
     * @return 元组
     * @see TupleUtil#tuple(Object)
     * @since 1.1.0
     */
    public static <A> Tuple1<A> of(final A first) {
        return new Tuple1<>(first);
    }

    /**
     * 反转元组
     *
     * @return 反转后的元组
     * @since 1.1.0
     */
    @Override
    public Tuple1<A> reverse() {
        return new Tuple1<>(this.first);
    }
}
