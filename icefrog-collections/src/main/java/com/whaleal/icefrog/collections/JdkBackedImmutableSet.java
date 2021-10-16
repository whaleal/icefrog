

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;


import javax.annotation.CheckForNull;
import java.util.Set;

/**
 * ImmutableSet implementation backed by a JDK HashSet, used to defend against apparent hash
 * flooding. This implementation is never used on the GWT client side, but it must be present there
 * for serialization to work.
 *
 * @author Louis Wasserman
 */


final class JdkBackedImmutableSet<E> extends IndexedImmutableSet<E> {
  private final Set<?> delegate;
  private final ImmutableList<E> delegateList;

  JdkBackedImmutableSet(Set<?> delegate, ImmutableList<E> delegateList) {
    this.delegate = delegate;
    this.delegateList = delegateList;
  }

  @Override
  E get(int index) {
    return delegateList.get(index);
  }

  @Override
  public boolean contains(@CheckForNull Object object) {
    return delegate.contains(object);
  }

  @Override
  boolean isPartialView() {
    return false;
  }

  @Override
  public int size() {
    return delegateList.size();
  }
}
