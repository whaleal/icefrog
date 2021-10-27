

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Tests for {@link MultimapBuilder}.
 *
 * 
 */

public class MultimapBuilderTest extends TestCase {

   // doesn't build without explicit type parameters on build() methods
  public void testGenerics() {
    ListMultimap<String, Integer> a = MultimapBuilder.hashKeys().arrayListValues().build();
    SortedSetMultimap<String, Integer> b = MultimapBuilder.linkedHashKeys().treeSetValues().build();
    SetMultimap<String, Integer> c =
        MultimapBuilder.treeKeys(String.CASE_INSENSITIVE_ORDER).hashSetValues().build();
  }

  public void testGenerics_gwtCompatible() {
    ListMultimap<String, Integer> a =
        MultimapBuilder.hashKeys().arrayListValues().<String, Integer>build();
    SortedSetMultimap<String, Integer> b =
        MultimapBuilder.linkedHashKeys().treeSetValues().<String, Integer>build();
    SetMultimap<String, Integer> c =
        MultimapBuilder.treeKeys(String.CASE_INSENSITIVE_ORDER)
            .hashSetValues()
            .<String, Integer>build();
  }

   // doesn't build without explicit type parameters on build() methods
  public void testTreeKeys() {
    ListMultimap<String, Integer> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
    assertTrue(multimap.keySet() instanceof SortedSet);
    assertTrue(multimap.asMap() instanceof SortedMap);
  }

  public void testTreeKeys_gwtCompatible() {
    ListMultimap<String, Integer> multimap =
        MultimapBuilder.treeKeys().arrayListValues().<String, Integer>build();
    assertTrue(multimap.keySet() instanceof SortedSet);
    assertTrue(multimap.asMap() instanceof SortedMap);
  }



   // serialization
  private static void reserializeAndAssert(Object object) throws Exception {
    Object copy = reserialize(object);
    assertEquals(object, copy);
    assertEquals(object.getClass(), copy.getClass());
  }

   // serialization
  private static Object reserialize(Object object) throws Exception {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    new ObjectOutputStream(bytes).writeObject(object);
    return new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())).readObject();
  }
}
