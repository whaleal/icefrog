

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Base class for {@link RangeSet} tests.
 *
 * 
 */
 // TreeRangeSet
public abstract class AbstractRangeSetTest extends TestCase {
  @Test
  public void test(){
  }
  public static void testInvariants(RangeSet<?> rangeSet) {
    testInvariantsInternal(rangeSet);
    testInvariantsInternal(rangeSet.complement());
  }

  private static <C extends Comparable> void testInvariantsInternal(RangeSet<C> rangeSet) {
    assertEquals(rangeSet.asRanges().isEmpty(), rangeSet.isEmpty());
    assertEquals(rangeSet.asDescendingSetOfRanges().isEmpty(), rangeSet.isEmpty());
    assertEquals(!rangeSet.asRanges().iterator().hasNext(), rangeSet.isEmpty());
    assertEquals(!rangeSet.asDescendingSetOfRanges().iterator().hasNext(), rangeSet.isEmpty());

    List<Range<C>> asRanges = ImmutableList.copyOf(rangeSet.asRanges());

    // test that connected ranges are coalesced
    for (int i = 0; i + 1 < asRanges.size(); i++) {
      Range<C> range1 = asRanges.get(i);
      Range<C> range2 = asRanges.get(i + 1);
      assertFalse(range1.isConnected(range2));
    }

    // test that there are no empty ranges
    for (Range<C> range : asRanges) {
      assertFalse(range.isEmpty());
    }

    // test that the RangeSet's span is the span of all the ranges
    Iterator<Range<C>> itr = rangeSet.asRanges().iterator();
    Range<C> expectedSpan = null;
    if (itr.hasNext()) {
      expectedSpan = itr.next();
      while (itr.hasNext()) {
        expectedSpan = expectedSpan.span(itr.next());
      }
    }

    try {
      Range<C> span = rangeSet.span();
      assertEquals(expectedSpan, span);
    } catch (NoSuchElementException e) {
      assertNull(expectedSpan);
    }

    // test that asDescendingSetOfRanges is the reverse of asRanges
    assertEquals(Lists.reverse(asRanges), ImmutableList.copyOf(rangeSet.asDescendingSetOfRanges()));
  }
}
