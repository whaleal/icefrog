package com.whaleal.icefrog.core.map.multi;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.collection.IterUtil;
import com.whaleal.icefrog.core.lang.func.Func0;

import java.util.*;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * 值作为集合的Map实现，通过调用putValue可以在相同key时加入多个值，多个值用集合表示<br>
 * 此类可以通过传入函数自定义集合类型的创建规则
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class CollectionValueMap<K, V> extends AbsCollValueMap<K, V, Collection<V>> {
    private static final long serialVersionUID = 9012989578038102983L;

    private final Func0<Collection<V>> collectionCreateFunc;

    // ------------------------------------------------------------------------- Constructor start

    /**
     * 构造
     */
    public CollectionValueMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始大小
     */
    public CollectionValueMap( int initialCapacity ) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 构造
     *
     * @param m Map
     */
    public CollectionValueMap( Map<? extends K, ? extends Collection<V>> m ) {
        this(DEFAULT_LOAD_FACTOR, m);
    }

    /**
     * 构造
     *
     * @param loadFactor 加载因子
     * @param m          Map
     */
    public CollectionValueMap( float loadFactor, Map<? extends K, ? extends Collection<V>> m ) {
        this(loadFactor, m, ArrayList::new);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始大小
     * @param loadFactor      加载因子
     */
    public CollectionValueMap( int initialCapacity, float loadFactor ) {
        this(initialCapacity, loadFactor, ArrayList::new);
    }

    /**
     * 构造
     *
     * @param loadFactor           加载因子
     * @param m                    Map
     * @param collectionCreateFunc Map中值的集合创建函数
     * @since 1.0.0
     */
    public CollectionValueMap( float loadFactor, Map<? extends K, ? extends Collection<V>> m, Func0<Collection<V>> collectionCreateFunc ) {
        this(m.size(), loadFactor, collectionCreateFunc);
        this.putAll(m);
    }

    /**
     * 构造
     *
     * @param initialCapacity      初始大小
     * @param loadFactor           加载因子
     * @param collectionCreateFunc Map中值的集合创建函数
     * @since 1.0.0
     */
    public CollectionValueMap( int initialCapacity, float loadFactor, Func0<Collection<V>> collectionCreateFunc ) {
        super(new HashMap<>(initialCapacity, loadFactor));
        this.collectionCreateFunc = collectionCreateFunc;
    }
    // ------------------------------------------------------------------------- Constructor end

    @Override
    protected Collection<V> createCollection() {
        return collectionCreateFunc.applyWithRuntimeException();
    }

    @Override
    public boolean containsEntry(  Object key,  Object value ) {
        Collection< V > collection = this.get(key);
        return collection != null && collection.contains(value);
    }

    @Override
    public boolean putAll( K key, Iterable<V> values ) {
        checkNotNull(values);
        Collection<? extends V> valueCollection = (Collection<? extends V>) values;
        if(valueCollection ==null){
           try{
               this.put(key,CollUtil.newArrayList(values));
               return true ;
           }catch (Exception e){
               return false ;
           }
        }

        // make sure we only call values.iterator() once
        // and we only call get(key) if values is nonempty
        if (values instanceof Collection) {

            return !valueCollection.isEmpty() && get(key).addAll(valueCollection);
        } else {
            Iterator<? extends V> valueItr = values.iterator();
            return valueItr.hasNext() && IterUtil.addAll(get(key), valueItr);
        }
    }

    @Override
    public Collection< V > replaceValues( K key, Iterable<  V > values ) {
        Iterator<? extends V> iterator = values.iterator();
        if (!iterator.hasNext()) {
            return this.remove(key);
        }
        Collection< V > coll = this.get(key);
        if(null != coll){
            coll.clear();
            IterUtil.addAll(coll,values);
            return coll ;
        }else {
            Collection< V > list = CollUtil.<V>addAll(CollUtil.<V>newArrayList(), values);

            this.put(key,list);
            return list ;
        }

    }

}
