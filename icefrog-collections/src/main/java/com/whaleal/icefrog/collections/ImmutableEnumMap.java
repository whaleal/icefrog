

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.collection.SpliteratorUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Spliterator;
import java.util.function.BiConsumer;

import static com.whaleal.icefrog.core.lang.Preconditions.checkArgument;

/**
 * Implementation of {@link ImmutableMap} backed by a non-empty {@link EnumMap}.
 *
 * 
 */

@SuppressWarnings("serial") // we're overriding default serialization

final class ImmutableEnumMap<K extends Enum<K>, V> extends ImmutableMap.IteratorBasedImmutableMap<K, V> {
  static <K extends Enum<K>, V> ImmutableMap<K, V> asImmutable(EnumMap<K, V> map) {
    switch (map.size()) {
      case 0:
        return ImmutableMap.of();
      case 1:
        Entry<K, V> entry = Iterables.getOnlyElement(map.entrySet());
        return ImmutableMap.of(entry.getKey(), entry.getValue());
      default:
        return new ImmutableEnumMap<>(map);
    }
  }

  private final transient EnumMap<K, V> delegate;

  private ImmutableEnumMap(EnumMap<K, V> delegate) {
    this.delegate = delegate;
    checkArgument(!delegate.isEmpty());
  }

  @Override
  UnmodifiableIterator<K> keyIterator() {

    return Iterators.unmodifiableIterator(delegate.keySet().iterator());
  }

  @Override
  Spliterator<K> keySpliterator() {
    return delegate.keySet().spliterator();
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public boolean containsKey(@CheckForNull Object key) {
    return delegate.containsKey(key);
  }

  @Override
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return delegate.get(key);
  }

  @Override
  public boolean equals(@CheckForNull Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof ImmutableEnumMap) {
      object = ((ImmutableEnumMap<?, ?>) object).delegate;
    }
    return delegate.equals(object);
  }

  @Override
  UnmodifiableIterator<Entry<K, V>> entryIterator() {
    return Maps.unmodifiableEntryIterator(delegate.entrySet().iterator());
  }

  @Override
  Spliterator<Entry<K, V>> entrySpliterator() {
    return SpliteratorUtil.map(delegate.entrySet().spliterator(), Maps::unmodifiableEntry);
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    delegate.forEach(action);
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  // All callers of the constructor are restricted to <K extends Enum<K>>.
  @Override
  Object writeReplace() {
    return new EnumSerializedForm<>(delegate);
  }

  /*
   * This class is used to serialize ImmutableEnumMap instances.
   */
  private static class EnumSerializedForm<K extends Enum<K>, V> implements Serializable {
    final EnumMap<K, V> delegate;

    EnumSerializedForm(EnumMap<K, V> delegate) {
      this.delegate = delegate;
    }

    Object readResolve() {
      return new ImmutableEnumMap<>(delegate);
    }

    private static final long serialVersionUID = 0;
  }
}
