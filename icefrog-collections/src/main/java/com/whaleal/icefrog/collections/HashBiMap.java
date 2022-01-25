package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.BiMap;
import com.whaleal.icefrog.core.util.NumberUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.whaleal.icefrog.core.lang.Precondition.*;
import static java.util.Objects.requireNonNull;
import com.whaleal.icefrog.core.map.MapUtil;

/**
 * 本质 是个HasMap + BiMap
 * <p>
 * HashBiMap 可以存储null值  并同时具备 Bimap 的限制
 * <p>
 * 因为与 HashMap 相关 ，其存储的值的顺序不能保证。
 *
 * @author wh
 */


public final class HashBiMap<K extends Object, V extends Object>
        extends BiMap<K, V> {

    private static final double LOAD_FACTOR = 1.0;
    // Not needed in emulated source
    private static final long serialVersionUID = 0;
    /*
     * The following two arrays may *contain* nulls, but they are never *themselves* null: Even though
     * they are not initialized inline in the constructor, they are initialized from init(), which the
     * constructor calls (as does readObject()).
     */
    private transient BiEntry<K, V>[] hashTableKToV;
    private transient BiEntry<K, V>[] hashTableVToK;
    @CheckForNull
    private transient BiEntry<K, V> firstInKeyInsertionOrder;
    @CheckForNull
    private transient BiEntry<K, V> lastInKeyInsertionOrder;
    private transient int size;
    private transient int mask;
    private transient int modCount;
    @CheckForNull
    private transient BiMap<V, K> inverse;

    private HashBiMap( int expectedSize ) {
        super(com.whaleal.icefrog.core.map.MapUtil.newHashMap(expectedSize));
        init(expectedSize);
    }

    /**
     * Returns a new, empty {@code HashBiMap} with the default initial capacity (16).
     */
    public static <K extends Object, V extends Object> HashBiMap<K, V> create() {
        return create(16);
    }

    /**
     * Constructs a new, empty bimap with the specified expected size.
     *
     * @param expectedSize the expected number of entries
     * @throws IllegalArgumentException if the specified expected size is negative
     */
    public static <K extends Object, V extends Object> HashBiMap<K, V> create(
            int expectedSize ) {
        return new HashBiMap<>(expectedSize);
    }

    /**
     * Constructs a new bimap containing initial values from {@code map}. The bimap is created with an
     * initial capacity sufficient to hold the mappings in the specified map.
     */
    public static <K extends Object, V extends Object> HashBiMap<K, V> create(
            Map<? extends K, ? extends V> map ) {
        HashBiMap<K, V> bimap = create(map.size());
        bimap.putAll(map);
        return bimap;
    }

    private void init( int expectedSize ) {
        checkNonnegative(expectedSize, "expectedSize");
        int tableSize = expectedSize;
        this.hashTableKToV = createTable(tableSize);
        this.hashTableVToK = createTable(tableSize);
        this.firstInKeyInsertionOrder = null;
        this.lastInKeyInsertionOrder = null;
        this.size = 0;
        this.mask = tableSize - 1;
        this.modCount = 0;
    }

    /**
     * Finds and removes {@code entry} from the bucket linked lists in both the key-to-value direction
     * and the value-to-key direction.
     */
    @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH", "NP_NULL_ON_SOME_PATH"})
    private void delete( BiEntry<K, V> entry ) {
        int keyBucket = entry.keyHash & mask;
        BiEntry<K, V> prevBucketEntry = null;
        for (BiEntry<K, V> bucketEntry = hashTableKToV[keyBucket];
             true;
             bucketEntry = bucketEntry.nextInKToVBucket) {
            if (bucketEntry == entry) {
                if (prevBucketEntry == null) {
                    hashTableKToV[keyBucket] = entry.nextInKToVBucket;
                } else {
                    prevBucketEntry.nextInKToVBucket = entry.nextInKToVBucket;
                }
                break;
            }
            prevBucketEntry = bucketEntry;
        }

        int valueBucket = entry.valueHash & mask;
        prevBucketEntry = null;
        for (BiEntry<K, V> bucketEntry = hashTableVToK[valueBucket];
             true;
             bucketEntry = bucketEntry.nextInVToKBucket) {
            if (bucketEntry == entry) {
                if (prevBucketEntry == null) {
                    hashTableVToK[valueBucket] = entry.nextInVToKBucket;
                } else {
                    prevBucketEntry.nextInVToKBucket = entry.nextInVToKBucket;
                }
                break;
            }
            prevBucketEntry = bucketEntry;
        }

        if (entry.prevInKeyInsertionOrder == null) {
            firstInKeyInsertionOrder = entry.nextInKeyInsertionOrder;
        } else {
            entry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = entry.nextInKeyInsertionOrder;
        }

        if (entry.nextInKeyInsertionOrder == null) {
            lastInKeyInsertionOrder = entry.prevInKeyInsertionOrder;
        } else {
            entry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = entry.prevInKeyInsertionOrder;
        }

        size--;
        modCount++;
    }

    private void insert( BiEntry<K, V> entry, @CheckForNull BiEntry<K, V> oldEntryForKey ) {
        int keyBucket = entry.keyHash & mask;
        entry.nextInKToVBucket = hashTableKToV[keyBucket];
        hashTableKToV[keyBucket] = entry;

        int valueBucket = entry.valueHash & mask;
        entry.nextInVToKBucket = hashTableVToK[valueBucket];
        hashTableVToK[valueBucket] = entry;

        if (oldEntryForKey == null) {
            entry.prevInKeyInsertionOrder = lastInKeyInsertionOrder;
            entry.nextInKeyInsertionOrder = null;
            if (lastInKeyInsertionOrder == null) {
                firstInKeyInsertionOrder = entry;
            } else {
                lastInKeyInsertionOrder.nextInKeyInsertionOrder = entry;
            }
            lastInKeyInsertionOrder = entry;
        } else {
            entry.prevInKeyInsertionOrder = oldEntryForKey.prevInKeyInsertionOrder;
            if (entry.prevInKeyInsertionOrder == null) {
                firstInKeyInsertionOrder = entry;
            } else {
                entry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = entry;
            }
            entry.nextInKeyInsertionOrder = oldEntryForKey.nextInKeyInsertionOrder;
            if (entry.nextInKeyInsertionOrder == null) {
                lastInKeyInsertionOrder = entry;
            } else {
                entry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = entry;
            }
        }

        size++;
        modCount++;
    }

    @CheckForNull
    private BiEntry<K, V> seekByKey( @CheckForNull Object key, int keyHash ) {
        for (BiEntry<K, V> entry = hashTableKToV[keyHash & mask];
             entry != null;
             entry = entry.nextInKToVBucket) {
            if (keyHash == entry.keyHash && ObjectUtil.equal(key, entry.key)) {
                return entry;
            }
        }
        return null;
    }

    @CheckForNull
    private BiEntry<K, V> seekByValue( @CheckForNull Object value, int valueHash ) {
        for (BiEntry<K, V> entry = hashTableVToK[valueHash & mask];
             entry != null;
             entry = entry.nextInVToKBucket) {
            if (valueHash == entry.valueHash && ObjectUtil.equal(value, entry.value)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey( @CheckForNull Object key ) {
        return seekByKey(key, ObjectUtil.hashCode(key)) != null;
    }

    /**
     * Returns {@code true} if this BiMap contains an entry whose value is equal to {@code value} (or,
     * equivalently, if this inverse view contains a key that is equal to {@code value}).
     *
     * <p>Due to the property that values in a BiMap are unique, this will tend to execute in
     * faster-than-linear time.
     *
     * @param value the object to search for in the values of this BiMap
     * @return true if a mapping exists from a key to the specified value
     */
    @Override
    public boolean containsValue( @CheckForNull Object value ) {
        return seekByValue(value, ObjectUtil.hashCode(value)) != null;
    }

    @Override
    @CheckForNull
    public V get( @CheckForNull Object key ) {
        return com.whaleal.icefrog.core.map.MapUtil.valueOrNull(seekByKey(key, ObjectUtil.hashCode(key)));
    }

    @Override
    @CheckForNull
    public V put( @ParametricNullness K key, @ParametricNullness V value ) {
        return put(key, value, true);
    }

    @CheckForNull
    private V put( @ParametricNullness K key, @ParametricNullness V value, boolean force ) {
        int keyHash = ObjectUtil.hashCode(key);
        int valueHash = ObjectUtil.hashCode(value);

        BiEntry<K, V> oldEntryForKey = seekByKey(key, keyHash);
        if (oldEntryForKey != null
                && valueHash == oldEntryForKey.valueHash
                && ObjectUtil.equal(value, oldEntryForKey.value)) {
            return value;
        }

        BiEntry<K, V> oldEntryForValue = seekByValue(value, valueHash);
        if (oldEntryForValue != null) {
            if (force) {
                delete(oldEntryForValue);
            } else {
                throw new IllegalArgumentException("value already present: " + value);
            }
        }

        BiEntry<K, V> newEntry = new BiEntry<>(key, keyHash, value, valueHash);
        if (oldEntryForKey != null) {
            delete(oldEntryForKey);
            insert(newEntry, oldEntryForKey);
            oldEntryForKey.prevInKeyInsertionOrder = null;
            oldEntryForKey.nextInKeyInsertionOrder = null;
            return oldEntryForKey.value;
        } else {
            insert(newEntry, null);
            rehashIfNecessary();
            return null;
        }
    }

    @CheckForNull
    private K putInverse( @ParametricNullness V value, @ParametricNullness K key, boolean force ) {
        int valueHash = ObjectUtil.hashCode(value);
        int keyHash = ObjectUtil.hashCode(key);

        BiEntry<K, V> oldEntryForValue = seekByValue(value, valueHash);
        BiEntry<K, V> oldEntryForKey = seekByKey(key, keyHash);
        if (oldEntryForValue != null
                && keyHash == oldEntryForValue.keyHash
                && ObjectUtil.equal(key, oldEntryForValue.key)) {
            return key;
        } else if (oldEntryForKey != null && !force) {
            throw new IllegalArgumentException("key already present: " + key);
        }

        /*
         * The ordering here is important: if we deleted the key entry and then the value entry,
         * the key entry's prev or next pointer might point to the dead value entry, and when we
         * put the new entry in the key entry's position in iteration order, it might invalidate
         * the linked list.
         */

        if (oldEntryForValue != null) {
            delete(oldEntryForValue);
        }

        if (oldEntryForKey != null) {
            delete(oldEntryForKey);
        }

        BiEntry<K, V> newEntry = new BiEntry<>(key, keyHash, value, valueHash);
        insert(newEntry, oldEntryForKey);

        if (oldEntryForKey != null) {
            oldEntryForKey.prevInKeyInsertionOrder = null;
            oldEntryForKey.nextInKeyInsertionOrder = null;
        }
        if (oldEntryForValue != null) {
            oldEntryForValue.prevInKeyInsertionOrder = null;
            oldEntryForValue.nextInKeyInsertionOrder = null;
        }
        rehashIfNecessary();
        return com.whaleal.icefrog.core.map.MapUtil.keyOrNull(oldEntryForValue);
    }

    private void rehashIfNecessary() {
        BiEntry<K, V>[] oldKToV = hashTableKToV;
        boolean flag = size > LOAD_FACTOR * oldKToV.length && oldKToV.length < NumberUtil.MAX_POWER_OF_TWO;
        if (flag) {
            int newTableSize = oldKToV.length * 2;

            this.hashTableKToV = createTable(newTableSize);
            this.hashTableVToK = createTable(newTableSize);
            this.mask = newTableSize - 1;
            this.size = 0;

            for (BiEntry<K, V> entry = firstInKeyInsertionOrder;
                 entry != null;
                 entry = entry.nextInKeyInsertionOrder) {
                insert(entry, entry);
            }
            this.modCount++;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private BiEntry<K, V>[] createTable( int length ) {
        return new BiEntry[length];
    }

    @Override
    @CheckForNull
    public V remove( @CheckForNull Object key ) {
        BiEntry<K, V> entry = seekByKey(key, ObjectUtil.hashCode(key));
        if (entry == null) {
            return null;
        } else {
            delete(entry);
            entry.prevInKeyInsertionOrder = null;
            entry.nextInKeyInsertionOrder = null;
            return entry.value;
        }
    }

    @Override
    public void clear() {
        size = 0;
        Arrays.fill(hashTableKToV, null);
        Arrays.fill(hashTableVToK, null);
        firstInKeyInsertionOrder = null;
        lastInKeyInsertionOrder = null;
        modCount++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Set<K> keySet() {
        return new KeySet();
    }

    @Override
    public Set<V> values() {
        return inverse().keySet();
    }

    Iterator<Entry<K, V>> entryIterator() {
        return new Itr<Entry<K, V>>() {
            @Override
            Entry<K, V> output( BiEntry<K, V> entry ) {
                return new MapEntry(entry);
            }

            class MapEntry extends AbstractMapEntry<K, V> {
                BiEntry<K, V> delegate;

                MapEntry( BiEntry<K, V> entry ) {
                    this.delegate = entry;
                }

                @Override
                public K getKey() {
                    return delegate.key;
                }

                @Override
                public V getValue() {
                    return delegate.value;
                }

                @Override
                public V setValue( V value ) {
                    V oldValue = delegate.value;
                    int valueHash = ObjectUtil.hashCode(value);
                    if (valueHash == delegate.valueHash && ObjectUtil.equal(value, oldValue)) {
                        return value;
                    }
                    checkArgument(seekByValue(value, valueHash) == null, "value already present: %s", value);
                    delete(delegate);
                    BiEntry<K, V> newEntry = new BiEntry<>(delegate.key, delegate.keyHash, value, valueHash);
                    insert(newEntry, delegate);
                    delegate.prevInKeyInsertionOrder = null;
                    delegate.nextInKeyInsertionOrder = null;
                    expectedModCount = modCount;
                    if (toRemove == delegate) {
                        toRemove = newEntry;
                    }
                    delegate = newEntry;
                    return oldValue;
                }
            }
        };
    }

    @Override
    public void forEach( BiConsumer<? super K, ? super V> action ) {
        checkNotNull(action);
        for (BiEntry<K, V> entry = firstInKeyInsertionOrder;
             entry != null;
             entry = entry.nextInKeyInsertionOrder) {
            action.accept(entry.key, entry.value);
        }
    }

    @Override
    public void replaceAll( BiFunction<? super K, ? super V, ? extends V> function ) {
        checkNotNull(function);
        BiEntry<K, V> oldFirst = firstInKeyInsertionOrder;
        clear();
        for (BiEntry<K, V> entry = oldFirst; entry != null; entry = entry.nextInKeyInsertionOrder) {
            put(entry.key, function.apply(entry.key, entry.value));
        }
    }

    public BiMap<V, K> inverse() {
        BiMap<V, K> result = inverse;
        return (result == null) ? new BiMap<>(new HashMap<>()) : result;
    }

    /**
     * @serialData the number of entries, first key, first value, second key, second value, and so on.
     */
    // java.io.ObjectOutputStream
    private void writeObject( ObjectOutputStream stream ) throws IOException {
        stream.defaultWriteObject();
        Serialization.writeMap(this, stream);
    }

    // java.io.ObjectInputStream
    private void readObject( ObjectInputStream stream ) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int size = Serialization.readCount(stream);
        init(16); // resist hostile attempts to allocate gratuitous heap
        Serialization.populateMap(this, stream, size);
    }

    private static final class BiEntry<K extends Object, V extends Object>
            extends ImmutableEntry<K, V> {
        final int keyHash;
        final int valueHash;

        // All BiEntry instances are strongly reachable from owning HashBiMap through
        // "HashBiMap.hashTableKToV" and "BiEntry.nextInKToVBucket" references.
        // Under that assumption, the remaining references can be safely marked as .
        // Using  is necessary to avoid retain-cycles between BiEntry instances on iOS,
        // which would cause memory leaks when non-empty HashBiMap with cyclic BiEntry
        // instances is deallocated.
        @CheckForNull
        BiEntry<K, V> nextInKToVBucket;
        @CheckForNull
        BiEntry<K, V> nextInVToKBucket;

        @CheckForNull
        BiEntry<K, V> nextInKeyInsertionOrder;
        @CheckForNull
        BiEntry<K, V> prevInKeyInsertionOrder;

        BiEntry( @ParametricNullness K key, int keyHash, @ParametricNullness V value, int valueHash ) {
            super(key, value);
            this.keyHash = keyHash;
            this.valueHash = valueHash;
        }
    }

    private static final class InverseSerializedForm<
            K extends Object, V extends Object>
            implements Serializable {
        private final HashBiMap<K, V> bimap;

        InverseSerializedForm( HashBiMap<K, V> bimap ) {
            this.bimap = bimap;
        }

        Object readResolve() {
            return bimap.inverse();
        }
    }

    abstract class Itr<T extends Object> implements Iterator<T> {
        @CheckForNull
        BiEntry<K, V> next = firstInKeyInsertionOrder;
        @CheckForNull
        BiEntry<K, V> toRemove = null;
        int expectedModCount = modCount;
        int remaining = size();

        @Override
        public boolean hasNext() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            return next != null && remaining > 0;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            // requireNonNull is safe because of the hasNext check.
            BiEntry<K, V> entry = requireNonNull(next);
            next = entry.nextInKeyInsertionOrder;
            toRemove = entry;
            remaining--;
            return output(entry);
        }

        @Override
        public void remove() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (toRemove == null) {
                throw new IllegalStateException("no calls to next() since the last call to remove()");
            }
            delete(toRemove);
            expectedModCount = modCount;
            toRemove = null;
        }

        abstract T output( BiEntry<K, V> entry );
    }

    private final class KeySet extends CKeySet<K, V> {
        KeySet() {
            super(HashBiMap.this);
        }

        @Override
        public Iterator<K> iterator() {
            return new Itr<K>() {
                @Override
                K output( BiEntry<K, V> entry ) {
                    return entry.key;
                }
            };
        }

        @Override
        public boolean remove( @CheckForNull Object o ) {
            BiEntry<K, V> entry = seekByKey(o, ObjectUtil.hashCode(o));
            if (entry == null) {
                return false;
            } else {
                delete(entry);
                entry.prevInKeyInsertionOrder = null;
                entry.nextInKeyInsertionOrder = null;
                return true;
            }
        }
    }
}
