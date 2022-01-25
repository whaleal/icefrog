package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * List returned by {@link ImmutableCollection#asList} that delegates {@code contains} checks to the
 * backing collection.
 */

@SuppressWarnings("serial")

abstract class ImmutableAsList<E> extends ImmutableList<E> {
    abstract ImmutableCollection<E> delegateCollection();

    @Override
    public boolean contains( @CheckForNull Object target ) {
        // The collection's contains() is at least as fast as ImmutableList's
        // and is often faster.
        return delegateCollection().contains(target);
    }

    @Override
    public int size() {
        return delegateCollection().size();
    }

    @Override
    public boolean isEmpty() {
        return delegateCollection().isEmpty();
    }

    @Override
    boolean isPartialView() {
        return delegateCollection().isPartialView();
    }

    // serialization
    private void readObject( ObjectInputStream stream ) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }

    // serialization
    @Override
    Object writeReplace() {
        return new SerializedForm(delegateCollection());
    }

    /**
     * Serialized form that leads to the same performance as the original list.
     */
    // serialization
    static class SerializedForm implements Serializable {
        private static final long serialVersionUID = 0;
        final ImmutableCollection<?> collection;

        SerializedForm( ImmutableCollection<?> collection ) {
            this.collection = collection;
        }

        Object readResolve() {
            return collection.asList();
        }
    }
}
