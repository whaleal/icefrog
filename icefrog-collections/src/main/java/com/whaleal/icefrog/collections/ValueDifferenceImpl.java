package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;

/**
 * @author wh
 */
class ValueDifferenceImpl< V extends Object >
        implements MapDifference.ValueDifference< V > {
    @ParametricNullness
    private final V left;
    @ParametricNullness
    private final V right;

    static < V extends Object > MapDifference.ValueDifference< V > create(
            @ParametricNullness V left, @ParametricNullness V right ) {
        return new ValueDifferenceImpl< V >(left, right);
    }

    private ValueDifferenceImpl( @ParametricNullness V left, @ParametricNullness V right ) {
        this.left = left;
        this.right = right;
    }

    @Override
    @ParametricNullness
    public V leftValue() {
        return left;
    }

    @Override
    @ParametricNullness
    public V rightValue() {
        return right;
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object instanceof MapDifference.ValueDifference) {
            MapDifference.ValueDifference< ? > that = (MapDifference.ValueDifference< ? >) object;
            return ObjectUtil.equals(this.left, that.leftValue())
                    && ObjectUtil.equals(this.right, that.rightValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ObjectUtil.hashCode(left, right);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + right + ")";
    }
}
