package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.collections.Multisets.ImmutableEntry;
import com.whaleal.icefrog.core.util.NumberUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.util.Arrays;
import java.util.Collection;

import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;


/**
 * Implementation of {@link ImmutableMultiset} with zero or more elements.
 */

@SuppressWarnings("serial")
        // uses writeReplace(), not default serialization

class RegularImmutableMultiset<E> extends ImmutableMultiset<E> {
    /**
     * Closed addressing tends to perform well even with high load factors. Being conservative here
     * ensures that the table is still likely to be relatively sparse (hence it misses fast) while
     * saving space.
     */
    static final double MAX_LOAD_FACTOR = 1.0;
    /**
     * Maximum allowed false positive probability of detecting a hash flooding attack given random
     * input.
     */
    static final double HASH_FLOODING_FPP = 0.001;
    /**
     * Maximum allowed length of a hash table bucket before falling back to a j.u.HashMap based
     * implementation. Experimentally determined.
     */
    static final int MAX_HASH_BUCKET_LENGTH = 9;
    private static final ImmutableEntry<?>[] EMPTY_ARRAY = new ImmutableEntry<?>[0];
    static final ImmutableMultiset<Object> EMPTY = create(ImmutableList.<Entry<Object>>of());
    private final transient ImmutableEntry<E>[] entries;
    private final transient ImmutableEntry<?>[] hashTable;
    private final transient int size;
    private final transient int hashCode;
    @CheckForNull
    private transient ImmutableSet<E> elementSet;
    private RegularImmutableMultiset(
            ImmutableEntry<E>[] entries,
            ImmutableEntry<?>[] hashTable,
            int size,
            int hashCode,
            @CheckForNull ImmutableSet<E> elementSet ) {
        this.entries = entries;
        this.hashTable = hashTable;
        this.size = size;
        this.hashCode = hashCode;
        this.elementSet = elementSet;
    }

    static <E> ImmutableMultiset<E> create( Collection<? extends Entry<? extends E>> entries ) {
        int distinct = entries.size();
        @SuppressWarnings({"unchecked", "rawtypes"})
        ImmutableEntry<E>[] entryArray = new ImmutableEntry[distinct];
        if (distinct == 0) {
            return new RegularImmutableMultiset<>(entryArray, EMPTY_ARRAY, 0, 0, ImmutableSet.of());
        }
        int tableSize = distinct;
        int mask = tableSize - 1;
        @SuppressWarnings({"unchecked", "rawtypes"})

        ImmutableEntry<E>[] hashTable = new ImmutableEntry[tableSize];

        int index = 0;
        int hashCode = 0;
        long size = 0;
        for (Entry<? extends E> entryWithWildcard : entries) {
            @SuppressWarnings("unchecked") // safe because we only read from it
            Entry<E> entry = (Entry<E>) entryWithWildcard;
            E element = checkNotNull(entry.getElement());
            int count = entry.getCount();
            int hash = element.hashCode();
            int bucket = (int) (0x1b873593 * Integer.rotateLeft((int) (hash * 0xcc9e2d51), 15)) & mask;
            ImmutableEntry<E> bucketHead = hashTable[bucket];
            ImmutableEntry<E> newEntry;
            if (bucketHead == null) {
                boolean canReuseEntry =
                        entry instanceof ImmutableEntry && !(entry instanceof NonTerminalEntry);
                newEntry =
                        canReuseEntry ? (ImmutableEntry<E>) entry : new ImmutableEntry<E>(element, count);
            } else {
                newEntry = new NonTerminalEntry<E>(element, count, bucketHead);
            }
            hashCode += hash ^ count;
            entryArray[index++] = newEntry;
            hashTable[bucket] = newEntry;
            size += count;
        }

        return hashFloodingDetected(hashTable)
                ? JdkBackedImmutableMultiset.create(ImmutableList.asImmutableList(entryArray))
                : new RegularImmutableMultiset<E>(
                entryArray, hashTable, (int) NumberUtil.saturatedCast(size, Integer.class), hashCode, null);
    }

    private static boolean hashFloodingDetected( ImmutableEntry<?>[] hashTable ) {
        for (int i = 0; i < hashTable.length; i++) {
            int bucketLength = 0;
            for (ImmutableEntry<?> entry = hashTable[i]; entry != null; entry = entry.nextInBucket()) {
                bucketLength++;
                if (bucketLength > MAX_HASH_BUCKET_LENGTH) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public int count( @CheckForNull Object element ) {
        ImmutableEntry<?>[] hashTable = this.hashTable;
        if (element == null || hashTable.length == 0) {
            return 0;
        }
        int hash = ObjectUtil.hashCode(element);
        int mask = hashTable.length - 1;
        for (ImmutableEntry<?> entry = hashTable[hash & mask];
             entry != null;
             entry = entry.nextInBucket()) {
            if (ObjectUtil.equal(element, entry.getElement())) {
                return entry.getCount();
            }
        }
        return 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public ImmutableSet<E> elementSet() {
        ImmutableSet<E> result = elementSet;
        return (result == null) ? elementSet = new ElementSet<E>(Arrays.asList(entries), this) : result;
    }

    @Override
    Entry<E> getEntry( int index ) {
        return entries[index];
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    private static final class NonTerminalEntry<E> extends ImmutableEntry<E> {
        private final ImmutableEntry<E> nextInBucket;

        NonTerminalEntry( E element, int count, ImmutableEntry<E> nextInBucket ) {
            super(element, count);
            this.nextInBucket = nextInBucket;
        }

        @Override
        public ImmutableEntry<E> nextInBucket() {
            return nextInBucket;
        }
    }
}
