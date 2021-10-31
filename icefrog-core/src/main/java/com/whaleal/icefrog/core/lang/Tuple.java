package com.whaleal.icefrog.core.lang;

import com.whaleal.icefrog.core.clone.CloneSupport;
import com.whaleal.icefrog.core.collection.ArrayIter;
import com.whaleal.icefrog.core.collection.ListUtil;
import com.whaleal.icefrog.core.util.ArrayUtil;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 不可变数组类型（元组），用于多值返回<br>
 * 多值可以支持每个元素值类型不同
 *
 * @author Looly
 * @author wh
 */
public class Tuple extends CloneSupport<Tuple> implements Iterable<Object>, Serializable {
    private static final long serialVersionUID = -7689304393482182157L;

    private final Object[] members;
    private int hashCode;
    private boolean cacheHash;

    /**
     * 构造
     *
     * @param members 成员数组
     */
    public Tuple( Object... members ) {
        this.members = members;
    }

    /**
     * 获取指定位置元素
     *
     * @param <T>   返回对象类型
     * @param index 位置
     * @return 元素
     */
    @SuppressWarnings("unchecked")
    public <T> T get( int index ) {
        return (T) members[index];
    }

    /**
     * 获得所有元素
     *
     * @return 获得所有元素
     */
    public Object[] getMembers() {
        return this.members;
    }

    /**
     * 将元组转换成列表
     *
     * @return 转换得到的列表
     * @since 1.0.0
     */
    public final List<Object> toList() {
        return ListUtil.toList(this.members);
    }

    /**
     * 缓存Hash值，当为true时，此对象的hash值只被计算一次，常用于Tuple中的值不变时使用。
     * 注意：当为true时，member变更对象后，hash值不会变更。
     *
     * @param cacheHash 是否缓存hash值
     * @return this
     * @since 1.0.0
     */
    public Tuple setCacheHash( boolean cacheHash ) {
        this.cacheHash = cacheHash;
        return this;
    }

    /**
     * 得到元组的大小
     *
     * @return 元组的大小
     * @since 1.0.0
     */
    public int size() {
        return this.members.length;
    }

    /**
     * 判断元组中是否包含某元素
     *
     * @param value 需要判定的元素
     * @return 是否包含
     * @since 1.0.0
     */
    public boolean contains( Object value ) {
        return ArrayUtil.contains(this.members, value);
    }

    /**
     * 将元组转成流
     *
     * @return 流
     * @since 1.0.0
     */
    public final Stream<Object> stream() {
        return Arrays.stream(this.members);
    }

    /**
     * 将元组转成并行流
     *
     * @return 流
     * @since 1.0.0
     */
    public final Stream<Object> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * 截取元组指定部分
     *
     * @param start 起始位置（包括）
     * @param end   终止位置（不包括）
     * @return 截取得到的元组
     * @since 1.0.0
     */
    public final Tuple sub( final int start, final int end ) {
        return new Tuple(ArrayUtil.sub(this.members, start, end));
    }

    @Override
    public int hashCode() {
        if (this.cacheHash && 0 != this.hashCode) {
            return this.hashCode;
        }
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(members);
        if (this.cacheHash) {
            this.hashCode = result;
        }
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Tuple other = (Tuple) obj;
        return false != Arrays.deepEquals(members, other.members);
    }

    @Override
    public String toString() {
        return Arrays.toString(members);
    }

    @Override
    public Iterator<Object> iterator() {
        return new ArrayIter<>(members);
    }

    @Override
    public final Spliterator<Object> spliterator() {
        return Spliterators.spliterator(this.members, Spliterator.ORDERED);
    }
}
