

package com.whaleal.icefrog.collections;


import com.whaleal.icefrog.collections.Table.Cell;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Tests for {@link Tables}.
 *
 *
 */

public class TableExample extends TestCase {





  @Test
  public  void test01(){

    Table<String ,String,String> table = HashBasedTable.create();
    table.put("Language","Java","1.8");
    table.put("Language","Scala","2.3");
    table.put("DataBase","Oracle","12C");
    table.put("DataBase","Mysql","7.0");
    System.out.println(table);

    Map<String, String> language = table.row("Language");
    table.row("Language").get("Java");

    assertTrue(language.containsKey("Java"));
    assertTrue(table.row("Language").get("Java").equals("1.8"));

    Map<String, String> java = table.column("Java");

    System.out.println(java);

    Set<Cell<String, String, String>> cells = table.cellSet();

    System.out.println(cells);


  }


}
