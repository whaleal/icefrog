package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.convert.Converter2;

import javax.annotation.CheckForNull;
import java.io.Serializable;

/**
 * @author wh
 */
final class ConverterComposition< A, B, C > implements Converter2< A, C > ,Serializable {
    private static final long serialVersionUID = 0L;
    final Converter2< A, B > first;
    final Converter2< B, C > second;

    /*
     * These gymnastics are a little confusing. Basically this class has neither legacy nor
     * non-legacy behavior; it just needs to let the behaviors of the backing converters shine
     * through (which might even differ from each other!). So, we override the correctedDo* methods,
     * after which the do* methods should never be reached.
     */

    ConverterComposition( Converter2< A, B > first, Converter2< B, C > second ) {
        this.first = first;
        this.second = second;
    }


    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object instanceof ConverterComposition) {
            ConverterComposition< ?, ?, ? > that = (ConverterComposition< ?, ?, ? >) object;
            return this.first.equals(that.first) && this.second.equals(that.second);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * first.hashCode() + second.hashCode();
    }

    @Override
    public String toString() {
        return first + ".andThen(" + second + ")";
    }

    @Override
    public C convert( A var1 ) throws IllegalArgumentException {
        B convert = first.convert(var1);
        return second.convert(convert);
    }
}
