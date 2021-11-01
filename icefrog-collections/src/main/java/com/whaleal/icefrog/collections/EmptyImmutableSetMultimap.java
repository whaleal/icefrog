

package com.whaleal.icefrog.collections;

/**
 * Implementation of {@link ImmutableListMultimap} with no entries.
 *
 * @author Mike Ward
 */


class EmptyImmutableSetMultimap extends ImmutableSetMultimap<Object, Object> {
  static final EmptyImmutableSetMultimap INSTANCE = new EmptyImmutableSetMultimap();

  private EmptyImmutableSetMultimap() {
    super(ImmutableMap.of(), 0, null);
  }

  private Object readResolve() {
    return INSTANCE; // preserve singleton property
  }

  private static final long serialVersionUID = 0;
}
