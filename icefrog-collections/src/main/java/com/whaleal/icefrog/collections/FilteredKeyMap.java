package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author wh
 */
class FilteredKeyMap< K extends Object, V extends Object >
        extends AbstractFilteredMap< K, V > {
    final Predicate< ? super K > keyPredicate;

    FilteredKeyMap(
            Map< K, V > unfiltered,
            Predicate< ? super K > keyPredicate,
            Predicate< ? super Entry< K, V > > entryPredicate ) {
        super(unfiltered, entryPredicate);
        this.keyPredicate = keyPredicate;
    }

    @Override
    protected Set< Entry< K, V > > createEntrySet() {
        return SetUtil.filter(unfiltered.entrySet(), predicate);
    }

    @Override
    Set< K > createKeySet() {
        return SetUtil.filter(unfiltered.keySet(), keyPredicate);
    }

    // The cast is called only when the key is in the unfiltered map, implying
    // that key is a K.
    @Override
    @SuppressWarnings("unchecked")
    public boolean containsKey( @CheckForNull Object key ) {
        return unfiltered.containsKey(key) && keyPredicate.apply((K) key);
    }
}
