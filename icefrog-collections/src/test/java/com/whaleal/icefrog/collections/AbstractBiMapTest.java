
package com.whaleal.icefrog.collections;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import junit.framework.TestCase;

/**
 * Tests for {@code AbstractBiMap}.
 *
 *
 */
public class AbstractBiMapTest extends TestCase {

  // The next two tests verify that map entries are not accessed after they're
  // removed, since IdentityHashMap throws an exception when that occurs.
  @SuppressWarnings("IdentityHashMapBoxing") // explicitly testing IdentityHashMap
  public void testIdentityKeySetIteratorRemove() {
    BiMap<Integer, String> bimap =
        new AbstractBiMap<Integer, String>(
            new IdentityHashMap<Integer, String>(), new IdentityHashMap<String, Integer>()) {};
    bimap.put(1, "one");
    bimap.put(2, "two");
    bimap.put(3, "three");
    Iterator<Integer> iterator = bimap.keySet().iterator();
    iterator.next();
    iterator.next();
    iterator.remove();
    iterator.next();
    iterator.remove();
    assertEquals(1, bimap.size());
    assertEquals(1, bimap.inverse().size());
  }

  @SuppressWarnings("IdentityHashMapBoxing") // explicitly testing IdentityHashMap
  public void testIdentityEntrySetIteratorRemove() {
    BiMap<Integer, String> bimap =
        new AbstractBiMap<Integer, String>(
            new IdentityHashMap<Integer, String>(), new IdentityHashMap<String, Integer>()) {};
    bimap.put(1, "one");
    bimap.put(2, "two");
    bimap.put(3, "three");
    Iterator<Entry<Integer, String>> iterator = bimap.entrySet().iterator();
    iterator.next();
    iterator.next();
    iterator.remove();
    iterator.next();
    iterator.remove();
    assertEquals(1, bimap.size());
    assertEquals(1, bimap.inverse().size());
  }
}
