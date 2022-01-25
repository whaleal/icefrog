package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @see MapUtil#unmodifiableBiMap(BiMap)
 */
class UnmodifiableBiMap< K extends Object, V extends Object >
        extends ForwardingMap< K, V > implements BiMap< K, V >, Serializable {
    final Map< K, V > unmodifiableMap;
    final BiMap< ? extends K, ? extends V > delegate;
    @RetainedWith
    @CheckForNull
    BiMap< V, K > inverse;
    @CheckForNull
    transient Set< V > values;

    UnmodifiableBiMap( BiMap< ? extends K, ? extends V > delegate, @CheckForNull BiMap< V, K > inverse ) {
        unmodifiableMap = Collections.unmodifiableMap(delegate);
        this.delegate = delegate;
        this.inverse = inverse;
    }

    @Override
    protected Map< K, V > delegate() {
        return unmodifiableMap;
    }

    @Override
    @CheckForNull
    public V forcePut( @ParametricNullness K key, @ParametricNullness V value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BiMap< V, K > inverse() {
        BiMap< V, K > result = inverse;
        return (result == null)
                ? inverse = new UnmodifiableBiMap<>(delegate.inverse(), this)
                : result;
    }

    @Override
    public Set< V > values() {
        Set< V > result = values;
        return (result == null) ? values = Collections.unmodifiableSet(delegate.values()) : result;
    }

    private static final long serialVersionUID = 0;
}
