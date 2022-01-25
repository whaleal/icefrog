package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.lang.Precondition;

import javax.annotation.CheckForNull;


/**
 * Implementation of {@link ImmutableSet} with exactly one element.
 *
 * @author Nick Kralevich
 */

@SuppressWarnings("serial") // uses writeReplace(), not default serialization

final class SingletonImmutableSet<E> extends ImmutableSet<E> {
    // We deliberately avoid caching the asList and hashCode here, to ensure that with
    // compressed oops, a SingletonImmutableSet packs all the way down to the optimal 16 bytes.

    final transient E element;

    SingletonImmutableSet( E element ) {
        this.element = Precondition.checkNotNull(element);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean contains( @CheckForNull Object target ) {
        return element.equals(target);
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return IterUtil.singletonIterator(element);
    }

    @Override
    public ImmutableList<E> asList() {
        return ImmutableList.of(element);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    int copyIntoArray( Object[] dst, int offset ) {
        dst[offset] = element;
        return offset + 1;
    }

    @Override
    public final int hashCode() {
        return element.hashCode();
    }

    @Override
    public String toString() {
        return '[' + element.toString() + ']';
    }
}
