

package com.whaleal.icefrog.collections;


/**
 * Test cases for {@link Tables#transpose}.
 *
 *
 */

public class TransposedTableTest extends AbstractTableTest {

  @Override
  protected Table<String, Integer, Character> create(Object... data) {
    Table<Integer, String, Character> original = HashBasedTable.create();
    Table<String, Integer, Character> table = Tables.transpose(original);
    table.clear();
    populate(table, data);
    return table;
  }

  public void testTransposeTransposed() {
    Table<Integer, String, Character> original = HashBasedTable.create();
    assertSame(original, Tables.transpose(Tables.transpose(original)));
  }

  public void testPutOriginalModifiesTranspose() {
    Table<Integer, String, Character> original = HashBasedTable.create();
    Table<String, Integer, Character> transpose = Tables.transpose(original);
    original.put(1, "foo", 'a');
    assertEquals((Character) 'a', transpose.get("foo", 1));
  }

  public void testPutTransposeModifiesOriginal() {
    Table<Integer, String, Character> original = HashBasedTable.create();
    Table<String, Integer, Character> transpose = Tables.transpose(original);
    transpose.put("foo", 1, 'a');
    assertEquals((Character) 'a', original.get(1, "foo"));
  }

  public void testTransposedViews() {
    Table<Integer, String, Character> original = HashBasedTable.create();
    Table<String, Integer, Character> transpose = Tables.transpose(original);
    original.put(1, "foo", 'a');
    assertSame(original.columnKeySet(), transpose.rowKeySet());
    assertSame(original.rowKeySet(), transpose.columnKeySet());
    assertSame(original.columnMap(), transpose.rowMap());
    assertSame(original.rowMap(), transpose.columnMap());
    assertSame(original.values(), transpose.values());
    assertEquals(original.row(1), transpose.column(1));
    assertEquals(original.row(2), transpose.column(2));
    assertEquals(original.column("foo"), transpose.row("foo"));
    assertEquals(original.column("bar"), transpose.row("bar"));
  }
}
