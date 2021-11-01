

package com.whaleal.icefrog.collections;

import java.io.Serializable;


/** */


class ImmutableEntry<K extends Object, V extends Object>
    extends AbstractMapEntry<K, V> implements Serializable {
  @ParametricNullness final K key;
  @ParametricNullness final V value;

  ImmutableEntry(@ParametricNullness K key, @ParametricNullness V value) {
    this.key = key;
    this.value = value;
  }

  @Override
  @ParametricNullness
  public final K getKey() {
    return key;
  }

  @Override
  @ParametricNullness
  public final V getValue() {
    return value;
  }

  @Override
  @ParametricNullness
  public final V setValue(@ParametricNullness V value) {
    throw new UnsupportedOperationException();
  }

  private static final long serialVersionUID = 0;
}
