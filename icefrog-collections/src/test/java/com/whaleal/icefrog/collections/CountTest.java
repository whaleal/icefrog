

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

/**
 * Tests for {@code Count}.
 *
 * 
 */

public class CountTest extends TestCase {
  @Test
  public void test(){
  }
  public void testGet() {
    assertEquals(20, new Count(20).get());
  }

  public void testGetAndAdd() {
    Count holder = new Count(20);
    assertEquals(20, holder.get());
    holder.add(1);
    assertEquals(21, holder.get());
  }

  public void testAddAndGet() {
    Count holder = new Count(20);
    assertEquals(21, holder.addAndGet(1));
  }

  public void testGetAndSet() {
    Count holder = new Count(10);
    assertEquals(10, holder.getAndSet(20));
    assertEquals(20, holder.get());
  }

  public void testSet() {
    Count holder = new Count(10);
    holder.set(20);
    assertEquals(20, holder.get());
  }
}
