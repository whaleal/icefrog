

package com.whaleal.icefrog.collections;


import java.util.function.Function;

import static com.whaleal.icefrog.core.lang.Preconditions.checkArgument;

/**
 * Test cases for {@link Tables#transformValues}.
 *
 *
 */

public class TablesTransformValuesTest extends AbstractTableTest {

  private static final Function<String, Character> FIRST_CHARACTER =
      new Function<String, Character>() {
        @Override
        public Character apply(String input) {
          return input == null ? null : input.charAt(0);
        }
      };

  @Override
  protected Table<String, Integer, Character> create(Object... data) {
    Table<String, Integer, String> table = HashBasedTable.create();
    checkArgument(data.length % 3 == 0);
    for (int i = 0; i < data.length; i += 3) {
      String value = (data[i + 2] == null) ? null : (data[i + 2] + "transformed");
      table.put((String) data[i], (Integer) data[i + 1], value);
    }
    return Tables.transformValues(table, FIRST_CHARACTER);
  }



  // put() and putAll() aren't supported.
  @Override
  public void testPut() {
    try {
      table.put("foo", 1, 'a');
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
    }
    assertSize(0);
  }

  @Override
  public void testPutAllTable() {
    table = create("foo", 1, 'a', "bar", 1, 'b', "foo", 3, 'c');
    Table<String, Integer, Character> other = HashBasedTable.create();
    other.put("foo", 1, 'd');
    other.put("bar", 2, 'e');
    other.put("cat", 2, 'f');
    try {
      table.putAll(other);
      fail("Expected UnsupportedOperationException");
    } catch (UnsupportedOperationException expected) {
    }
    assertEquals((Character) 'a', table.get("foo", 1));
    assertEquals((Character) 'b', table.get("bar", 1));
    assertEquals((Character) 'c', table.get("foo", 3));
    assertSize(3);
  }

  @Override
  public void testPutNull() {}

  @Override
  public void testPutNullReplace() {}

  @Override
  public void testRowClearAndPut() {}
}
