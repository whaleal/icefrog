package com.whaleal.icefrog.core.collection.set;

/**
 * @author wh
 */

import java.util.AbstractSet;
import java.util.Collection;

import static com.whaleal.icefrog.core.collection.set.SetUtil.removeAllImpl;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * {@link AbstractSet} substitute without the potentially-quadratic {@code removeAll}
 * implementation.
 */
abstract  class ImprovedAbstractSet<E extends Object> extends AbstractSet<E> {
    @Override
    public boolean removeAll( Collection<?> c ) {
        return removeAllImpl(this, c);
    }

    @Override
    public boolean retainAll( Collection<?> c ) {
        return super.retainAll(checkNotNull(c)); // GWT compatibility
    }
}
