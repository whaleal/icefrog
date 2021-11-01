package com.whaleal.icefrog.core.map;

import java.util.Map;

/**
 * 双向Map<br>
 * 互换键值对不检查值是否有重复，如果有则后加入的元素替换先加入的元素<br>
 * 值的顺序在HashMap中不确定，所以谁覆盖谁也不确定，在有序的Map中按照先后顺序覆盖，保留最后的值<br>
 * 它与TableMap的区别是，BiMap维护两个Map实现高效的正向和反向查找
 * <p>
 * bimap和普通HashMap区别
 * 在Java集合类库中的Map，它的特点是存放的键（Key）是唯一的，而值（Value）可以不唯一，而
 * bimap要求key和value都唯一，如果key不唯一则覆盖key，如果value不唯一则直接报错。
 * <p>
 * 在 guava 中 BiMap的常用实现有：
 * <p>
 * 1、HashBiMap: key 集合与 value 集合都有 HashMap 实现
 * 2、EnumBiMap: key 与 value 都必须是 enum 类型
 * 3、ImmutableBiMap: 不可修改的 BiMap  这次不涉及
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 1.0.0
 */
public class BiMap<K, V> extends MapWrapper<K, V> {
    private static final long serialVersionUID = 1L;

    //  反向的 map 作为一个临时保存
    private Map<V, K> inverse;

    /**
     * 构造
     *
     * @param raw 被包装的Map
     */
    public BiMap( Map<K, V> raw ) {
        super(raw);
    }

    @Override
    public V put( K key, V value ) {
        if (null != this.inverse) {
            this.inverse.put(value, key);
        }
        return super.put(key, value);
    }

    @Override
    public void putAll( Map<? extends K, ? extends V> m ) {
        super.putAll(m);
        if (null != this.inverse) {
            m.forEach(( key, value ) -> this.inverse.put(value, key));
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.inverse = null;
    }

    /**
     * 获取反向Map
     *
     * @return 反向Map
     */
    public Map<V, K> getInverse() {
        if (null == this.inverse) {
            inverse = MapUtil.inverse(getRaw());
        }
        return this.inverse;
    }

    /**
     * 根据值获得键
     *
     * @param value 值
     * @return 键
     */
    public K getKey( V value ) {
        return getInverse().get(value);
    }
}
