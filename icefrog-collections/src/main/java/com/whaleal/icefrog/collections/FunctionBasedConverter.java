package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.convert.Converter2;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
final class FunctionBasedConverter< A, B > implements Converter2< A, B >
        , Serializable {
    private final Function< ? super A, ? extends B > forwardFunction;
    private final Function< ? super B, ? extends A > backwardFunction;

    FunctionBasedConverter(
            Function< ? super A, ? extends B > forwardFunction,
            Function< ? super B, ? extends A > backwardFunction ) {
        this.forwardFunction = checkNotNull(forwardFunction);
        this.backwardFunction = checkNotNull(backwardFunction);
    }


    protected B doForward( A a ) {
        return forwardFunction.apply(a);
    }


    protected A doBackward( B b ) {
        return backwardFunction.apply(b);
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object instanceof FunctionBasedConverter) {
            FunctionBasedConverter< ?, ? > that = (FunctionBasedConverter< ?, ? >) object;
            return this.forwardFunction.equals(that.forwardFunction)
                    && this.backwardFunction.equals(that.backwardFunction);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return forwardFunction.hashCode() * 31 + backwardFunction.hashCode();
    }

    @Override
    public String toString() {
        return "Converter.from(" + forwardFunction + ", " + backwardFunction + ")";
    }

    @Override
    public B convert( A var1 ) throws IllegalArgumentException {
        return doForward(var1);
    }
}
