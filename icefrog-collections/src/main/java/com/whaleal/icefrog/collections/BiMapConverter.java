package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.convert.Converter2;
import com.whaleal.icefrog.core.map.BiMap;

import javax.annotation.CheckForNull;
import java.io.Serializable;

import static com.whaleal.icefrog.core.lang.Precondition.checkArgument;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * @author wh
 */
final class BiMapConverter< A, B > implements Converter2< A, B > , Serializable {
    private static final long serialVersionUID = 0L;
    private final BiMap< A, B > bimap;

    BiMapConverter( BiMap< A, B > bimap ) {
        this.bimap = checkNotNull(bimap);
    }

    private static < X, Y > Y convert( BiMap< X, Y > bimap, X input ) {
        Y output = bimap.get(input);
        checkArgument(output != null, "No non-null mapping present for input: %s", input);
        return output;
    }


    protected B doForward( A a ) {
        return convert(bimap, a);
    }


    protected A doBackward( B b ) {
        return convert(new BiMap<>(bimap.getInverse()), b);
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

    @Override
    public B convert( A var1 ) throws IllegalArgumentException {
        return convert(bimap,var1);
    }
}
