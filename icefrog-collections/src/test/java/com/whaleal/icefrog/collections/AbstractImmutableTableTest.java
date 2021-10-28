

package com.whaleal.icefrog.collections;




import junit.framework.TestCase;

/**
 * Tests {@link ImmutableTable}
 *
 *
 */

public abstract class AbstractImmutableTableTest extends TestCase {

  abstract Iterable<ImmutableTable<Character, Integer, String>> getTestInstances();

  public final void testClear() {
    for (Table<Character, Integer, String> testInstance : getTestInstances()) {
      try {
        testInstance.clear();
        fail();
      } catch (UnsupportedOperationException e) {
        // success
      }
    }
  }

  public final void testPut() {
    for (Table<Character, Integer, String> testInstance : getTestInstances()) {
      try {
        testInstance.put('a', 1, "blah");
        fail();
      } catch (UnsupportedOperationException e) {
        // success
      }
    }
  }

  public final void testPutAll() {
    for (Table<Character, Integer, String> testInstance : getTestInstances()) {
      try {
        testInstance.putAll(ImmutableTable.of('a', 1, "blah"));
        fail();
      } catch (UnsupportedOperationException e) {
        // success
      }
    }
  }

  public final void testRemove() {
    for (Table<Character, Integer, String> testInstance : getTestInstances()) {
      try {
        testInstance.remove('a', 1);
        fail();
      } catch (UnsupportedOperationException e) {
        // success
      }
    }
  }

  public final void testConsistentToString() {
    for (ImmutableTable<Character, Integer, String> testInstance : getTestInstances()) {
      assertEquals(testInstance.rowMap().toString(), testInstance.toString());
    }
  }

  public final void testConsistentHashCode() {
    for (ImmutableTable<Character, Integer, String> testInstance : getTestInstances()) {
      assertEquals(testInstance.cellSet().hashCode(), testInstance.hashCode());
    }
  }
}
