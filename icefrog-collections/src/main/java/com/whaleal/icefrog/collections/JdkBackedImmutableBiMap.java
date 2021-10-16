
package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;

import javax.annotation.CheckForNull;
import java.util.Map;

import static java.util.Objects.requireNonNull;


/**
 * Implementation of ImmutableBiMap backed by a pair of JDK HashMapUtil, which have smartness
 * protecting against hash flooding.
 */


final class JdkBackedImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {

  static <K, V> ImmutableBiMap<K, V> create(int n, Entry<K, V>[] entryArray) {
    Map<K, V> forwardDelegate = MapUtil.newHashMap(n);
    Map<V, K> backwardDelegate = MapUtil.newHashMap(n);
    for (int i = 0; i < n; i++) {
      // requireNonNull is safe because the first `n` elements have been filled in.
      Entry<K, V> e = RegularImmutableMap.makeImmutable(requireNonNull(entryArray[i]));
      entryArray[i] = e;
      V oldValue = forwardDelegate.putIfAbsent(e.getKey(), e.getValue());
      if (oldValue != null) {
        throw conflictException("key", e.getKey() + "=" + oldValue, entryArray[i]);
      }
      K oldKey = backwardDelegate.putIfAbsent(e.getValue(), e.getKey());
      if (oldKey != null) {
        throw conflictException("value", oldKey + "=" + e.getValue(), entryArray[i]);
      }
    }
    ImmutableList<Entry<K, V>> entryList = ImmutableList.asImmutableList(entryArray, n);
    return new JdkBackedImmutableBiMap<>(entryList, forwardDelegate, backwardDelegate);
  }

  private final transient ImmutableList<Entry<K, V>> entries;
  private final Map<K, V> forwardDelegate;
  private final Map<V, K> backwardDelegate;

  private JdkBackedImmutableBiMap(
      ImmutableList<Entry<K, V>> entries, Map<K, V> forwardDelegate, Map<V, K> backwardDelegate) {
    this.entries = entries;
    this.forwardDelegate = forwardDelegate;
    this.backwardDelegate = backwardDelegate;
  }

  @Override
  public int size() {
    return entries.size();
  }

   @CheckForNull private transient JdkBackedImmutableBiMap<V, K> inverse;

  @Override
  public ImmutableBiMap<V, K> inverse() {
    JdkBackedImmutableBiMap<V, K> result = inverse;
    if (result == null) {
      inverse =
          result =
              new JdkBackedImmutableBiMap<>(
                  new InverseEntries(), backwardDelegate, forwardDelegate);
      result.inverse = this;
    }
    return result;
  }


  private final class InverseEntries extends ImmutableList<Entry<V, K>> {
    @Override
    public Entry<V, K> get(int index) {
      Entry<K, V> entry = entries.get(index);
      return  new ImmutableEntry(entry.getValue(), entry.getKey());
    }

    @Override
    boolean isPartialView() {
      return false;
    }

    @Override
    public int size() {
      return entries.size();
    }
  }

  @Override
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return forwardDelegate.get(key);
  }

  @Override
  ImmutableSet<Entry<K, V>> createEntrySet() {
    return new ImmutableMapEntrySet.RegularEntrySet<>(this, entries);
  }

  @Override
  ImmutableSet<K> createKeySet() {
    return new ImmutableMapKeySet<>(this);
  }

  @Override
  boolean isPartialView() {
    return false;
  }
}
