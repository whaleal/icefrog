package com.whaleal.icefrog.core.lang.tuple;

/**
 * 表示有0个元素的元组类型
 * @author wh
 *
 */
public final class Tuple0 extends Tuple {

    private static final Object[] EMPTY    = new Object[]{};
    private static final Tuple0   INSTANCE = new Tuple0();

    private Tuple0() {
        super(EMPTY);
    }

    /**
     * 反转元组
     *
     * @return 反转后的元组
     * @since 1.1.0
     */
    @Override
    public Tuple0 reverse() {
        return this;
    }

    /**
     * 得到一个包含0个元素的元组
     *
     * @return 元组
     * @since 1.1.0
     * @see TupleUtil#tuple()
     *
     */
    public static Tuple0 of() {
        return INSTANCE;
    }
}
