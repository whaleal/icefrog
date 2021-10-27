

package com.whaleal.icefrog.collections;





import com.whaleal.icefrog.collections.Maps;
import com.whaleal.icefrog.collections.Table;
import com.whaleal.icefrog.collections.Tables;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Test cases for {@link Tables#newCustomTable}.
 *
 *
 */

public class NewCustomTableTest extends AbstractTableTest {

  @Override
  protected Table<String, Integer, Character> create(Object... data) {
    Supplier<TreeMap<Integer, Character>> factory =
        new Supplier<TreeMap<Integer, Character>>() {
          @Override
          public TreeMap<Integer, Character> get() {
            return Maps.newTreeMap();
          }
        };
    Map<String, Map<Integer, Character>> backingMap = Maps.newLinkedHashMap();
    Table<String, Integer, Character> table = Tables.newCustomTable(backingMap, factory);
    populate(table, data);
    return table;
  }

  public void testRowKeySetOrdering() {
    table = create("foo", 3, 'a', "bar", 1, 'b', "foo", 2, 'c');
    //assertThat(table.rowKeySet()).containsExactly("foo", "bar").inOrder();
  }

  public void testRowOrdering() {
    table = create("foo", 3, 'a', "bar", 1, 'b', "foo", 2, 'c');
    //assertThat(table.row("foo").keySet()).containsExactly(2, 3).inOrder();
  }
}
