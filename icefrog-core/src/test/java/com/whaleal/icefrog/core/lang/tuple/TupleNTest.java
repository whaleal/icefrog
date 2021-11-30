package com.whaleal.icefrog.core.lang.tuple;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author wh
 *
 */
public class TupleNTest {



    @Test
    @Ignore
    public void testof() {
        TupleN tupleN = TupleN.of(123, 456, "test", "hello", "world", true, 2.5, null, 'B');

        Integer first = tupleN.get(0);
        String third = tupleN.get(2);

    }

    @Test
    @Ignore
    public void testSwap() {
        TupleN tupleN = TupleN.of(123, 456, "test", "hello", "world", true, 2.5, null, 'B');
    }

}
