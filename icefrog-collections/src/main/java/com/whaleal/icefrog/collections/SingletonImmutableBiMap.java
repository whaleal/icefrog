

package com.whaleal.icefrog.collections;

import javax.annotation.CheckForNull;
import java.util.function.BiConsumer;

import static com.whaleal.icefrog.core.lang.Preconditions.checkEntryNotNull;
import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;

/**
 * Implementation of {@link ImmutableMap} with exactly one entry.
 *
 * @author Jesse Wilson
 *
 */

@SuppressWarnings("serial") // uses writeReplace(), not default serialization

final class SingletonImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {

  final transient K singleKey;
  final transient V singleValue;

  SingletonImmutableBiMap(K singleKey, V singleValue) {
    checkEntryNotNull(singleKey, singleValue);
    this.singleKey = singleKey;
    this.singleValue = singleValue;
    this.inverse = null;
  }

  private SingletonImmutableBiMap(K singleKey, V singleValue, ImmutableBiMap<V, K> inverse) {
    this.singleKey = singleKey;
    this.singleValue = singleValue;
    this.inverse = inverse;
  }

  @Override
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return singleKey.equals(key) ? singleValue : null;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    checkNotNull(action).accept(singleKey, singleValue);
  }

  @Override
  public boolean containsKey(@CheckForNull Object key) {
    return singleKey.equals(key);
  }

  @Override
  public boolean containsValue(@CheckForNull Object value) {
    return singleValue.equals(value);
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override
  ImmutableSet<Entry<K, V>> createEntrySet() {
    return ImmutableSet.of( new ImmutableEntry(singleKey, singleValue));
  }

  @Override
  ImmutableSet<K> createKeySet() {
    return ImmutableSet.of(singleKey);
  }

  @CheckForNull private final transient ImmutableBiMap<V, K> inverse;
   @CheckForNull private transient ImmutableBiMap<V, K> lazyInverse;

  @Override
  public ImmutableBiMap<V, K> inverse() {
    if (inverse != null) {
      return inverse;
    } else {
      // racy single-check idiom
      ImmutableBiMap<V, K> result = lazyInverse;
      if (result == null) {
        return lazyInverse = new SingletonImmutableBiMap<>(singleValue, singleKey, this);
      } else {
        return result;
      }
    }
  }
}
