package com.whaleal.icefrog.core.lang.tuple;

import org.junit.Test;


/**
 * @author wh
 *
 */
public class Tuple5Test {



    @Test
    public void testof() {
        Tuple5 tuple5 = Tuple5.of(123, "test", 186.5, true, null);

    }

    @Test
    public void testSwap() {
        Tuple5 tuple5 = Tuple5.of(123, "test", 186.5, true, null);
    }
}
