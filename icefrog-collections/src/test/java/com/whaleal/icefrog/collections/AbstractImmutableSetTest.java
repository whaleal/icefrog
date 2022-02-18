

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.collection.IterUtil;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;

/**
 * Base class for {@link ImmutableSet} and {@link ImmutableSortedSet} tests.
 *
 *
 *
 */

public abstract class AbstractImmutableSetTest extends TestCase {
  @Test
  public void test(){
  }

  protected abstract <E extends Comparable<? super E>> Set<E> of();

  protected abstract <E extends Comparable<? super E>> Set<E> of(E e);

  protected abstract <E extends Comparable<? super E>> Set<E> of(E e1, E e2);

  protected abstract <E extends Comparable<? super E>> Set<E> of(E e1, E e2, E e3);

  protected abstract <E extends Comparable<? super E>> Set<E> of(E e1, E e2, E e3, E e4);

  protected abstract <E extends Comparable<? super E>> Set<E> of(E e1, E e2, E e3, E e4, E e5);

  @SuppressWarnings("unchecked")
  protected abstract <E extends Comparable<? super E>> Set<E> of(
      E e1, E e2, E e3, E e4, E e5, E e6, E... rest);

  protected abstract <E extends Comparable<? super E>> Set<E> copyOf(E[] elements);

  protected abstract <E extends Comparable<? super E>> Set<E> copyOf(
      Collection<? extends E> elements);

  protected abstract <E extends Comparable<? super E>> Set<E> copyOf(
      Iterable<? extends E> elements);

  protected abstract <E extends Comparable<? super E>> Set<E> copyOf(
      Iterator<? extends E> elements);

  public void testCreation_noArgs() {
    Set<String> set = of();
    assertEquals(Collections.<String>emptySet(), set);
    assertSame(of(), set);
  }

  public void testCreation_oneElement() {
    Set<String> set = of("a");
    assertEquals(Collections.singleton("a"), set);
  }

  public void testCreation_twoElements() {
    Set<String> set = of("a", "b");
    assertEquals(SetUtil.newHashSet("a", "b"), set);
  }

  public void testCreation_threeElements() {
    Set<String> set = of("a", "b", "c");
    assertEquals(SetUtil.newHashSet("a", "b", "c"), set);
  }

  public void testCreation_fourElements() {
    Set<String> set = of("a", "b", "c", "d");
    assertEquals(SetUtil.newHashSet("a", "b", "c", "d"), set);
  }

  public void testCreation_fiveElements() {
    Set<String> set = of("a", "b", "c", "d", "e");
    assertEquals(SetUtil.newHashSet("a", "b", "c", "d", "e"), set);
  }

  public void testCreation_sixElements() {
    Set<String> set = of("a", "b", "c", "d", "e", "f");
    assertEquals(SetUtil.newHashSet("a", "b", "c", "d", "e", "f"), set);
  }

  public void testCreation_sevenElements() {
    Set<String> set = of("a", "b", "c", "d", "e", "f", "g");
    assertEquals(SetUtil.newHashSet("a", "b", "c", "d", "e", "f", "g"), set);
  }

  public void testCreation_eightElements() {
    Set<String> set = of("a", "b", "c", "d", "e", "f", "g", "h");
    assertEquals(SetUtil.newHashSet("a", "b", "c", "d", "e", "f", "g", "h"), set);
  }

  public void testCopyOf_emptyArray() {
    String[] array = new String[0];
    Set<String> set = copyOf(array);
    assertEquals(Collections.<String>emptySet(), set);
    assertSame(of(), set);
  }

  public void testCopyOf_arrayOfOneElement() {
    String[] array = new String[] {"a"};
    Set<String> set = copyOf(array);
    assertEquals(Collections.singleton("a"), set);
  }

