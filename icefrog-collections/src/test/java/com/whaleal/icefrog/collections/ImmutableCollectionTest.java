

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

/**
 * Tests for {@code ImmutableCollection}.
 *
 * 
 */
public class ImmutableCollectionTest extends TestCase {
  @Test
  public void test(){
  }
  public void testCapacityExpansion() {
    assertEquals(1, ImmutableCollection.Builder.expandedCapacity(0, 1));
    assertEquals(2, ImmutableCollection.Builder.expandedCapacity(0, 2));
    assertEquals(2, ImmutableCollection.Builder.expandedCapacity(1, 2));
    assertEquals(
        Integer.MAX_VALUE, ImmutableCollection.Builder.expandedCapacity(0, Integer.MAX_VALUE));
    assertEquals(
        Integer.MAX_VALUE, ImmutableCollection.Builder.expandedCapacity(1, Integer.MAX_VALUE));
    assertEquals(
        Integer.MAX_VALUE,
        ImmutableCollection.Builder.expandedCapacity(Integer.MAX_VALUE - 1, Integer.MAX_VALUE));

    assertEquals(13, ImmutableCollection.Builder.expandedCapacity(8, 9));
  }
}
