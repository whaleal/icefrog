package com.whaleal.icefrog.core.collection.set;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.Consumer;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
public class KeySet<K extends Object, V extends Object>
        extends ImprovedAbstractSet<K> {
    final Map<K, V> map;

    protected KeySet( Map<K, V> map ) {
        this.map = checkNotNull(map);
    }

    protected Map<K, V> map() {
        return map;
    }

    @Override
    public Iterator<K> iterator() {
        return map().keySet().iterator();
    }

    @Override
    public void forEach( Consumer<? super K> action ) {
        checkNotNull(action);
        // avoids entry allocation for those maps that allocate entries on iteration
        map.forEach(( k, v ) -> action.accept(k));
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
        return map().containsKey(o);
    }

    @Override
    public boolean remove( @CheckForNull Object o ) {
        if (contains(o)) {
            map().remove(o);
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        map().clear();
    }
}

