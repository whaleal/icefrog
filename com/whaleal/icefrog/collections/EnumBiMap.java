package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.map.BiMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Map;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * A {@code BiMap} backed by two {@code EnumMap} instances. Null keys and values are not permitted.
 * An {@code EnumBiMap} and its inverse are both serializable.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#bimap"> {@code BiMap}</a>.
 * <p>
 * 枚举值 相关的 BiMap
 * key  value  两部分均为 枚举类的子类 ，且 key value 均不为null .
 */


public final class EnumBiMap<K extends Enum<K>, V extends Enum<V>> extends BiMap<K, V> {

    // not needed in emulated source.
    private static final long serialVersionUID = 0;
    // 临时 变量 保存 key  和 value 的 类型
    private transient Class<K> keyType;
    private transient Class<V> valueType;

    private EnumBiMap( Class<K> keyType, Class<V> valueType ) {
        super(new EnumMap<K, V>(keyType));
        this.keyType = keyType;
        this.valueType = valueType;
    }

    /**
     * Returns a new, empty {@code EnumBiMap} using the specified key and value types.
     * <p>
     * 静态方法 主要用于 构造本类对象
     *
     * @param keyType   the key type
     * @param valueType the value type
     */
    public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create(
            Class<K> keyType, Class<V> valueType ) {
        return new EnumBiMap<>(keyType, valueType);
    }

    /**
     * Returns a new bimap with the same mappings as the specified map. If the specified map is an
     * {@code EnumBiMap}, the new bimap has the same types as the provided map. Otherwise, the
     * specified map must contain at least one mapping, in order to determine the key and value types.
     *
     * @param map the map whose mappings are to be placed in this map
     * @throws IllegalArgumentException if map is not an {@code EnumBiMap} instance and contains no
     *                                  mappings
     */
    public static <K extends Enum<K>, V extends Enum<V>> EnumBiMap<K, V> create( Map<K, V> map ) {
        EnumBiMap<K, V> bimap = create(inferKeyType(map), inferValueType(map));
        bimap.putAll(map);
        return bimap;
    }

    static <K extends Enum<K>> Class<K> inferKeyType( Map<K, ?> map ) {
        if (map instanceof EnumBiMap) {
            return ((EnumBiMap<K, ?>) map).keyType();
        }
        if (map instanceof EnumHashBiMap) {
            return ((EnumHashBiMap<K, ?>) map).keyType();
        }
        checkArgument(!map.isEmpty());
        return map.keySet().iterator().next().getDeclaringClass();
    }

    private static <V extends Enum<V>> Class<V> inferValueType( Map<?, V> map ) {
        if (map instanceof EnumBiMap) {
            return ((EnumBiMap<?, V>) map).valueType;
        }
        checkArgument(!map.isEmpty());
        return map.values().iterator().next().getDeclaringClass();
    }

    /**
     * Returns the associated key type.
     */
    public Class<K> keyType() {
        return keyType;
    }

    /**
     * Returns the associated value type.
     */
    public Class<V> valueType() {
        return valueType;
    }

    K checkKey( K key ) {
        return checkNotNull(key);
    }

    V checkValue( V value ) {
        return checkNotNull(value);
    }

    /**
     * @serialData the key class, value class, number of entries, first key, first value, second key,
     * second value, and so on.
     */
    // java.io.ObjectOutputStream
    private void writeObject( ObjectOutputStream stream ) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(keyType);
        stream.writeObject(valueType);
        Serialization.writeMap(this, stream);
    }

    @SuppressWarnings("unchecked") // reading fields populated by writeObject
    // java.io.ObjectInputStream
    private void readObject( ObjectInputStream stream ) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        keyType = (Class<K>) stream.readObject();
        valueType = (Class<V>) stream.readObject();
        //setDelegates(new EnumMap<K, V>(keyType), new EnumMap<V, K>(valueType));
        clear();
        putAll(new EnumMap<K, V>(keyType));

        Serialization.populateMap(this, stream);
    }
}
