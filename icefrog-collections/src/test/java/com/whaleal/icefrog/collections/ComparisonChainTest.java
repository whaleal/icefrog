

package com.whaleal.icefrog.collections;



import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * Unit test for {@link ComparisonChain}.
 *
 *
 */

public class ComparisonChainTest extends TestCase {
  private static final DontCompareMe DONT_COMPARE_ME = new DontCompareMe();

  private static class DontCompareMe implements Comparable<DontCompareMe> {
    @Override
    public int compareTo(DontCompareMe o) {
      throw new AssertionFailedError();
    }
  }

  public void testCompareBooleans() {
    assertEquals(
        0,
        ComparisonChain.start()
            .compare(true, true)
            .compare(true, Boolean.TRUE)
            .compare(Boolean.TRUE, true)
            .compare(Boolean.TRUE, Boolean.TRUE)
            .result());
  }

  public void testDegenerate() {
    // kinda bogus, but who cares?
    assertEquals(0, ComparisonChain.start().result());
  }

  public void testOneEqual() {
    assertEquals(0, ComparisonChain.start().compare("a", "a").result());
  }

  public void testOneEqualUsingComparator() {
    assertEquals(
        0, ComparisonChain.start().compare("a", "A", String.CASE_INSENSITIVE_ORDER).result());
  }

  public void testManyEqual() {
    assertEquals(
        0,
        ComparisonChain.start()
            .compare(1, 1)
            .compare(1L, 1L)
            .compareFalseFirst(true, true)
            .compare(1.0, 1.0)
            .compare(1.0f, 1.0f)
            .compare("a", "a", Ordering.usingToString())
            .result());
  }

  public void testShortCircuitLess() {
    assertTrue(
        ComparisonChain.start().compare("a", "b").compare(DONT_COMPARE_ME, DONT_COMPARE_ME).result()
            < 0);
  }

  public void testShortCircuitGreater() {
    assertTrue(
        ComparisonChain.start().compare("b", "a").compare(DONT_COMPARE_ME, DONT_COMPARE_ME).result()
            > 0);
  }

  public void testShortCircuitSecondStep() {
    assertTrue(
        ComparisonChain.start()
                .compare("a", "a")
                .compare("a", "b")
                .compare(DONT_COMPARE_ME, DONT_COMPARE_ME)
                .result()
            < 0);
  }

  public void testCompareFalseFirst() {
    assertTrue(ComparisonChain.start().compareFalseFirst(true, true).result() == 0);
    assertTrue(ComparisonChain.start().compareFalseFirst(true, false).result() > 0);
    assertTrue(ComparisonChain.start().compareFalseFirst(false, true).result() < 0);
    assertTrue(ComparisonChain.start().compareFalseFirst(false, false).result() == 0);
  }

  public void testCompareTrueFirst() {
    assertTrue(ComparisonChain.start().compareTrueFirst(true, true).result() == 0);
    assertTrue(ComparisonChain.start().compareTrueFirst(true, false).result() < 0);
    assertTrue(ComparisonChain.start().compareTrueFirst(false, true).result() > 0);
    assertTrue(ComparisonChain.start().compareTrueFirst(false, false).result() == 0);
  }
}
