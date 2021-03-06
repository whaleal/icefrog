

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collections;
import java.util.Map.Entry;

/**
 * Tests for {@code AbstractMapEntry}.
 *
 * @author Mike Bostock
 */

public class AbstractMapEntryTest extends TestCase {
  @Test
  public void test(){
  }
  private static final String NK = null;
  private static final Integer NV = null;

  private static <K, V> Entry<K, V> entry(final K key, final V value) {
    return new AbstractMapEntry<K, V>() {
      @Override
      public K getKey() {
        return key;
      }

      @Override
      public V getValue() {
        return value;
      }
    };
  }

  private static <K, V> Entry<K, V> control(K key, V value) {
    return Collections.singletonMap(key, value).entrySet().iterator().next();
  }

  public void testToString() {
    assertEquals("foo=1", entry("foo", 1).toString());
  }

  public void testToStringNull() {
    assertEquals("null=1", entry(NK, 1).toString());
    assertEquals("foo=null", entry("foo", NV).toString());
    assertEquals("null=null", entry(NK, NV).toString());
  }

  public void testEquals() {
    Entry<String, Integer> foo1 = entry("foo", 1);
    assertEquals(foo1, foo1);
    assertEquals(control("foo", 1), foo1);
    assertEquals(control("bar", 2), entry("bar", 2));
    assertFalse(control("foo", 1).equals(entry("foo", 2)));
    assertFalse(foo1.equals(control("bar", 1)));
    assertFalse(foo1.equals(new Object()));
    assertFalse(foo1.equals(null));
  }

  public void testEqualsNull() {
    assertEquals(control(NK, 1), entry(NK, 1));
    assertEquals(control("bar", NV), entry("bar", NV));
    assertFalse(control(NK, 1).equals(entry(NK, 2)));
    assertFalse(entry(NK, 1).equals(control("bar", 1)));
    assertFalse(entry(NK, 1).equals(new Object()));
    assertFalse(entry(NK, 1).equals(null));
  }

  public void testHashCode() {
    assertEquals(control("foo", 1).hashCode(), entry("foo", 1).hashCode());
    assertEquals(control("bar", 2).hashCode(), entry("bar", 2).hashCode());
  }

  public void testHashCodeNull() {
    assertEquals(control(NK, 1).hashCode(), entry(NK, 1).hashCode());
    assertEquals(control("bar", NV).hashCode(), entry("bar", NV).hashCode());
    assertEquals(control(NK, NV).hashCode(), entry(NK, NV).hashCode());
  }
}
