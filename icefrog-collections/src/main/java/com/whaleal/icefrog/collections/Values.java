package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.Consumer;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
class Values<K extends Object, V extends Object>
        extends AbstractCollection<V> {
    final Map<K, V> map;

    Values( Map<K, V> map ) {
        this.map = checkNotNull(map);
    }

    final Map<K, V> map() {
        return map;
    }

    @Override
    public Iterator<V> iterator() {
        return valueIterator(map().entrySet().iterator());
    }

    @Override
    public void forEach( Consumer<? super V> action ) {
        checkNotNull(action);
        // avoids allocation of entries for those maps that generate fresh entries on iteration
        map.forEach(( k, v ) -> action.accept(v));
    }

    @Override
    public boolean remove( @CheckForNull Object o ) {
        try {
            return super.remove(o);
        } catch (UnsupportedOperationException e) {
            for (Map.Entry<K, V> entry : map().entrySet()) {
                if (ObjectUtil.equal(o, entry.getValue())) {
                    map().remove(entry.getKey());
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        try {
            return super.removeAll(checkNotNull(c));
        } catch (UnsupportedOperationException e) {
            Set<K> toRemove = SetUtil.newHashSet();
            for (Map.Entry<K, V> entry : map().entrySet()) {
                if (c.contains(entry.getValue())) {
                    toRemove.add(entry.getKey());
                }
            }
            return map().keySet().removeAll(toRemove);
        }
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        try {
            return super.retainAll(checkNotNull(c));
        } catch (UnsupportedOperationException e) {
            Set<K> toRetain = SetUtil.newHashSet();
            for (Map.Entry<K, V> entry : map().entrySet()) {
                if (c.contains(entry.getValue())) {
                    toRetain.add(entry.getKey());
                }
            }
            return map().keySet().retainAll(toRetain);
        }
    }

    @Override
    public int size() {
        return map().size();
    }

    @Override
    public boolean isEmpty() {
        return map().isEmpty();
    }

    @Override
    public boolean contains( @CheckForNull Object o ) {
        return map().containsValue(o);
    }

    @Override
    public void clear() {
        map().clear();
    }
}

