/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whaleal.icefrog.collections;




import com.whaleal.icefrog.collections.HashBasedTable;
import com.whaleal.icefrog.collections.Table.Cell;
import com.whaleal.icefrog.collections.Tables;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;

/**
 * Tests for {@link Tables}.
 *
 * @author Jared Levy
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
    table.row("Language").get("Java")

    assertTrue(language.containsKey("Java"));
    assertTrue(table.row("Language").get("Java").equals("1.8"));

    Map<String, String> java = table.column("Java");

    System.out.println(java);

    Set<Cell<String, String, String>> cells = table.cellSet();

    System.out.println(cells);


  }


}
