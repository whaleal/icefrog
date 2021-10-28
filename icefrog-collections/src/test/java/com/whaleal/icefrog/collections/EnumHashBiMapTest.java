
package com.whaleal.icefrog.collections;




import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.whaleal.icefrog.collections.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for {@code EnumHashBiMap}.
 *
 * @author Mike Bostock
 */

public class EnumHashBiMapTest extends TestCase {
  private enum Currency {
    DOLLAR,
    FRANC,
    PESO,
    POUND,
    YEN
  }

  private enum Country {
    CANADA,
    CHILE,
    JAPAN,
    SWITZERLAND,
    UK
  }



  public void testCreate() {
    EnumHashBiMap<Currency, String> bimap = EnumHashBiMap.create(Currency.class);
    assertTrue(bimap.isEmpty());
    assertEquals("{}", bimap.toString());
    assertEquals(HashBiMap.create(), bimap);
    bimap.put(Currency.DOLLAR, "dollar");
    assertEquals("dollar", bimap.get(Currency.DOLLAR));
    assertEquals(Currency.DOLLAR, bimap.inverse().get("dollar"));
  }

  public void testCreateFromMap() {
    /* Test with non-empty Map. */
    Map<Currency, String> map =
        ImmutableMap.of(
            Currency.DOLLAR, "dollar",
            Currency.PESO, "peso",
            Currency.FRANC, "franc");
    EnumHashBiMap<Currency, String> bimap = EnumHashBiMap.create(map);
    assertEquals("dollar", bimap.get(Currency.DOLLAR));
    assertEquals(Currency.DOLLAR, bimap.inverse().get("dollar"));

    /* Map must have at least one entry if not an EnumHashBiMap. */
    try {
      EnumHashBiMap.create(Collections.<Currency, String>emptyMap());
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException expected) {
    }

    /* Map can be empty if it's an EnumHashBiMap. */
    Map<Currency, String> emptyBimap = EnumHashBiMap.create(Currency.class);
    bimap = EnumHashBiMap.create(emptyBimap);
    assertTrue(bimap.isEmpty());

    /* Map can be empty if it's an EnumBiMap. */
    Map<Currency, Country> emptyBimap2 = EnumBiMap.create(Currency.class, Country.class);
    EnumHashBiMap<Currency, Country> bimap2 = EnumHashBiMap.create(emptyBimap2);
    assertTrue(bimap2.isEmpty());
  }

  public void testEnumHashBiMapConstructor() {
    /* Test that it copies existing entries. */
    EnumHashBiMap<Currency, String> bimap1 = EnumHashBiMap.create(Currency.class);
    bimap1.put(Currency.DOLLAR, "dollar");
    EnumHashBiMap<Currency, String> bimap2 = EnumHashBiMap.create(bimap1);
    assertEquals("dollar", bimap2.get(Currency.DOLLAR));
    assertEquals(bimap1, bimap2);
    bimap2.inverse().put("franc", Currency.FRANC);
    assertEquals("franc", bimap2.get(Currency.FRANC));
    assertNull(bimap1.get(Currency.FRANC));
    assertFalse(bimap2.equals(bimap1));

    /* Test that it can be empty. */
    EnumHashBiMap<Currency, String> emptyBimap = EnumHashBiMap.create(Currency.class);
    EnumHashBiMap<Currency, String> bimap3 = EnumHashBiMap.create(emptyBimap);
    assertEquals(bimap3, emptyBimap);
  }

  public void testEnumBiMapConstructor() {
    /* Test that it copies existing entries. */
    EnumBiMap<Currency, Country> bimap1 = EnumBiMap.create(Currency.class, Country.class);
    bimap1.put(Currency.DOLLAR, Country.SWITZERLAND);
    EnumHashBiMap<Currency, Object> bimap2 = // use supertype
        EnumHashBiMap.<Currency, Object>create(bimap1);
    assertEquals(Country.SWITZERLAND, bimap2.get(Currency.DOLLAR));
    assertEquals(bimap1, bimap2);
    bimap2.inverse().put("franc", Currency.FRANC);
    assertEquals("franc", bimap2.get(Currency.FRANC));
    assertNull(bimap1.get(Currency.FRANC));
    assertFalse(bimap2.equals(bimap1));

    /* Test that it can be empty. */
    EnumBiMap<Currency, Country> emptyBimap = EnumBiMap.create(Currency.class, Country.class);
    EnumHashBiMap<Currency, Country> bimap3 = // use exact type
        EnumHashBiMap.create(emptyBimap);
    assertEquals(bimap3, emptyBimap);
  }

  public void testKeyType() {
    EnumHashBiMap<Currency, String> bimap = EnumHashBiMap.create(Currency.class);
    assertEquals(Currency.class, bimap.keyType());
  }

  public void testEntrySet() {
    // Bug 3168290
    Map<Currency, String> map =
        ImmutableMap.of(
            Currency.DOLLAR, "dollar",
            Currency.PESO, "peso",
            Currency.FRANC, "franc");
    EnumHashBiMap<Currency, String> bimap = EnumHashBiMap.create(map);

    Set<Object> uniqueEntries = Sets.newIdentityHashSet();
    uniqueEntries.addAll(bimap.entrySet());
    assertEquals(3, uniqueEntries.size());
  }


}