package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.convert.Converter2;

import java.io.Serializable;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;

/**
 * A converter that always converts or reverses an object to itself. Note that T is now a
 * "pass-through type".
 */
final class IdentityConverter< T > implements Converter2< T, T > , Serializable {
    static final IdentityConverter< ? > INSTANCE = new IdentityConverter<>();
    private static final long serialVersionUID = 0L;

    protected T doForward( T t ) {
        return t;
    }

    protected T doBackward( T t ) {
        return t;
    }

    public IdentityConverter< T > reverse() {
        return this;
    }


    @Override
    public String toString() {
        return "Converter.identity()";
    }

    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public T convert( T var1 ) throws IllegalArgumentException {
        return var1;
    }
}
