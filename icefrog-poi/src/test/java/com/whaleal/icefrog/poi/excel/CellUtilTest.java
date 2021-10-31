package com.whaleal.icefrog.poi.excel;

import com.whaleal.icefrog.core.lang.Console;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.junit.Ignore;
import org.junit.Test;

public class CellUtilTest {

    @Test
    @Ignore
    public void isDateTest() {
        String[] all = BuiltinFormats.getAll();
        for (int i = 0; i < all.length; i++) {
            Console.log("{} {}", i, all[i]);
        }
    }
}
