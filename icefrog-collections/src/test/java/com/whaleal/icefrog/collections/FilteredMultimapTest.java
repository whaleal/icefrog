

package com.whaleal.icefrog.collections;


import java.util.Arrays;
import java.util.Map.Entry;

import com.whaleal.icefrog.core.util.Predicate;
import junit.framework.TestCase;

/**
 * Unit tests for {@link Multimaps} filtering methods.
 *
 *
 */
 // nottested
public class FilteredMultimapTest extends TestCase {

  private static final Predicate<Entry<String, Integer>> ENTRY_PREDICATE =
      new Predicate<Entry<String, Integer>>() {
        @Override
        public boolean apply(Entry<String, Integer> entry) {
          return !"badkey".equals(entry.getKey()) && !((Integer) 55556).equals(entry.getValue());
        }
      };

  protected Multimap<String, Integer> create() {
    Multimap<String, Integer> unfiltered = HashMultimap.create();
    unfiltered.put("foo", 55556);
    unfiltered.put("badkey", 1);
    return Multimaps.filterEntries(unfiltered, ENTRY_PREDICATE);
  }

  private static final Predicate<String> KEY_PREDICATE =
      new Predicate<String>() {
        @Override
        public boolean apply(String key) {
          return !"badkey".equals(key);
        }
      };

  public void testFilterKeys() {
    Multimap<String, Integer> unfiltered = HashMultimap.create();
    unfiltered.put("foo", 55556);
    unfiltered.put("badkey", 1);
    Multimap<String, Integer> filtered = Multimaps.filterKeys(unfiltered, KEY_PREDICATE);
    assertEquals(1, filtered.size());
    assertTrue(filtered.containsEntry("foo", 55556));
  }

  private static final Predicate<Integer> VALUE_PREDICATE =
      new Predicate<Integer>() {
        @Override
        public boolean apply(Integer value) {
          return !((Integer) 55556).equals(value);
        }
      };

  public void testFilterValues() {
    Multimap<String, Integer> unfiltered = HashMultimap.create();
    unfiltered.put("foo", 55556);
    unfiltered.put("badkey", 1);
    Multimap<String, Integer> filtered = Multimaps.filterValues(unfiltered, VALUE_PREDICATE);
    assertEquals(1, filtered.size());
    assertFalse(filtered.containsEntry("foo", 55556));
    assertTrue(filtered.containsEntry("badkey", 1));
  }

  public void testFilterFiltered() {
    Multimap<String, Integer> unfiltered = HashMultimap.create();
    unfiltered.put("foo", 55556);
    unfiltered.put("badkey", 1);
    unfiltered.put("foo", 1);
    Multimap<String, Integer> keyFiltered = Multimaps.filterKeys(unfiltered, KEY_PREDICATE);
    Multimap<String, Integer> filtered = Multimaps.filterValues(keyFiltered, VALUE_PREDICATE);
    assertEquals(1, filtered.size());
    assertTrue(filtered.containsEntry("foo", 1));
    assertTrue(filtered.keySet().retainAll(Arrays.asList("cat", "dog")));
    assertEquals(0, filtered.size());
  }

  // TODO(jlevy): Many more tests needed.
}
