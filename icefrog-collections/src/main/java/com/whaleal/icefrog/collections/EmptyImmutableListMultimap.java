

package com.whaleal.icefrog.collections;

import com.whaleal.icefrog.core.map.MapUtil;






/**
 * Implementation of {@link ImmutableListMultimap} with no entries.
 *
 * @author Jared Levy
 */


class EmptyImmutableListMultimap extends ImmutableListMultimap<Object, Object> {
  static final EmptyImmutableListMultimap INSTANCE = new EmptyImmutableListMultimap();

  private EmptyImmutableListMultimap() {
    super(ImmutableMap.of(), 0);
  }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }

  private static final long serialVersionUID = 0;
}
