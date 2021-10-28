

package com.whaleal.icefrog.collections;


import static java.util.Arrays.asList;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collector;

import com.whaleal.icefrog.collections.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests for {@link ImmutableMultiset}.
 *
 *
 */

public class ImmutableMultisetTest extends TestCase {

   // suite // TODO(cpovirk): add to collect/gwt/suites

  public void testCreation_noArgs() {
    Multiset<String> multiset = ImmutableMultiset.of();
    assertTrue(multiset.isEmpty());
  }

  public void testCreation_oneElement() {
    Multiset<String> multiset = ImmutableMultiset.of("a");
    assertEquals(HashMultiset.create(asList("a")), multiset);
  }

  public void testCreation_twoElements() {
    Multiset<String> multiset = ImmutableMultiset.of("a", "b");
    assertEquals(HashMultiset.create(asList("a", "b")), multiset);
  }

  public void testCreation_threeElements() {
    Multiset<String> multiset = ImmutableMultiset.of("a", "b", "c");
    assertEquals(HashMultiset.create(asList("a", "b", "c")), multiset);
  }

  public void testCreation_fourElements() {
    Multiset<String> multiset = ImmutableMultiset.of("a", "b", "c", "d");
    assertEquals(HashMultiset.create(asList("a", "b", "c", "d")), multiset);
  }

  public void testCreation_fiveElements() {
    Multiset<String> multiset = ImmutableMultiset.of("a", "b", "c", "d", "e");
    assertEquals(HashMultiset.create(asList("a", "b", "c", "d", "e")), multiset);
  }

  public void testCreation_sixElements() {
    Multiset<String> multiset = ImmutableMultiset.of("a", "b", "c", "d", "e", "f");
    assertEquals(HashMultiset.create(asList("a", "b", "c", "d", "e", "f")), multiset);
  }

  public void testCreation_sevenElements() {
    Multiset<String> multiset = ImmutableMultiset.of("a", "b", "c", "d", "e", "f", "g");
    assertEquals(HashMultiset.create(asList("a", "b", "c", "d", "e", "f", "g")), multiset);
  }

  public void testCreation_emptyArray() {
    String[] array = new String[0];
    Multiset<String> multiset = ImmutableMultiset.copyOf(array);
    assertTrue(multiset.isEmpty());
  }

  public void testCreation_arrayOfOneElement() {
    String[] array = new String[] {"a"};
    Multiset<String> multiset = ImmutableMultiset.copyOf(array);
    assertEquals(HashMultiset.create(asList("a")), multiset);
  }

  public void testCreation_arrayOfArray() {
    String[] array = new String[] {"a"};
    Multiset<String[]> multiset = ImmutableMultiset.<String[]>of(array);
    Multiset<String[]> expected = HashMultiset.create();
    expected.add(array);
    assertEquals(expected, multiset);
  }

  public void testCreation_arrayContainingOnlyNull() {
    String[] array = new String[] {null};
    try {
      ImmutableMultiset.copyOf(array);
      fail();
    } catch (NullPointerException expected) {
    }
  }


  public void testCopyOf_multiset_empty() {
    Multiset<String> c = HashMultiset.create();
    Multiset<String> multiset = ImmutableMultiset.copyOf(c);
    assertTrue(multiset.isEmpty());
  }

  public void testCopyOf_multiset_oneElement() {
    Multiset<String> c = HashMultiset.create(asList("a"));
    Multiset<String> multiset = ImmutableMultiset.copyOf(c);
    assertEquals(HashMultiset.create(asList("a")), multiset);
  }

  public void testCopyOf_multiset_general() {
    Multiset<String> c = HashMultiset.create(asList("a", "b", "a"));
    Multiset<String> multiset = ImmutableMultiset.copyOf(c);
    assertEquals(HashMultiset.create(asList("a", "b", "a")), multiset);
  }

