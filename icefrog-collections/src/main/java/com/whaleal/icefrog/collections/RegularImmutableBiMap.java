

package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.whaleal.icefrog.collections.ImmutableMapEntry.createEntryArray;
import static com.whaleal.icefrog.collections.RegularImmutableMap.checkNoConflictInKeyBucket;
import static com.whaleal.icefrog.core.lang.Preconditions.*;
import static java.util.Objects.requireNonNull;


/**
 * Bimap with zero or more mappings.
 *
 *
 */

@SuppressWarnings("serial") // uses writeReplace(), not default serialization

class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
  static final RegularImmutableBiMap<Object, Object> EMPTY =
      new RegularImmutableBiMap<>(
          null, null, (Entry<Object, Object>[]) ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0);

  static final double MAX_LOAD_FACTOR = 1.2;

  @CheckForNull private final transient ImmutableMapEntry<K, V>[] keyTable;
  @CheckForNull private final transient ImmutableMapEntry<K, V>[] valueTable;
  final transient Entry<K, V>[] entries;
  private final transient int mask;
  private final transient int hashCode;

  static <K, V> ImmutableBiMap<K, V> fromEntries(Entry<K, V>... entries) {
    return fromEntryArray(entries.length, entries);
  }

  static <K, V> ImmutableBiMap<K, V> fromEntryArray(int n, Entry<K, V>[] entryArray) {
    checkPositionIndex(n, entryArray.length);
    int tableSize = Hashing.closedTableSize(n, MAX_LOAD_FACTOR);
    int mask = tableSize - 1;
    ImmutableMapEntry<K, V>[] keyTable = createEntryArray(tableSize);
    ImmutableMapEntry<K, V>[] valueTable = createEntryArray(tableSize);
    /*
     * The cast is safe: n==entryArray.length means that we have filled the whole array with Entry
     * instances, in which case it is safe to cast it from an array of nullable entries to an array
     * of non-null entries.
     */
    @SuppressWarnings("nullness")
    Entry<K, V>[] entries =
        (n == entryArray.length) ? entryArray : createEntryArray(n);
    int hashCode = 0;

    for (int i = 0; i < n; i++) {
      // requireNonNull is safe because the first `n` elements have been filled in.
      Entry<K, V> entry = requireNonNull(entryArray[i]);
      K key = entry.getKey();
      V value = entry.getValue();
      checkEntryNotNull(key, value);
      int keyHash = key.hashCode();
      int valueHash = value.hashCode();
      int keyBucket = Hashing.smear(keyHash) & mask;
      int valueBucket = Hashing.smear(valueHash) & mask;

      ImmutableMapEntry<K, V> nextInKeyBucket = keyTable[keyBucket];
      int keyBucketLength = checkNoConflictInKeyBucket(key, entry, nextInKeyBucket);
      ImmutableMapEntry<K, V> nextInValueBucket = valueTable[valueBucket];
      int valueBucketLength = checkNoConflictInValueBucket(value, entry, nextInValueBucket);
      if (keyBucketLength > RegularImmutableMap.MAX_HASH_BUCKET_LENGTH
          || valueBucketLength > RegularImmutableMap.MAX_HASH_BUCKET_LENGTH) {
        return JdkBackedImmutableBiMap.create(n, entryArray);
      }
      ImmutableMapEntry<K, V> newEntry =
          (nextInValueBucket == null && nextInKeyBucket == null)
              ? RegularImmutableMap.makeImmutable(entry, key, value)
              : new ImmutableMapEntry.NonTerminalImmutableBiMapEntry<>(
                  key, value, nextInKeyBucket, nextInValueBucket);
      keyTable[keyBucket] = newEntry;
      valueTable[valueBucket] = newEntry;
      entries[i] = newEntry;
      hashCode += keyHash ^ valueHash;
    }
    return new RegularImmutableBiMap<>(keyTable, valueTable, entries, mask, hashCode);
  }

  private RegularImmutableBiMap(
      @CheckForNull ImmutableMapEntry<K, V>[] keyTable,
      @CheckForNull ImmutableMapEntry<K, V>[] valueTable,
      Entry<K, V>[] entries,
      int mask,
      int hashCode) {
    this.keyTable = keyTable;
    this.valueTable = valueTable;
    this.entries = entries;
    this.mask = mask;
    this.hashCode = hashCode;
  }

  // checkNoConflictInKeyBucket is static imported from RegularImmutableMap

  /**
   * @return number of entries in this bucket
   * @throws IllegalArgumentException if another entry in the bucket has the same key
   */
  
  private static int checkNoConflictInValueBucket(
          Object value, Entry<?, ?> entry, @CheckForNull ImmutableMapEntry<?, ?> valueBucketHead) {
    int bucketSize = 0;
    for (; valueBucketHead != null; valueBucketHead = valueBucketHead.getNextInValueBucket()) {
      checkNoConflict(!value.equals(valueBucketHead.getValue()), "value", entry, valueBucketHead);
      bucketSize++;
    }
    return bucketSize;
  }

  @Override
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return RegularImmutableMap.get(key, keyTable, mask);
  }

  @Override
  ImmutableSet<Entry<K, V>> createEntrySet() {
    return isEmpty()
        ? ImmutableSet.of()
        : new ImmutableMapEntrySet.RegularEntrySet<K, V>(this, entries);
  }

  @Override
  ImmutableSet<K> createKeySet() {
    return new ImmutableMapKeySet<>(this);
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    checkNotNull(action);
    for (Entry<K, V> entry : entries) {
      action.accept(entry.getKey(), entry.getValue());
    }
  }

  @Override
  boolean isHashCodeFast() {
    return true;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override
  public int size() {
    return entries.length;
  }

   @CheckForNull private transient ImmutableBiMap<V, K> inverse;

  @Override
  public ImmutableBiMap<V, K> inverse() {
    if (isEmpty()) {
      return ImmutableBiMap.of();
    }
    ImmutableBiMap<V, K> result = inverse;
    return (result == null) ? inverse = new Inverse() : result;
  }

  private final class Inverse extends ImmutableBiMap<V, K> {

    @Override
    public int size() {
      return inverse().size();
    }

    @Override
    public ImmutableBiMap<K, V> inverse() {
      return RegularImmutableBiMap.this;
    }

    @Override
    public void forEach(BiConsumer<? super V, ? super K> action) {
      checkNotNull(action);
      RegularImmutableBiMap.this.forEach((k, v) -> action.accept(v, k));
    }

    @Override
    @CheckForNull
    public K get(@CheckForNull Object value) {
      if (value == null || valueTable == null) {
        return null;
      }
      int bucket = Hashing.smear(value.hashCode()) & mask;
      for (ImmutableMapEntry<K, V> entry = valueTable[bucket];
          entry != null;
          entry = entry.getNextInValueBucket()) {
        if (value.equals(entry.getValue())) {
          return entry.getKey();
        }
      }
      return null;
    }

    @Override
    ImmutableSet<V> createKeySet() {
      return new ImmutableMapKeySet<>(this);
    }

    @Override
    ImmutableSet<Entry<V, K>> createEntrySet() {
      return new InverseEntrySet();
    }

    final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {
      @Override
      ImmutableMap<V, K> map() {
        return Inverse.this;
      }

      @Override
      boolean isHashCodeFast() {
        return true;
      }

      @Override
      public int hashCode() {
        return hashCode;
      }

      @Override
      public UnmodifiableIterator<Entry<V, K>> iterator() {
        return asList().iterator();
      }

      @Override
      public void forEach(Consumer<? super Entry<V, K>> action) {
        asList().forEach(action);
      }

      @Override
      ImmutableList<Entry<V, K>> createAsList() {
        return new ImmutableAsList<Entry<V, K>>() {
          @Override
          public Entry<V, K> get(int index) {
            Entry<K, V> entry = entries[index];
            return  new ImmutableEntry(entry.getValue(), entry.getKey());
          }

          @Override
          ImmutableCollection<Entry<V, K>> delegateCollection() {
            return InverseEntrySet.this;
          }
        };
      }
    }

    @Override
    boolean isPartialView() {
      return false;
    }

    @Override
    Object writeReplace() {
      return new InverseSerializedForm<>(RegularImmutableBiMap.this);
    }
  }

  private static class InverseSerializedForm<K, V> implements Serializable {
    private final ImmutableBiMap<K, V> forward;

    InverseSerializedForm(ImmutableBiMap<K, V> forward) {
      this.forward = forward;
    }

    Object readResolve() {
      return forward.inverse();
    }

    private static final long serialVersionUID = 1;
  }
}
