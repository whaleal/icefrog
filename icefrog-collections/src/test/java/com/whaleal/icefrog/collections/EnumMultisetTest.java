

package com.whaleal.icefrog.collections;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Tests for an {@link EnumMultiset}.
 *
 *
 */

public class EnumMultisetTest extends TestCase {



  private enum Color {
    BLUE,
    RED,
    YELLOW,
    GREEN,
    WHITE
  }

  private enum Gender {
    MALE,
    FEMALE
  }

  public void testClassCreate() {
    Multiset<Color> ms = EnumMultiset.create(Color.class);
    ms.add(Color.RED);
    ms.add(Color.YELLOW);
    ms.add(Color.RED);
    assertEquals(0, ms.count(Color.BLUE));
    assertEquals(1, ms.count(Color.YELLOW));
    assertEquals(2, ms.count(Color.RED));
  }

  public void testCollectionCreate() {
    Multiset<Color> ms = EnumMultiset.create(asList(Color.RED, Color.YELLOW, Color.RED));
    assertEquals(0, ms.count(Color.BLUE));
    assertEquals(1, ms.count(Color.YELLOW));
    assertEquals(2, ms.count(Color.RED));
  }

  public void testIllegalCreate() {
    Collection<Color> empty = EnumSet.noneOf(Color.class);
    try {
      EnumMultiset.create(empty);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testCreateEmptyWithClass() {
    Multiset<Color> ms = EnumMultiset.create(ImmutableList.<Color>of(), Color.class);
    ms.add(Color.RED);
  }

  public void testCreateEmptyWithoutClassFails() {
    try {
      EnumMultiset.create(ImmutableList.<Color>of());
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testToString() {
    Multiset<Color> ms = EnumMultiset.create(Color.class);
    ms.add(Color.BLUE, 3);
    ms.add(Color.YELLOW, 1);
    ms.add(Color.RED, 2);
    assertEquals("[BLUE x 3, RED x 2, YELLOW]", ms.toString());
  }



  public void testEntrySet() {
    Multiset<Color> ms = EnumMultiset.create(Color.class);
    ms.add(Color.BLUE, 3);
    ms.add(Color.YELLOW, 1);
    ms.add(Color.RED, 2);

    Set<Object> uniqueEntries = Sets.newIdentityHashSet();
    uniqueEntries.addAll(ms.entrySet());
    assertEquals(3, uniqueEntries.size());
  }

  // Wrapper of EnumMultiset factory methods, because we need to skip create(Class).
  // create(Enum1.class) is equal to create(Enum2.class) but testEquals() expects otherwise.
  // For the same reason, we need to skip create(Iterable, Class).
  private static class EnumMultisetFactory {
    public static <E extends Enum<E>> EnumMultiset<E> create(Iterable<E> elements) {
      return EnumMultiset.create(elements);
    }
  }


}
