package com.whaleal.icefrog.core.collection;

import com.whaleal.icefrog.core.bean.BeanUtil;
import com.whaleal.icefrog.core.comparator.CompareUtil;
import com.whaleal.icefrog.core.comparator.PinyinComparator;
import com.whaleal.icefrog.core.comparator.PropertyComparator;
import com.whaleal.icefrog.core.convert.Convert;
import com.whaleal.icefrog.core.convert.ConverterRegistry;
import com.whaleal.icefrog.core.exceptions.UtilException;
import com.whaleal.icefrog.core.lang.Editor;
import com.whaleal.icefrog.core.lang.Matcher;
import com.whaleal.icefrog.core.lang.Predicate;
import com.whaleal.icefrog.core.lang.func.Func1;
import com.whaleal.icefrog.core.lang.hash.Hash32;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.*;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.whaleal.icefrog.core.collection.ListUtil.of;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;
import static com.whaleal.icefrog.core.lang.Precondition.notNull;

/**
 * 集合相关工具类
 * <p>
 * 此工具方法针对{@link Collection}及其实现类封装的工具。
 * <p>
 * 由于{@link Collection} 实现了{@link Iterable}接口，因此部分工具此类不提供，而是在{@link IterUtil} 中提供
 *
 * @author Looly
 * @author wh
 * @see IterUtil
 * @since 1.0.0
 */
public class CollUtil {
    public static final float DEFAULT_LOAD_FACTOR = 0.75f ;


    /**
     * 如果提供的集合为{@code null}，返回一个不可变的默认空集合，否则返回原集合<br>
     * 空集合使用{@link Collections#emptySet()}
     *
     * @param <T> 集合元素类型
     * @param set 提供的集合，可能为null
     * @return 原集合，若为null返回空集合
     * @since 1.0.0
     */
    public static <T> Set<T> emptyIfNull( Set<T> set ) {
        return (null == set) ? Collections.emptySet() : set;
    }

    /**
     * 如果提供的集合为{@code null}，返回一个不可变的默认空集合，否则返回原集合<br>
     * 空集合使用{@link Collections#emptyList()}
     *
     * @param <T>  集合元素类型
     * @param list 提供的集合，可能为null
     * @return 原集合，若为null返回空集合
     * @since 1.0.0
     */
    public static <T> List<T> emptyIfNull( List<T> list ) {
        return (null == list) ? Collections.emptyList() : list;
    }

    /**
     * 两个集合的并集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最多的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c, c]，此结果中只保留了三个c
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 并集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> union( Collection<T> coll1, Collection<T> coll2 ) {
        if (isEmpty(coll1)) {
            return new ArrayList<>(coll2);
        } else if (isEmpty(coll2)) {
            return new ArrayList<>(coll1);
        }

        final ArrayList<T> list = new ArrayList<>(Math.max(coll1.size(), coll2.size()));
        final Map<T, Integer> map1 = countMap(coll1);
        final Map<T, Integer> map2 = countMap(coll2);
        final Set<T> elts = newHashSet(coll2);
        elts.addAll(coll1);
        int m;
        for (T t : elts) {
            m = Math.max(Convert.toInt(map1.get(t), 0), Convert.toInt(map2.get(t), 0));
            for (int i = 0; i < m; i++) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 多个集合的并集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最多的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c, c]，此结果中只保留了三个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link ArrayList}
     */
    @SafeVarargs
    public static <T> Collection<T> union( Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls ) {
        Collection<T> union = union(coll1, coll2);
        for (Collection<T> coll : otherColls) {
            union = union(union, coll);
        }
        return union;
    }

    /**
     * 多个集合的非重复并集，类似于SQL中的“UNION DISTINCT”<br>
     * 针对一个集合中存在多个相同元素的情况，只保留一个<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c]，此结果中只保留了一个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link LinkedHashSet}
     */
    @SafeVarargs
    public static <T> Set<T> unionDistinct( Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls ) {
        final Set<T> result;
        if (isEmpty(coll1)) {
            result = new LinkedHashSet<>();
        } else {
            result = new LinkedHashSet<>(coll1);
        }

        if (isNotEmpty(coll2)) {
            result.addAll(coll2);
        }

        if (ArrayUtil.isNotEmpty(otherColls)) {
            for (Collection<T> otherColl : otherColls) {
                result.addAll(otherColl);
            }
        }
        return result;
    }

    /**
     * 多个集合的完全并集，类似于SQL中的“UNION ALL”<br>
     * 针对一个集合中存在多个相同元素的情况，保留全部元素<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c, c, a, b, c, c]
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 并集的集合，返回 {@link ArrayList}
     */
    @SafeVarargs
    public static <T> List<T> unionAll( Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls ) {
        final List<T> result;
        if (isEmpty(coll1)) {
            result = new ArrayList<>();
        } else {
            result = new ArrayList<>(coll1);
        }

        if (isNotEmpty(coll2)) {
            result.addAll(coll2);
        }

        if (ArrayUtil.isNotEmpty(otherColls)) {
            for (Collection<T> otherColl : otherColls) {
                result.addAll(otherColl);
            }
        }

        return result;
    }

    /**
     * 两个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最少的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c]，此结果中只保留了两个c
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 交集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> intersection( Collection<T> coll1, Collection<T> coll2 ) {
        if (isNotEmpty(coll1) && isNotEmpty(coll2)) {
            final ArrayList<T> list = new ArrayList<>(Math.min(coll1.size(), coll2.size()));
            final Map<T, Integer> map1 = countMap(coll1);
            final Map<T, Integer> map2 = countMap(coll2);
            final Set<T> elts = newHashSet(coll2);
            int m;
            for (T t : elts) {
                m = Math.min(Convert.toInt(map1.get(t), 0), Convert.toInt(map2.get(t), 0));
                for (int i = 0; i < m; i++) {
                    list.add(t);
                }
            }
            return list;
        }

        return new ArrayList<>();
    }

    /**
     * 多个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留最少的个数<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c, c]，此结果中只保留了两个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 交集的集合，返回 {@link ArrayList}
     */
    @SafeVarargs
    public static <T> Collection<T> intersection( Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls ) {
        Collection<T> intersection = intersection(coll1, coll2);
        if (isEmpty(intersection)) {
            return intersection;
        }
        for (Collection<T> coll : otherColls) {
            intersection = intersection(intersection, coll);
            if (isEmpty(intersection)) {
                return intersection;
            }
        }
        return intersection;
    }

    /**
     * 多个集合的交集<br>
     * 针对一个集合中存在多个相同元素的情况，只保留一个<br>
     * 例如：集合1：[a, b, c, c, c]，集合2：[a, b, c, c]<br>
     * 结果：[a, b, c]，此结果中只保留了一个c
     *
     * @param <T>        集合元素类型
     * @param coll1      集合1
     * @param coll2      集合2
     * @param otherColls 其它集合
     * @return 交集的集合，返回 {@link LinkedHashSet}
     * @since 1.0.0
     */
    @SafeVarargs
    public static <T> Set<T> intersectionDistinct( Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls ) {
        final Set<T> result;
        if (isEmpty(coll1) || isEmpty(coll2)) {
            // 有一个空集合就直接返回空
            return new LinkedHashSet<>();
        } else {
            result = new LinkedHashSet<>(coll1);
        }

        if (ArrayUtil.isNotEmpty(otherColls)) {
            for (Collection<T> otherColl : otherColls) {
                if (isNotEmpty(otherColl)) {
                    result.retainAll(otherColl);
                } else {
                    // 有一个空集合就直接返回空
                    return new LinkedHashSet<>();
                }
            }
        }

        result.retainAll(coll2);

        return result;
    }

