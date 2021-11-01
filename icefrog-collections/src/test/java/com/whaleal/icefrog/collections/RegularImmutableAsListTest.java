

package com.whaleal.icefrog.collections;



import junit.framework.TestCase;
import org.junit.Test;

/**
 * Tests for {@link RegularImmutableAsList}.
 *
 *
 */

public class RegularImmutableAsListTest extends TestCase {
  @Test
  public void test(){
  }
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
