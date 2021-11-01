

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;

import javax.annotation.CheckForNull;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.whaleal.icefrog.collections.RegularImmutableMap.makeImmutable;
import static com.whaleal.icefrog.core.lang.Precondition.checkNotNull;
import static java.util.Objects.requireNonNull;



/**
 * Implementation of ImmutableMap backed by a JDK HashMap, which has smartness protecting against
 * hash flooding.
 */


final class JdkBackedImmutableMap<K, V> extends ImmutableMap<K, V> {
  /**
   * Creates an {@code ImmutableMap} backed by a JDK HashMap. Used when probable hash flooding is
   * detected. This implementation may replace the entries in entryArray with its own entry objects
   * (though they will have the same key/value contents), and will take ownership of entryArray.
   */
  static <K, V> ImmutableMap<K, V> create(int n, Entry<K, V>[] entryArray) {
    Map<K, V> delegateMap = MapUtil.newHashMap(n);
    for (int i = 0; i < n; i++) {
      // requireNonNull is safe because the first `n` elements have been filled in.
      entryArray[i] = makeImmutable(requireNonNull(entryArray[i]));
      V oldValue = delegateMap.putIfAbsent(entryArray[i].getKey(), entryArray[i].getValue());
      if (oldValue != null) {
        throw conflictException("key", entryArray[i], entryArray[i].getKey() + "=" + oldValue);
      }
    }
    return new JdkBackedImmutableMap<>(delegateMap, ImmutableList.asImmutableList(entryArray, n));
  }

  private final transient Map<K, V> delegateMap;
  private final transient ImmutableList<Entry<K, V>> entries;

  JdkBackedImmutableMap(Map<K, V> delegateMap, ImmutableList<Entry<K, V>> entries) {
    this.delegateMap = delegateMap;
    this.entries = entries;
  }

  @Override
  public int size() {
    return entries.size();
  }

  @Override
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return delegateMap.get(key);
  }

  @Override
  ImmutableSet<Entry<K, V>> createEntrySet() {
    return new ImmutableMapEntrySet.RegularEntrySet<>(this, entries);
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    checkNotNull(action);
    entries.forEach(e -> action.accept(e.getKey(), e.getValue()));
  }

  @Override
  ImmutableSet<K> createKeySet() {
    return new ImmutableMapKeySet<>(this);
  }

  @Override
  ImmutableCollection<V> createValues() {
    return new ImmutableMapValues<>(this);
  }

  @Override
  boolean isPartialView() {
    return false;
  }
}
