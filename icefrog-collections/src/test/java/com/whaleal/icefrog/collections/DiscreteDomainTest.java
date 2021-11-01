

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.math.BigInteger;


/**
 * Tests for {@link DiscreteDomain}.
 *
 *
 */
 // SerializableTester
public class DiscreteDomainTest extends TestCase {
  @Test
  public void test(){
  }

  public void testIntegersOffset() {
    assertEquals(1, DiscreteDomain.integers().offset(0, 1).intValue());
    assertEquals(
        Integer.MAX_VALUE,
        DiscreteDomain.integers().offset(Integer.MIN_VALUE, (1L << 32) - 1).intValue());
  }

  public void testIntegersOffsetExceptions() {
    try {
      DiscreteDomain.integers().offset(0, -1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
    try {
      DiscreteDomain.integers().offset(Integer.MAX_VALUE, 1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testLongsOffset() {
    assertEquals(1, DiscreteDomain.longs().offset(0L, 1).longValue());
    assertEquals(Long.MAX_VALUE, DiscreteDomain.longs().offset(0L, Long.MAX_VALUE).longValue());
  }

  public void testLongsOffsetExceptions() {
    try {
      DiscreteDomain.longs().offset(0L, -1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
    try {
      DiscreteDomain.longs().offset(Long.MAX_VALUE, 1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testBigIntegersOffset() {
    assertEquals(BigInteger.ONE, DiscreteDomain.bigIntegers().offset(BigInteger.ZERO, 1));
    assertEquals(
        BigInteger.valueOf(Long.MAX_VALUE),
        DiscreteDomain.bigIntegers().offset(BigInteger.ZERO, Long.MAX_VALUE));
  }

  public void testBigIntegersOffsetExceptions() {
    try {
      DiscreteDomain.bigIntegers().offset(BigInteger.ZERO, -1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testCustomOffsetExceptions() {
    try {
      new MyIntegerDomain().offset(0, -1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
    try {
      new MyIntegerDomain().offset(Integer.MAX_VALUE, 1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  private static final class MyIntegerDomain extends DiscreteDomain<Integer> {
    static final DiscreteDomain<Integer> DELEGATE = integers();

    @Override
    public Integer next(Integer value) {
      return DELEGATE.next(value);
    }

    @Override
    public Integer previous(Integer value) {
      return DELEGATE.previous(value);
    }

    // Do *not* override offset() to delegate: We want to test the default implementation.

    @Override
    public long distance(Integer start, Integer end) {
      return DELEGATE.distance(start, end);
    }

    @Override
    public Integer minValue() {
      return DELEGATE.minValue();
    }

    @Override
    public Integer maxValue() {
      return DELEGATE.maxValue();
    }
  }
}
