package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.util.ArrayUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;


/**
 * An ordering that tries several comparators in order.
 */


final class CompoundOrdering<T extends Object> extends Ordering<T>
        implements Serializable {
    private static final long serialVersionUID = 0;
    final Comparator<? super T>[] comparators;

    CompoundOrdering( Comparator<? super T> primary, Comparator<? super T> secondary ) {
        this.comparators = (Comparator<? super T>[]) new Comparator[]{primary, secondary};
    }

    CompoundOrdering( Iterable<? extends Comparator<? super T>> comparators ) {
        this.comparators = ArrayUtil.toArray(comparators, new Comparator[0]);
    }

    @Override
    public int compare( @ParametricNullness T left, @ParametricNullness T right ) {
        for (int i = 0; i < comparators.length; i++) {
            int result = comparators[i].compare(left, right);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object == this) {
            return true;
        }
        if (object instanceof CompoundOrdering) {
            CompoundOrdering<?> that = (CompoundOrdering<?>) object;
            return Arrays.equals(this.comparators, that.comparators);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(comparators);
    }

    @Override
    public String toString() {
        return "Ordering.compound(" + Arrays.toString(comparators) + ")";
    }
}
