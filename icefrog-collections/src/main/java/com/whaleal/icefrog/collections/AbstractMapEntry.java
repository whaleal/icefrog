

package com.whaleal.icefrog.collections;



import com.whaleal.icefrog.core.util.ObjectUtil;

import javax.annotation.CheckForNull;
import java.util.Map.Entry;




/**
 * Implementation of the {@code equals}, {@code hashCode}, and {@code toString} methods of {@code
 * Entry}.
 *
 *
 */


abstract class AbstractMapEntry<K extends Object, V extends Object>
    implements Entry<K, V> {

  @Override
  @ParametricNullness
  public abstract K getKey();

  @Override
  @ParametricNullness
  public abstract V getValue();

  @Override
  @ParametricNullness
  public V setValue(@ParametricNullness V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(@CheckForNull Object object) {
    if (object instanceof Entry) {
      Entry<?, ?> that = (Entry<?, ?>) object;
      return ObjectUtil.equal(this.getKey(), that.getKey())
          && ObjectUtil.equal(this.getValue(), that.getValue());
    }
    return false;
  }

  @Override
  public int hashCode() {
    K k = getKey();
    V v = getValue();
    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
  }

  /** Returns a string representation of the form {@code {key}={value}}. */
  @Override
  public String toString() {
    return getKey() + "=" + getValue();
  }
}
