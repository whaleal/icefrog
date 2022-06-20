package com.whaleal.icefrog.core.map.multi;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.collection.IterUtil;

import java.util.*;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * 值作为集合Set（LinkedHashSet）的Map实现，通过调用putValue可以在相同key时加入多个值，多个值用集合表示
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class SetValueMap<K, V> extends AbsCollValueMap<K, V, Set<V>> {
    private static final long serialVersionUID = 6044017508487827899L;

    // ------------------------------------------------------------------------- Constructor start

    /**
     * 构造
     */
    public SetValueMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始大小
     */
    public SetValueMap( int initialCapacity ) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 构造
     *
     * @param m Map
     */
    public SetValueMap( Map<? extends K, ? extends Collection<V>> m ) {
        this(DEFAULT_LOAD_FACTOR, m);
    }

    /**
     * 构造
     *
     * @param loadFactor 加载因子
     * @param m          Map
     */
    public SetValueMap( float loadFactor, Map<? extends K, ? extends Collection<V>> m ) {
        this(m.size(), loadFactor);
        this.putAllValues(m);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始大小
     * @param loadFactor      加载因子
     */
    public SetValueMap( int initialCapacity, float loadFactor ) {
        super(new HashMap<>(initialCapacity, loadFactor));
    }
    // ------------------------------------------------------------------------- Constructor end

    @Override
    protected Set<V> createCollection() {
        return new LinkedHashSet<>(DEFAULT_COLLECTION_INITIAL_CAPACITY);
    }

    @Override
    public boolean containsEntry( Object key, Object value ) {
        Collection< V > collection = this.get(key);
        return collection != null && collection.contains(value);
    }

    @Override
    public boolean putAll( K key, Iterable< V > values ) {
        checkNotNull(values);
        Set<? extends V> valueCollection = (Set<? extends V>) values;
        if(valueCollection ==null){
            try{
                this.put(key,CollUtil.newHashSet(false ,values.iterator()));
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
    public Set< V > replaceValues( K key, Iterable< V > values ) {
        Iterator<? extends V> iterator = values.iterator();
        if (!iterator.hasNext()) {
            return this.remove(key);
        }
        Set< V > coll = this.get(key);
        if(null != coll){
            coll.clear();
            IterUtil.addAll(coll,values);
            return coll ;
        }else {
            HashSet<V> set = CollUtil.newHashSet(false ,values.iterator());
            this.put(key,set);
            return set ;
        }
    }
}
