package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.Set;

/**
 * @see MapUtil#unmodifiableEntrySet(Set)
 */
class UnmodifiableEntrySet< K extends Object, V extends Object >
        extends UnmodifiableEntries< K, V > implements Set< Map.Entry< K, V > > {
    UnmodifiableEntrySet( Set< Map.Entry< K, V > > entries ) {
        super(entries);
    }

    // See java.util.Collections.UnmodifiableEntrySet for details on attacks.

    @Override
    public boolean equals( @CheckForNull Object object ) {
        return SetUtil.equalsImpl(this, object);
    }

    @Override
    public int hashCode() {
        return SetUtil.hashCodeImpl(this);
    }
}