  public void testCopyOf_multisetContainingNull() {
    Multiset<String> c = HashMultiset.create(asList("a", null, "b"));
    try {
      ImmutableMultiset.copyOf(c);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testCopyOf_iterator_empty() {
    Iterator<String> iterator = Iterators.emptyIterator();
    Multiset<String> multiset = ImmutableMultiset.copyOf(iterator);
    assertTrue(multiset.isEmpty());
  }

  public void testCopyOf_iterator_oneElement() {
    Iterator<String> iterator = Iterators.singletonIterator("a");
    Multiset<String> multiset = ImmutableMultiset.copyOf(iterator);
    assertEquals(HashMultiset.create(asList("a")), multiset);
  }

  public void testCopyOf_iterator_general() {
    Iterator<String> iterator = asList("a", "b", "a").iterator();
    Multiset<String> multiset = ImmutableMultiset.copyOf(iterator);
    assertEquals(HashMultiset.create(asList("a", "b", "a")), multiset);
  }

  public void testCopyOf_iteratorContainingNull() {
    Iterator<String> iterator = asList("a", null, "b").iterator();
    try {
      ImmutableMultiset.copyOf(iterator);
      fail();
    } catch (NullPointerException expected) {
    }
  }

  public void testToImmutableMultiset_duplicates() {
    class TypeWithDuplicates {
      final int a;
      final int b;

      TypeWithDuplicates(int a, int b) {
        this.a = a;
        this.b = b;
      }

      @Override
      public int hashCode() {
        return a;
      }

      @Override
      public boolean equals(Object obj) {
        return obj instanceof TypeWithDuplicates && ((TypeWithDuplicates) obj).a == a;
      }

      public boolean fullEquals(TypeWithDuplicates other) {
        return other != null && a == other.a && b == other.b;
      }
    }

  }

  private static class CountingIterable implements Iterable<String> {
    int count = 0;

    @Override
    public Iterator<String> iterator() {
      count++;
      return asList("a", "b", "a").iterator();
    }
  }

  public void testCopyOf_plainIterable() {
    CountingIterable iterable = new CountingIterable();
    Multiset<String> multiset = ImmutableMultiset.copyOf(iterable);
    assertEquals(HashMultiset.create(asList("a", "b", "a")), multiset);
    assertEquals(1, iterable.count);
  }

  public void testCopyOf_hashMultiset() {
    Multiset<String> iterable = HashMultiset.create(asList("a", "b", "a"));
    Multiset<String> multiset = ImmutableMultiset.copyOf(iterable);
    assertEquals(HashMultiset.create(asList("a", "b", "a")), multiset);
  }

  public void testCopyOf_treeMultiset() {
    Multiset<String> iterable = TreeMultiset.create(asList("a", "b", "a"));
    Multiset<String> multiset = ImmutableMultiset.copyOf(iterable);
    assertEquals(HashMultiset.create(asList("a", "b", "a")), multiset);
  }

  public void testCopyOf_shortcut_empty() {
    Collection<String> c = ImmutableMultiset.of();
    assertSame(c, ImmutableMultiset.copyOf(c));
  }

  public void testCopyOf_shortcut_singleton() {
    Collection<String> c = ImmutableMultiset.of("a");
    assertSame(c, ImmutableMultiset.copyOf(c));
  }

  public void testCopyOf_shortcut_immutableMultiset() {
    Collection<String> c = ImmutableMultiset.of("a", "b", "c");
    assertSame(c, ImmutableMultiset.copyOf(c));
  }

  public void testBuilderAdd() {
    ImmutableMultiset<String> multiset =
        new ImmutableMultiset.Builder<String>().add("a").add("b").add("a").add("c").build();
    assertEquals(HashMultiset.create(asList("a", "b", "a", "c")), multiset);
  }

  public void testBuilderAddAll() {
    List<String> a = asList("a", "b");
    List<String> b = asList("c", "d");
    ImmutableMultiset<String> multiset =
        new ImmutableMultiset.Builder<String>().addAll(a).addAll(b).build();
    assertEquals(HashMultiset.create(asList("a", "b", "c", "d")), multiset);
  }

  public void testBuilderAddAllHashMultiset() {
    Multiset<String> a = HashMultiset.create(asList("a", "b", "b"));
    Multiset<String> b = HashMultiset.create(asList("c", "b"));
    ImmutableMultiset<String> multiset =
        new ImmutableMultiset.Builder<String>().addAll(a).addAll(b).build();
    assertEquals(HashMultiset.create(asList("a", "b", "b", "b", "c")), multiset);
  }

  public void testBuilderAddAllImmutableMultiset() {
    Multiset<String> a = ImmutableMultiset.of("a", "b", "b");
    Multiset<String> b = ImmutableMultiset.of("c", "b");
    ImmutableMultiset<String> multiset =
        new ImmutableMultiset.Builder<String>().addAll(a).addAll(b).build();
    assertEquals(HashMultiset.create(asList("a", "b", "b", "b", "c")), multiset);
  }

  public void testBuilderAddAllTreeMultiset() {
    Multiset<String> a = TreeMultiset.create(asList("a", "b", "b"));
    Multiset<String> b = TreeMultiset.create(asList("c", "b"));
    ImmutableMultiset<String> multiset =
        new ImmutableMultiset.Builder<String>().addAll(a).addAll(b).build();
    assertEquals(HashMultiset.create(asList("a", "b", "b", "b", "c")), multiset);
  }

  public void testBuilderAddAllIterator() {
    Iterator<String> iterator = asList("a", "b", "a", "c").iterator();
    ImmutableMultiset<String> multiset =
        new ImmutableMultiset.Builder<String>().addAll(iterator).build();
    assertEquals(HashMultiset.create(asList("a", "b", "a", "c")), multiset);
  }

  public void testBuilderAddCopies() {
    ImmutableMultiset<String> multiset =
        new ImmutableMultiset.Builder<String>()
            .addCopies("a", 2)
            .addCopies("b", 3)
            .addCopies("c", 0)
            .build();
    assertEquals(HashMultiset.create(asList("a", "a", "b", "b", "b")), multiset);
  }

  public void testBuilderSetCount() {
    ImmutableMultiset<String> multiset =
        new ImmutableMultiset.Builder<String>().add("a").setCount("a", 2).setCount("b", 3).build();
    assertEquals(HashMultiset.create(asList("a", "a", "b", "b", "b")), multiset);
  }

  public void testBuilderAddHandlesNullsCorrectly() {
    ImmutableMultiset.Builder<String> builder = ImmutableMultiset.builder();
    try {
      builder.add((String) null);
      fail("expected NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testBuilderAddAllHandlesNullsCorrectly() {
    ImmutableMultiset.Builder<String> builder = ImmutableMultiset.builder();
    try {
      builder.addAll((Collection<String>) null);
      fail("expected NullPointerException");
    } catch (NullPointerException expected) {
    }

    builder = ImmutableMultiset.builder();
    List<String> listWithNulls = asList("a", null, "b");
    try {
      builder.addAll(listWithNulls);
      fail("expected NullPointerException");
    } catch (NullPointerException expected) {
    }

    builder = ImmutableMultiset.builder();
    Multiset<String> multisetWithNull = LinkedHashMultiset.create(asList("a", null, "b"));
    try {
      builder.addAll(multisetWithNull);
      fail("expected NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testBuilderAddCopiesHandlesNullsCorrectly() {
    ImmutableMultiset.Builder<String> builder = ImmutableMultiset.builder();
    try {
      builder.addCopies(null, 2);
      fail("expected NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testBuilderAddCopiesIllegal() {
    ImmutableMultiset.Builder<String> builder = ImmutableMultiset.builder();
    try {
      builder.addCopies("a", -2);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testBuilderSetCountHandlesNullsCorrectly() {
    ImmutableMultiset.Builder<String> builder = ImmutableMultiset.builder();
    try {
      builder.setCount(null, 2);
      fail("expected NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  public void testBuilderSetCountIllegal() {
    ImmutableMultiset.Builder<String> builder = ImmutableMultiset.builder();
    try {
      builder.setCount("a", -2);
      fail("expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
    }
  }



  public void testAsList() {
    ImmutableMultiset<String> multiset = ImmutableMultiset.of("a", "a", "b", "b", "b");
    ImmutableList<String> list = multiset.asList();
    assertEquals(ImmutableList.of("a", "a", "b", "b", "b"), list);
    assertEquals(2, list.indexOf("b"));
    assertEquals(4, list.lastIndexOf("b"));
  }

   // SerializableTester


  public static class FloodingTest extends AbstractHashFloodingTest<Multiset<Object>> {
    public FloodingTest() {
      super(
          Arrays.asList(ConstructionPathway.values()),
          n -> n * Math.log(n),
          ImmutableList.of(
              QueryOp.create(
                  "count",
                  (ms, o) -> {
                    int unused = ms.count(o);
                  },
                  Math::log)));
    }

    /** All the ways to create an ImmutableMultiset. */
    enum ConstructionPathway implements Construction<Multiset<Object>> {
      COPY_OF_COLLECTION {
        @Override
        public ImmutableMultiset<Object> create(List<?> keys) {
          return ImmutableMultiset.copyOf(keys);
        }
      },
      COPY_OF_ITERATOR {
        @Override
        public ImmutableMultiset<Object> create(List<?> keys) {
          return ImmutableMultiset.copyOf(keys.iterator());
        }
      },
      BUILDER_ADD_ENTRY_BY_ENTRY {
        @Override
        public ImmutableMultiset<Object> create(List<?> keys) {
          ImmutableMultiset.Builder<Object> builder = ImmutableMultiset.builder();
          for (Object o : keys) {
            builder.add(o);
          }
          return builder.build();
        }
      },
      BUILDER_ADD_ALL_COLLECTION {
        @Override
        public ImmutableMultiset<Object> create(List<?> keys) {
          ImmutableMultiset.Builder<Object> builder = ImmutableMultiset.builder();
          builder.addAll(keys);
          return builder.build();
        }
      };


      @Override
      public abstract ImmutableMultiset<Object> create(List<?> keys);
    }
  }
}