    /**
     * 两个集合的差集<br>
     * 针对一个集合中存在多个相同元素的情况，计算两个集合中此元素的个数，保留两个集合中此元素个数差的个数<br>
     * 例如：
     *
     * <pre>
     *     disjunction([a, b, c, c, c], [a, b, c, c]) -》 [c]
     *     disjunction([a, b], [])                    -》 [a, b]
     *     disjunction([a, b, c], [b, c, d])          -》 [a, d]
     * </pre>
     * 任意一个集合为空，返回另一个集合<br>
     * 两个集合无差集则返回空集合
     *
     * @param <T>   集合元素类型
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 差集的集合，返回 {@link ArrayList}
     */
    public static <T> Collection<T> disjunction( Collection<T> coll1, Collection<T> coll2 ) {
        if (isEmpty(coll1)) {
            return coll2;
        }
        if (isEmpty(coll2)) {
            return coll1;
        }

        final List<T> result = new ArrayList<>();
        final Map<T, Integer> map1 = countMap(coll1);
        final Map<T, Integer> map2 = countMap(coll2);
        final Set<T> elts = newHashSet(coll2);
        elts.addAll(coll1);
        int m;
        for (T t : elts) {
            m = Math.abs(Convert.toInt(map1.get(t), 0) - Convert.toInt(map2.get(t), 0));
            for (int i = 0; i < m; i++) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 计算集合的单差集，即只返回【集合1】中有，但是【集合2】中没有的元素，例如：
     *
     * <pre>
     *     subtract([1,2,3,4],[2,3,4,5]) -》 [1]
     * </pre>
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @param <T>   元素类型
     * @return 单差集
     */
    public static <T> Collection<T> subtract( Collection<T> coll1, Collection<T> coll2 ) {
        Collection<T> result = ObjectUtil.clone(coll1);
        if (null == result) {
            result = CollUtil.create(coll1.getClass());
            result.addAll(coll1);
        }
        result.removeAll(coll2);
        return result;
    }

    /**
     * 计算集合的单差集，即只返回【集合1】中有，但是【集合2】中没有的元素，例如：
     *
     * <pre>
     *     subtractToList([1,2,3,4],[2,3,4,5]) -》 [1]
     * </pre>
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @param <T>   元素类型
     * @return 单差集
     * @since 1.0.0
     */
    public static <T> List<T> subtractToList( Collection<T> coll1, Collection<T> coll2 ) {

        if (isEmpty(coll1)) {
            return ListUtil.empty();
        }
        if (isEmpty(coll2)) {
            return ListUtil.list(true, coll1);
        }

        //将被交数用链表储存，防止因为频繁扩容影响性能
        final List<T> result = new LinkedList<>();
        Set<T> set = new HashSet<>(coll2);
        for (T t : coll1) {
            if (false == set.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 判断指定集合是否包含指定值，如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     * <p>
     * 异常这块 主要是 contains 方法抛出。其他同理。
     *
     * @param collection 集合
     * @param value      需要查找的值
     * @return 如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     * @throws ClassCastException 如果类型不一致会抛出转换异常
     * @throws NullPointerException 当指定的元素 值为 null ,或集合类不支持null 时抛出该异常
     * @see Collection#contains(Object)
     * @see CollUtil#safeContains(Collection, Object)
     * @since 1.0.0
     */
    public static boolean contains( Collection<?> collection, Object value ) {
        return isNotEmpty(collection) && collection.contains(value);
    }


    /**
     * 判断指定集合是否包含指定值，如果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     * @param collection 集合
     * @param value  需要查找的值
     * @return 果集合为空（null或者空），返回{@code false}，否则找到元素返回{@code true}
     */
    public static boolean safeContains(Collection<?> collection, Object value) {

        try {
            return contains(collection ,value);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * 自定义函数判断集合是否包含某类值
     *
     * @param collection  集合
     * @param containFunc 自定义判断函数
     * @param <T>         值类型
     * @return 是否包含自定义规则的值
     */
    public static <T> boolean contains( Collection<T> collection, Predicate containFunc ) {
        if (isEmpty(collection)) {
            return false;
        }
        for (T t : collection) {
            if (containFunc.test(t)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 其中一个集合在另一个集合中是否至少包含一个元素，即是两个集合是否至少有一个共同的元素
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 其中一个集合在另一个集合中是否至少包含一个元素
     * @see #intersection
     * @since 1.0.0
     */
    public static boolean containsAny( Collection<?> coll1, Collection<?> coll2 ) {
        if (isEmpty(coll1) || isEmpty(coll2)) {
            return false;
        }
        if (coll1.size() < coll2.size()) {
            for (Object object : coll1) {
                if (coll2.contains(object)) {
                    return true;
                }
            }
        } else {
            for (Object object : coll2) {
                if (coll1.contains(object)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 集合1中是否包含集合2中所有的元素，即集合2是否为集合1的子集
     *
     * @param coll1 集合1
     * @param coll2 集合2
     * @return 集合1中是否包含集合2中所有的元素
     * @since 1.0.0
     */
    public static boolean containsAll( Collection<?> coll1, Collection<?> coll2 ) {
        if (isEmpty(coll1)) {
            return isEmpty(coll2);
        }

        if (isEmpty(coll2)) {
            return true;
        }

        if (coll1.size() < coll2.size()) {
            return false;
        }

        for (Object object : coll2) {
            if (false == coll1.contains(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据集合返回一个元素计数的 {@link Map}<br>
     * 所谓元素计数就是假如这个集合中某个元素出现了n次，那将这个元素做为key，n做为value<br>
     * 例如：[a,b,c,c,c] 得到：<br>
     * a: 1<br>
     * b: 1<br>
     * c: 3<br>
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link Map}
     * @see IterUtil#countMap(Iterator)
     */
    public static <T> Map<T, Integer> countMap( Iterable<T> collection ) {
        return IterUtil.countMap(null == collection ? null : collection.iterator());
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @param func        集合元素转换器，将元素转换为字符串
     * @return 连接后的字符串
     * @see IterUtil#join(Iterator, CharSequence, Function)
     * @since 1.0.0
     */
    public static <T> String join( Iterable<T> iterable, CharSequence conjunction, Function<T, ? extends CharSequence> func ) {
        if (null == iterable) {
            return null;
        }
        return IterUtil.join(iterable.iterator(), conjunction, func);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @return 连接后的字符串
     * @see IterUtil#join(Iterator, CharSequence)
     */
    public static <T> String join( Iterable<T> iterable, CharSequence conjunction ) {
        if (null == iterable) {
            return null;
        }
        return IterUtil.join(iterable.iterator(), conjunction);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串
     *
     * @param <T>         集合元素类型
     * @param iterable    {@link Iterable}
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀，null表示不添加
     * @param suffix      每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     * @since 1.0.0
     */
    public static <T> String join( Iterable<T> iterable, CharSequence conjunction, String prefix, String suffix ) {
        if (null == iterable) {
            return null;
        }
        return IterUtil.join(iterable.iterator(), conjunction, prefix, suffix);
    }

    /**
     * 以 conjunction 为分隔符将集合转换为字符串<br>
     * 如果集合元素为数组、{@link Iterable}或{@link Iterator}，则递归组合其为字符串
     *
     * @param <T>         集合元素类型
     * @param iterator    集合
     * @param conjunction 分隔符
     * @return 连接后的字符串
     * @deprecated 请使用IterUtil#join(Iterator, CharSequence)
     */
    @Deprecated
    public static <T> String join( Iterator<T> iterator, CharSequence conjunction ) {
        return IterUtil.join(iterator, conjunction);
    }

    /**
     * 切取部分数据<br>
     * 切取后的栈将减少这些元素
     *
     * @param <T>             集合元素类型
     * @param surplusAlaDatas 原数据
     * @param partSize        每部分数据的长度
     * @return 切取出的数据或null
     */
    public static <T> List<T> popPart( Stack<T> surplusAlaDatas, int partSize ) {
        if (isEmpty(surplusAlaDatas)) {
            return ListUtil.empty();
        }

        final List<T> currentAlaDatas = new ArrayList<>();
        int size = surplusAlaDatas.size();
        // 切割
        if (size > partSize) {
            for (int i = 0; i < partSize; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        } else {
            for (int i = 0; i < size; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        }
        return currentAlaDatas;
    }

    // ----------------------------------------------------------------------------------------------- new HashSet

    /**
     * 切取部分数据<br>
     * 切取后的栈将减少这些元素
     *
     * @param <T>             集合元素类型
     * @param surplusAlaDatas 原数据
     * @param partSize        每部分数据的长度
     * @return 切取出的数据或null
     */
    public static <T> List<T> popPart( Deque<T> surplusAlaDatas, int partSize ) {
        if (isEmpty(surplusAlaDatas)) {
            return ListUtil.empty();
        }

        final List<T> currentAlaDatas = new ArrayList<>();
        int size = surplusAlaDatas.size();
        // 切割
        if (size > partSize) {
            for (int i = 0; i < partSize; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        } else {
            for (int i = 0; i < size; i++) {
                currentAlaDatas.add(surplusAlaDatas.pop());
            }
        }
        return currentAlaDatas;
    }

    /**
     * 新建一个HashSet
     *
     * @param <T> 集合元素类型
     * @param ts  元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> newHashSet( T... ts ) {
        return set(false, ts);
    }

    /**
     * 新建一个LinkedHashSet
     *
     * @param <T> 集合元素类型
     * @param ts  元素数组
     * @return HashSet对象
     * @since 1.0.0
     */
    @SafeVarargs
    public static <T> LinkedHashSet<T> newLinkedHashSet( T... ts ) {
        return (LinkedHashSet<T>) set(true, ts);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>      集合元素类型
     * @param isSorted 是否有序，有序返回 {@link LinkedHashSet}，否则返回 {@link HashSet}
     * @param ts       元素数组
     * @return HashSet对象
     */
    @SafeVarargs
    public static <T> HashSet<T> set( boolean isSorted, T... ts ) {
        if (null == ts) {
            return isSorted ? new LinkedHashSet<>() : new HashSet<>();
        }
        int initialCapacity = Math.max((int) (ts.length / .75f) + 1, 16);
        final HashSet<T> set = isSorted ? new LinkedHashSet<>(initialCapacity) : new HashSet<>(initialCapacity);
        Collections.addAll(set, ts);
        return set;
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return HashSet对象
     */
    public static <T> HashSet<T> newHashSet( Collection<T> collection ) {
        return newHashSet(false, collection);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>        集合元素类型
     * @param isSorted   是否有序，有序返回 {@link LinkedHashSet}，否则返回{@link HashSet}
     * @param collection 集合，用于初始化Set
     * @return HashSet对象
     */
    public static <T> HashSet<T> newHashSet( boolean isSorted, Collection<T> collection ) {
        return isSorted ? new LinkedHashSet<>(collection) : new HashSet<>(collection);
    }

    /**
     * 新建一个HashSet
     *
     * @param <T>      集合元素类型
     * @param isSorted 是否有序，有序返回 {@link LinkedHashSet}，否则返回{@link HashSet}
     * @param iter     {@link Iterator}
     * @return HashSet对象
     * @since 1.0.0
     */
    public static <T> HashSet<T> newHashSet( boolean isSorted, Iterator<T> iter ) {
        if (null == iter) {
            return set(isSorted, (T[]) null);
        }
        final HashSet<T> set = isSorted ? new LinkedHashSet<>() : new HashSet<>();
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        return set;
    }

    // ----------------------------------------------------------------------------------------------- List

    /**
     * 新建一个HashSet
     *
     * @param <T>         集合元素类型
     * @param isSorted    是否有序，有序返回 {@link LinkedHashSet}，否则返回{@link HashSet}
     * @param enumeration {@link Enumeration}
     * @return HashSet对象
     * @since 1.0.0
     */
    public static <T> HashSet<T> newHashSet( boolean isSorted, Enumeration<T> enumeration ) {
        if (null == enumeration) {
            return set(isSorted, (T[]) null);
        }
        final HashSet<T> set = isSorted ? new LinkedHashSet<>() : new HashSet<>();
        while (enumeration.hasMoreElements()) {
            set.add(enumeration.nextElement());
        }
        return set;
    }

    /**
     * 新建一个空List
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @return List对象
     * @since 1.0.0
     */
    public static <T> List<T> list( boolean isLinked ) {
        return ListUtil.list(isLinked);
    }

    /**
     * 新建一个List
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param values   数组
     * @return List对象
     * @since 1.0.0
     */
    @SafeVarargs
    public static <T> List<T> list( boolean isLinked, T... values ) {
        return ListUtil.list(isLinked, values);
    }

    /**
     * 新建一个List
     *
     * @param <T>        集合元素类型
     * @param isLinked   是否新建LinkedList
     * @param collection 集合
     * @return List对象
     * @since 1.0.0
     */
    public static <T> List<T> list( boolean isLinked, Collection<T> collection ) {
        return ListUtil.list(isLinked, collection);
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param iterable {@link Iterable}
     * @return List对象
     * @since 1.0.0
     */
    public static <T> List<T> list( boolean isLinked, Iterable<T> iterable ) {
        return ListUtil.list(isLinked, iterable);
    }

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param isLinked 是否新建LinkedList
     * @param iter     {@link Iterator}
     * @return ArrayList对象
     * @since 1.0.0
     */
    public static <T> List<T> list( boolean isLinked, Iterator<T> iter ) {
        return ListUtil.list(isLinked, iter);
    }

    /**
     * 新建一个List<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>         集合元素类型
     * @param isLinked    是否新建LinkedList
     * @param enumeration {@link Enumeration}
     * @return ArrayList对象
     * @since 1.0.0
     */
    public static <T> List<T> list( boolean isLinked, Enumeration<T> enumeration ) {
        return ListUtil.list(isLinked, enumeration);
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>    集合元素类型
     * @param values 数组
     * @return ArrayList对象
     * @see #toList(Object[])
     */
    @SafeVarargs
    public static <T> ArrayList<T> newArrayList( T... values ) {
        return ListUtil.toList(values);
    }

    /**
     * 数组转为ArrayList
     *
     * @param <T>    集合元素类型
     * @param values 数组
     * @return ArrayList对象
     * @since 1.0.0
     */
    @SafeVarargs
    public static <T> ArrayList<T> toList( T... values ) {
        return ListUtil.toList(values);
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return ArrayList对象
     */
    public static <T> ArrayList<T> newArrayList( Collection<T> collection ) {
        return ListUtil.toList(collection);
    }

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return ArrayList对象
     * @since 1.0.0
     */
    public static <T> ArrayList<T> newArrayList( Iterable<T> iterable ) {
        return ListUtil.toList(iterable);
    }

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return ArrayList对象
     * @since 1.0.0
     */
    public static <T> ArrayList<T> newArrayList( Iterator<T> iterator ) {
        return ListUtil.toList(iterator);
    }

    // ----------------------------------------------------------------------new LinkedList

    /**
     * 新建一个ArrayList<br>
     * 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>         集合元素类型
     * @param enumeration {@link Enumeration}
     * @return ArrayList对象
     * @since 1.0.0
     */
    public static <T> ArrayList<T> newArrayList( Enumeration<T> enumeration ) {
        return ListUtil.toList(enumeration);
    }

    /**
     * 新建LinkedList
     *
     * @param values 数组
     * @param <T>    类型
     * @return LinkedList
     * @since 1.0.0
     */
    @SafeVarargs
    public static <T> LinkedList<T> newLinkedList( T... values ) {
        return ListUtil.toLinkedList(values);
    }

    /**
     * 新建一个CopyOnWriteArrayList
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link CopyOnWriteArrayList}
     */
    public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList( Collection<T> collection ) {
        return ListUtil.toCopyOnWriteArrayList(collection);
    }

    /**
     * 新建{@link BlockingQueue}<br>
     * 在队列为空时，获取元素的线程会等待队列变为非空。当队列满时，存储元素的线程会等待队列可用。
     *
     * @param <T>      集合类型
     * @param capacity 容量
     * @param isLinked 是否为链表形式
     * @return {@link BlockingQueue}
     * @since 1.0.0
     */
    public static <T> BlockingQueue<T> newBlockingQueue( int capacity, boolean isLinked ) {
        BlockingQueue<T> queue;
        if (isLinked) {
            queue = new LinkedBlockingDeque<>(capacity);
        } else {
            queue = new ArrayBlockingQueue<>(capacity);
        }
        return queue;
    }

    /**
     * 创建新的集合对象
     *
     * @param <T>            集合类型
     * @param collectionType 集合类型
     * @return 集合类型对应的实例
     * @since 1.0.0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Collection<T> create( Class<?> collectionType ) {
        Collection<T> list;
        if (collectionType.isAssignableFrom(AbstractCollection.class)) {
            // 抽象集合默认使用ArrayList
            list = new ArrayList<>();
        }

        // Set
        else if (collectionType.isAssignableFrom(HashSet.class)) {
            list = new HashSet<>();
        } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
            list = new LinkedHashSet<>();
        } else if (collectionType.isAssignableFrom(TreeSet.class)) {
            list = new TreeSet<>(( o1, o2 ) -> {
                // 优先按照对象本身比较，如果没有实现比较接口，默认按照toString内容比较
                if (o1 instanceof Comparable) {
                    return ((Comparable<T>) o1).compareTo(o2);
                }
                return CompareUtil.compare(o1.toString(), o2.toString());
            });
        } else if (collectionType.isAssignableFrom(EnumSet.class)) {
            list = (Collection<T>) EnumSet.noneOf((Class<Enum>) ClassUtil.getTypeArgument(collectionType));
        }

        // List
        else if (collectionType.isAssignableFrom(ArrayList.class)) {
            list = new ArrayList<>();
        } else if (collectionType.isAssignableFrom(LinkedList.class)) {
            list = new LinkedList<>();
        }

        // Others，直接实例化
        else {
            try {
                list = (Collection<T>) ReflectUtil.newInstance(collectionType);
            } catch (Exception e) {
                // 无法创建当前类型的对象，尝试创建父类型对象
                final Class<?> superclass = collectionType.getSuperclass();
                if (null != superclass && collectionType != superclass) {
                    return create(superclass);
                }
                throw new UtilException(e);
            }
        }
        return list;
    }

    /**
     * 去重集合
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link ArrayList}
     */
    public static <T> ArrayList<T> distinct( Collection<T> collection ) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        } else if (collection instanceof Set) {
            return new ArrayList<>(collection);
        } else {
            return new ArrayList<>(new LinkedHashSet<>(collection));
        }
    }

    /**
     * 截取列表的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     * @see ListUtil#sub(List, int, int)
     */
    public static <T> List<T> sub( List<T> list, int start, int end ) {
        return ListUtil.sub(list, start, end);
    }

    /**
     * 截取列表的部分
     *
     * @param <T>   集合元素类型
     * @param list  被截取的数组
     * @param start 开始位置（包含）
     * @param end   结束位置（不包含）
     * @param step  步进
     * @return 截取后的数组，当开始位置超过最大时，返回空的List
     * @see ListUtil#sub(List, int, int, int)
     * @since 1.0.0
     */
    public static <T> List<T> sub( List<T> list, int start, int end, int step ) {
        return ListUtil.sub(list, start, end, step);
    }

    /**
     * 截取集合的部分
     *
     * @param <T>        集合元素类型
     * @param collection 被截取的数组
     * @param start      开始位置（包含）
     * @param end        结束位置（不包含）
     * @return 截取后的数组，当开始位置超过最大时，返回null
     */
    public static <T> List<T> sub( Collection<T> collection, int start, int end ) {
        return sub(collection, start, end, 1);
    }

    /**
     * 截取集合的部分
     *
     * @param <T>        集合元素类型
     * @param collection 被截取的数组
     * @param start      开始位置（包含）
     * @param end        结束位置（不包含）
     * @param step       步进
     * @return 截取后的数组，当开始位置超过最大时，返回空集合
     * @since 1.0.0
     */
    public static <T> List<T> sub( Collection<T> collection, int start, int end, int step ) {
        if (isEmpty(collection)) {
            return ListUtil.empty();
        }

        final List<T> list = collection instanceof List ? (List<T>) collection : ListUtil.toList(collection);
        return sub(list, start, end, step);
    }

    /**
     * 对集合按照指定长度分段，每一个段为单独的集合，返回这个集合的列表
     * <p>
     * 需要特别注意的是，此方法调用{@link List#subList(int, int)}切分List，
     * 此方法返回的是原List的视图，也就是说原List有变更，切分后的结果也会变更。
     * </p>
     *
     * @param <T>  集合元素类型
     * @param list 列表
     * @param size 每个段的长度
     * @return 分段列表
     * @since 1.0.0
     * @deprecated 请使用 {@link ListUtil#partition(List, int)}
     */
    @Deprecated
    public static <T> List<List<T>> splitList( List<T> list, int size ) {
        return ListUtil.partition(list, size);
    }

    /**
     * 对集合按照指定长度分段，每一个段为单独的集合，返回这个集合的列表
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param size       每个段的长度
     * @return 分段列表
     */
    public static <T> List<List<T>> split( Collection<T> collection, int size ) {
        final List<List<T>> result = new ArrayList<>();
        if (CollUtil.isEmpty(collection)) {
            return result;
        }

        ArrayList<T> subList = new ArrayList<>(size);
        for (T t : collection) {
            if (subList.size() >= size) {
                result.add(subList);
                subList = new ArrayList<>(size);
            }
            subList.add(t);
        }
        result.add(subList);
        return result;
    }

    /**
     * 编辑，此方法产生一个新集合<br>
     * 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回null表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param editor     编辑器接口，{@code null}返回原集合
     * @return 过滤后的集合
     */
    public static <T> Collection<T> edit( Collection<T> collection, Editor<T> editor ) {
        if (null == collection || null == editor) {
            return collection;
        }

        Collection<T> collection2 = ObjectUtil.clone(collection);
        if (null == collection2) {
            // 不支持clone
            collection2 = create(collection.getClass());
        }
        if (isEmpty(collection2)) {
            return collection2;
        }
        try {
            collection2.clear();
        } catch (UnsupportedOperationException e) {
            // 克隆后的对象不支持清空，说明为不可变集合对象，使用默认的ArrayList保存结果
            collection2 = new ArrayList<>();
        }

        T modified;
        for (T t : collection) {
            modified = editor.edit(t);
            if (null != modified) {
                collection2.add(modified);
            }
        }
        return collection2;
    }

    /**
     * 过滤<br>
     * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Predicate#apply(Object)}方法返回true的对象将被加入结果集合中
     * </pre>
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param predicate  过滤器，{@code null}返回原集合
     * @return 过滤后的数组
     * @since 1.0.0
     */
    public static <T> Collection<T> filterNew( Collection<T> collection, Predicate<T> predicate ) {
        if (null == collection || null == predicate) {
            return collection;
        }
        return edit(collection, t -> predicate.apply(t) ? t : null);
    }

    /**
     * 去掉集合中的多个元素，此方法直接修改原集合
     *
     * @param <T>         集合类型
     * @param <E>         集合元素类型
     * @param collection  集合
     * @param elesRemoved 被去掉的元素数组
     * @return 原集合
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <T extends Collection<E>, E> T removeAny( T collection, E... elesRemoved ) {
        collection.removeAll(newHashSet(elesRemoved));
        return collection;
    }

    /**
     * 去除指定元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @param predicate  过滤器
     * @return 处理后的集合
     * @since 1.0.0
     */
    public static <T extends Collection<E>, E> T filter( T collection, final Predicate<E> predicate ) {
        return IterUtil.filter(collection, predicate);
    }

    /**
     * 去除{@code null} 元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @return 处理后的集合
     * @since 1.0.0
     */
    public static <T extends Collection<E>, E> T removeNull( T collection ) {
        return filter(collection, Objects::nonNull);
    }

    /**
     * 去除{@code null}或者"" 元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @return 处理后的集合
     * @since 1.0.0
     */
    public static <T extends Collection<E>, E extends CharSequence> T removeEmpty( T collection ) {
        return filter(collection, StrUtil::isNotEmpty);
    }

    /**
     * 去除{@code null}或者""或者空白字符串 元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @return 处理后的集合
     * @since 1.0.0
     */
    public static <T extends Collection<E>, E extends CharSequence> T removeBlank( T collection ) {
        return filter(collection, StrUtil::isNotBlank);
    }

    /**
     * 通过Editor抽取集合元素中的某些值返回为新列表<br>
     * 例如提供的是一个Bean列表，通过Editor接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param collection 原集合
     * @param editor     编辑器
     * @return 抽取后的新列表
     */
    public static List<Object> extract( Iterable<?> collection, Editor<Object> editor ) {
        return extract(collection, editor, false);
    }

    /**
     * 通过Editor抽取集合元素中的某些值返回为新列表<br>
     * 例如提供的是一个Bean列表，通过Editor接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param collection 原集合
     * @param editor     编辑器
     * @param ignoreNull 是否忽略空值
     * @return 抽取后的新列表
     * @see #map(Iterable, Function, boolean)
     * @since 1.0.0
     */
    public static List<Object> extract( Iterable<?> collection, Editor<Object> editor, boolean ignoreNull ) {
        return map(collection, editor::edit, ignoreNull);
    }

    /**
     * 通过func自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的列表返回<br>
     * 例如提供的是一个Bean列表，通过Function接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @param ignoreNull 是否忽略空值，这里的空值包括函数处理前和处理后的null值
     * @return 抽取后的新列表
     * @since 1.0.0
     */
    public static <T, R> List<R> map( Iterable<T> collection, Function<? super T, ? extends R> func, boolean ignoreNull ) {
        final List<R> fieldValueList = new ArrayList<>();
        if (null == collection) {
            return fieldValueList;
        }

        R value;
        for (T t : collection) {
            if (null == t && ignoreNull) {
                continue;
            }
            value = func.apply(t);
            if (null == value && ignoreNull) {
                continue;
            }
            fieldValueList.add(value);
        }
        return fieldValueList;
    }

    /**
     * 获取给定Bean列表中指定字段名对应字段值的列表<br>
     * 列表元素支持Bean与Map
     *
     * @param collection Bean集合或Map集合
     * @param fieldName  字段名或map的键
     * @return 字段值列表
     * @since 1.0.0
     */
    public static List<Object> getFieldValues( Iterable<?> collection, final String fieldName ) {
        return getFieldValues(collection, fieldName, false);
    }

    /**
     * 获取给定Bean列表中指定字段名对应字段值的列表<br>
     * 列表元素支持Bean与Map
     *
     * @param collection Bean集合或Map集合
     * @param fieldName  字段名或map的键
     * @param ignoreNull 是否忽略值为{@code null}的字段
     * @return 字段值列表
     * @since 1.0.0
     */
    public static List<Object> getFieldValues( Iterable<?> collection, final String fieldName, boolean ignoreNull ) {
        return map(collection, bean -> {
            if (bean instanceof Map) {
                return ((Map<?, ?>) bean).get(fieldName);
            } else {
                return ReflectUtil.getFieldValue(bean, fieldName);
            }
        }, ignoreNull);
    }

    /**
     * 获取给定Bean列表中指定字段名对应字段值的列表<br>
     * 列表元素支持Bean与Map
     *
     * @param <T>         元素类型
     * @param collection  Bean集合或Map集合
     * @param fieldName   字段名或map的键
     * @param elementType 元素类型类
     * @return 字段值列表
     * @since 1.0.0
     */
    public static <T> List<T> getFieldValues( Iterable<?> collection, final String fieldName, final Class<T> elementType ) {
        List<Object> fieldValues = getFieldValues(collection, fieldName);
        return Convert.toList(elementType, fieldValues);
    }

    /**
     * 字段值与列表值对应的Map，常用于元素对象中有唯一ID时需要按照这个ID查找对象的情况<br>
     * 例如：车牌号 =》车
     *
     * @param <K>       字段名对应值得类型，不确定请使用Object
     * @param <V>       对象类型
     * @param iterable  对象列表
     * @param fieldName 字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> fieldValueMap( Iterable<V> iterable, String fieldName ) {
        return IterUtil.fieldValueMap(IterUtil.getIter(iterable), fieldName);
    }

    /**
     * 两个字段值组成新的Map
     *
     * @param <K>               字段名对应值得类型，不确定请使用Object
     * @param <V>               值类型，不确定使用Object
     * @param iterable          对象列表
     * @param fieldNameForKey   做为键的字段名（会通过反射获取其值）
     * @param fieldNameForValue 做为值的字段名（会通过反射获取其值）
     * @return 某个字段值与对象对应Map
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> fieldValueAsMap( Iterable<?> iterable, String fieldNameForKey, String fieldNameForValue ) {
        return IterUtil.fieldValueAsMap(IterUtil.getIter(iterable), fieldNameForKey, fieldNameForValue);
    }

    /**
     * 查找第一个匹配元素对象
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param predicate  过滤器，满足过滤条件的第一个元素将被返回
     * @return 满足过滤条件的第一个元素
     * @since 1.0.0
     */
    public static <T> T findOne( Iterable<T> collection, Predicate<T> predicate ) {
        if (null != collection) {
            for (T t : collection) {
                if (predicate.apply(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    /**
     * 查找第一个匹配元素对象<br>
     * 如果集合元素是Map，则比对键和值是否相同，相同则返回<br>
     * 如果为普通Bean，则通过反射比对元素字段名对应的字段值是否相同，相同则返回<br>
     * 如果给定字段值参数是{@code null} 且元素对象中的字段值也为{@code null}则认为相同
     *
     * @param <T>        集合元素类型
     * @param collection 集合，集合元素可以是Bean或者Map
     * @param fieldName  集合元素对象的字段名或map的键
     * @param fieldValue 集合元素对象的字段值或map的值
     * @return 满足条件的第一个元素
     * @since 1.0.0
     */
    public static <T> T findOneByField( Iterable<T> collection, final String fieldName, final Object fieldValue ) {
        return findOne(collection, t -> {
            if (t instanceof Map) {
                final Map<?, ?> map = (Map<?, ?>) t;
                final Object value = map.get(fieldName);
                return ObjectUtil.equal(value, fieldValue);
            }

            // 普通Bean
            final Object value = ReflectUtil.getFieldValue(t, fieldName);
            return ObjectUtil.equal(value, fieldValue);
        });
    }

    /**
     * 集合中匹配规则的数量
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @param matcher  匹配器，为空则全部匹配
     * @return 匹配数量
     */
    public static <T> int count( Iterable<T> iterable, Matcher<T> matcher ) {
        int count = 0;
        if (null != iterable) {
            for (T t : iterable) {
                if (null == matcher || matcher.match(t)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 获取匹配规则定义中匹配到元素的第一个位置<br>
     * 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param matcher    匹配器，为空则全部匹配
     * @return 第一个位置
     * @since 1.0.0
     */
    public static <T> int indexOf( Collection<T> collection, Matcher<T> matcher ) {
        if (isNotEmpty(collection)) {
            int index = 0;
            for (T t : collection) {
                if (null == matcher || matcher.match(t)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    /**
     * 获取匹配规则定义中匹配到元素的最后位置<br>
     * 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param matcher    匹配器，为空则全部匹配
     * @return 最后一个位置
     * @since 1.0.0
     */
    public static <T> int lastIndexOf( Collection<T> collection, Matcher<T> matcher ) {
        if (collection instanceof List) {
            // List的查找最后一个有优化算法
            return ListUtil.lastIndexOf((List<T>) collection, matcher);
        }
        int matchIndex = -1;
        if (isNotEmpty(collection)) {
            int index = collection.size();
            for (T t : collection) {
                if (null == matcher || matcher.match(t)) {
                    matchIndex = index;
                }
                index--;
            }
        }
        return matchIndex;
    }

    // ---------------------------------------------------------------------- isEmpty

    /**
     * 获取匹配规则定义中匹配到元素的所有位置<br>
     * 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param matcher    匹配器，为空则全部匹配
     * @return 位置数组
     * @since 1.0.0
     */
    public static <T> int[] indexOfAll( Collection<T> collection, Matcher<T> matcher ) {
        final List<Integer> indexList = new ArrayList<>();
        if (null != collection) {
            int index = 0;
            for (T t : collection) {
                if (null == matcher || matcher.match(t)) {
                    indexList.add(index);
                }
                index++;
            }
        }
        return Convert.convert(int[].class, indexList);
    }

    /**
     * 集合是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty( Collection<?> collection ) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 如果给定集合为空，返回默认集合
     *
     * @param <T>               集合类型
     * @param <E>               集合元素类型
     * @param collection        集合
     * @param defaultCollection 默认数组
     * @return 非空（empty）的原集合或默认集合
     * @since 1.0.0
     */
    public static <T extends Collection<E>, E> T defaultIfEmpty( T collection, T defaultCollection ) {
        return isEmpty(collection) ? defaultCollection : collection;
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     * @see IterUtil#isEmpty(Iterable)
     */
    public static boolean isEmpty( Iterable<?> iterable ) {
        return IterUtil.isEmpty(iterable);
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     * @see IterUtil#isEmpty(Iterator)
     */
    public static boolean isEmpty( Iterator<?> Iterator ) {
        return IterUtil.isEmpty(Iterator);
    }

    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isEmpty( Enumeration<?> enumeration ) {
        return null == enumeration || false == enumeration.hasMoreElements();
    }

    // ---------------------------------------------------------------------- isNotEmpty

    /**
     * Map是否为空
     *
     * @param map 集合
     * @return 是否为空
     * @see MapUtil#isEmpty(Map)
     * @since 1.0.0
     */
    public static boolean isEmpty( Map<?, ?> map ) {
        return MapUtil.isEmpty(map);
    }

    /**
     * 集合是否为非空
     *
     * @param collection 集合
     * @return 是否为非空
     */
    public static boolean isNotEmpty( Collection<?> collection ) {
        return false == isEmpty(collection);
    }

    /**
     * Iterable是否为空
     *
     * @param iterable Iterable对象
     * @return 是否为空
     * @see IterUtil#isNotEmpty(Iterable)
     */
    public static boolean isNotEmpty( Iterable<?> iterable ) {
        return IterUtil.isNotEmpty(iterable);
    }

    /**
     * Iterator是否为空
     *
     * @param Iterator Iterator对象
     * @return 是否为空
     * @see IterUtil#isNotEmpty(Iterator)
     */
    public static boolean isNotEmpty( Iterator<?> Iterator ) {
        return IterUtil.isNotEmpty(Iterator);
    }

    /**
     * Enumeration是否为空
     *
     * @param enumeration {@link Enumeration}
     * @return 是否为空
     */
    public static boolean isNotEmpty( Enumeration<?> enumeration ) {
        return null != enumeration && enumeration.hasMoreElements();
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param iterable 被检查的Iterable对象，如果为{@code null} 返回true
     * @return 是否包含{@code null}元素
     * @see IterUtil#hasNull(Iterable)
     * @since 1.0.0
     */
    public static boolean hasNull( Iterable<?> iterable ) {
        return IterUtil.hasNull(iterable);
    }

    // ---------------------------------------------------------------------- zip

    /**
     * Map是否为非空
     *
     * @param map 集合
     * @return 是否为非空
     * @see MapUtil#isNotEmpty(Map)
     * @since 1.0.0
     */
    public static boolean isNotEmpty( Map<?, ?> map ) {
        return MapUtil.isNotEmpty(map);
    }

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = a,b,c,d<br>
     * values = 1,2,3,4<br>
     * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param keys      键列表
     * @param values    值列表
     * @param delimiter 分隔符
     * @param isOrder   是否有序
     * @return Map
     * @since 1.0.0
     */
    public static Map<String, String> zip( String keys, String values, String delimiter, boolean isOrder ) {
        return ArrayUtil.zip(StrUtil.splitToArray(keys, delimiter), StrUtil.splitToArray(values, delimiter), isOrder);
    }

    /**
     * 映射键值（参考Python的zip()函数），返回Map无序<br>
     * 例如：<br>
     * keys = a,b,c,d<br>
     * values = 1,2,3,4<br>
     * delimiter = , 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param keys      键列表
     * @param values    值列表
     * @param delimiter 分隔符
     * @return Map
     */
    public static Map<String, String> zip( String keys, String values, String delimiter ) {
        return zip(keys, values, delimiter, false);
    }

    /**
     * 映射键值（参考Python的zip()函数）<br>
     * 例如：<br>
     * keys = [a,b,c,d]<br>
     * values = [1,2,3,4]<br>
     * 则得到的Map是 {a=1, b=2, c=3, d=4}<br>
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K>    键类型
     * @param <V>    值类型
     * @param keys   键列表
     * @param values 值列表
     * @return Map
     */
    public static <K, V> Map<K, V> zip( Collection<K> keys, Collection<V> values ) {
        if (isEmpty(keys) || isEmpty(values)) {
            return MapUtil.empty();
        }

        int entryCount = Math.min(keys.size(), values.size());
        final Map<K, V> map = MapUtil.newHashMap(entryCount);

        final Iterator<K> keyIterator = keys.iterator();
        final Iterator<V> valueIterator = values.iterator();
        while (entryCount > 0) {
            map.put(keyIterator.next(), valueIterator.next());
            entryCount--;
        }

        return map;
    }

    /**
     * 将Entry集合转换为HashMap
     *
     * @param <K>       键类型
     * @param <V>       值类型
     * @param entryIter entry集合
     * @return Map
     * @see IterUtil#toMap(Iterable)
     */
    public static <K, V> HashMap<K, V> toMap( Iterable<Entry<K, V>> entryIter ) {
        return IterUtil.toMap(entryIter);
    }

    /**
     * 将数组转换为Map（HashMap），支持数组元素类型为：
     *
     * <pre>
     * Map.Entry
     * 长度大于1的数组（取前两个值），如果不满足跳过此元素
     * Iterable 长度也必须大于1（取前两个值），如果不满足跳过此元素
     * Iterator 长度也必须大于1（取前两个值），如果不满足跳过此元素
     * </pre>
     *
     * <pre>
     * Map&lt;Object, Object&gt; colorMap = CollectionUtil.toMap(new String[][] {{
     *     {"RED", "#FF0000"},
     *     {"GREEN", "#00FF00"},
     *     {"BLUE", "#0000FF"}});
     * </pre>
     * <p>
     * 参考：icefrogs-lang
     *
     * @param array 数组。元素类型为Map.Entry、数组、Iterable、Iterator
     * @return {@link HashMap}
     * @see MapUtil#of(Object[])
     * @since 1.0.0
     */
    public static HashMap<Object, Object> toMap( Object[] array ) {
        return MapUtil.of(array);
    }

    /**
     * 将集合转换为排序后的TreeSet
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param comparator 比较器
     * @return treeSet
     */
    public static <T> TreeSet<T> toTreeSet( Collection<T> collection, Comparator<T> comparator ) {
        final TreeSet<T> treeSet = new TreeSet<>(comparator);
        treeSet.addAll(collection);
        return treeSet;
    }

    /**
     * Iterator转换为Enumeration
     * <p>
     * Adapt the specified {@link Iterator} to the {@link Enumeration} interface.
     *
     * @param <E>  集合元素类型
     * @param iter {@link Iterator}
     * @return {@link Enumeration}
     */
    public static <E> Enumeration<E> asEnumeration( Iterator<E> iter ) {
        return new IteratorEnumeration<>(iter);
    }

    /**
     * Enumeration转换为Iterator
     * <p>
     * Adapt the specified {@code Enumeration} to the {@code Iterator} interface
     *
     * @param <E> 集合元素类型
     * @param e   {@link Enumeration}
     * @return {@link Iterator}
     * @see IterUtil#asIterator(Enumeration)
     */
    public static <E> Iterator<E> asIterator( Enumeration<E> e ) {
        return IterUtil.asIterator(e);
    }

    /**
     * {@link Iterator} 转为 {@link Iterable}
     *
     * @param <E>  元素类型
     * @param iter {@link Iterator}
     * @return {@link Iterable}
     * @see IterUtil#asIterable(Iterator)
     */
    public static <E> Iterable<E> asIterable( final Iterator<E> iter ) {
        return IterUtil.asIterable(iter);
    }

    /**
     * {@link Iterable}转为{@link Collection}<br>
     * 首先尝试强转，强转失败则构建一个新的{@link ArrayList}
     *
     * @param <E>      集合元素类型
     * @param iterable {@link Iterable}
     * @return {@link Collection} 或者 {@link ArrayList}
     * @since 1.0.0
     */
    public static <E> Collection<E> toCollection( Iterable<E> iterable ) {
        return (iterable instanceof Collection) ? (Collection<E>) iterable : newArrayList(iterable.iterator());
    }

    /**
     * 行转列，合并相同的键，值合并为列表<br>
     * 将Map列表中相同key的值组成列表做为Map的value<br>
     * 是{@link #toMapList(Map)}的逆方法<br>
     * 比如传入数据：
     *
     * <pre>
     * [
     *  {a: 1, b: 1, c: 1}
     *  {a: 2, b: 2}
     *  {a: 3, b: 3}
     *  {a: 4}
     * ]
     * </pre>
     * <p>
     * 结果是：
     *
     * <pre>
     * {
     *   a: [1,2,3,4]
     *   b: [1,2,3,]
     *   c: [1]
     * }
     * </pre>
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param mapList Map列表
     * @return Map
     * @see MapUtil#toListMap(Iterable)
     */
    public static <K, V> Map<K, List<V>> toListMap( Iterable<? extends Map<K, V>> mapList ) {
        return MapUtil.toListMap(mapList);
    }

    /**
     * 列转行。将Map中值列表分别按照其位置与key组成新的map。<br>
     * 是{@link #toListMap(Iterable)}的逆方法<br>
     * 比如传入数据：
     *
     * <pre>
     * {
     *   a: [1,2,3,4]
     *   b: [1,2,3,]
     *   c: [1]
     * }
     * </pre>
     * <p>
     * 结果是：
     *
     * <pre>
     * [
     *  {a: 1, b: 1, c: 1}
     *  {a: 2, b: 2}
     *  {a: 3, b: 3}
     *  {a: 4}
     * ]
     * </pre>
     *
     * @param <K>     键类型
     * @param <V>     值类型
     * @param listMap 列表Map
     * @return Map列表
     * @see MapUtil#toMapList(Map)
     */
    public static <K, V> List<Map<K, V>> toMapList( Map<K, ? extends Iterable<V>> listMap ) {
        return MapUtil.toMapList(listMap);
    }

    /**
     * 集合转换为Map，转换规则为：<br>
     * 按照keyFunc函数规则根据元素对象生成Key，元素作为值
     *
     * @param <K>     Map键类型
     * @param <V>     Map值类型
     * @param values  数据列表
     * @param map     Map对象，转换后的键值对加入此Map，通过传入此对象自定义Map类型
     * @param keyFunc 生成key的函数
     * @return 生成的map
     * @since 1.0.0
     */
    public static <K, V> Map<K, V> toMap( Iterable<V> values, Map<K, V> map, Func1<V, K> keyFunc ) {
        return IterUtil.toMap(null == values ? null : values.iterator(), map, keyFunc);
    }

    /**
     * 集合转换为Map，转换规则为：<br>
     * 按照keyFunc函数规则根据元素对象生成Key，按照valueFunc函数规则根据元素对象生成value组成新的Map
     *
     * @param <K>       Map键类型
     * @param <V>       Map值类型
     * @param <E>       元素类型
     * @param values    数据列表
     * @param map       Map对象，转换后的键值对加入此Map，通过传入此对象自定义Map类型
     * @param keyFunc   生成key的函数
     * @param valueFunc 生成值的策略函数
     * @return 生成的map
     * @since 1.0.0
     */
    public static <K, V, E> Map<K, V> toMap( Iterable<E> values, Map<K, V> map, Func1<E, K> keyFunc, Func1<E, V> valueFunc ) {
        return IterUtil.toMap(null == values ? null : values.iterator(), map, keyFunc, valueFunc);
    }

    /**
     * 将指定对象全部加入到集合中<br>
     * 提供的对象如果为集合类型，会自动转换为目标元素类型<br>
     *
     * @param <T>        元素类型
     * @param collection 被加入的集合
     * @param value      对象，可能为Iterator、Iterable、Enumeration、Array
     * @return 被加入集合
     */
    public static <T> Collection<T> addAll( Collection<T> collection, Object value ) {
        return addAll(collection, value, TypeUtil.getTypeArgument(collection.getClass()));
    }

    /**
     * 将指定对象全部加入到集合中<br>
     * 提供的对象如果为集合类型，会自动转换为目标元素类型<br>
     * 如果为String，支持类似于[1,2,3,4] 或者 1,2,3,4 这种格式
     *
     * @param <T>         元素类型
     * @param collection  被加入的集合
     * @param value       对象，可能为Iterator、Iterable、Enumeration、Array，或者与集合元素类型一致
     * @param elementType 元素类型，为空时，使用Object类型来接纳所有类型
     * @return 被加入集合
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Collection<T> addAll( Collection<T> collection, Object value, Type elementType ) {
        if (null == collection || null == value) {
            return collection;
        }
        if (TypeUtil.isUnknown(elementType)) {
            // 元素类型为空时，使用Object类型来接纳所有类型
            elementType = Object.class;
        }

        Iterator iter;
        if (value instanceof Iterator) {
            iter = (Iterator) value;
        } else if (value instanceof Iterable) {
            iter = ((Iterable) value).iterator();
        } else if (value instanceof Enumeration) {
            iter = new EnumerationIter<>((Enumeration) value);
        } else if (ArrayUtil.isArray(value)) {
            iter = new ArrayIter<>(value);
        } else if (value instanceof CharSequence) {
            // String按照逗号分隔的列表对待
            final String ArrayStr = StrUtil.unWrap((CharSequence) value, '[', ']');
            iter = StrUtil.splitTrim(ArrayStr, CharUtil.COMMA).iterator();
        } else {
            // 其它类型按照单一元素处理
            iter = CollUtil.newArrayList(value).iterator();
        }

        final ConverterRegistry convert = ConverterRegistry.getInstance();
        while (iter.hasNext()) {
            collection.add(convert.convert(elementType, iter.next()));
        }

        return collection;
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param iterator   要加入的{@link Iterator}
     * @return 原集合
     */
    public static <T> Collection<T> addAll( Collection<T> collection, Iterator<T> iterator ) {
        if (null != collection && null != iterator) {
            while (iterator.hasNext()) {
                collection.add(iterator.next());
            }
        }
        return collection;
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param iterable   要加入的内容{@link Iterable}
     * @return 原集合
     */
    public static <T> Collection<T> addAll( Collection<T> collection, Iterable<T> iterable ) {
        if (iterable == null) {
            return collection;
        }
        return addAll(collection, iterable.iterator());
    }

    /**
     * 加入全部
     *
     * @param <T>         集合元素类型
     * @param collection  被加入的集合 {@link Collection}
     * @param enumeration 要加入的内容{@link Enumeration}
     * @return 原集合
     */
    public static <T> Collection<T> addAll( Collection<T> collection, Enumeration<T> enumeration ) {
        if (null != collection && null != enumeration) {
            while (enumeration.hasMoreElements()) {
                collection.add(enumeration.nextElement());
            }
        }
        return collection;
    }

    /**
     * 加入全部
     *
     * @param <T>        集合元素类型
     * @param collection 被加入的集合 {@link Collection}
     * @param values     要加入的内容数组
     * @return 原集合
     * @since 1.0.0
     */
    public static <T> Collection<T> addAll( Collection<T> collection, T[] values ) {
        if (null != collection && null != values) {
            Collections.addAll(collection, values);
        }
        return collection;
    }

    /**
     * 将另一个列表中的元素加入到列表中，如果列表中已经存在此元素则忽略之
     *
     * @param <T>       集合元素类型
     * @param list      列表
     * @param otherList 其它列表
     * @return 此列表
     */
    public static <T> List<T> addAllIfNotContains( List<T> list, List<T> otherList ) {
        for (T t : otherList) {
            if (false == list.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    /**
     * 获取集合中指定下标的元素值，下标可以为负数，例如-1表示最后一个元素<br>
     * 如果元素越界，返回null
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param index      下标，支持负数
     * @return 元素值
     * @since 1.0.0
     */
    public static <T> T get( Collection<T> collection, int index ) {

        if(isEmpty(collection)) {
            return null ;
        }

        final int size = collection.size();

        if (index < 0) {
            index += size;
        }

        // 检查越界
        if (index >= size || index < 0) {
            return null;
        }

        // 如果是List 相关实现
        if (collection instanceof List) {
            final List<T> list = ((List<T>) collection);
            return list.get(index);
        } else {
            int i = 0;
            for (T t : collection) {
                if (i > index) {
                    break;
                } else if (i == index) {
                    return t;
                }
                i++;
            }
        }
        return null;
    }

    /**
     * 获取集合中指定多个下标的元素值，下标可以为负数，例如-1表示最后一个元素
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param indexes    下标，支持负数
     * @return 元素值列表
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getAny( Collection<T> collection, int... indexes ) {
        final int size = collection.size();
        final ArrayList<T> result = new ArrayList<>();
        if (collection instanceof List) {
            final List<T> list = ((List<T>) collection);
            for (int index : indexes) {
                if (index < 0) {
                    index += size;
                }
                result.add(list.get(index));
            }
        } else {
            final Object[] array = collection.toArray();
            for (int index : indexes) {
                if (index < 0) {
                    index += size;
                }
                result.add((T) array[index]);
            }
        }
        return result;
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return 第一个元素
     * @see IterUtil#getFirst(Iterable)
     * @since 1.0.0
     */
    public static <T> T getFirst( Iterable<T> iterable ) {
        return IterUtil.getFirst(iterable);
    }

    /**
     * 获取集合的第一个元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个元素
     * @see IterUtil#getFirst(Iterator)
     * @since 1.0.0
     */
    public static <T> T getFirst( Iterator<T> iterator ) {
        return IterUtil.getFirst(iterator);
    }

    /**
     * 获取集合的最后一个元素
     *
     * @param <T>        集合元素类型
     * @param collection {@link Collection}
     * @return 最后一个元素
     * @since 1.0.0
     */
    public static <T> T getLast( Collection<T> collection ) {
        if (isEmpty(collection)) {
            return null;
        }
        if (collection instanceof Set) {

            return getLast((Set<T>) collection);
        }else if(collection instanceof List){
            return getLast((List<T>) collection);
        }
        return get(collection, -1);
    }
    /**
     * 使用 {@link SortedSet#last()} 检索给定 Set 的最后一个元素
     * 或以其他方式迭代所有元素。
     *
     * @param set the Set to check (may be {@code null} or empty)
     * @param <T> 传入的set 的泛型参数
     * @return the last element, or {@code null} if none
     * @see SortedSet
     * @see LinkedHashMap#keySet()
     * @see LinkedHashSet
     */
    public static <T> T getLast( Set<T> set ) {
        if (isEmpty(set)) {
            return null;
        }
        if (set instanceof SortedSet) {
            return ((SortedSet<T>) set).last();
        }

        // Full iteration necessary...
        Iterator<T> it = set.iterator();
        T last = null;
        while (it.hasNext()) {
            last = it.next();
        }
        return last;
    }

    /**
     * 检索给定 List 的最后一个元素。
     *
     * @param list the List to check (may be {@code null} or empty)
     * @param <T>  传入的List 泛型参数
     * @return the last element, or {@code null} if none
     */
    public static <T> T getLast( List<T> list ) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(list.size() - 1);
    }


    /**
     * 获得{@link Iterable}对象的元素类型（通过第一个非空元素判断）
     *
     * @param iterable {@link Iterable}
     * @return 元素类型，当列表为空或元素全部为null时，返回null
     * @see IterUtil#getElementType(Iterable)
     * @since 1.0.0
     */
    public static Class<?> getElementType( Iterable<?> iterable ) {
        return IterUtil.getElementType(iterable);
    }

    /**
     * 获得{@link Iterator}对象的元素类型（通过第一个非空元素判断）
     *
     * @param iterator {@link Iterator}
     * @return 元素类型，当列表为空或元素全部为null时，返回null
     * @see IterUtil#getElementType(Iterator)
     * @since 1.0.0
     */
    public static Class<?> getElementType( Iterator<?> iterator ) {
        return IterUtil.getElementType(iterator);
    }

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ArrayList<V> valuesOfKeys( Map<K, V> map, K... keys ) {
        final ArrayList<V> values = new ArrayList<>();
        for (K k : keys) {
            values.add(map.get(k));
        }
        return values;
    }

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     * @since 1.0.0
     */
    public static <K, V> ArrayList<V> valuesOfKeys( Map<K, V> map, Iterable<K> keys ) {
        return valuesOfKeys(map, keys.iterator());
    }

    // ------------------------------------------------------------------------------------------------- sort

    /**
     * 从Map中获取指定键列表对应的值列表<br>
     * 如果key在map中不存在或key对应值为null，则返回值列表对应位置的值也为null
     *
     * @param <K>  键类型
     * @param <V>  值类型
     * @param map  {@link Map}
     * @param keys 键列表
     * @return 值列表
     * @since 1.0.0
     */
    public static <K, V> ArrayList<V> valuesOfKeys( Map<K, V> map, Iterator<K> keys ) {
        final ArrayList<V> values = new ArrayList<>();
        while (keys.hasNext()) {
            values.add(map.get(keys.next()));
        }
        return values;
    }

    /**
     * 将多个集合排序并显示不同的段落（分页）<br>
     * 采用{@link BoundedPriorityQueue}实现分页取局部
     *
     * @param <T>        集合元素类型
     * @param pageNo     页码，从0开始计数，0表示第一页
     * @param pageSize   每页的条目数
     * @param comparator 比较器
     * @param colls      集合数组
     * @return 分页后的段落内容
     */
    @SafeVarargs
    public static <T> List<T> sortPageAll( int pageNo, int pageSize, Comparator<T> comparator, Collection<T>... colls ) {
        final List<T> list = new ArrayList<>(pageNo * pageSize);
        for (Collection<T> coll : colls) {
            list.addAll(coll);
        }
        if (null != comparator) {
            list.sort(comparator);
        }

        return page(pageNo, pageSize, list);
    }

    /**
     * 对指定List分页取值
     *
     * @param <T>      集合元素类型
     * @param pageNo   页码，从0开始计数，0表示第一页
     * @param pageSize 每页的条目数
     * @param list     列表
     * @return 分页后的段落内容
     * @since 1.0.0
     */
    public static <T> List<T> page( int pageNo, int pageSize, List<T> list ) {
        return ListUtil.page(pageNo, pageSize, list);
    }

    /**
     * 排序集合，排序不会修改原集合
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @param comparator 比较器
     * @return treeSet
     */
    public static <T> List<T> sort( Collection<T> collection, Comparator<? super T> comparator ) {
        List<T> list = new ArrayList<>(collection);
        list.sort(comparator);
        return list;
    }

    /**
     * 针对List排序，排序会修改原List
     *
     * @param <T>  元素类型
     * @param list 被排序的List
     * @param c    {@link Comparator}
     * @return 原list
     * @see Collections#sort(List, Comparator)
     */
    public static <T> List<T> sort( List<T> list, Comparator<? super T> c ) {
        return ListUtil.sort(list, c);
    }

    /**
     * 根据Bean的属性排序
     *
     * @param <T>        元素类型
     * @param collection 集合，会被转换为List
     * @param property   属性名
     * @return 排序后的List
     * @since 1.0.0
     */
    public static <T> List<T> sortByProperty( Collection<T> collection, String property ) {
        return sort(collection, new PropertyComparator<>(property));
    }

    /**
     * 根据Bean的属性排序
     *
     * @param <T>      元素类型
     * @param list     List
     * @param property 属性名
     * @return 排序后的List
     * @since 1.0.0
     */
    public static <T> List<T> sortByProperty( List<T> list, String property ) {
        return ListUtil.sortByProperty(list, property);
    }

    /**
     * 根据汉字的拼音顺序排序
     *
     * @param collection 集合，会被转换为List
     * @return 排序后的List
     * @since 1.0.0
     */
    public static List<String> sortByPinyin( Collection<String> collection ) {
        return sort(collection, new PinyinComparator());
    }

    /**
     * 根据汉字的拼音顺序排序
     *
     * @param list List
     * @return 排序后的List
     * @since 1.0.0
     */
    public static List<String> sortByPinyin( List<String> list ) {
        return ListUtil.sortByPinyin(list);
    }

    /**
     * 排序Map
     *
     * @param <K>        键类型
     * @param <V>        值类型
     * @param map        Map
     * @param comparator Entry比较器
     * @return {@link TreeMap}
     * @since 1.0.0
     */
    public static <K, V> TreeMap<K, V> sort( Map<K, V> map, Comparator<? super K> comparator ) {
        final TreeMap<K, V> result = new TreeMap<>(comparator);
        result.putAll(map);
        return result;
    }

    /**
     * 通过Entry排序，可以按照键排序，也可以按照值排序，亦或者两者综合排序
     *
     * @param <K>             键类型
     * @param <V>             值类型
     * @param entryCollection Entry集合
     * @param comparator      {@link Comparator}
     * @return {@link LinkedList}
     * @since 1.0.0
     */
    public static <K, V> LinkedHashMap<K, V> sortToMap( Collection<Map.Entry<K, V>> entryCollection, Comparator<Map.Entry<K, V>> comparator ) {
        List<Map.Entry<K, V>> list = new LinkedList<>(entryCollection);
        list.sort(comparator);

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 通过Entry排序，可以按照键排序，也可以按照值排序，亦或者两者综合排序
     *
     * @param <K>        键类型
     * @param <V>        值类型
     * @param map        被排序的Map
     * @param comparator {@link Comparator}
     * @return {@link LinkedList}
     * @since 1.0.0
     */
    public static <K, V> LinkedHashMap<K, V> sortByEntry( Map<K, V> map, Comparator<Map.Entry<K, V>> comparator ) {
        return sortToMap(map.entrySet(), comparator);
    }

    // ------------------------------------------------------------------------------------------------- forEach

    /**
     * 将Set排序（根据Entry的值）
     *
     * @param <K>        键类型
     * @param <V>        值类型
     * @param collection 被排序的{@link Collection}
     * @return 排序后的Set
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <K, V> List<Entry<K, V>> sortEntryToList( Collection<Entry<K, V>> collection ) {
        List<Entry<K, V>> list = new LinkedList<>(collection);
        list.sort(( o1, o2 ) -> {
            V v1 = o1.getValue();
            V v2 = o2.getValue();

            if (v1 instanceof Comparable) {
                return ((Comparable) v1).compareTo(v2);
            } else {
                return v1.toString().compareTo(v2.toString());
            }
        });
        return list;
    }

    /**
     * 循环遍历 {@link Iterable}，使用{@link Consumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @param consumer {@link Consumer} 遍历的每条数据处理器
     * @since 1.0.0
     */
    public static <T> void forEach( Iterable<T> iterable, Consumer<T> consumer ) {
        if (iterable == null) {
            return;
        }
        forEach(iterable.iterator(), consumer);
    }

    /**
     * 循环遍历 {@link Iterator}，使用{@link Consumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @param consumer {@link Consumer} 遍历的每条数据处理器
     */
    public static <T> void forEach( Iterator<T> iterator, Consumer<T> consumer ) {
        if (iterator == null) {
            return;
        }
        int index = 0;
        while (iterator.hasNext()) {
            consumer.accept(iterator.next(), index);
            index++;
        }
    }

    /**
     * 循环遍历 {@link Enumeration}，使用{@link Consumer} 接受遍历的每条数据，并针对每条数据做处理
     *
     * @param <T>         集合元素类型
     * @param enumeration {@link Enumeration}
     * @param consumer    {@link Consumer} 遍历的每条数据处理器
     */
    public static <T> void forEach( Enumeration<T> enumeration, Consumer<T> consumer ) {
        if (enumeration == null) {
            return;
        }
        int index = 0;
        while (enumeration.hasMoreElements()) {
            consumer.accept(enumeration.nextElement(), index);
            index++;
        }
    }

    /**
     * 循环遍历Map，使用{@link KVConsumer} 接受遍历的每条数据，并针对每条数据做处理<br>
     * 和JDK8中的map.forEach不同的是，此方法支持index
     *
     * @param <K>        Key类型
     * @param <V>        Value类型
     * @param map        {@link Map}
     * @param kvConsumer {@link KVConsumer} 遍历的每条数据处理器
     */
    public static <K, V> void forEach( Map<K, V> map, KVConsumer<K, V> kvConsumer ) {
        if (map == null) {
            return;
        }
        int index = 0;
        for (Entry<K, V> entry : map.entrySet()) {
            kvConsumer.accept(entry.getKey(), entry.getValue(), index);
            index++;
        }
    }

    /**
     * 分组，按照{@link Hash32}接口定义的hash算法，集合中的元素放入hash值对应的子列表中
     *
     * @param <T>        元素类型
     * @param collection 被分组的集合
     * @param hash       Hash值算法，决定元素放在第几个分组的规则
     * @return 分组后的集合
     */
    public static <T> List<List<T>> group( Collection<T> collection, Hash32<T> hash ) {
        final List<List<T>> result = new ArrayList<>();
        if (isEmpty(collection)) {
            return result;
        }
        if (null == hash) {
            // 默认hash算法，按照元素的hashCode分组
            hash = t -> (null == t) ? 0 : t.hashCode();
        }

        int index;
        List<T> subList;
        for (T t : collection) {
            index = hash.hash32(t);
            if (result.size() - 1 < index) {
                while (result.size() - 1 < index) {
                    result.add(null);
                }
                result.set(index, newArrayList(t));
            } else {
                subList = result.get(index);
                if (null == subList) {
                    result.set(index, newArrayList(t));
                } else {
                    subList.add(t);
                }
            }
        }
        return result;
    }

    /**
     * 根据元素的指定字段名分组，非Bean都放在第一个分组中
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param fieldName  元素Bean中的字段名，非Bean都放在第一个分组中
     * @return 分组列表
     */
    public static <T> List<List<T>> groupByField( Collection<T> collection, final String fieldName ) {
        return group(collection, new Hash32<T>() {
            private final List<Object> fieldNameList = new ArrayList<>();

            @Override
            public int hash32( T t ) {
                if (null == t || false == BeanUtil.isBean(t.getClass())) {
                    // 非Bean放在同一子分组中
                    return 0;
                }
                final Object value = ReflectUtil.getFieldValue(t, fieldName);
                int hash = fieldNameList.indexOf(value);
                if (hash < 0) {
                    fieldNameList.add(value);
                    return fieldNameList.size() - 1;
                } else {
                    return hash;
                }
            }
        });
    }

    /**
     * 反序给定List，会在原List基础上直接修改
     *
     * @param <T>  元素类型
     * @param list 被反转的List
     * @return 反转后的List
     * @since 1.0.0
     */
    public static <T> List<T> reverse( List<T> list ) {
        return ListUtil.reverse(list);
    }

    /**
     * 反序给定List，会创建一个新的List，原List数据不变
     *
     * @param <T>  元素类型
     * @param list 被反转的List
     * @return 反转后的List
     * @since 1.0.0
     */
    public static <T> List<T> reverseNew( List<T> list ) {
        return ListUtil.reverseNew(list);
    }

    /**
     * 设置或增加元素。当index小于List的长度时，替换指定位置的值，否则在尾部追加
     *
     * @param <T>     元素类型
     * @param list    List列表
     * @param index   位置
     * @param element 新元素
     * @return 原List
     * @since 1.0.0
     */
    public static <T> List<T> setOrAppend( List<T> list, int index, T element ) {
        return ListUtil.setOrAppend(list, index, element);
    }

    /**
     * 获取指定Map列表中所有的Key
     *
     * @param <K>           键类型
     * @param mapCollection Map列表
     * @return key集合
     * @since 1.0.0
     */
    public static <K> Set<K> keySet( Collection<Map<K, ?>> mapCollection ) {
        if (isEmpty(mapCollection)) {
            return new HashSet<>();
        }
        final HashSet<K> set = new HashSet<>(mapCollection.size() * 16);
        for (Map<K, ?> map : mapCollection) {
            set.addAll(map.keySet());
        }

        return set;
    }

    /**
     * 获取指定Map列表中所有的Value
     *
     * @param <V>           值类型
     * @param mapCollection Map列表
     * @return Value集合
     * @since 1.0.0
     */
    public static <V> List<V> values( Collection<Map<?, V>> mapCollection ) {
        final List<V> values = new ArrayList<>();
        for (Map<?, V> map : mapCollection) {
            values.addAll(map.values());
        }

        return values;
    }

    /**
     * 取最大值
     *
     * @param <T>  元素类型
     * @param coll 集合
     * @return 最大值
     * @see Collections#max(Collection)
     * @since 1.0.0
     */
    public static <T extends Comparable<? super T>> T max( Collection<T> coll ) {
        return Collections.max(coll);
    }

    /**
     * 取最小值
     *
     * @param <T>  元素类型
     * @param coll 集合
     * @return 最小值
     * @see Collections#min(Collection)
     * @since 1.0.0
     */
    public static <T extends Comparable<? super T>> T min( Collection<T> coll ) {
        return Collections.min(coll);
    }

    /**
     * 转为只读集合
     *
     * @param <T> 元素类型
     * @param c   集合
     * @return 只读集合
     * @since 1.0.0
     */
    public static <T> Collection<T> unmodifiable( Collection<? extends T> c ) {
        return Collections.unmodifiableCollection(c);
    }

    /**
     * 根据给定的集合类型，返回对应的空集合，支持类型包括：
     * *
     * <pre>
     *     1. NavigableSet
     *     2. SortedSet
     *     3. Set
     *     4. List
     * </pre>
     *
     * @param <E>             元素类型
     * @param <T>             集合类型
     * @param collectionClass 集合类型
     * @return 空集合
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <E, T extends Collection<E>> T empty( Class<?> collectionClass ) {
        if (null == collectionClass) {
            return (T) Collections.emptyList();
        }

        if (Set.class.isAssignableFrom(collectionClass)) {
            if (NavigableSet.class == collectionClass) {
                return (T) Collections.emptyNavigableSet();
            } else if (SortedSet.class == collectionClass) {
                return (T) Collections.emptySortedSet();
            } else {
                return (T) Collections.emptySet();
            }
        } else if (List.class.isAssignableFrom(collectionClass)) {
            return (T) Collections.emptyList();
        }

        // 不支持空集合的集合类型
        throw new IllegalArgumentException(StrUtil.format("[{}] is not support to get empty!", collectionClass));
    }

    /**
     * 清除一个或多个集合内的元素，每个集合调用clear()方法
     *
     * @param collections 一个或多个集合
     * @since 1.0.0
     */
    public static void clear( Collection<?>... collections ) {
        for (Collection<?> collection : collections) {
            if (isNotEmpty(collection)) {
                collection.clear();
            }
        }
    }

    /**
     * 填充List，以达到最小长度
     *
     * @param <T>    集合元素类型
     * @param list   列表
     * @param minLen 最小长度
     * @param padObj 填充的对象
     * @since 1.0.0
     */
    public static <T> void padLeft( List<T> list, int minLen, T padObj ) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            padRight(list, minLen, padObj);
            return;
        }
        for (int i = list.size(); i < minLen; i++) {
            list.add(0, padObj);
        }
    }

    /**
     * 填充List，以达到最小长度
     *
     * @param <T>    集合元素类型
     * @param list   列表
     * @param minLen 最小长度
     * @param padObj 填充的对象
     * @since 1.0.0
     */
    public static <T> void padRight( Collection<T> list, int minLen, T padObj ) {
        Objects.requireNonNull(list);
        for (int i = list.size(); i < minLen; i++) {
            list.add(padObj);
        }
    }

    // ---------------------------------------------------------------------------------------------- Interface start

    /**
     * 使用给定的转换函数，转换源集合为新类型的集合
     *
     * @param <F>        源元素类型
     * @param <T>        目标元素类型
     * @param collection 集合
     * @param function   转换函数
     * @return 新类型的集合
     * @since 1.0.0
     */
    public static <F, T> Collection<T> trans( Collection<F> collection, Function<? super F, ? extends T> function ) {
        return new TransCollection<>(collection, function);
    }

    /**
     * 获取Collection或者iterator的大小，此方法可以处理的对象类型如下：
     * <ul>
     * <li>Collection - the collection size
     * <li>Map - the map size
     * <li>Array - the array size
     * <li>Iterator - the number of elements remaining in the iterator
     * <li>Enumeration - the number of elements remaining in the enumeration
     * </ul>
     *
     * @param object 可以为空的对象
     * @return 如果object为空则返回0
     * @throws IllegalArgumentException 参数object不是Collection或者iterator
     * @since 1.0.0
     */
    public static int size( final Object object ) {
        if (object == null) {
            return 0;
        }

        int total = 0;
        if (object instanceof Map<?, ?>) {
            total = ((Map<?, ?>) object).size();
        } else if (object instanceof Collection<?>) {
            total = ((Collection<?>) object).size();
        } else if (object instanceof Iterable<?>) {
            total = IterUtil.size((Iterable<?>) object);
        } else if (object instanceof Iterator<?>) {
            total = IterUtil.size((Iterator<?>) object);
        } else if (object instanceof Enumeration<?>) {
            final Enumeration<?> it = (Enumeration<?>) object;
            while (it.hasMoreElements()) {
                total++;
                it.nextElement();
            }
        } else if (ArrayUtil.isArray(object)) {
            total = ArrayUtil.length(object);
        } else {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
        return total;
    }
    // ---------------------------------------------------------------------------------------------- Interface end

    /**
     * 判断两个{@link Collection} 是否元素和顺序相同，返回{@code true}的条件是：
     * <ul>
     *     <li>两个{@link Collection}必须长度相同</li>
     *     <li>两个{@link Collection}元素相同index的对象必须equals，满足{@link Objects#equals(Object, Object)}</li>
     * </ul>
     * 此方法来自Apache-commmons-Collections4。
     *
     * @param list1 列表1
     * @param list2 列表2
     * @return 是否相同
     * @since 1.0.0
     */
    public static boolean isEqualList( final Collection<?> list1, final Collection<?> list2 ) {
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }
        if (list1 == list2) {
            return true;
        }

        return IterUtil.isEqualList(list1, list2);
    }


    /**
     * Convert the supplied array into a List. A primitive array gets converted
     * into a List of the appropriate wrapper type.
     * <p><b>NOTE:</b> Generally prefer the standard {@link Arrays#asList} method.
     * This {@code arrayToList} method is just meant to deal with an incoming Object
     * value that might be an {@code Object[]} or a primitive array at runtime.
     * <p>A {@code null} source value will be converted to an empty List.
     *
     * @param value the (potentially primitive) array
     * @return the converted List result
     * @see ArrayUtil#toArray(Object)
     * @see Arrays#asList(Object[])
     */
    public static List<?> arrayToList( Object value ) {
        return Arrays.asList(ArrayUtil.toArray(value));
    }

    /**
     * Merge the given array into the given Collection.
     *
     * @param array      the array to merge (may be {@code null})
     * @param collection the target Collection to merge the array into
     * @param <E>        泛型参数
     */
    @SuppressWarnings("unchecked")
    public static <E> void mergeArrayIntoCollection( Object array, Collection<E> collection ) {
        Object[] arr = ArrayUtil.toArray(array);
        for (Object elem : arr) {
            collection.add((E) elem);
        }
    }

    /**
     * Merge the given Properties instance into the given Map,
     * copying all properties (key-value pairs) over.
     * <p>Uses {@code Properties.propertyNames()} to even catch
     * default properties linked into the original Properties instance.
     *
     * @param props the Properties instance to merge (may be {@code null})
     * @param map   the target Map to merge the properties into
     * @param <K>   键
     * @param <V>   值
     */
    @SuppressWarnings("unchecked")
    public static <K, V> void mergePropertiesIntoMap( Properties props, Map<K, V> map ) {
        if (props != null) {
            for (Enumeration<?> en = props.propertyNames(); en.hasMoreElements(); ) {
                String key = (String) en.nextElement();
                Object value = props.get(key);
                if (value == null) {
                    // Allow for defaults fallback or potentially overridden accessor...
                    value = props.getProperty(key);
                }
                map.put((K) key, (V) value);
            }
        }
    }

    /**
     * Check whether the given Iterator contains the given element.
     *
     * @param iterator the Iterator to check
     * @param element  the element to look for
     * @return {@code true} if found, {@code false} otherwise
     */
    public static boolean contains( Iterator<?> iterator, Object element ) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (ObjectUtil.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Enumeration contains the given element.
     *
     * @param enumeration the Enumeration to check
     * @param element     the element to look for
     * @return {@code true} if found, {@code false} otherwise
     */
    public static boolean contains( Enumeration<?> enumeration, Object element ) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (ObjectUtil.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Collection contains the given element instance.
     * <p>Enforces the given instance to be present, rather than returning
     * {@code true} for an equal element as well.
     *
     * @param collection the Collection to check
     * @param element    the element to look for
     * @return {@code true} if found, {@code false} otherwise
     */
    public static boolean containsInstance( Collection<?> collection, Object element ) {
        if (collection != null) {
            for (Object candidate : collection) {
                if (candidate == element) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Find a single value of the given type in the given Collection.
     *
     * @param collection the Collection to search
     * @param type       the type to look for
     * @param <T>        the input type
     * @return a value of the given type found if there is a clear match,
     * or {@code null} if none or more than one such value found
     */
    @SuppressWarnings("unchecked")

    public static <T> T findValueOfType( Collection<?> collection, Class<T> type ) {
        if (isEmpty(collection)) {
            return null;
        }
        T value = null;
        for (Object element : collection) {
            if (type == null || type.isInstance(element)) {
                if (value != null) {
                    // More than one value found... no clear single value.
                    return null;
                }
                value = (T) element;
            }
        }
        return value;
    }

    /**
     * Find a single value of one of the given types in the given Collection:
     * searching the Collection for a value of the first type, then
     * searching for a value of the second type, etc.
     *
     * @param collection the collection to search
     * @param types      the types to look for, in prioritized order
     * @return a value of one of the given types found if there is a clear match,
     * or {@code null} if none or more than one such value found
     */

    public static Object findValueOfType( Collection<?> collection, Class<?>[] types ) {
        if (isEmpty(collection) || ObjectUtil.isEmpty(types)) {
            return null;
        }
        for (Class<?> type : types) {
            Object value = findValueOfType(collection, type);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * Determine whether the given Collection only contains a single unique object.
     *
     * @param collection the Collection to check
     * @return {@code true} if the collection contains a single reference or
     * multiple references to the same instance, {@code false} otherwise
     */
    public static boolean hasUniqueObject( Collection<?> collection ) {
        if (isEmpty(collection)) {
            return false;
        }
        boolean hasCandidate = false;
        Object candidate = null;
        for (Object elem : collection) {
            if (!hasCandidate) {
                hasCandidate = true;
                candidate = elem;
            } else if (candidate != elem) {
                return false;
            }
        }
        return true;
    }

    /**
     * Find the common element type of the given Collection, if any.
     *
     * @param collection the Collection to check
     * @return the common element type, or {@code null} if no clear
     * common type has been found (or the collection was empty)
     */
    public static Class<?> findCommonElementType( Collection<?> collection ) {
        if (isEmpty(collection)) {
            return null;
        }
        Class<?> candidate = null;
        for (Object val : collection) {
            if (val != null) {
                if (candidate == null) {
                    candidate = val.getClass();
                } else if (candidate != val.getClass()) {
                    return null;
                }
            }
        }
        return candidate;
    }

    /**
     * Retrieve the first element of the given Set, using {@link SortedSet#first()}
     * or otherwise using the iterator.
     *
     * @param set the Set to check (may be {@code null} or empty)
     * @param <T> Set Type  when input
     * @return the first element, or {@code null} if none
     * @see SortedSet
     * @see LinkedHashMap#keySet()
     * @see LinkedHashSet
     */

    public static <T> T getFirst( Set<T> set ) {
        if (isEmpty(set)) {
            return null;
        }
        if (set instanceof SortedSet) {
            return ((SortedSet<T>) set).first();
        }

        Iterator<T> it = set.iterator();
        T first = null;
        if (it.hasNext()) {
            first = it.next();
        }
        return first;
    }

    /**
     * Retrieve the first element of the given List, accessing the zero index.
     *
     * @param list the List to check (may be {@code null} or empty)
     * @param <T>  返回的list  泛型参数
     * @return the first element, or {@code null} if none
     */
    public static <T> T getFirst( List<T> list ) {
        if (isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }



    /**
     * Marshal the elements from the given enumeration into an array of the given type.
     * Enumeration elements must be assignable to the type of the given array. The array
     * returned will be a different instance than the array given.
     *
     * @param enumeration 入参
     * @param array       入参
     * @param <A>         泛型
     * @param <E>         泛型
     * @return 返回值
     */
    public static <A, E extends A> A[] toArray( Enumeration<E> enumeration, A[] array ) {
        ArrayList<A> elements = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            elements.add(enumeration.nextElement());
        }
        return elements.toArray(array);
    }

    /**
     * Adapt an {@link Enumeration} to an {@link Iterator}.
     *
     * @param enumeration the original {@code Enumeration}
     * @param <E>         传入值的泛型参数
     * @return the adapted {@code Iterator}
     */
    public static <E> Iterator<E> toIterator( Enumeration<E> enumeration ) {
        return (enumeration != null ? new EnumerationIterator<>(enumeration) : Collections.emptyIterator());
    }

    /**
     * Description:实例化一个 arraylist,容量为10
     *
     * @param <T> 泛型标记
     * @return 返回值 ArrayList
     */
    public static <T> ArrayList<T> createArrayList() {
        return new ArrayList();
    }

    /**
     * Description:实例化一个特定容量的 arraylist
     * 不像数组的特定罐子，当容量超出设定，会自动扩容
     *
     * @param initialCapacity 入参容量
     * @param <T>             返回泛型
     * @return 返回值为ArrayList
     */
    public static <T> ArrayList<T> createArrayList( int initialCapacity ) {
        return new ArrayList(initialCapacity);
    }

    /**
     * @param c   入参 迭代器
     * @param <T> 泛型
     * @return 返回一个 new ArrayList()
     */
    public static <T> ArrayList<T> createArrayList( Iterable<? extends T> c ) {
        ArrayList<T> list;
        if ((c instanceof Collection)) {
            list = new ArrayList((Collection) c);
        } else {
            list = new ArrayList();
            iterableToCollection(c, list);
            list.trimToSize();
        }
        return list;
    }

    /**
     * Description：根据一个集合创建 arraylist,集合可以是 数组，list,map
     *
     * @param args 入参
     * @param <T>  泛型T
     * @param <V>  泛型V
     * @return 返回ArrayList
     */
    public static <T, V extends T> ArrayList<T> createArrayList( V... args ) {
        if ((args == null) || (args.length == 0)) {
            return new ArrayList();
        }
        ArrayList<T> list = new ArrayList(args.length);
        for (V v : args) {
            list.add(v);
        }
        return list;
    }

    /**
     * @param <T> 泛型参数
     * @return 返回 LinkedList
     */
    public static <T> LinkedList<T> createLinkedList() {
        return new LinkedList();
    }

    /**
     * 根据一个迭代器返回一个LinkedList
     *
     * @param c   入参迭代器
     * @param <T> 迭代器泛型
     * @return 返回一个LinkedList
     */
    public static <T> LinkedList<T> createLinkedList( Iterable<? extends T> c ) {
        LinkedList<T> list = new LinkedList();

        iterableToCollection(c, list);

        return list;
    }

    /**
     * Description：根据一个集合创建 arraylist,集合可以是 数组，list,map
     *
     * @param args 入参
     * @param <T>  泛型T
     * @param <V>  泛型V
     * @return 返回LinkedList
     */
    public static <T, V extends T> LinkedList<T> createLinkedList( V... args ) {
        LinkedList<T> list = new LinkedList();
        if (args != null) {
            for (V v : args) {
                list.add(v);
            }
        }
        return list;
    }

    /**
     * Description：根据一个集合创建 arraylist,集合可以是 数组，list,map
     *
     * @param args 入参
     * @param <T>  泛型T
     * @return 返回List
     */
    public static <T> List<T> asList( T... args ) {
        if ((args == null) || (args.length == 0)) {
            return Collections.emptyList();
        }
        return Arrays.asList(args);
    }

    /**
     * 创建一个HasMap
     *
     * @param <K> 泛型
     * @param <V> 泛型
     * @return HashMap
     */
    public static <K, V> HashMap<K, V> createHashMap() {
        return new HashMap();
    }

    /**
     * 创建一个HashMap
     *
     * @param initialCapacity 初始容量
     * @param <K>             泛型
     * @param <V>             泛型
     * @return HashMap
     */
    public static <K, V> HashMap<K, V> createHashMap( int initialCapacity ) {
        return new HashMap(initialCapacity);
    }

    /**
     * 创建一个LinkedHashMap
     *
     * @param <K> 泛型
     * @param <V> 泛型
     * @return LinkedHashMap
     */
    public static <K, V> LinkedHashMap<K, V> createLinkedHashMap() {
        return new LinkedHashMap();
    }

    /**
     * 创建一个LinkedHashMap
     *
     * @param initialCapacity 初始容量
     * @param <K>             泛型
     * @param <V>             泛型
     * @return LinkedHashMap
     */
    public static <K, V> LinkedHashMap<K, V> createLinkedHashMap( int initialCapacity ) {
        return new LinkedHashMap(initialCapacity);
    }

    /**
     * 创建一个TreeMap
     *
     * @param <K> 泛型
     * @param <V> 泛型
     * @return TreeMap
     */
    public static <K, V> TreeMap<K, V> createTreeMap() {
        return new TreeMap();
    }

    /**
     * 创建一个TreeMap
     *
     * @param comparator 比较器
     * @param <K>        泛型
     * @param <V>        泛型
     * @return TreeMap
     */
    public static <K, V> TreeMap<K, V> createTreeMap( Comparator<? super K> comparator ) {
        return new TreeMap(comparator);
    }

    /**
     * 创建一个 ConcurrentHashMap
     *
     * @param <K> 泛型
     * @param <V> 泛型
     * @return ConcurrentHashMap
     */
    public static <K, V> ConcurrentHashMap<K, V> createConcurrentHashMap() {
        return new ConcurrentHashMap();
    }

    /**
     * 创建一个空的HashSet
     *
     * @param <T> 泛型参数
     * @return HashSet
     */
    public static <T> HashSet<T> createHashSet() {
        return new HashSet();
    }

    /**
     * 根据传入的参数创建一个有值的 HashSet
     *
     * @param args 传入的参数列表
     * @param <T>  入参泛型的父类
     * @param <V>  入参的泛型
     * @return HashSet
     */
    public static <T, V extends T> HashSet<T> createHashSet( V... args ) {
        if ((args == null) || (args.length == 0)) {
            return new HashSet();
        }
        HashSet<T> set = new HashSet(args.length);
        for (V v : args) {
            set.add(v);
        }
        return set;
    }

    /**
     * 根据一个可迭代iterable 创建一个 HashSet
     *
     * @param c   Iterable
     * @param <T> Iterable的泛型参数
     * @return HashSet
     */
    public static <T> HashSet<T> createHashSet( Iterable<? extends T> c ) {
        HashSet<T> set;
        if ((c instanceof Collection)) {
            set = new HashSet((Collection) c);
        } else {
            set = new HashSet();
            iterableToCollection(c, set);
        }
        return set;
    }

    /**
     * 创建一个 LinkedHashSet
     * @param <T> 泛型参数
     * @return 返回一个新的 LinkedHashSet
     */
    public static <T> LinkedHashSet<T> createLinkedHashSet() {
        return new LinkedHashSet();
    }

    /**
     * 创建一个 LinkedHashSet
     * @param args 参数列表
     * @param <T> 泛型
     * @param <V> 泛型
     * @return  新的LinkedHashSet
     */
    public static <T, V extends T> LinkedHashSet<T> createLinkedHashSet( V... args ) {
        if ((args == null) || (args.length == 0)) {
            return new LinkedHashSet();
        }
        LinkedHashSet<T> set = new LinkedHashSet(args.length);
        for (V v : args) {
            set.add(v);
        }
        return set;
    }

    /**
     * 创建一个 LinkedHashSet
     * @param c 参数列表
     * @param <T> 泛型
     * @return  新的LinkedHashSet
     */
    public static <T> LinkedHashSet<T> createLinkedHashSet( Iterable<? extends T> c ) {
        LinkedHashSet<T> set;
        if ((c instanceof Collection)) {
            set = new LinkedHashSet((Collection) c);
        } else {
            set = new LinkedHashSet();
            iterableToCollection(c, set);
        }
        return set;
    }

    /**
     * 创建一个TreeSet
     * @param <T>  泛型参数
     * @return 返回新创建的TreeSet
     */
    public static <T> TreeSet<T> createTreeSet() {
        return new TreeSet();
    }

    /**
     * 创建一个TreeSet
     * @param <T>  泛型参数
     * @return 返回新创建的TreeSet
     */
    public static <T, V extends T> TreeSet<T> createTreeSet( V... args ) {
        return createTreeSet(null, args);
    }

    /**
     * 创建一个TreeSet
     * @param <T>  泛型参数
     * @return 返回新创建的TreeSet
     */
    public static <T> TreeSet<T> createTreeSet( Iterable<? extends T> c ) {
        return createTreeSet(null, c);
    }

    /**
     * 创建一个TreeSet
     * @param <T>  泛型参数
     * @return 返回新创建的TreeSet
     */
    public static <T> TreeSet<T> createTreeSet( Comparator<? super T> comparator ) {
        return new TreeSet(comparator);
    }

    /**
     * 创建一个TreeSet
     * @param <T>  泛型参数
     * @return 返回新创建的TreeSet
     */
    public static <T, V extends T> TreeSet<T> createTreeSet( Comparator<? super T> comparator, V... args ) {
        TreeSet<T> set = new TreeSet(comparator);
        if (args != null) {
            for (V v : args) {
                set.add(v);
            }
        }
        return set;
    }

    /**
     * 创建一个TreeSet
     * @param <T>  泛型参数
     * @return 返回新创建的TreeSet
     */
    public static <T> TreeSet<T> createTreeSet( Comparator<? super T> comparator, Iterable<? extends T> c ) {
        TreeSet<T> set = new TreeSet(comparator);
        iterableToCollection(c, set);
        return set;
    }


    private static <T> void iterableToCollection( Iterable<? extends T> c, Collection<T> list ) {
        for (T element : c) {
            list.add(element);
        }
    }

    public static String[] toNoNullStringArray( Collection collection ) {
        if (collection == null) {
            return ArrayUtil.EMPTY_STRING_ARRAY;
        }
        return toNoNullStringArray(collection.toArray());
    }


    public static String[] toNoNullStringArray( Object[] array ) {
        ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; i++) {
            Object e = array[i];
            if (e != null) {
                list.add(e.toString());
            }
        }
        return (String[]) list.toArray(ArrayUtil.EMPTY_STRING_ARRAY);
    }


    public static void main( String[] args ) {
        String[] array = new String[5];
        array[0] = "AAAAAA";
        array[1] = "BBBBBB";
        array[2] = "CCCCCC";
        array[3] = "DDDDDD";
        array[4] = "EEEEEE";

        ArrayList<String> arrayList = createArrayList();
        for (String s : array) {
            arrayList.add(s);
        }
        System.out.println("arrayList:" + arrayList);

        ArrayList<String> arrayList1 = createArrayList(4);
        for (String s : array) {
            arrayList1.add(s);
        }
        System.out.println("arrayList1:" + arrayList1);

        //根据 array 索引创建一个 arraylist
        ArrayList<Object> arrayList2 = createArrayList(array);
        System.out.println("arrayList2:" + arrayList2.toString());
    }

    /**
     * 集合类的委托执行 {@link Collection#remove}
     * 有异常时 返回为false ;
     * 否则 返回 remove  的结果
     * @see Collection#remove(Object)
     *
     * @see CollUtil#remove(Collection, Object)
     * @param collection collection
     * @param object     object
     * @return 是否删除
     *
     */
    public static boolean safeRemove( Collection<?> collection, @CheckForNull Object object ) {
        checkNotNull(collection);
        try {
            return remove(collection ,object);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * 集合类的委托执行 {@link Collection#remove}
     * 有异常时 返回为false ;
     * 否则 返回 remove  的结果
     * @see Collection#remove(Object)
     *
     * @param collection collection
     * @param object     object
     * @return 是否删除
     * @throws NullPointerException   当对象为null 时强转类型可能会抛出该错误
     * @throws ClassCastException  类型转换错误
     *
     */
    public static boolean remove( Collection<?> collection, @CheckForNull Object object ) {
        checkNotNull(collection);
        return collection.remove(object);
    }

    /**
     * 集合类toString方法的一个实现 {@link Collection#toString()}.
     * 将集合类相关转为 String
     *
     * @param collection collection
     * @return 返回字符串
     */
    public static String toString( final Collection<?> collection ) {
        StringBuilder sb = StrUtil.builder((int) Math.min(collection.size() * 8L, 1 << (Integer.SIZE - 2)));
        sb.append('[');

        boolean first = true;
        for (Object o : collection) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            if (o == collection) {
                sb.append("(this Collection)");
            } else {
                sb.append(o);
            }
        }
        return sb.append(']').toString();
    }


    /**
     * 
     * 集合类相关的 hashCode 实现{@link Collection#hashCode()}.
     * @see ObjectUtil#hashCode(Object...) 
     * @param list  非空集合
     * @return hashcode值
     *
     */
    public static int hashCode( Collection<?> list ) {
        if(list ==null){
            return  0;
        }
        int result = 1;
        for (Object o : list) {
            result = 31 * result + (o == null ? 0 : o.hashCode());

            result = ~~result;
            // needed to deal with GWT integer overflow
        }
        return result;
    }

    /**
     * 笛卡尔积
     * 两个集合X和Y的笛卡尔积（Cartesian product），又称直积，表示为X × Y，第一个对象是X的成员而第二个对象是Y的所有可能有序对的其中一个成员 [1]  。
     * 返回可以通过从每个给定的元素中选择一个元素来形成的每个可能的列表
     * 按顺序列出； “n-ary <a href="http://en.wikipedia.org/wiki/Cartesian_product">笛卡尔
     * product</a>" 的列表。例如：
     *
     * <pre>{@code
     * CollUtil.cartesianProduct(
     *     List.of(1, 2),
     *     List.of("A", "B", "C"))
     * }</pre>
     *
     * <p>returns a list containing six lists in the following order:
     *
     * <ul>
     *   <li>{@code List.of(1, "A")}
     *   <li>{@code List.of(1, "B")}
     *   <li>{@code List.of(1, "C")}
     *   <li>{@code List.of(2, "A")}
     *   <li>{@code List.of(2, "B")}
     *   <li>{@code List.of(2, "C")}
     * </ul>
     * 请注意，如果任何输入列表为空，则笛卡尔积也将为空。如果没有列表
     * 提供（一个空列表），生成的笛卡尔积只有一个元素，一个空的
     * 列表（违反直觉，但在数学上是一致的）。
     * 性能说明：虽然大小为 {@code m, n, p} 的列表的笛卡尔积是一个
     * 大小为 {@code m x n x p} 的列表，其实际内存消耗要小得多。当构造笛卡尔乘积的时候，只是复制输入列表。仅作为结果列表。
     * 迭代的是创建的单个列表，迭代后不会保留这些列表。
     *
     * @param coll1  集合1
     * @param coll2  集合2
     * @param <T>  any common base class shared by all axes (often just {@link Object})
     * @return  笛卡尔积，作为包含不可变列表的不可变列表
     * @throws IllegalArgumentException if the size of the cartesian product would be greater than
     *                                  {@link Integer#MAX_VALUE}
     * @throws NullPointerException     if {@code lists}, any one of the {@code lists}, or any element of
     *                                  a provided list is null
     *
     */
    public static <T> List<List<T>> cartesianProduct(Collection<T> coll1, Collection<T> coll2 ) {

        // 判断非空  当有一个为空时则该笛卡尔积为空的笛卡尔积
        if (isEmpty(coll1)||isEmpty(coll2)) {
            return newArrayList();
        }

        List<List<T>> collect = coll1.stream().flatMap(s1 -> coll2.stream().map(s2 -> CollUtil.newArrayList(s1, s2)))
                .collect(Collectors.toList());

        return collect ;

    }

    /**
     *
     * @param coll1  集合1
     * @param coll2  集合2
     * @param <T>  any common base class shared by all axes (often just {@link Object})
     * @return  笛卡尔积，作为包含不可变列表的不可变列表
     * @throws IllegalArgumentException if the size of the cartesian product would be greater than
     *                                  {@link Integer#MAX_VALUE}
     * @throws NullPointerException     if {@code lists}, any one of the {@code lists}, or any element of
     *                                  a provided list is null
     *
     */
    public static <T> List<List<T>> cartesianProduct(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls ) {
        List<List<T>> header = cartesianProduct(coll1, coll2);
        if(header.isEmpty()){
            return newArrayList();
        }

        for(Collection<T> coll :otherColls){

            if(isEmpty(coll)){
                return newArrayList();
            }
            header = header.stream().flatMap(l1-> coll.stream().map( s2-> {List<T> l2 = CollUtil.newArrayList(l1);l2.add(s2);return l2;})).collect(Collectors.toList());
        }

        return header;

    }

    /**
     * @param coll1  集合1
     * @param coll2  集合2
     * @param <T>  any common base class shared by all axes (often just {@link Object})
     * @return  笛卡尔积，作为包含不可变列表的不可变列表
     * @throws IllegalArgumentException if the size of the cartesian product would be greater than
     *                                  {@link Integer#MAX_VALUE}
     * @throws NullPointerException     if {@code lists}, any one of the {@code lists}, or any element of
     *                                  a provided list is null
     *
     */
    public static <T> Set<List<T>> cartesianProductDistinct( Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {

        List<List<T>> lists = cartesianProduct(coll1, coll2, otherColls);

        return CollUtil.newHashSet(true,lists);
    }

    /**
     * 针对一个参数做相应的操作<br>
     * 此函数接口与JDK8中Consumer不同是多提供了index参数，用于标记遍历对象是第几个。
     *
     * @param <T> 处理参数类型
     */
    @FunctionalInterface
    public interface Consumer<T> extends Serializable {
        /**
         * 接受并处理一个参数
         *
         * @param value 参数值
         * @param index 参数在集合中的索引
         */
        void accept( T value, int index );
    }


    /**
     * 针对两个参数做相应的操作，例如Map中的KEY和VALUE
     */
    @FunctionalInterface
    public interface KVConsumer<K, V> extends Serializable {
        /**
         * 接受并处理一对参数
         *
         * @param key   键
         * @param value 值
         * @param index 参数在集合中的索引
         */
        void accept( K key, V value, int index );
    }

    /**
     * Iterator wrapping an Enumeration.
     *
     * @param <E>
     */
    private static class EnumerationIterator<E> implements Iterator<E> {

        private final Enumeration<E> enumeration;

        public EnumerationIterator( Enumeration<E> enumeration ) {
            this.enumeration = enumeration;
        }

        @Override
        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }

        @Override
        public E next() {
            return this.enumeration.nextElement();
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported");
        }
    }

}
