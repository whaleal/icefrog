package com.whaleal.icefrog.core.lang;

import com.whaleal.icefrog.core.clone.CloneSupport;

import java.io.Serializable;
import java.util.Objects;

/**
 * 键值对对象，只能在构造时传入键值
 *
 * @param <L> 键类型 第一个事物类型
 * @param <R> 值类型 第二个事物类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class Pair< L, R > extends CloneSupport<Pair< L, R >> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final L left;
    private final R right;

    /**
     * 构造
     *
     * @param left   键
     * @param value 值
     */
    public Pair( L left, R value ) {
        this.left = left;
        this.right = value;
    }

    /**
     * 构建{@link Pair}对象
     *
     * @param <L>   键类型
     * @param <R>   值类型
     * @param key   键
     * @param value 值
     * @return {@link Pair}
     * @since 1.0.0
     */
    public static <L, R> Pair<L, R> of( L key, R value ) {
        return new Pair<>(key, value);
    }

    /**
     * 获取键
     *
     * @return 键
     */
    public L left() {
        return this.left;
    }

    /**
     * 获取值
     *
     * @return 值
     */
    public R right() {
        return this.right;
    }

    @Override
    public String toString() {
        return "Pair [left=" + left + ", right=" + right + "]";
    }

    @Override
    public boolean equals( Object o ) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        }else if (o.getClass() != this.getClass()) {
            return false;
        } else {
            Pair<?, ?> that = (Pair)o;
            return Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right);
        }
    }

    @Override
    public int hashCode() {
        //copy from 1.8 HashMap.Node
        return Objects.hashCode(left) ^ Objects.hashCode(right);
    }
}
