

package com.whaleal.icefrog.collections;


import org.junit.Test;

/**
 * Tests
 *
 *
 */

public class EmptyImmutableTableTest extends AbstractImmutableTableTest {
  @Test
  public void test(){
  }
  private static final ImmutableTable<Character, Integer, String> INSTANCE = ImmutableTable.of();

  @Override
  Iterable<ImmutableTable<Character, Integer, String>> getTestInstances() {
    return ImmutableSet.of(INSTANCE);
  }

  public void testHashCode() {
    assertEquals(0, INSTANCE.hashCode());
  }

  public void testEqualsObject() {
    Table<Character, Integer, String> nonEmptyTable = HashBasedTable.create();
    nonEmptyTable.put('A', 1, "blah");


  }


  public void testToString() {
    assertEquals("{}", INSTANCE.toString());
  }

  public void testSize() {
    assertEquals(0, INSTANCE.size());
  }

  public void testGet() {
    assertNull(INSTANCE.get('a', 1));
  }

  public void testIsEmpty() {
    assertTrue(INSTANCE.isEmpty());
  }

  public void testCellSet() {
    assertEquals(ImmutableSet.of(), INSTANCE.cellSet());
  }

  public void testColumn() {
    assertEquals(ImmutableMap.of(), INSTANCE.column(1));
  }

  public void testColumnKeySet() {
    assertEquals(ImmutableSet.of(), INSTANCE.columnKeySet());
  }

  public void testColumnMap() {
    assertEquals(ImmutableMap.of(), INSTANCE.columnMap());
  }

  public void testContains() {
    assertFalse(INSTANCE.contains('a', 1));
  }

  public void testContainsColumn() {
    assertFalse(INSTANCE.containsColumn(1));
  }

  public void testContainsRow() {
    assertFalse(INSTANCE.containsRow('a'));
  }

  public void testContainsValue() {
    assertFalse(INSTANCE.containsValue("blah"));
  }

  public void testRow() {
    assertEquals(ImmutableMap.of(), INSTANCE.row('a'));
  }

  public void testRowKeySet() {
    assertEquals(ImmutableSet.of(), INSTANCE.rowKeySet());
  }

  public void testRowMap() {
    assertEquals(ImmutableMap.of(), INSTANCE.rowMap());
  }

  public void testValues() {
    assertTrue(INSTANCE.values().isEmpty());
  }
}
