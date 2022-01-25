package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/**
 * An ordering that orders elements by applying an order to the result of a function on those
 * elements.
 */


final class ByFunctionOrdering<F extends Object, T extends Object>
        extends Ordering<F> implements Serializable {
    private static final long serialVersionUID = 0;
    final Function<F, ? extends T> function;
    final Ordering<T> ordering;

    ByFunctionOrdering( Function<F, ? extends T> function, Ordering<T> ordering ) {
        this.function = checkNotNull(function);
        this.ordering = checkNotNull(ordering);
    }

    @Override
    public int compare( @ParametricNullness F left, @ParametricNullness F right ) {
        return ordering.compare(function.apply(left), function.apply(right));
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object == this) {
            return true;
        }
        if (object instanceof ByFunctionOrdering) {
            ByFunctionOrdering<?, ?> that = (ByFunctionOrdering<?, ?>) object;
            return this.function.equals(that.function) && this.ordering.equals(that.ordering);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hashCode(function, ordering);
    }

    @Override
    public String toString() {
        return ordering + ".onResultOf(" + function + ")";
    }
}