  public void testCopyOf_nullArray() {
    try {
      copyOf((String[]) null);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testCopyOf_arrayContainingOnlyNull() {
    String[] array = new String[] {null};
    try {
      copyOf(array);
      fail();
    } catch (NullPointerException expected) {
    }
  }

 

  enum TestEnum {
    A,
    B,
    C,
    D
  }

  public void testCopyOf_collection_enumSet() {
    Collection<TestEnum> c = EnumSet.of(TestEnum.A, TestEnum.B, TestEnum.D);
    Set<TestEnum> set = copyOf(c);
    assertEquals(3, set.size());
    assertEquals(c, set);
  }

  public void testCopyOf_iterator_empty() {
    Iterator<String> iterator = IterUtil.empty();
    Set<String> set = copyOf(iterator);
    assertEquals(Collections.<String>emptySet(), set);
    assertSame(of(), set);
  }

  public void testCopyOf_iterator_oneElement() {
    Iterator<String> iterator = CollUtil.newArrayList("a").iterator();
    Set<String> set = copyOf(iterator);
    assertEquals(Collections.singleton("a"), set);
  }

  public void testCopyOf_iterator_oneElementRepeated() {
    Iterator<String> iterator =CollUtil.newArrayList("a", "a", "a").iterator();
    Set<String> set = copyOf(iterator);
    assertEquals(Collections.singleton("a"), set);
  }

  public void testCopyOf_iterator_general() {
    Iterator<String> iterator = CollUtil.newArrayList("a", "b", "a").iterator();
    Set<String> set = copyOf(iterator);
    assertEquals(2, set.size());
    assertTrue(set.contains("a"));
    assertTrue(set.contains("b"));
  }

  public void testCopyOf_iteratorContainingNull() {
    Iterator<String> c = CollUtil.newArrayList("a", null, "b").iterator();
    try {
      copyOf(c);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  private static class CountingIterable implements Iterable<String> {
    int count = 0;

    @Override
    public Iterator<String> iterator() {
      count++;
      return CollUtil.newArrayList("a", "b", "a").iterator();
    }
  }

  public void testCopyOf_plainIterable() {
    CountingIterable iterable = new CountingIterable();
    Set<String> set = copyOf(iterable);
    assertEquals(2, set.size());
    assertTrue(set.contains("a"));
    assertTrue(set.contains("b"));
  }

  public void testCopyOf_plainIterable_iteratesOnce() {
    CountingIterable iterable = new CountingIterable();
    Set<String> unused = copyOf(iterable);
    assertEquals(1, iterable.count);
  }

  public void testCopyOf_shortcut_empty() {
    Collection<String> c = of();
    assertEquals(Collections.<String>emptySet(), copyOf(c));
    assertSame(c, copyOf(c));
  }

  public void testCopyOf_shortcut_singleton() {
    Collection<String> c = of("a");
    assertEquals(Collections.singleton("a"), copyOf(c));
    assertSame(c, copyOf(c));
  }

  public void testCopyOf_shortcut_sameType() {
    Collection<String> c = of("a", "b", "c");
    assertSame(c, copyOf(c));
  }

  public void testToString() {
    Set<String> set = of("a", "b", "c", "d", "e", "f", "g");
    assertEquals("[a, b, c, d, e, f, g]", set.toString());
  }



  public void testContainsAll_sameType() {
    Collection<String> c = of("a", "b", "c");
    assertFalse(c.containsAll(of("a", "b", "c", "d")));
    assertFalse(c.containsAll(of("a", "d")));
    assertTrue(c.containsAll(of("a", "c")));
    assertTrue(c.containsAll(of("a", "b", "c")));
  }

  public void testEquals_sameType() {
    Collection<String> c = of("a", "b", "c");
    assertTrue(c.equals(of("a", "b", "c")));
    assertFalse(c.equals(of("a", "b", "d")));
  }

  abstract <E extends Comparable<E>> ImmutableSet.Builder<E> builder();

  

  public void testBuilderWithDuplicateElements() {
    ImmutableSet<String> set =
        this.<String>builder()
            .add("a")
            .add("a", "a")
            .add("a", "a", "a")
            .add("a", "a", "a", "a")
            .build();
    assertTrue(set.contains("a"));
    assertFalse(set.contains("b"));
    assertEquals(1, set.size());
  }

 

  static final int LAST_COLOR_ADDED = 0x00BFFF;

  public void testComplexBuilder() {
    List<Integer> colorElem = asList(0x00, 0x33, 0x66, 0x99, 0xCC, 0xFF);
    // javac won't compile this without "this.<Integer>"
    ImmutableSet.Builder<Integer> webSafeColorsBuilder = this.<Integer>builder();
    for (Integer red : colorElem) {
      for (Integer green : colorElem) {
        for (Integer blue : colorElem) {
          webSafeColorsBuilder.add((red << 16) + (green << 8) + blue);
        }
      }
    }
    ImmutableSet<Integer> webSafeColors = webSafeColorsBuilder.build();
    assertEquals(216, webSafeColors.size());
    Integer[] webSafeColorArray = webSafeColors.toArray(new Integer[webSafeColors.size()]);
    assertEquals(0x000000, (int) webSafeColorArray[0]);
    assertEquals(0x000033, (int) webSafeColorArray[1]);
    assertEquals(0x000066, (int) webSafeColorArray[2]);
    assertEquals(0x003300, (int) webSafeColorArray[6]);
    assertEquals(0x330000, (int) webSafeColorArray[36]);
    ImmutableSet<Integer> addedColor = webSafeColorsBuilder.add(LAST_COLOR_ADDED).build();
    assertEquals(
        "Modifying the builder should not have changed any already built sets",
        216,
        webSafeColors.size());
    assertEquals("the new array should be one bigger than webSafeColors", 217, addedColor.size());
    Integer[] appendColorArray = addedColor.toArray(new Integer[addedColor.size()]);
    assertEquals(getComplexBuilderSetLastElement(), (int) appendColorArray[216]);
  }

  abstract int getComplexBuilderSetLastElement();

  public void testBuilderAddHandlesNullsCorrectly() {
    ImmutableSet.Builder<String> builder = this.<String>builder();
    try {
      builder.add((String) null);
      fail("expected NullPointerException"); // COV_NF_LINE
    } catch (NullPointerException expected) {
    }

    builder = this.<String>builder();
    try {
      builder.add((String[]) null);
      fail("expected NullPointerException"); // COV_NF_LINE
    } catch (NullPointerException expected) {
    }

    builder = this.<String>builder();
    try {
      builder.add("a", (String) null);
      fail("expected NullPointerException"); // COV_NF_LINE
    } catch (NullPointerException expected) {
    }

    builder = this.<String>builder();
    try {
      builder.add("a", "b", (String) null);
      fail("expected NullPointerException"); // COV_NF_LINE
    } catch (NullPointerException expected) {
    }

    builder = this.<String>builder();
    try {
      builder.add("a", "b", "c", null);
      fail("expected NullPointerException"); // COV_NF_LINE
    } catch (NullPointerException expected) {
    }

    builder = this.<String>builder();
    try {
      builder.add("a", "b", null, "c");
      fail("expected NullPointerException"); // COV_NF_LINE
    } catch (NullPointerException expected) {
    }
  }

  

}
