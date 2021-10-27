

package com.whaleal.icefrog.collections;



import junit.framework.TestCase;

/**
 * Tests for {@link RegularImmutableAsList}.
 *
 *
 */

public class RegularImmutableAsListTest extends TestCase {
  /**
   * RegularImmutableAsList should assume its input is null-free without checking, because it only
   * gets invoked from other immutable collections.
   */
  public void testDoesntCheckForNull() {
    ImmutableSet<Integer> set = ImmutableSet.of(1, 2, 3);
    new RegularImmutableAsList<Integer>(set, new Object[] {null, null, null});
    // shouldn't throw!
  }
}
