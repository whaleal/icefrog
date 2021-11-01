

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.util.AbstractList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Tests for {@link EvictingQueue}.
 *
 * @author Kurt Alfred Kluever
 */

public class EvictingQueueTest extends TestCase {

  @Test
  public void test(){
  }
  public void testCreateWithNegativeSize() throws Exception {
    try {
      EvictingQueue.create(-1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testCreateWithZeroSize() throws Exception {
    EvictingQueue<String> queue = EvictingQueue.create(0);
    assertEquals(0, queue.size());

    assertTrue(queue.add("hi"));
    assertEquals(0, queue.size());

    assertTrue(queue.offer("hi"));
    assertEquals(0, queue.size());

    assertFalse(queue.remove("hi"));
    assertEquals(0, queue.size());

    try {
      queue.element();
      fail();
    } catch (NoSuchElementException expected) {
    }

    assertNull(queue.peek());
    assertNull(queue.poll());
    try {
      queue.remove();
      fail();
    } catch (NoSuchElementException expected) {
    }
  }

  public void testRemainingCapacity_maxSize0() {
    EvictingQueue<String> queue = EvictingQueue.create(0);
    assertEquals(0, queue.remainingCapacity());
  }

  public void testRemainingCapacity_maxSize1() {
    EvictingQueue<String> queue = EvictingQueue.create(1);
    assertEquals(1, queue.remainingCapacity());
    queue.add("hi");
    assertEquals(0, queue.remainingCapacity());
  }

  public void testRemainingCapacity_maxSize3() {
    EvictingQueue<String> queue = EvictingQueue.create(3);
    assertEquals(3, queue.remainingCapacity());
    queue.add("hi");
    assertEquals(2, queue.remainingCapacity());
    queue.add("hi");
    assertEquals(1, queue.remainingCapacity());
    queue.add("hi");
    assertEquals(0, queue.remainingCapacity());
  }

  public void testEvictingAfterOne() throws Exception {
    EvictingQueue<String> queue = EvictingQueue.create(1);
    assertEquals(0, queue.size());
    assertEquals(1, queue.remainingCapacity());

    assertTrue(queue.add("hi"));
    assertEquals("hi", queue.element());
    assertEquals("hi", queue.peek());
    assertEquals(1, queue.size());
    assertEquals(0, queue.remainingCapacity());

    assertTrue(queue.add("there"));
    assertEquals("there", queue.element());
    assertEquals("there", queue.peek());
    assertEquals(1, queue.size());
    assertEquals(0, queue.remainingCapacity());

    assertEquals("there", queue.remove());
    assertEquals(0, queue.size());
    assertEquals(1, queue.remainingCapacity());
  }

  public void testEvictingAfterThree() throws Exception {
    EvictingQueue<String> queue = EvictingQueue.create(3);
    assertEquals(0, queue.size());
    assertEquals(3, queue.remainingCapacity());

    assertTrue(queue.add("one"));
    assertTrue(queue.add("two"));
    assertTrue(queue.add("three"));
    assertEquals("one", queue.element());
    assertEquals("one", queue.peek());
    assertEquals(3, queue.size());
    assertEquals(0, queue.remainingCapacity());

    assertTrue(queue.add("four"));
    assertEquals("two", queue.element());
    assertEquals("two", queue.peek());
    assertEquals(3, queue.size());
    assertEquals(0, queue.remainingCapacity());

    assertEquals("two", queue.remove());
    assertEquals(2, queue.size());
    assertEquals(1, queue.remainingCapacity());
  }

  public void testAddAll() throws Exception {
    EvictingQueue<String> queue = EvictingQueue.create(3);
    assertEquals(0, queue.size());
    assertEquals(3, queue.remainingCapacity());

    assertTrue(queue.addAll(ImmutableList.of("one", "two", "three")));
    assertEquals("one", queue.element());
    assertEquals("one", queue.peek());
    assertEquals(3, queue.size());
    assertEquals(0, queue.remainingCapacity());

    assertTrue(queue.addAll(ImmutableList.of("four")));
    assertEquals("two", queue.element());
    assertEquals("two", queue.peek());
    assertEquals(3, queue.size());
    assertEquals(0, queue.remainingCapacity());

    assertEquals("two", queue.remove());
    assertEquals(2, queue.size());
    assertEquals(1, queue.remainingCapacity());
  }

  public void testAddAll_largeList() {
    final List<String> list = ImmutableList.of("one", "two", "three", "four", "five");
    List<String> misbehavingList =
        new AbstractList<String>() {
          @Override
          public int size() {
            return list.size();
          }

          @Override
          public String get(int index) {
            if (index < 2) {
              throw new AssertionError();
            }
            return list.get(index);
          }
        };

    EvictingQueue<String> queue = EvictingQueue.create(3);
    assertTrue(queue.addAll(misbehavingList));

    assertEquals("three", queue.remove());
    assertEquals("four", queue.remove());
    assertEquals("five", queue.remove());
    assertTrue(queue.isEmpty());
  }



}
