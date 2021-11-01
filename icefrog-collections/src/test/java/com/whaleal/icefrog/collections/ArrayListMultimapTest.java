

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.util.RandomAccess;

import static java.util.Arrays.asList;

/**
 * Unit tests for {@code ArrayListMultimap}.
 *
 *
 */

public class ArrayListMultimapTest extends TestCase {
  @Test
  public void test(){
  }


  protected ListMultimap<String, Integer> create() {
    return ArrayListMultimap.create();
  }

  /** Confirm that get() returns a List implementing RandomAccess. */
  public void testGetRandomAccess() {
    Multimap<String, Integer> multimap = create();
    multimap.put("foo", 1);
    multimap.put("foo", 3);
    assertTrue(multimap.get("foo") instanceof RandomAccess);
    assertTrue(multimap.get("bar") instanceof RandomAccess);
  }

  /** Confirm that removeAll() returns a List implementing RandomAccess. */
  public void testRemoveAllRandomAccess() {
    Multimap<String, Integer> multimap = create();
    multimap.put("foo", 1);
    multimap.put("foo", 3);
    assertTrue(multimap.removeAll("foo") instanceof RandomAccess);
    assertTrue(multimap.removeAll("bar") instanceof RandomAccess);
  }

  /** Confirm that replaceValues() returns a List implementing RandomAccess. */
  public void testReplaceValuesRandomAccess() {
    Multimap<String, Integer> multimap = create();
    multimap.put("foo", 1);
    multimap.put("foo", 3);
    assertTrue(multimap.replaceValues("foo", asList(2, 4)) instanceof RandomAccess);
    assertTrue(multimap.replaceValues("bar", asList(2, 4)) instanceof RandomAccess);
  }


  public void testCreateFromMultimap() {
    Multimap<String, Integer> multimap = create();
    multimap.put("foo", 1);
    multimap.put("foo", 3);
    multimap.put("bar", 2);
    ArrayListMultimap<String, Integer> copy = ArrayListMultimap.create(multimap);
    assertEquals(multimap, copy);
  }

  public void testCreate() {
    ArrayListMultimap<String, Integer> multimap = ArrayListMultimap.create();
    assertEquals(3, multimap.expectedValuesPerKey);
  }

  public void testCreateFromSizes() {
    ArrayListMultimap<String, Integer> multimap = ArrayListMultimap.create(15, 20);
    assertEquals(20, multimap.expectedValuesPerKey);
  }

  public void testCreateFromIllegalSizes() {
    try {
      ArrayListMultimap.create(15, -2);
      fail();
    } catch (IllegalArgumentException expected) {
    }

    try {
      ArrayListMultimap.create(-15, 2);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testCreateFromHashMultimap() {
    Multimap<String, Integer> original = HashMultimap.create();
    ArrayListMultimap<String, Integer> multimap = ArrayListMultimap.create(original);
    assertEquals(3, multimap.expectedValuesPerKey);
  }

  public void testCreateFromArrayListMultimap() {
    ArrayListMultimap<String, Integer> original = ArrayListMultimap.create(15, 20);
    ArrayListMultimap<String, Integer> multimap = ArrayListMultimap.create(original);
    assertEquals(20, multimap.expectedValuesPerKey);
  }

}
