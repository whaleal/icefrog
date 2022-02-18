package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.collection.IterUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Implementation of {@link ImmutableSet} backed by a non-empty {@link EnumSet}.
 */

@SuppressWarnings("serial") // we're overriding default serialization

final class ImmutableEnumSet<E extends Enum<E>> extends ImmutableSet<E> {
    /*
     * Notes on EnumSet and <E extends Enum<E>>:
     *
     * This class isn't an arbitrary ForwardingImmutableSet because we need to
     * know that calling {@code clone()} during deserialization will return an
     * object that no one else has a reference to, allowing us to guarantee
     * immutability. Hence, we support only {@link EnumSet}.
     */
    private final transient EnumSet<E> delegate;
    private transient int hashCode;

    private ImmutableEnumSet( EnumSet<E> delegate ) {
        this.delegate = delegate;
    }

    @SuppressWarnings("rawtypes") // necessary to compile against Java 8
    static ImmutableSet asImmutable( EnumSet set ) {
        switch (set.size()) {
            case 0:
                return ImmutableSet.of();
            case 1:
                return ImmutableSet.of(IterUtil.getOnlyElement(set));
            default:
                return new ImmutableEnumSet(set);
        }
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.unmodifiableIterator(delegate.iterator());
    }

    @Override
    public Spliterator<E> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public void forEach( Consumer<? super E> action ) {
        delegate.forEach(action);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean contains( @CheckForNull Object object ) {
        return delegate.contains(object);
    }

    @Override
    public boolean containsAll( Collection<?> collection ) {
        if (collection instanceof ImmutableEnumSet<?>) {
            collection = ((ImmutableEnumSet<?>) collection).delegate;
        }
        return delegate.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object == this) {
            return true;
        }
        if (object instanceof ImmutableEnumSet) {
            object = ((ImmutableEnumSet<?>) object).delegate;
        }
        return delegate.equals(object);
    }

    @Override
    boolean isHashCodeFast() {
        return true;
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        return (result == 0) ? hashCode = delegate.hashCode() : result;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    // All callers of the constructor are restricted to <E extends Enum<E>>.
    @Override
    Object writeReplace() {
        return new EnumSerializedForm<E>(delegate);
    }

    /*
     * This class is used to serialize ImmutableEnumSet instances.
     */
    private static class EnumSerializedForm<E extends Enum<E>> implements Serializable {
        private static final long serialVersionUID = 0;
        final EnumSet<E> delegate;

        EnumSerializedForm( EnumSet<E> delegate ) {
            this.delegate = delegate;
        }

        Object readResolve() {
            // EJ2 #76: Write readObject() methods defensively.
            return new ImmutableEnumSet<E>(delegate.clone());
        }
    }
}
