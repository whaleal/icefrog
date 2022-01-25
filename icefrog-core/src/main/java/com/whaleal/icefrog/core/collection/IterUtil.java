package com.whaleal.icefrog.core.collection;

import com.whaleal.icefrog.core.exceptions.UtilException;
import com.whaleal.icefrog.core.lang.Editor;
import com.whaleal.icefrog.core.lang.Matcher;
import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.lang.Predicate;
import com.whaleal.icefrog.core.lang.func.Func1;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.stream.StreamUtil;
import com.whaleal.icefrog.core.text.StrJoiner;
import com.whaleal.icefrog.core.util.ArrayUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;
import com.whaleal.icefrog.core.util.ReflectUtil;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.whaleal.icefrog.core.lang.Precondition.*;

/**
 * {@link Iterable} 和 {@link Iterator} 相关工具类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class IterUtil {

    /**
     * 获取{@link Iterator}
     *
     * @param iterable {@link Iterable}
     * @param <T>      元素类型
     * @return 当iterable为null返回{@code null}，否则返回对应的{@link Iterator}
     * @since 1.0.0
     */
    public static <T> Iterator<T> getIter( Iterable<T> iterable ) {
        return null == iterable ? null : iterable.iterator();
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isEmpty( Iterable<?> iterable ) {
        return null == iterable || isEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isEmpty( Iterator<?> Iterator ) {
        return null == Iterator || false == Iterator.hasNext();
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isNotEmpty( Iterable<?> iterable ) {
        return null != iterable && isNotEmpty(iterable.iterator());
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isNotEmpty( Iterator<?> Iterator ) {
        return null != Iterator && Iterator.hasNext();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iter 被检查的{@link Iterable}对象，如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull( Iterable<?> iter ) {
        return hasNull(null == iter ? null : iter.iterator());
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iter 被检查的{@link Iterator}对象，如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     */
    public static boolean hasNull( Iterator<?> iter ) {
        if (null == iter) {
            return true;
        }
        while (iter.hasNext()) {
            if (null == iter.next()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 是否全部元素为null
     *
     * @param iter iter 被检查的{@link Iterable}对象，如果为{@code null} 返回true
     * @return 是否全部元素为null
     * @since 1.0.0
     */
    public static boolean isAllNull( Iterable<?> iter ) {
        return isAllNull(null == iter ? null : iter.iterator());
    }

    /**
     * 是否全部元素为null
     *
     * @param iter iter 被检查的{@link Iterator}对象，如果为{@code null} 返回true
     * @return 是否全部元素为null
     * @since 1.0.0
     */
    public static boolean isAllNull( Iterator<?> iter ) {
        return null == getFirstNoneNull(iter);
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}<br>
     * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value<br>
     * 例如：[a,b,c,c,c] 得到：<br>
     * a: 1<br>
     * b: 1<br>
     * c: 3<br>
     *
     * @param <T>  集合元素类型
     * @param iter {@link Iterator}，如果为null返回一个空的Map
     * @return {@link Map}
     */
    public static <T> Map<T, Integer> countMap( Iterator<T> iter ) {
        final HashMap<T, Integer> countMap = new HashMap<>();
        if (null != iter) {
            T t;
            while (iter.hasNext()) {
                t = iter.next();
                countMap.put(t, countMap.getOrDefault(t, 0) + 1);
            }
        }
        return countMap;
    }

    /**
     * 字段值与列表值对应的Map，常用于元素对象中有唯一ID时需要按照这个ID查找对象的情况<br>
     * 例如：车牌号 =》车
     *
     * @param <K>       字段名对应值得类型，不确定请使用Object
     * @param <V>       对象类型
     * @param iter      对象列表
     * @param fieldName 字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> fieldValueMap( Iterator<V> iter, String fieldName ) {
        return toMap(iter, new HashMap<>(), ( value ) -> (K) ReflectUtil.getFieldValue(value, fieldName));
    }

    /**
     * 两个字段值组成新的Map
     *
     * @param <K>               字段名对应值得类型，不确定请使用Object
     * @param <V>               值类型，不确定使用Object
     * @param iter              对象列表
     * @param fieldNameForKey   做为键的字段名（会通过反射获取其值）
     * @param fieldNameForValue 做为值的字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> fieldValueAsMap( Iterator<?> iter, String fieldNameForKey, String fieldNameForValue ) {
        return toMap(iter, new HashMap<>(),
                ( value ) -> (K) ReflectUtil.getFieldValue(value, fieldNameForKey),
                ( value ) -> (V) ReflectUtil.getFieldValue(value, fieldNameForValue)
        );
    }

    /**
     * 获取指定Bean列表中某个字段，生成新的列表
     *
     * @param <V>       对象类型
     * @param iterable  对象列表
     * @param fieldName 字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     * @since 1.0.0
     */
    public static <V> List<Object> fieldValueList( Iterable<V> iterable, String fieldName ) {
        return fieldValueList(getIter(iterable), fieldName);
    }

    /**
     * 获取指定Bean列表中某个字段，生成新的列表
     *
     * @param <V>       对象类型
     * @param iter      对象列表
     * @param fieldName 字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     * @since 1.0.0
     */
    public static <V> List<Object> fieldValueList( Iterator<V> iter, String fieldName ) {
        final List<Object> result = new ArrayList<>();
        if (null != iter) {
            V value;
            while (iter.hasNext()) {
                value = iter.next();
                result.add(ReflectUtil.getFieldValue(value, fieldName));
            }
        }
        return result;
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join( Iterator<T> iterator, CharSequence conjunction ) {
        return StrJoiner.of(conjunction).append(iterator).toString();
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀，null表示不添加
     * @param suffix      每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     * @since 1.0.0
     */
    public static <T> String join( Iterator<T> iterator, CharSequence conjunction, String prefix, String suffix ) {
        return StrJoiner.of(conjunction, prefix, suffix)
                // 每个元素都添加前后缀
                .setWrapElement(true)
                .append(iterator)
                .toString();
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @param func        集合元素转换器，将元素转换为字符串
     * @return 连接后的字符串
     * @since 1.0.0
     */
    public static <T> String join( Iterator<T> iterator, CharSequence conjunction, Function<T, ? extends CharSequence> func ) {
        if (null == iterator) {
            return null;
        }

        return StrJoiner.of(conjunction).append(iterator, func).toString();
    }

    /**
     * 将Entry集合转换为HashMap
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param entryIter entry集合
     * @return Map
     */
    public static <K, V> HashMap<K, V> toMap( Iterable<Entry<K, V>> entryIter ) {
        final HashMap<K, V> map = new HashMap<>();
        if (isNotEmpty(entryIter)) {
            for (Entry<K, V> entry : entryIter) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return 标题内容Map
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> toMap( Iterable<K> keys, Iterable<V> values ) {
        return toMap(keys, values, false);
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return 标题内容Map
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> toMap( Iterable<K> keys, Iterable<V> values, boolean isOrder ) {
        return toMap(null == keys ? null : keys.iterator(), null == values ? null : values.iterator(), isOrder);
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return 标题内容Map
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> toMap( Iterator<K> keys, Iterator<V> values ) {
        return toMap(keys, values, false);
    }

    /**
     * 将键列表和值列表转换为Map<br>
     * 以键为准，值与键位置需对应。如果键元素数多于值元素，多余部分值用null代替。<br>
     * 如果值多于键，忽略多余的值。
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return 标题内容Map
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> toMap( Iterator<K> keys, Iterator<V> values, boolean isOrder ) {
        final Map<K, V> resultMap = MapUtil.newHashMap(isOrder);
        if (isNotEmpty(keys)) {
            while (keys.hasNext()) {
                resultMap.put(keys.next(), (null != values && values.hasNext()) ? values.next() : null);
            }
        }
        return resultMap;
    }

    /**
     * 将列表转成值为List的HashMap
     *
     * @param iterable  值列表
     * @param keyMapper Map的键映射
     * @param <K>       键类型
     * @param <V>       值类型
     * @return HashMap
     * @since 1.0.0
     */
    public static <K, V> Map<K, List<V>> toListMap( Iterable<V> iterable, Function<V, K> keyMapper ) {
        return toListMap(iterable, keyMapper, v -> v);
    }

    /**
     * 将列表转成值为List的HashMap
     *
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map中List的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return HashMap
     * @since 1.0.0
     */
    public static <T, K, V> Map<K, List<V>> toListMap( Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper ) {
        return toListMap(MapUtil.newHashMap(), iterable, keyMapper, valueMapper);
    }

    /**
     * 将列表转成值为List的HashMap
     *
     * @param resultMap   结果Map，可自定义结果Map类型
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map中List的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return HashMap
     * @since 1.0.0
     */
    public static <T, K, V> Map<K, List<V>> toListMap( Map<K, List<V>> resultMap, Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper ) {
        if (null == resultMap) {
            resultMap = MapUtil.newHashMap();
        }
        if (ObjectUtil.isNull(iterable)) {
            return resultMap;
        }

        for (T value : iterable) {
            resultMap.computeIfAbsent(keyMapper.apply(value), k -> new ArrayList<>()).add(valueMapper.apply(value));
        }

        return resultMap;
    }

    /**
     * 将列表转成HashMap
     *
     * @param iterable  值列表
     * @param keyMapper Map的键映射
     * @param <K>       键类型
     * @param <V>       值类型
     * @return HashMap
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> toMap( Iterable<V> iterable, Function<V, K> keyMapper ) {
        return toMap(iterable, keyMapper, v -> v);
    }

    /**
     * 将列表转成HashMap
     *
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return HashMap
     * @since 1.0.0
     */
    public static <T, K, V> Map<K, V> toMap( Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper ) {
        return toMap(MapUtil.newHashMap(), iterable, keyMapper, valueMapper);
    }

    /**
     * 将列表转成Map
     *
     * @param resultMap   结果Map，通过传入map对象决定结果的Map类型
     * @param iterable    值列表
     * @param keyMapper   Map的键映射
     * @param valueMapper Map的值映射
     * @param <T>         列表值类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return HashMap
     * @since 1.0.0
     */
    public static <T, K, V> Map<K, V> toMap( Map<K, V> resultMap, Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper ) {
        if (null == resultMap) {
            resultMap = MapUtil.newHashMap();
        }
        if (ObjectUtil.isNull(iterable)) {
            return resultMap;
        }

        for (T value : iterable) {
            resultMap.put(keyMapper.apply(value), valueMapper.apply(value));
        }

        return resultMap;
    }

    /**
     * Iterator转List<br>
     * 不判断，直接生成新的List
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return List
     * @since 1.0.0
     */
    public static <E> List<E> toList( Iterable<E> iter ) {
        if (null == iter) {
            return null;
        }
        return toList(iter.iterator());
    }

    /**
     * Iterator转List<br>
     * 不判断，直接生成新的List
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return List
     * @since 1.0.0
     */
    public static <E> List<E> toList( Iterator<E> iter ) {
        final List<E> list = new ArrayList<>();
        while (iter.hasNext()) {
            list.add(iter.next());
        }
        return list;
    }

    /**
     * Enumeration转换为Iterator
     * <p>
     * Adapt the specified {@code Enumeration} to the {@code Iterator} interface
     *
     * @param <E> 集合元素类型
     * @param e   {@link Enumeration}
     * @return {@link Iterator}
     */
    public static <E> Iterator<E> asIterator( Enumeration<E> e ) {
        return new EnumerationIter<>(e);
    }

    /**
     * {@link Iterator} 转为 {@link Iterable}
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return {@link Iterable}
     */
    public static <E> Iterable<E> asIterable( final Iterator<E> iter ) {
        return () -> iter;
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return 第一个元素
     */
    public static <T> T getFirst( Iterable<T> iterable ) {
        if (null == iterable) {
            return null;
        }
        return getFirst(iterable.iterator());
    }

    /**
     * 获取集合的第一个非空元素
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return 第一个元素
     * @since 1.0.0
     */
    public static <T> T getFirstNoneNull( Iterable<T> iterable ) {
        if (null == iterable) {
            return null;
        }
        return getFirstNoneNull(iterable.iterator());
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个元素
     */
    public static <T> T getFirst( Iterator<T> iterator ) {
        if (null != iterator && iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * 获取集合的第一个非空元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个非空元素，null表示未找到
     * @since 1.0.0
     */
    public static <T> T getFirstNoneNull( Iterator<T> iterator ) {
        return firstMatch(iterator, Objects::nonNull);
    }

    /**
     * 返回{@link Iterator}中第一个匹配规则的值
     *
     * @param <T>      数组元素类型
     * @param iterator {@link Iterator}
     * @param matcher  匹配接口，实现此接口自定义匹配规则
     * @return 匹配元素，如果不存在匹配元素或{@link Iterator}为空，返回 {@code null}
     * @since 1.0.0
     */
    public static <T> T firstMatch( Iterator<T> iterator, Matcher<T> matcher ) {
        Precondition.notNull(matcher, "Matcher must be not null !");
        if (null != iterator) {
            while (iterator.hasNext()) {
                final T next = iterator.next();
                if (matcher.match(next)) {
                    return next;
                }
            }
        }
        return null;
    }

    /**
     * 获得{@link Iterable}对象的元素类型（通过第一个非空元素判断）<br>
     * 注意，此方法至少会调用多次next方法
     *
     * @param iterable {@link Iterable}
     * @return 元素类型，当列表为空或元素全部为null时，返回null
     */
    public static Class<?> getElementType( Iterable<?> iterable ) {
        if (null != iterable) {
            final Iterator<?> iterator = iterable.iterator();
            return getElementType(iterator);
        }
        return null;
    }

    /**
     * 获得{@link Iterator}对象的元素类型（通过第一个非空元素判断）<br>
     * 注意，此方法至少会调用多次next方法
     *
     * @param iterator {@link Iterator}
     * @return 元素类型，当列表为空或元素全部为null时，返回null
     */
    public static Class<?> getElementType( Iterator<?> iterator ) {
        final Iterator<?> iter2 = new CopiedIter<>(iterator);
        if (iter2.hasNext()) {
            final Object t = iter2.next();
            if (null != t) {
                return t.getClass();
            }
        }
        return null;
    }

    /**
     * 编辑，此方法产生一个新{@link ArrayList}<br>
     * 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回null表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     *
     * @param <T>    集合元素类型
     * @param iter   集合
     * @param editor 编辑器接口, {@code null}表示不编辑
     * @return 过滤后的集合
     * @since 1.0.0
     */
    public static <T> List<T> edit( Iterable<T> iter, Editor<T> editor ) {
        final List<T> result = new ArrayList<>();
        if (null == iter) {
            return result;
        }

        T modified;
        for (T t : iter) {
            modified = (null == editor) ? t : editor.edit(t);
            if (null != modified) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 过滤集合，此方法在原集合上直接修改<br>
     * 通过实现Filter接口，完成元素的过滤，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#apply(Object)}方法返回false的对象将被使用{@link Iterator#remove()}方法移除
     * </pre>
     *
     * @param <T>       集合类型
     * @param <E>       集合元素类型
     * @param iter      集合
     * @param predicate 过滤器接口
     * @return 编辑后的集合
     * @since 1.0.0
     */
    public static <T extends Iterable<E>, E> T filter( T iter, Predicate<E> predicate ) {
        if (null == iter) {
            return null;
        }

        filter(iter.iterator(), predicate);

        return iter;
    }

    /**
     * 过滤集合，此方法在原集合上直接修改<br>
     * 通过实现Filter接口，完成元素的过滤，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#apply(Object)}方法返回false的对象将被使用{@link Iterator#remove()}方法移除
     * </pre>
     *
     * @param <E>       集合元素类型
     * @param iter      集合
     * @param predicate 过滤器接口
     * @return 编辑后的集合
     * @since 1.0.0
     */
    public static <E> Iterator<E> filter( Iterator<E> iter, Predicate<E> predicate ) {
        if (null == iter || null == predicate) {
            return iter;
        }

        while (iter.hasNext()) {
            if (false == predicate.apply(iter.next())) {
                iter.remove();
            }
        }
        return iter;
    }

    /**
     * Iterator转换为Map，转换规则为：<br>
     * 按照keyFunc函数规则根据元素对象生成Key，元素作为值
     *
     * @param <K>      Map键类型
     * @param <V>      Map值类型
     * @param iterator 数据列表
     * @param map      Map对象，转换后的键值对加入此Map，通过传入此对象自定义Map类型
     * @param keyFunc  生成key的函数
     * @return 生成的map
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> toMap( Iterator<V> iterator, Map<K, V> map, Func1<V, K> keyFunc ) {
        return toMap(iterator, map, keyFunc, ( value ) -> value);
    }

    /**
     * 集合转换为Map，转换规则为：<br>
     * 按照keyFunc函数规则根据元素对象生成Key，按照valueFunc函数规则根据元素对象生成value组成新的Map
     *
     * @param <K>       Map键类型
     * @param <V>       Map值类型
     * @param <E>       元素类型
     * @param iterator  数据列表
     * @param map       Map对象，转换后的键值对加入此Map，通过传入此对象自定义Map类型
     * @param keyFunc   生成key的函数
     * @param valueFunc 生成值的策略函数
     * @return 生成的map
     * @since 1.0.0
     */
    public static <K, V, E> Map<K, V> toMap( Iterator<E> iterator, Map<K, V> map, Func1<E, K> keyFunc, Func1<E, V> valueFunc ) {
        if (null == iterator) {
            return map;
        }

        if (null == map) {
            map = MapUtil.newHashMap(true);
        }

        E element;
        while (iterator.hasNext()) {
            element = iterator.next();
            try {
                map.put(keyFunc.apply(element), valueFunc.apply(element));
            } catch (Exception e) {
                throw new UtilException(e);
            }
        }
        return map;
    }

    /**
     * 返回一个空Iterator
     *
     * @param <T> 元素类型
     * @return 空Iterator
     * @see Collections#emptyIterator()
     * @since 1.0.0
     */
    public static <T> Iterator<T> empty() {
        return Collections.emptyIterator();
    }


    /**
     * 按照给定函数，转换{@link Iterator}为另一种类型的{@link Iterator}
     *
     * @param <F>      源元素类型
     * @param <T>      目标元素类型
     * @param iterable 源{@link Iterator}
     * @param function 转换函数
     * @return 转换后的{@link Iterator}
     * @since 1.0.0
     */
    public static <F, T> Iterable<T> trans( Iterable<F> iterable, Function<? super F, ? extends T> function ) {

        if (iterable instanceof Collection) {
            return CollUtil.trans((Collection) iterable, function);
        }

        return CollUtil.trans(CollUtil.newArrayList(iterable), function);
    }


    /**
     * 按照给定函数，转换{@link Iterator}为另一种类型的{@link Iterator}
     *
     * @param <F>      源元素类型
     * @param <T>      目标元素类型
     * @param iterator 源{@link Iterator}
     * @param function 转换函数
     * @return 转换后的{@link Iterator}
     * @since 1.0.0
     */
    public static <F, T> Iterator<T> trans( Iterator<F> iterator, Function<? super F, ? extends T> function ) {
        return new TransIter<>(iterator, function);
    }

    /**
     * 返回 Iterable 对象的元素数量
     * Returns the number of elements in {@code iterable}.
     *
     * @param iterable Iterable对象
     * @return Iterable对象的元素数量
     * @since 1.0.0
     */
    public static int size( final Iterable<?> iterable ) {
        if (null == iterable) {
            return 0;
        }

        if (iterable instanceof Collection<?>) {
            return ((Collection<?>) iterable).size();
        } else {
            return size(iterable.iterator());
        }
    }

    /**
     * 返回 Iterator 对象的元素数量
     *
     * @param iterator Iterator对象
     * @return Iterator对象的元素数量
     * @since 1.0.0
     */
    public static int size( final Iterator<?> iterator ) {
        int size = 0;
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
        }
        return size;
    }

    /**
     * 判断两个{@link Iterable} 是否元素和顺序相同，返回{@code true}的条件是：
     * <ul>
     *     <li>两个{@link Iterable}必须长度相同</li>
     *     <li>两个{@link Iterable}元素相同index的对象必须equals，满足{@link Objects#equals(Object, Object)}</li>
     * </ul>
     * 此方法来自Apache-icefrogs-Collections4。
     *
     * @param list1 列表1
     * @param list2 列表2
     * @return 是否相同
     * @since 1.0.0
     */
    public static boolean isEqualList( final Iterable<?> list1, final Iterable<?> list2 ) {
        if (list1 == list2) {
            return true;
        }

        final Iterator<?> it1 = list1.iterator();
        final Iterator<?> it2 = list2.iterator();
        Object obj1;
        Object obj2;
        while (it1.hasNext() && it2.hasNext()) {
            obj1 = it1.next();
            obj2 = it2.next();

            if (false == Objects.equals(obj1, obj2)) {
                return false;
            }
        }

        // 当两个Iterable长度不一致时返回false
        return false == (it1.hasNext() || it2.hasNext());
    }


    /**
     * Returns {@code true} if {@code iterable} contains any element {@code o} for which {@code
     * ObjectUtil.equals(o, element)} would return {@code true}. Otherwise returns {@code false}, even in
     * cases where {@link Collection#contains} might throw {@link NullPointerException} or {@link
     * ClassCastException}.
     */
    // <? extends Object> instead of <?> because of Kotlin b/189937072, discussed in Joiner.
    public static boolean contains(
            Iterable<? extends Object> iterable, @CheckForNull Object element ) {
        if (iterable instanceof Collection) {
            Collection<?> collection = (Collection<?>) iterable;
            return CollUtil.safeContains(collection, element);
        }
        return contains(iterable.iterator(), element);
    }


    /**
     * Returns {@code true} if {@code iterator} contains {@code element}.
     */
    public static boolean contains( Iterator<?> iterator, @CheckForNull Object element ) {
        if (element == null) {
            while (iterator.hasNext()) {
                if (iterator.next() == null) {
                    return true;
                }
            }
        } else {
            while (iterator.hasNext()) {
                if (element.equals(iterator.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Traverses an iterator and removes every element that belongs to the provided collection. The
     * iterator will be left exhausted: its {@code hasNext()} method will return {@code false}.
     *
     * @param removeFrom       the iterator to (potentially) remove elements from
     * @param elementsToRemove the elements to remove
     * @return {@code true} if any element was removed from {@code iterator}
     */

    public static boolean removeAll( Iterator<?> removeFrom, Collection<?> elementsToRemove ) {
        checkNotNull(elementsToRemove);
        boolean result = false;
        while (removeFrom.hasNext()) {
            if (elementsToRemove.contains(removeFrom.next())) {
                removeFrom.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * Removes, from an iterable, every element that satisfies the provided predicate.
     *
     * <p>Removals may or may not happen immediately as each element is tested against the predicate.
     * The behavior of this method is not specified if {@code predicate} is dependent on {@code
     * removeFrom}.
     *
     * <p><b>Java 8 users:</b> if {@code removeFrom} is a {@link Collection}, use {@code
     * removeFrom.removeIf(predicate)} instead.
     *
     * @param removeFrom the iterable to (potentially) remove elements from
     * @param predicate  a predicate that determines whether an element should be removed
     * @return {@code true} if any elements were removed from the iterable
     * @throws UnsupportedOperationException if the iterable does not support {@code remove()}.
     */


    public static <T extends Object> boolean removeIf(
            Iterable<T> removeFrom, Predicate<? super T> predicate ) {
        if (removeFrom instanceof Collection) {
            return ((Collection<T>) removeFrom).removeIf(predicate);
        }
        return removeIf(removeFrom.iterator(), predicate);
    }

    /**
     * Removes every element that satisfies the provided predicate from the iterator. The iterator
     * will be left exhausted: its {@code hasNext()} method will return {@code false}.
     *
     * @param removeFrom the iterator to (potentially) remove elements from
     * @param predicate  a predicate that determines whether an element should be removed
     * @return {@code true} if any elements were removed from the iterator
     */

    public static <T extends Object> boolean removeIf(
            Iterator<T> removeFrom, Predicate predicate ) {
        checkNotNull(predicate);
        boolean modified = false;
        while (removeFrom.hasNext()) {
            if (predicate.apply(removeFrom.next())) {
                removeFrom.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Traverses an iterator and removes every element that does not belong to the provided
     * collection. The iterator will be left exhausted: its {@code hasNext()} method will return
     * {@code false}.
     *
     * @param removeFrom       the iterator to (potentially) remove elements from
     * @param elementsToRetain the elements to retain
     * @return {@code true} if any element was removed from {@code iterator}
     */

    public static boolean retainAll( Iterator<?> removeFrom, Collection<?> elementsToRetain ) {
        checkNotNull(elementsToRetain);
        boolean result = false;
        while (removeFrom.hasNext()) {
            if (!elementsToRetain.contains(removeFrom.next())) {
                removeFrom.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * Removes and returns the first matching element, or returns {@code null} if there is none.
     */
    @CheckForNull
    public static <T extends Object> T removeFirstMatching(
            Iterable<T> removeFrom, Predicate<? super T> predicate ) {
        checkNotNull(predicate);
        Iterator<T> iterator = removeFrom.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (predicate.apply(next)) {
                iterator.remove();
                return next;
            }
        }
        return null;
    }

    /**
     * Determines whether two iterators contain equal elements in the same order. More specifically,
     * this method returns {@code true} if {@code iterator1} and {@code iterator2} contain the same
     * number of elements and every element of {@code iterator1} is equal to the corresponding element
     * of {@code iterator2}.
     *
     * <p>Note that this will modify the supplied iterators, since they will have been advanced some
     * number of elements forward.
     */
    public static boolean elementsEqual( Iterator<?> iterator1, Iterator<?> iterator2 ) {
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            if (!ObjectUtil.equal(o1, o2)) {
                return false;
            }
        }
        return !iterator2.hasNext();
    }

    /**
     * Returns a string representation of {@code iterable}, with the format {@code [e1, e2, ..., en]}
     * (that is, identical to {@link java.util.Arrays Arrays}{@code
     * .toString(Iterables.toArray(iterable))}). Note that for <i>most</i> implementations of {@link
     * Collection}, {@code collection.toString()} also gives the same result, but that behavior is not
     * generally guaranteed.
     */
    public static String toString( Iterable<?> iterable ) {
        return toString(iterable.iterator());
    }

    /**
     * Returns a string representation of {@code iterator}, with the format {@code [e1, e2, ..., en]}.
     * The iterator will be left exhausted: its {@code hasNext()} method will return {@code false}.
     */
    public static String toString( Iterator<?> iterator ) {
        StringBuilder sb = new StringBuilder().append('[');
        boolean first = true;
        while (iterator.hasNext()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(iterator.next());
        }
        return sb.append(']').toString();
    }

    /**
     * Returns the single element contained in {@code iterable}.
     *
     * <p><b>Java 8 users:</b> the {@code Stream} equivalent to this method is {@code
     * stream.collect(MoreCollectors.onlyElement())}.
     *
     * @throws NoSuchElementException   if the iterable is empty
     * @throws IllegalArgumentException if the iterable contains multiple elements
     */

    public static <T extends Object> T getOnlyElement( Iterable<T> iterable ) {
        return getOnlyElement(iterable.iterator());
    }

    /**
     * Returns the single element contained in {@code iterator}.
     *
     * @throws NoSuchElementException   if the iterator is empty
     * @throws IllegalArgumentException if the iterator contains multiple elements. The state of the
     *                                  iterator is unspecified.
     */

    public static <T extends Object> T getOnlyElement( Iterator<T> iterator ) {
        T first = iterator.next();
        if (!iterator.hasNext()) {
            return first;
        }

        StringBuilder sb = new StringBuilder().append("expected one element but was: <").append(first);
        for (int i = 0; i < 4 && iterator.hasNext(); i++) {
            sb.append(", ").append(iterator.next());
        }
        if (iterator.hasNext()) {
            sb.append(", ...");
        }
        sb.append('>');

        throw new IllegalArgumentException(sb.toString());
    }

    /**
     * Returns the single element contained in {@code iterator}, or {@code defaultValue} if the
     * iterator is empty.
     *
     * @throws IllegalArgumentException if the iterator contains multiple elements. The state of the
     *                                  iterator is unspecified.
     */

    public static <T extends Object> T getOnlyElement(
            Iterator<? extends T> iterator, T defaultValue ) {
        return iterator.hasNext() ? getOnlyElement(iterator) : defaultValue;
    }

    /**
     * Adds all elements in {@code iterable} to {@code collection}.
     *
     * @return {@code true} if {@code collection} was modified as a result of this operation.
     */

    public static <T extends Object> boolean addAll(
            Collection<T> addTo, Iterable<? extends T> elementsToAdd ) {
        if (elementsToAdd instanceof Collection) {
            Collection<? extends T> c = (Collection<? extends T>) elementsToAdd;
            return addTo.addAll(c);
        }
        return addAll(addTo, checkNotNull(elementsToAdd).iterator());
    }

    /**
     * Adds all elements in {@code iterator} to {@code collection}. The iterator will be left
     * exhausted: its {@code hasNext()} method will return {@code false}.
     * <p>
     * 获取迭代器里的每一个元素 ，并将其元素 加入到collection  中
     *
     * @param addTo    原始的 Collection
     * @param iterator 有数据的迭代器
     * @param <T>
     * @return {@code true} if {@code collection} was modified as a result of this operation
     */

    public static <T extends Object> boolean addAll(
            Collection<T> addTo, Iterator<? extends T> iterator ) {
        checkNotNull(addTo);
        checkNotNull(iterator);
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= addTo.add(iterator.next());
        }
        return wasModified;
    }

    /**
     * Returns the number of elements in the specified iterator that equal the specified object. The
     * iterator will be left exhausted: its {@code hasNext()} method will return {@code false}.
     *
     * @see Collections#frequency
     */
    public static int frequency( Iterator<?> iterator, @CheckForNull Object element ) {
        int count = 0;
        while (contains(iterator, element)) {
            // Since it lives in the same class, we know contains gets to the element and then stops,
            // though that isn't currently publicly documented.
            count++;
        }
        return count;
    }

    /**
     * Returns {@code true} if any element in {@code iterable} satisfies the predicate.
     *
     * <p><b>{@code Stream} equivalent:</b> {@link Stream#anyMatch}.
     */
    public static <T extends Object> boolean any(
            Iterable<T> iterable, Predicate<? super T> predicate ) {
        return any(iterable.iterator(), predicate);
    }


    /**
     * Returns {@code true} if one or more elements returned by {@code iterator} satisfy the given
     * predicate.
     */
    public static <T extends Object> boolean any(
            Iterator<T> iterator, Predicate predicate ) {
        return indexOf(iterator, predicate) != -1;
    }
    /**
     * Returns {@code true} if every element in {@code iterable} satisfies the predicate. If {@code
     * iterable} is empty, {@code true} is returned.
     *
     * <p><b>{@code Stream} equivalent:</b> {@link Stream#allMatch}.
     */
    public static <T extends Object> boolean all(
            Iterable<T> iterable, Predicate<? super T> predicate ) {
        return all(iterable.iterator(), predicate);
    }

    /**
     * Returns {@code true} if every element returned by {@code iterator} satisfies the given
     * predicate. If {@code iterator} is empty, {@code true} is returned.
     */
    public static <T extends Object> boolean all(
            Iterator<T> iterator, Predicate predicate ) {
        checkNotNull(predicate);
        while (iterator.hasNext()) {
            T element = iterator.next();
            if (!predicate.apply(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the first element in {@code iterator} that satisfies the given predicate. If no such
     * element is found, {@code defaultValue} will be returned from this method and the iterator will
     * be left exhausted: its {@code hasNext()} method will return {@code false}. Note that this can
     * usually be handled more naturally using {@code tryFind(iterator, predicate).or(defaultValue)}.
     */
    // For discussion of this signature, see the corresponding overload of *IterUtil*.find.
    @CheckForNull
    public static <T extends Object> T find(
            Iterator<? extends T> iterator,
            Predicate predicate,
            @CheckForNull T defaultValue ) {
        checkNotNull(iterator);
        checkNotNull(predicate);
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (predicate.apply(t)) {
                return t;
            }
        }
        return defaultValue;
    }

    /**
     * Returns an {@link Optional} containing the first element in {@code iterator} that satisfies the
     * given predicate, if such an element exists. If no such element is found, an empty {@link
     * Optional} will be returned from this method and the iterator will be left exhausted: its {@code
     * hasNext()} method will return {@code false}.
     *
     * <p><b>Warning:</b> avoid using a {@code predicate} that matches {@code null}. If {@code null}
     * is matched in {@code iterator}, a NullPointerException will be thrown.
     */
    public static <T> Optional<T> tryFind( Iterator<T> iterator, Predicate predicate ) {
        checkNotNull(iterator);
        checkNotNull(predicate);
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (predicate.apply(t)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the index in {@code iterator} of the first element that satisfies the provided {@code
     * predicate}, or {@code -1} if the Iterator has no such elements.
     *
     * <p>More formally, returns the lowest index {@code i} such that {@code
     * predicate.apply(Iterators.get(iterator, i))} returns {@code true}, or {@code -1} if there is no
     * such index.
     *
     * <p>If -1 is returned, the iterator will be left exhausted: its {@code hasNext()} method will
     * return {@code false}. Otherwise, the iterator will be set to the element which satisfies the
     * {@code predicate}.
     */
    public static <T extends Object> int indexOf(
            Iterator<T> iterator, Predicate predicate ) {
        checkNotNull(predicate, "predicate");
        for (int i = 0; iterator.hasNext(); i++) {
            T current = iterator.next();
            if (predicate.apply(current)) {
                return i;
            }
        }
        return -1;
    }


    /**
     * Advances {@code iterator} {@code position + 1} times, returning the element at the {@code
     * position}th position.
     *
     * @param position position of the element to return
     * @return the element at the specified position in {@code iterator}
     * @throws IndexOutOfBoundsException if {@code position} is negative or greater than or equal to
     *                                   the number of elements remaining in {@code iterator}
     */

    public static <T extends Object> T get( Iterator<T> iterator, int position ) {
        checkNonnegative(position);
        int skipped = advance(iterator, position);
        if (!iterator.hasNext()) {
            throw new IndexOutOfBoundsException(
                    "position ("
                            + position
                            + ") must be less than the number of elements that remained ("
                            + skipped
                            + ")");
        }
        return iterator.next();
    }

    /**
     * Advances {@code iterator} {@code position + 1} times, returning the element at the {@code
     * position}th position or {@code defaultValue} otherwise.
     *
     * @param position     position of the element to return
     * @param defaultValue the default value to return if the iterator is empty or if {@code position}
     *                     is greater than the number of elements remaining in {@code iterator}
     * @return the element at the specified position in {@code iterator} or {@code defaultValue} if
     * {@code iterator} produces fewer than {@code position + 1} elements.
     * @throws IndexOutOfBoundsException if {@code position} is negative
     */

    public static <T extends Object> T get(
            Iterator<? extends T> iterator, int position, T defaultValue ) {
        checkNonnegative(position);
        advance(iterator, position);
        return getNext(iterator, defaultValue);
    }

    static void checkNonnegative( int position ) {
        if (position < 0) {
            throw new IndexOutOfBoundsException("position (" + position + ") must not be negative");
        }
    }

    /**
     * Returns the next element in {@code iterator} or {@code defaultValue} if the iterator is empty.
     * The {@link IterUtil} analog to this method is {@link IterUtil#getFirst}.
     *
     * @param defaultValue the default value to return if the iterator is empty
     * @return the next element of {@code iterator} or the default value
     * @since 7.0
     */

    public static <T extends Object> T getNext(
            Iterator<? extends T> iterator, T defaultValue ) {
        return iterator.hasNext() ? iterator.next() : defaultValue;
    }

    /**
     * Advances {@code iterator} to the end, returning the last element.
     *
     * @return the last element of {@code iterator}
     * @throws NoSuchElementException if the iterator is empty
     */

    public static <T extends Object> T getLast( Iterator<T> iterator ) {
        while (true) {
            T current = iterator.next();
            if (!iterator.hasNext()) {
                return current;
            }
        }
    }

    /**
     * Advances {@code iterator} to the end, returning the last element or {@code defaultValue} if the
     * iterator is empty.
     *
     * @param defaultValue the default value to return if the iterator is empty
     * @return the last element of {@code iterator}
     */

    public static <T extends Object> T getLast(
            Iterator<? extends T> iterator, T defaultValue ) {
        return iterator.hasNext() ? getLast(iterator) : defaultValue;
    }

    /**
     * Calls {@code next()} on {@code iterator}, either {@code numberToAdvance} times or until {@code
     * hasNext()} returns {@code false}, whichever comes first.
     *
     * @return the number of elements the iterator was advanced
     */

    public static int advance( Iterator<?> iterator, int numberToAdvance ) {
        checkNotNull(iterator);
        checkArgument(numberToAdvance >= 0, "numberToAdvance must be nonnegative");

        int i;
        for (i = 0; i < numberToAdvance && iterator.hasNext(); i++) {
            iterator.next();
        }
        return i;
    }

    /**
     * Returns a view containing the first {@code limitSize} elements of {@code iterator}. If {@code
     * iterator} contains fewer than {@code limitSize} elements, the returned view contains all of its
     * elements. The returned iterator supports {@code remove()} if {@code iterator} does.
     *
     * @param iterator  the iterator to limit
     * @param limitSize the maximum number of elements in the returned iterator
     * @throws IllegalArgumentException if {@code limitSize} is negative
     */
    public static <T extends Object> Iterator<T> limit(
            final Iterator<T> iterator, final int limitSize ) {
        checkNotNull(iterator);
        checkArgument(limitSize >= 0, "limit is negative");
        return new Iterator<T>() {
            private int count;

            @Override
            public boolean hasNext() {
                return count < limitSize && iterator.hasNext();
            }

            @Override

            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                count++;
                return iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }


    /**
     * Clears the iterator using its remove method.
     *
     * @param iterator
     */
    public static void clear( Iterator<?> iterator ) {
        checkNotNull(iterator);
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    /**
     * Returns the first element in {@code iterable} that satisfies the given predicate, or {@code
     * defaultValue} if none found. Note that this can usually be handled more naturally using {@code
     * tryFind(iterable, predicate).or(defaultValue)}.
     *
     * <p><b>{@code Stream} equivalent:</b> {@code
     * stream.filter(predicate).findFirst().orElse(defaultValue)}
     * @param iterable
     * @param predicate
     * @param defaultValue
     * @param <T>
     * @return
     */
    @CheckForNull
    public static <T extends Object> T find(
            Iterable<? extends T> iterable,
            Predicate<? super T> predicate,
            @CheckForNull T defaultValue ) {
        return find(iterable.iterator(), predicate, defaultValue);
    }

    /**
     * Returns the first element in {@code iterable} that satisfies the given predicate; use this
     * method only when such an element is known to exist. If it is possible that <i>no</i> element
     * will match, use {@link #tryFind} or {@link #find(Iterable, Predicate, Object)} instead.
     *
     * <p><b>{@code Stream} equivalent:</b> {@code stream.filter(predicate).findFirst().get()}
     *
     * @throws NoSuchElementException if no element in {@code iterable} matches the given predicate
     */

    public static <T extends Object> T find(
            Iterable<T> iterable, Predicate<? super T> predicate ) {
        return find(iterable.iterator(), predicate);
    }

    /**
     * Returns the first element in {@code iterator} that satisfies the given predicate; use this
     * method only when such an element is known to exist. If no such element is found, the iterator
     * will be left exhausted: its {@code hasNext()} method will return {@code false}. If it is
     * possible that <i>no</i> element will match, use {@link #tryFind} or {@link #find(Iterator,
     * Predicate, Object)} instead.
     *
     * @throws NoSuchElementException if no element in {@code iterator} matches the given predicate
     */

    public static <T extends Object> T find(
            Iterator<T> iterator, Predicate<? super T> predicate ) {
        checkNotNull(iterator);
        checkNotNull(predicate);
        while (iterator.hasNext()) {
            T t = iterator.next();
            if (predicate.apply(t)) {
                return t;
            }
        }
        throw new NoSuchElementException();
    }


    /**
     * Returns the element at the specified position in an iterable.
     *
     * <p><b>{@code Stream} equivalent:</b> {@code stream.skip(position).findFirst().get()} (throws
     * {@code NoSuchElementException} if out of bounds)
     *
     * @param position position of the element to return
     * @return the element at the specified position in {@code iterable}
     * @throws IndexOutOfBoundsException if {@code position} is negative or greater than or equal to
     *                                   the size of {@code iterable}
     */
    public static <T extends Object> T get( Iterable<T> iterable, int position ) {
        checkNotNull(iterable);
        return (iterable instanceof List)
                ? ((List<T>) iterable).get(position)
                : get(iterable.iterator(), position);
    }

    /**
     * Returns the element at the specified position in an iterable or a default value otherwise.
     *
     * <p><b>{@code Stream} equivalent:</b> {@code
     * stream.skip(position).findFirst().orElse(defaultValue)} (returns the default value if the index
     * is out of bounds)
     *
     * @param position     position of the element to return
     * @param defaultValue the default value to return if {@code position} is greater than or equal to
     *                     the size of the iterable
     * @return the element at the specified position in {@code iterable} or {@code defaultValue} if
     * {@code iterable} contains fewer than {@code position + 1} elements.
     * @throws IndexOutOfBoundsException if {@code position} is negative
     */

    public static <T extends Object> T get(
            Iterable<? extends T> iterable, int position,  T defaultValue ) {
        checkNotNull(iterable);
        checkNonnegative(position);
        if (iterable instanceof List) {
            List<? extends T> list = (List<T>)(iterable);
            return (position < list.size()) ? list.get(position) : defaultValue;
        } else {
            Iterator<? extends T> iterator = iterable.iterator();
            advance(iterator, position);
            return getNext(iterator, defaultValue);
        }
    }

    /**
     * Returns the first element in {@code iterable} or {@code defaultValue} if the iterable is empty.
     * The  analog to this method is }.
     *
     * <p>If no default value is desired (and the caller instead wants a {@link
     * NoSuchElementException} to be thrown), it is recommended that {@code
     * iterable.iterator().next()} is used instead.
     *
     * <p>To get the only element in a single-element {@code Iterable}, consider using  or instead.

     * <p><b>{@code Stream} equivalent:</b> {@code stream.findFirst().orElse(defaultValue)}
     *
     * @param defaultValue the default value to return if the iterable is empty
     * @return the first element of {@code iterable} or the default value
     */

    public static <T extends Object> T getFirst(
            Iterable<? extends T> iterable,T defaultValue ) {
        return getNext(iterable.iterator(), defaultValue);
    }


    /**
     * Returns the last element of {@code iterable}. If {@code iterable} is a {@link List} with {@link
     * RandomAccess} support, then this operation is guaranteed to be {@code O(1)}.
     *
     * <p><b>{@code Stream} equivalent:</b>
     *
     * @return the last element of {@code iterable}
     * @throws NoSuchElementException if the iterable is empty
     */

    public static <T extends Object> T getLast( Iterable<T> iterable ) {

        if (iterable instanceof List) {
            List<T> list = (List<T>) iterable;
            if (list.isEmpty()) {
                throw new NoSuchElementException();
            }
            return list.get(list.size() - 1);
        }

        return getLast(iterable.iterator());
    }

    /**
     * Returns the last element of {@code iterable} or {@code defaultValue} if the iterable is empty.
     * If {@code iterable} is a {@link List} with {@link RandomAccess} support, then this operation is
     * guaranteed to be {@code O(1)}.
     *
     * <p><b>{@code Stream} equivalent:</b> {@code Streams.findLast(stream).orElse(defaultValue)}
     *
     * @param defaultValue the value to return if {@code iterable} is empty
     * @return the last element of {@code iterable} or the default value
     */

    public static <T extends Object> T getLast(
            Iterable<? extends T> iterable,  T defaultValue ) {
        if (iterable instanceof List) {
            List<T> list = (List<T>) iterable;
            if (list.isEmpty()) {
                return defaultValue;
            } else if (iterable instanceof List) {
                return  list.get(list.size() - 1);
            }
        }

        return getLast(iterable.iterator(), defaultValue);
    }


    /**
     * Returns a view of {@code iterable} that skips its first {@code numberToSkip} elements. If
     * {@code iterable} contains fewer than {@code numberToSkip} elements, the returned iterable skips
     * all of its elements.
     *
     * <p>Modifications to the underlying {@link Iterable} before a call to {@code iterator()} are
     * reflected in the returned iterator. That is, the iterator skips the first {@code numberToSkip}
     * elements that exist when the {@code Iterator} is created, not when {@code skip()} is called.
     *
     * <p>The returned iterable's iterator supports {@code remove()} if the iterator of the underlying
     * iterable supports it. Note that it is <i>not</i> possible to delete the last skipped element by
     * immediately calling {@code remove()} on that iterator, as the {@code Iterator} contract states
     * that a call to {@code remove()} before a call to {@code next()} will throw an {@link
     * IllegalStateException}.
     *
     * <p><b>{@code Stream} equivalent:</b> {@link Stream#skip}
     */
    public static <T extends Object> Iterable<T> skip(
            final Iterable<T> iterable, final int numberToSkip ) {
        checkNotNull(iterable);
        checkArgument(numberToSkip >= 0, "number to skip cannot be negative");

        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                if (iterable instanceof List) {
                    final List<T> list = (List<T>) iterable;
                    int toSkip = Math.min(list.size(), numberToSkip);
                    return list.subList(toSkip, list.size()).iterator();
                }
                final Iterator<T> iterator = iterable.iterator();

                advance(iterator, numberToSkip);

                /*
                 * We can't just return the iterator because an immediate call to its
                 * remove() method would remove one of the skipped elements instead of
                 * throwing an IllegalStateException.
                 */
                return new Iterator<T>() {
                    boolean atStart = true;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public T next() {
                        T result = iterator.next();
                        atStart = false; // not called if next() fails
                        return result;
                    }

                    @Override
                    public void remove() {
                        checkRemove(!atStart);
                        iterator.remove();
                    }
                };
            }

            @Override
            public Spliterator<T> spliterator() {
                if (iterable instanceof List) {
                    final List<T> list = (List<T>) iterable;
                    int toSkip = Math.min(list.size(), numberToSkip);
                    return list.subList(toSkip, list.size()).spliterator();
                } else {
                    return StreamUtil.of(iterable).skip(numberToSkip).spliterator();
                }
            }
        };
    }


    /**
     * Returns a view of {@code iterable} containing its first {@code limitSize} elements. If {@code
     * iterable} contains fewer than {@code limitSize} elements, the returned view contains all of its
     * elements. The returned iterable's iterator supports {@code remove()} if {@code iterable}'s
     * iterator does.
     *
     * <p><b>{@code Stream} equivalent:</b> {@link Stream#limit}
     *
     * @param iterable  the iterable to limit
     * @param limitSize the maximum number of elements in the returned iterable
     * @throws IllegalArgumentException if {@code limitSize} is negative
     */
    public static <T extends Object> Iterable<T> limit(
            final Iterable<T> iterable, final int limitSize ) {
        checkNotNull(iterable);
        checkArgument(limitSize >= 0, "limit is negative");
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return limit(iterable.iterator(), limitSize);
            }

            @Override
            public Spliterator<T> spliterator() {
                return StreamUtil.of(iterable).limit(limitSize).spliterator();
            }
        };
    }

    public static <T> Iterator<T> concat(
            Iterator< T> a, Iterator<T> b ) {
        CompositeIterator< T> compositeIterator = new CompositeIterator<>();
        compositeIterator.add(a);
        compositeIterator.add(b);
        return compositeIterator;
    }


    public static <T> Iterable<T> concat(
            Iterable< T> a, Iterable<T> b ) {

        Iterable [] cc = {a,b};
        ArrayIter<T> arrayIter = new ArrayIter<T>( cc);
        return arrayIter;
    }


}
