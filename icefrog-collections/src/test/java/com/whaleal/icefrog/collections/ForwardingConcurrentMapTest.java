

package com.whaleal.icefrog.collections;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Tests for {@link ForwardingConcurrentMap}.
 *
 *
 */
public class ForwardingConcurrentMapTest extends TestCase {
  @Test
  public void test(){
  }

  private static class TestMap extends ForwardingConcurrentMap<String, Integer> {
    final ConcurrentMap<String, Integer> delegate = new ConcurrentHashMap<>();

    @Override
    protected ConcurrentMap<String, Integer> delegate() {
      return delegate;
    }
  }

  public void testPutIfAbsent() {
    TestMap map = new TestMap();
    map.put("foo", 1);
    assertEquals(Integer.valueOf(1), map.putIfAbsent("foo", 2));
    assertEquals(Integer.valueOf(1), map.get("foo"));
    assertNull(map.putIfAbsent("bar", 3));
    assertEquals(Integer.valueOf(3), map.get("bar"));
  }

  public void testRemove() {
    TestMap map = new TestMap();
    map.put("foo", 1);
    assertFalse(map.remove("foo", 2));
    assertFalse(map.remove("bar", 1));
    assertEquals(Integer.valueOf(1), map.get("foo"));
    assertTrue(map.remove("foo", 1));
    assertTrue(map.isEmpty());
  }

  public void testReplace() {
    TestMap map = new TestMap();
    map.put("foo", 1);
    assertEquals(Integer.valueOf(1), map.replace("foo", 2));
    assertNull(map.replace("bar", 3));
    assertEquals(Integer.valueOf(2), map.get("foo"));
    assertFalse(map.containsKey("bar"));
  }

  public void testReplaceConditional() {
    TestMap map = new TestMap();
    map.put("foo", 1);
    assertFalse(map.replace("foo", 2, 3));
    assertFalse(map.replace("bar", 1, 2));
    assertEquals(Integer.valueOf(1), map.get("foo"));
    assertFalse(map.containsKey("bar"));
    assertTrue(map.replace("foo", 1, 4));
    assertEquals(Integer.valueOf(4), map.get("foo"));
  }
}
