package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.Serializable;

/**
 * @author wh
 */
final class BiMapConverter< A, B > extends Converter< A, B > implements Serializable {
    private final BiMap< A, B > bimap;

    BiMapConverter( BiMap< A, B > bimap ) {
        this.bimap = checkNotNull(bimap);
    }

    @Override
    protected B doForward( A a ) {
        return convert(bimap, a);
    }

    @Override
    protected A doBackward( B b ) {
        return convert(bimap.inverse(), b);
    }

    private static < X, Y > Y convert( BiMap< X, Y > bimap, X input ) {
        Y output = bimap.get(input);
        checkArgument(output != null, "No non-null mapping present for input: %s", input);
        return output;
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object instanceof BiMapConverter) {
            BiMapConverter< ?, ? > that = (BiMapConverter< ?, ? >) object;
            return this.bimap.equals(that.bimap);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return bimap.hashCode();
    }

    // There's really no good way to implement toString() without printing the entire BiMap, right?
    @Override
    public String toString() {
        return "Maps.asConverter(" + bimap + ")";
    }

    private static final long serialVersionUID = 0L;
}
