package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Collections;
import java.util.List;

/**
 * @author wh
 */
public class UnmodifiableListMultimap<
        K extends Object, V extends Object >
        extends UnmodifiableMultimap< K, V > implements ListMultimap< K, V > {
    private static final long serialVersionUID = 0;

    UnmodifiableListMultimap( ListMultimap< K, V > delegate ) {
        super(delegate);
    }

    @Override
    public ListMultimap< K, V > delegate() {
        return (ListMultimap< K, V >) super.delegate();
    }

    @Override
    public List< V > get( @ParametricNullness K key ) {
        return Collections.unmodifiableList(delegate().get(key));
    }

    @Override
    public List< V > removeAll( @CheckForNull Object key ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List< V > replaceValues( @ParametricNullness K key, Iterable< V > values ) {
        throw new UnsupportedOperationException();
    }
}
