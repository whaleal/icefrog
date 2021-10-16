

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.collection.SpliteratorUtil;

import javax.annotation.CheckForNull;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.function.Consumer;

import static com.whaleal.icefrog.core.lang.Preconditions.checkNotNull;

/**
 * {@code values()} implementation for {@link ImmutableMap}.
 *
 * @author Jesse Wilson
 * @author Kevin Bourrillion
 */


final class ImmutableMapValues<K, V> extends ImmutableCollection<V> {
  private final ImmutableMap<K, V> map;

  ImmutableMapValues(ImmutableMap<K, V> map) {
    this.map = map;
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public UnmodifiableIterator<V> iterator() {
    return new UnmodifiableIterator<V>() {
      final Iterator<Entry<K, V>> entryItr = map.entrySet().iterator();

      @Override
      public boolean hasNext() {
        return entryItr.hasNext();
      }

      @Override
      public V next() {
        return entryItr.next().getValue();
      }
    };
  }

  @Override
  public Spliterator<V> spliterator() {
    return SpliteratorUtil.map(map.entrySet().spliterator(), Entry::getValue);
  }

  @Override
  public boolean contains(@CheckForNull Object object) {
    return object != null && Iterators.contains(iterator(), object);
  }

  @Override
  boolean isPartialView() {
    return true;
  }

  @Override
  public ImmutableList<V> asList() {
    final ImmutableList<Entry<K, V>> entryList = map.entrySet().asList();
    return new ImmutableAsList<V>() {
      @Override
      public V get(int index) {
        return entryList.get(index).getValue();
      }

      @Override
      ImmutableCollection<V> delegateCollection() {
        return ImmutableMapValues.this;
      }
    };
  }

 // serialization
  @Override
  public void forEach(Consumer<? super V> action) {
    checkNotNull(action);
    map.forEach((k, v) -> action.accept(v));
  }

  // No longer used for new writes, but kept so that old data can still be read.
 // serialization
  @SuppressWarnings("unused")
  private static class SerializedForm<V> implements Serializable {
    final ImmutableMap<?, V> map;

    SerializedForm(ImmutableMap<?, V> map) {
      this.map = map;
    }

    Object readResolve() {
      return map.values();
    }

    private static final long serialVersionUID = 0;
  }
}
