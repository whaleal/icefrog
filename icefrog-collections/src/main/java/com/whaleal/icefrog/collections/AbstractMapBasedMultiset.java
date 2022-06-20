package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.NumberUtil;

import javax.annotation.CheckForNull;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.ObjIntConsumer;

import static com.whaleal.icefrog.core.lang.Precondition.*;
import static java.util.Objects.requireNonNull;


/**
 * Basic implementation of {@code Multiset<E>} backed by an instance of {@code Map<E, Count>}.
 *
 * <p>For serialization to work, the subclass must specify explicit {@code readObject} and {@code
 * writeObject} methods.
 */


abstract class AbstractMapBasedMultiset<E extends Object> extends AbstractMultiset<E>
        implements Serializable {
    // not needed in emulated source.
    private static final long serialVersionUID = -2250766705698539974L;
    // TODO(lowasser): consider overhauling this back to Map<E, Integer>
    private transient Map<E, Count> backingMap;
    /*
     * Cache the size for efficiency. Using a long lets us avoid the need for
     * overflow checking and ensures that size() will function correctly even if
     * the multiset had once been larger than Integer.MAX_VALUE.
     */
    private transient long size;

    /**
     * Standard constructor.
     */
    protected AbstractMapBasedMultiset( Map<E, Count> backingMap ) {
        checkArgument(backingMap.isEmpty());
        this.backingMap = backingMap;
    }

    // Required Implementations

    private static int getAndSet( @CheckForNull Count i, int count ) {
        if (i == null) {
            return 0;
        }

        return i.getAndSet(count);
    }

    /**
     * Used during deserialization only. The backing map must be empty.
     */
    void setBackingMap( Map<E, Count> backingMap ) {
        this.backingMap = backingMap;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Invoking {@link Entry#getCount} on an entry in the returned set always returns the
     * current count of that element in the multiset, as opposed to the count at the time the entry
     * was retrieved.
     */
    @Override
    public Set<Entry<E>> entrySet() {
        return super.entrySet();
    }

    @Override
    Iterator<E> elementIterator() {
        final Iterator<Map.Entry<E, Count>> backingEntries = backingMap.entrySet().iterator();
        return new Iterator<E>() {
            @CheckForNull
            Map.Entry<E, Count> toRemove;

            @Override
            public boolean hasNext() {
                return backingEntries.hasNext();
            }

            @Override
            @ParametricNullness
            public E next() {
                final Map.Entry<E, Count> mapEntry = backingEntries.next();
                toRemove = mapEntry;
                return mapEntry.getKey();
            }

            @Override
            public void remove() {
                checkState(toRemove != null, "no calls to next() since the last call to remove()");
                size -= toRemove.getValue().getAndSet(0);
                backingEntries.remove();
                toRemove = null;
            }
        };
    }

    @Override
    Iterator<Entry<E>> entryIterator() {
        final Iterator<Map.Entry<E, Count>> backingEntries = backingMap.entrySet().iterator();
        return new Iterator<Entry<E>>() {
            @CheckForNull
            Map.Entry<E, Count> toRemove;

            @Override
            public boolean hasNext() {
                return backingEntries.hasNext();
            }

            @Override
            public Entry<E> next() {
                final Map.Entry<E, Count> mapEntry = backingEntries.next();
                toRemove = mapEntry;
                return new Multisets.AbstractEntry<E>() {
                    @Override
                    @ParametricNullness
                    public E getElement() {
                        return mapEntry.getKey();
                    }

                    @Override
                    public int getCount() {
                        Count count = mapEntry.getValue();
                        if (count == null || count.get() == 0) {
                            Count frequency = backingMap.get(getElement());
                            if (frequency != null) {
                                return frequency.get();
                            }
                        }
                        return (count == null) ? 0 : count.get();
                    }
                };
            }

            @Override
            public void remove() {
                checkState(toRemove != null, "no calls to next() since the last call to remove()");
                size -= toRemove.getValue().getAndSet(0);
                backingEntries.remove();
                toRemove = null;
            }
        };
    }

    @Override
    public void forEachEntry( ObjIntConsumer<? super E> action ) {
        checkNotNull(action);
        backingMap.forEach(( element, count ) -> action.accept(element, count.get()));
    }

    // Optimizations - Query Operations

    @Override
    public void clear() {
        for (Count frequency : backingMap.values()) {
            frequency.set(0);
        }
        backingMap.clear();
        size = 0L;
    }

    @Override
    int distinctElements() {
        return backingMap.size();
    }

    @Override
    public int size() {
        return (int) NumberUtil.saturatedCast(size, Integer.class);
    }

    @Override
    public Iterator<E> iterator() {
        return new MapBasedMultisetIterator();
    }

    // Optional Operations - Modification Operations

    @Override
    public int count( @CheckForNull Object element ) {
        Count frequency = MapUtil.safeGet(backingMap, element);
        return (frequency == null) ? 0 : frequency.get();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the call would result in more than {@link
     *                                  Integer#MAX_VALUE} occurrences of {@code element} in this multiset.
     */

    @Override
    public int add( @ParametricNullness E element, int occurrences ) {
        if (occurrences == 0) {
            return count(element);
        }
        checkArgument(occurrences > 0, "occurrences cannot be negative: %s", occurrences);
        Count frequency = backingMap.get(element);
        int oldCount;
        if (frequency == null) {
            oldCount = 0;
            backingMap.put(element, new Count(occurrences));
        } else {
            oldCount = frequency.get();
            long newCount = (long) oldCount + (long) occurrences;
            checkArgument(newCount <= Integer.MAX_VALUE, "too many occurrences: %s", newCount);
            frequency.add(occurrences);
        }
        size += occurrences;
        return oldCount;
    }

    // Roughly a 33% performance improvement over AbstractMultiset.setCount().

    @Override
    public int remove( @CheckForNull Object element, int occurrences ) {
        if (occurrences == 0) {
            return count(element);
        }
        checkArgument(occurrences > 0, "occurrences cannot be negative: %s", occurrences);
        Count frequency = backingMap.get(element);
        if (frequency == null) {
            return 0;
        }

        int oldCount = frequency.get();

        int numberRemoved;
        if (oldCount > occurrences) {
            numberRemoved = occurrences;
        } else {
            numberRemoved = oldCount;
            backingMap.remove(element);
        }

        frequency.add(-numberRemoved);
        size -= numberRemoved;
        return oldCount;
    }

    @Override
    public int setCount( @ParametricNullness E element, int count ) {
        checkNonnegative(count, "count");

        Count existingCounter;
        int oldCount;
        if (count == 0) {
            existingCounter = backingMap.remove(element);
            oldCount = getAndSet(existingCounter, count);
        } else {
            existingCounter = backingMap.get(element);
            oldCount = getAndSet(existingCounter, count);

            if (existingCounter == null) {
                backingMap.put(element, new Count(count));
            }
        }

        size += (count - oldCount);
        return oldCount;
    }

    // Don't allow default serialization.
    // java.io.ObjectStreamException
    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("Stream data required");
    }

    /*
     * Not subclassing AbstractMultiset$MultisetIterator because next() needs to
     * retrieve the Map.Entry<E, Count> entry, which can then be used for
     * a more efficient remove() call.
     */
    private class MapBasedMultisetIterator implements Iterator<E> {
        final Iterator<Map.Entry<E, Count>> entryIterator;
        @CheckForNull
        Map.Entry<E, Count> currentEntry;
        int occurrencesLeft;
        boolean canRemove;

        MapBasedMultisetIterator() {
            this.entryIterator = backingMap.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return occurrencesLeft > 0 || entryIterator.hasNext();
        }

        @Override
        @ParametricNullness
        public E next() {
            if (occurrencesLeft == 0) {
                currentEntry = entryIterator.next();
                occurrencesLeft = currentEntry.getValue().get();
            }
            occurrencesLeft--;
            canRemove = true;
            /*
             * requireNonNull is safe because occurrencesLeft starts at 0, forcing us to initialize
             * currentEntry above. After that, we never clear it.
             */
            return requireNonNull(currentEntry).getKey();
        }

        @Override
        public void remove() {
            checkRemove(canRemove);
            /*
             * requireNonNull is safe because canRemove is set to true only after we initialize
             * currentEntry (which we never subsequently clear).
             */
            int frequency = requireNonNull(currentEntry).getValue().get();
            if (frequency <= 0) {
                throw new ConcurrentModificationException();
            }
            if (currentEntry.getValue().addAndGet(-1) == 0) {
                entryIterator.remove();
            }
            size--;
            canRemove = false;
        }
    }
}
