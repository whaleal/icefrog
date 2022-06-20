package com.whaleal.icefrog.collections;


import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Collection;

import static com.whaleal.icefrog.collections.BoundType.CLOSED;
import static com.whaleal.icefrog.core.lang.Precondition.*;
import static java.util.Objects.requireNonNull;

/**
 * An implementation of {@link ContiguousSet} that contains one or more elements.
 */

@SuppressWarnings("unchecked") // allow ungenerified Comparable types

final class RegularContiguousSet<C extends Comparable> extends ContiguousSet<C> {
    private static final long serialVersionUID = 0;
    private final Range<C> range;

    RegularContiguousSet( Range<C> range, DiscreteDomain<C> domain ) {
        super(domain);
        this.range = range;
    }

    private static boolean equalsOrThrow( Comparable<?> left, @CheckForNull Comparable<?> right ) {
        return right != null && Range.compareOrThrow(left, right) == 0;
    }

    private ContiguousSet<C> intersectionInCurrentDomain( Range<C> other ) {
        return range.isConnected(other)
                ? ContiguousSet.create(range.intersection(other), domain)
                : new EmptyContiguousSet<C>(domain);
    }

    @Override
    ContiguousSet<C> headSetImpl( C toElement, boolean inclusive ) {
        return intersectionInCurrentDomain(Range.upTo(toElement, BoundType.forBoolean(inclusive)));
    }

    @Override
    ContiguousSet<C> subSetImpl(
            C fromElement, boolean fromInclusive, C toElement, boolean toInclusive ) {
        if (fromElement.compareTo(toElement) == 0 && !fromInclusive && !toInclusive) {
            // Range would reject our attempt to create (x, x).
            return new EmptyContiguousSet<>(domain);
        }
        return intersectionInCurrentDomain(
                Range.range(
                        fromElement, BoundType.forBoolean(fromInclusive),
                        toElement, BoundType.forBoolean(toInclusive)));
    }

    @Override
    ContiguousSet<C> tailSetImpl( C fromElement, boolean inclusive ) {
        return intersectionInCurrentDomain(Range.downTo(fromElement, BoundType.forBoolean(inclusive)));
    }

    // not used by GWT emulation
    @Override
    int indexOf( @CheckForNull Object target ) {
        // requireNonNull is safe because of the contains check.
        return contains(target) ? (int) domain.distance(first(), (C) requireNonNull(target)) : -1;
    }

    @Override
    public UnmodifiableIterator<C> iterator() {
        return new AbstractSequentialIterator<C>(first()) {
            final C last = last();

            @Override
            @CheckForNull
            protected C computeNext( C previous ) {
                return equalsOrThrow(previous, last) ? null : domain.next(previous);
            }
        };
    }

    // NavigableSet
    @Override
    public UnmodifiableIterator<C> descendingIterator() {
        return new AbstractSequentialIterator<C>(last()) {
            final C first = first();

            @Override
            @CheckForNull
            protected C computeNext( C previous ) {
                return equalsOrThrow(previous, first) ? null : domain.previous(previous);
            }
        };
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public C first() {
        // requireNonNull is safe because we checked the range is not empty in ContiguousSet.create.
        return requireNonNull(range.lowerBound.leastValueAbove(domain));
    }

    @Override
    public C last() {
        // requireNonNull is safe because we checked the range is not empty in ContiguousSet.create.
        return requireNonNull(range.upperBound.greatestValueBelow(domain));
    }

    @Override
    ImmutableList<C> createAsList() {
        if (domain.supportsFastOffset) {
            return new ImmutableAsList<C>() {
                @Override
                ImmutableSortedSet<C> delegateCollection() {
                    return RegularContiguousSet.this;
                }

                @Override
                public C get( int i ) {
                    checkElementIndex(i, size());
                    return domain.offset(first(), i);
                }
            };
        } else {
            return super.createAsList();
        }
    }

    @Override
    public int size() {
        long distance = domain.distance(first(), last());
        return (distance >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) distance + 1;
    }

    @Override
    public boolean contains( @CheckForNull Object object ) {
        if (object == null) {
            return false;
        }
        try {
            return range.contains((C) object);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean containsAll( Collection<?> targets ) {
        return Collections2.containsAllImpl(this, targets);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ContiguousSet<C> intersection( ContiguousSet<C> other ) {
        checkNotNull(other);
        checkArgument(this.domain.equals(other.domain));
        if (other.isEmpty()) {
            return other;
        } else {
            C lowerEndpoint = Ordering.natural().max(this.first(), other.first());
            C upperEndpoint = Ordering.natural().min(this.last(), other.last());
            return (lowerEndpoint.compareTo(upperEndpoint) <= 0)
                    ? ContiguousSet.create(Range.closed(lowerEndpoint, upperEndpoint), domain)
                    : new EmptyContiguousSet<C>(domain);
        }
    }

    @Override
    public Range<C> range() {
        return range(CLOSED, CLOSED);
    }

    @Override
    public Range<C> range( BoundType lowerBoundType, BoundType upperBoundType ) {
        return Range.create(
                range.lowerBound.withLowerBoundType(lowerBoundType, domain),
                range.upperBound.withUpperBoundType(upperBoundType, domain));
    }

    @Override
    public boolean equals( @CheckForNull Object object ) {
        if (object == this) {
            return true;
        } else if (object instanceof RegularContiguousSet) {
            RegularContiguousSet<?> that = (RegularContiguousSet<?>) object;
            if (this.domain.equals(that.domain)) {
                return this.first().equals(that.first()) && this.last().equals(that.last());
            }
        }
        return super.equals(object);
    }

    // copied to make sure not to use the GWT-emulated version
    @Override
    public int hashCode() {
        return SetUtil.hashCodeImpl(this);
    }

    // serialization
    @Override
    Object writeReplace() {
        return new SerializedForm<>(range, domain);
    }

    // serialization
    private static final class SerializedForm<C extends Comparable> implements Serializable {
        final Range<C> range;
        final DiscreteDomain<C> domain;

        private SerializedForm( Range<C> range, DiscreteDomain<C> domain ) {
            this.range = range;
            this.domain = domain;
        }

        private Object readResolve() {
            return new RegularContiguousSet<>(range, domain);
        }
    }
}
