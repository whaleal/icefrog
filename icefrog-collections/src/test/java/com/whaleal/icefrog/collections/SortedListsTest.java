

package com.whaleal.icefrog.collections;


import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * Tests for SortedLists.
 *
 * 
 */

public class SortedListsTest extends TestCase {
  @Test
  public void test(){
  }
  private static final ImmutableList<Integer> LIST_WITH_DUPS =
      ImmutableList.of(1, 1, 2, 4, 4, 4, 8);

  private static final ImmutableList<Integer> LIST_WITHOUT_DUPS = ImmutableList.of(1, 2, 4, 8);

  void assertModelAgrees(
      List<Integer> list,
      Integer key,
      int answer,
      SortedLists.KeyPresentBehavior presentBehavior,
      SortedLists.KeyAbsentBehavior absentBehavior) {
    switch (presentBehavior) {
      case FIRST_PRESENT:
        if (list.contains(key)) {
          assertEquals(list.indexOf(key), answer);
          return;
        }
        break;
      case LAST_PRESENT:
        if (list.contains(key)) {
          assertEquals(list.lastIndexOf(key), answer);
          return;
        }
        break;
      case ANY_PRESENT:
        if (list.contains(key)) {
          assertEquals(key, list.get(answer));
          return;
        }
        break;
      case FIRST_AFTER:
        if (list.contains(key)) {
          assertEquals(list.lastIndexOf(key) + 1, answer);
          return;
        }
        break;
      case LAST_BEFORE:
        if (list.contains(key)) {
          assertEquals(list.indexOf(key) - 1, answer);
          return;
        }
        break;
      default:
        throw new AssertionError();
    }
    // key is not present
    int nextHigherIndex = list.size();
    for (int i = list.size() - 1; i >= 0 && list.get(i) > key; i--) {
      nextHigherIndex = i;
    }
    switch (absentBehavior) {
      case NEXT_LOWER:
        assertEquals(nextHigherIndex - 1, answer);
        return;
      case NEXT_HIGHER:
        assertEquals(nextHigherIndex, answer);
        return;
      case INVERTED_INSERTION_INDEX:
        assertEquals(-1 - nextHigherIndex, answer);
        return;
      default:
        throw new AssertionError();
    }
  }

  public void testWithoutDups() {
    for (SortedLists.KeyPresentBehavior presentBehavior : SortedLists.KeyPresentBehavior.values()) {
      for (SortedLists.KeyAbsentBehavior absentBehavior : SortedLists.KeyAbsentBehavior.values()) {
        for (int key = 0; key <= 10; key++) {
          assertModelAgrees(
              LIST_WITHOUT_DUPS,
              key,
              SortedLists.binarySearch(LIST_WITHOUT_DUPS, key, presentBehavior, absentBehavior),
              presentBehavior,
              absentBehavior);
        }
      }
    }
  }

  public void testWithDups() {
    for (SortedLists.KeyPresentBehavior presentBehavior : SortedLists.KeyPresentBehavior.values()) {
      for (SortedLists.KeyAbsentBehavior absentBehavior : SortedLists.KeyAbsentBehavior.values()) {
        for (int key = 0; key <= 10; key++) {
          assertModelAgrees(
              LIST_WITH_DUPS,
              key,
              SortedLists.binarySearch(LIST_WITH_DUPS, key, presentBehavior, absentBehavior),
              presentBehavior,
              absentBehavior);
        }
      }
    }
  }

}
