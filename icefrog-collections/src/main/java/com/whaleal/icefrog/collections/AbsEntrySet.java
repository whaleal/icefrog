package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
abstract class AbsEntrySet<K extends Object, V extends Object>
        extends SetUtil.ImprovedAbstractSet< Map.Entry<K, V> > {
    abstract Map<K, V> map();

    @Override
    public int size() {
        return map().size();
    }

    @Override
    public void clear() {
        map().clear();
    }

    @Override
    public boolean contains( @CheckForNull Object o ) {
        if (o instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            Object key = entry.getKey();
            V value = MapUtil.safeGet(map(), key);
            return ObjectUtil.equal(value, entry.getValue()) && (value != null || map().containsKey(key));
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return map().isEmpty();
    }

    @Override
    public boolean remove( @CheckForNull Object o ) {
        /*
         * `o instanceof Entry` is guaranteed by `contains`, but we check it here to satisfy our
         * nullness checker.
         */
        if (contains(o) && o instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            return map().keySet().remove(entry.getKey());
        }
        return false;
    }

    @Override
    public boolean removeAll( Collection<?> c ) {
        try {
            return super.removeAll(checkNotNull(c));
        } catch (UnsupportedOperationException e) {
            // if the iterators don't support remove
            return SetUtil.removeAllImpl(this, c.iterator());
        }
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        try {
            return super.retainAll(checkNotNull(c));
        } catch (UnsupportedOperationException e) {
            // if the iterators don't support remove
            Set<Object> keys = SetUtil.newHashSetWithExpectedSize(c.size());
            for (Object o : c) {
                /*
                 * `o instanceof Entry` is guaranteed by `contains`, but we check it here to satisfy our
                 * nullness checker.
                 */
                if (contains(o) && o instanceof Map.Entry) {
                    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                    keys.add(entry.getKey());
                }
            }
            return map().keySet().retainAll(keys);
        }
    }
}
