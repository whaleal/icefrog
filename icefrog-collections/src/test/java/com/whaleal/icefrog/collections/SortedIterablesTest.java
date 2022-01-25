

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.util.SortedSet;

/**
 * Unit tests for {@code SortedIterables}.
 *
 * 
 */

public class SortedIterablesTest extends TestCase {
  @Test
  public void test(){
  }
  public void testSameComparator() {
    assertTrue(SortedIterables.hasSameComparator(Ordering.natural(), SetUtil.newTreeSet()));
    // Before JDK6 (including under GWT), the TreeMap keySet is a plain Set.
    if (MapUtil.newTreeMap().keySet() instanceof SortedSet) {
      assertTrue(SortedIterables.hasSameComparator(Ordering.natural(), MapUtil.newTreeMap().keySet()));
    }
    assertTrue(
        SortedIterables.hasSameComparator(
            Ordering.natural().reverse(), SetUtil.newTreeSet(Ordering.natural().reverse())));
  }

  public void testComparator() {
    assertEquals(Ordering.natural(), SortedIterables.comparator(SetUtil.newTreeSet()));
  }
}
