package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * CompactLinkedHashMap is an implementation of a Map with insertion or LRU iteration order,
 * maintained with a doubly linked list through the entries. All optional operations (put and
 * remove) are supported. Null keys and values are supported.
 *
 * <p>{@code containsKey(k)}, {@code put(k, v)} and {@code remove(k)} are all (expected and
 * amortized) constant time operations. Expected in the hashtable sense (depends on the hash
 * function doing a good job of distributing the elements to the buckets to a distribution not far
 * from uniform), and amortized since some operations can trigger a hash table resize.
 *
 * <p>As compared with {@link LinkedHashMap}, this structure places significantly reduced
 * load on the garbage collector by only using a constant number of internal objects.
 *
 * <p>This class should not be assumed to be universally superior to {@code
 * java.util.LinkedHashMap}. Generally speaking, this class reduces object allocation and memory
 * consumption at the price of moderately increased constant factors of CPU. Only use this class
 * when there is a specific reason to prioritize memory over CPU.
 */


class CompactLinkedHashMap<K extends Object, V extends Object>
        extends CompactHashMap<K, V> {
    // TODO(lowasser): implement removeEldestEntry so this can be used as a drop-in replacement

    private static final int ENDPOINT = -2;
    private final boolean accessOrder;
    /**
     * Contains the link pointers corresponding with the entries, in the range of [0, size()). The
     * high 32 bits of each long is the "prev" pointer, whereas the low 32 bits is the "succ" pointer
     * (pointing to the next entry in the linked list). The pointers in [size(), entries.length) are
     * all "null" (UNSET).
     *
     * <p>A node with "prev" pointer equal to {@code ENDPOINT} is the first node in the linked list,
     * and a node with "next" pointer equal to {@code ENDPOINT} is the last node.
     */
    @CheckForNull
    transient long[] links;
    /**
     * Pointer to the first node in the linked list, or {@code ENDPOINT} if there are no entries.
     */
    private transient int firstEntry;
    /**
     * Pointer to the last node in the linked list, or {@code ENDPOINT} if there are no entries.
     */
    private transient int lastEntry;

    CompactLinkedHashMap() {
        this(CompactHashing.DEFAULT_SIZE);
    }

    CompactLinkedHashMap( int expectedSize ) {
        this(expectedSize, false);
    }

    CompactLinkedHashMap( int expectedSize, boolean accessOrder ) {
        super(expectedSize);
        this.accessOrder = accessOrder;
    }

    /**
     * Creates an empty {@code CompactLinkedHashMap} instance.
     */
    public static <K extends Object, V extends Object>
    CompactLinkedHashMap<K, V> create() {
        return new CompactLinkedHashMap<>();
    }

    /**
     * Creates a {@code CompactLinkedHashMap} instance, with a high enough "initial capacity" that it
     * <i>should</i> hold {@code expectedSize} elements without rebuilding internal data structures.
     *
     * @param expectedSize the number of elements you expect to add to the returned set
     * @return a new, empty {@code CompactLinkedHashMap} with enough capacity to hold {@code
     * expectedSize} elements without resizing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    public static <K extends Object, V extends Object>
    CompactLinkedHashMap<K, V> createWithExpectedSize( int expectedSize ) {
        return new CompactLinkedHashMap<>(expectedSize);
    }

    @Override
    void init( int expectedSize ) {
        super.init(expectedSize);
        this.firstEntry = ENDPOINT;
        this.lastEntry = ENDPOINT;
    }

    @Override
    int allocArrays() {
        int expectedSize = super.allocArrays();
        this.links = new long[expectedSize];
        return expectedSize;
    }

    @Override
    Map<K, V> createHashFloodingResistantDelegate( int tableSize ) {
        return new LinkedHashMap<K, V>(tableSize, 1.0f, accessOrder);
    }

    @Override
    Map<K, V> convertToHashFloodingResistantImplementation() {
        Map<K, V> result = super.convertToHashFloodingResistantImplementation();
        links = null;
        return result;
    }

    /*
     * For discussion of the safety of the following methods for operating on predecessors and
     * successors, see the comments near the end of CompactHashMap, noting that the methods here call
     * link(), which is defined at the end of this file.
     */

    private int getPredecessor( int entry ) {
        return ((int) (link(entry) >>> 32)) - 1;
    }

    @Override
    int getSuccessor( int entry ) {
        return ((int) link(entry)) - 1;
    }

    private void setSuccessor( int entry, int succ ) {
        long succMask = (~0L) >>> 32;
        setLink(entry, (link(entry) & ~succMask) | ((succ + 1) & succMask));
    }

    private void setPredecessor( int entry, int pred ) {
        long predMask = ~0L << 32;
        setLink(entry, (link(entry) & ~predMask) | ((long) (pred + 1) << 32));
    }

    private void setSucceeds( int pred, int succ ) {
        if (pred == ENDPOINT) {
            firstEntry = succ;
        } else {
            setSuccessor(pred, succ);
        }

        if (succ == ENDPOINT) {
            lastEntry = pred;
        } else {
            setPredecessor(succ, pred);
        }
    }

    @Override
    void insertEntry(
            int entryIndex, @ParametricNullness K key, @ParametricNullness V value, int hash, int mask ) {
        super.insertEntry(entryIndex, key, value, hash, mask);
        setSucceeds(lastEntry, entryIndex);
        setSucceeds(entryIndex, ENDPOINT);
    }

    @Override
    void accessEntry( int index ) {
        if (accessOrder) {
            // delete from previous position...
            setSucceeds(getPredecessor(index), getSuccessor(index));
            // ...and insert at the end.
            setSucceeds(lastEntry, index);
            setSucceeds(index, ENDPOINT);
            incrementModCount();
        }
    }

    @Override
    void moveLastEntry( int dstIndex, int mask ) {
        int srcIndex = size() - 1;
        super.moveLastEntry(dstIndex, mask);

        setSucceeds(getPredecessor(dstIndex), getSuccessor(dstIndex));
        if (dstIndex < srcIndex) {
            setSucceeds(getPredecessor(srcIndex), dstIndex);
            setSucceeds(dstIndex, getSuccessor(srcIndex));
        }
        setLink(srcIndex, 0);
    }

    @Override
    void resizeEntries( int newCapacity ) {
        super.resizeEntries(newCapacity);
        links = Arrays.copyOf(requireLinks(), newCapacity);
    }

    @Override
    int firstEntryIndex() {
        return firstEntry;
    }

    @Override
    int adjustAfterRemove( int indexBeforeRemove, int indexRemoved ) {
        return (indexBeforeRemove >= size()) ? indexRemoved : indexBeforeRemove;
    }

    @Override
    Set<Entry<K, V>> createEntrySet() {

        class EntrySetImpl extends EntrySetView {
            @Override
            public Spliterator<Entry<K, V>> spliterator() {
                return Spliterators.spliterator(this, Spliterator.ORDERED | Spliterator.DISTINCT);
            }
        }
        return new EntrySetImpl();
    }

    @Override
    Set<K> createKeySet() {

        class KeySetImpl extends KeySetView {
            @Override
            public Object[] toArray() {
                return ObjectArrays.toArrayImpl(this);
            }

            @Override
            @SuppressWarnings("nullness") // b/192354773 in our checker affects toArray declarations
            public <T extends Object> T[] toArray( T[] a ) {
                return ObjectArrays.toArrayImpl(this, a);
            }

            @Override
            public Spliterator<K> spliterator() {
                return Spliterators.spliterator(this, Spliterator.ORDERED | Spliterator.DISTINCT);
            }
        }
        return new KeySetImpl();
    }

    @Override
    Collection<V> createValues() {

        class ValuesImpl extends ValuesView {
            @Override
            public Object[] toArray() {
                return ObjectArrays.toArrayImpl(this);
            }

            @Override
            @SuppressWarnings("nullness") // b/192354773 in our checker affects toArray declarations
            public <T extends Object> T[] toArray( T[] a ) {
                return ObjectArrays.toArrayImpl(this, a);
            }

            @Override
            public Spliterator<V> spliterator() {
                return Spliterators.spliterator(this, Spliterator.ORDERED);
            }
        }
        return new ValuesImpl();
    }

    @Override
    public void clear() {
        if (needsAllocArrays()) {
            return;
        }
        this.firstEntry = ENDPOINT;
        this.lastEntry = ENDPOINT;
        if (links != null) {
            Arrays.fill(links, 0, size(), 0);
        }
        super.clear();
    }

    /*
     * For discussion of the safety of the following methods, see the comments near the end of
     * CompactHashMap.
     */

    private long[] requireLinks() {
        return requireNonNull(links);
    }

    private long link( int i ) {
        return requireLinks()[i];
    }

    private void setLink( int i, long value ) {
        requireLinks()[i] = value;
    }

    /*
     * We don't define getPredecessor+getSuccessor and setPredecessor+setSuccessor here because
     * they're defined above -- including logic to add and subtract 1 to map between the values stored
     * in the predecessor/successor arrays and the indexes in the elements array that they identify.
     */
}
