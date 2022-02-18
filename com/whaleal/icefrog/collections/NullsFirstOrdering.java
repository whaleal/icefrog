package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.Serializable;


/**
 * An ordering that treats {@code null} as less than all other values.
 */


final class NullsFirstOrdering<T extends Object> extends Ordering<T>
        implements Serializable {
    private static final long serialVersionUID = 0;
    final Ordering<? super T> ordering;

    NullsFirstOrdering( Ordering<? super T> ordering ) {
        this.ordering = ordering;
    }

    @Override
    public int compare( @CheckForNull T left, @CheckForNull T right ) {
        if (left == right) {
            return 0;
        }
        if (left == null) {
            return RIGHT_IS_GREATER;
        }
        if (right == null) {
            return LEFT_IS_GREATER;
        }
        return ordering.compare(left, right);
    }

    @Override
    @SuppressWarnings("nullness") // should be safe, but not sure if we can avoid the warning
    public <S extends T> Ordering<S> reverse() {
        // ordering.reverse() might be optimized, so let it do its thing
        return ordering.reverse().nullsLast();
    }

    @SuppressWarnings("unchecked") // still need the right way to explain this
    @Override
    public <S extends T> Ordering<S> nullsFirst() {
        return (Ordering<S>) this;
    }

    @Override
    public <S extends T> Ordering<S> nullsLast() {
        return ordering.nullsLast();
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object == this) {
            return true;
        }
        if (object instanceof NullsFirstOrdering) {
            NullsFirstOrdering<?> that = (NullsFirstOrdering<?>) object;
            return this.ordering.equals(that.ordering);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ordering.hashCode() ^ 957692532; // meaningless
    }

    @Override
    public String toString() {
        return ordering + ".nullsFirst()";
    }
}
