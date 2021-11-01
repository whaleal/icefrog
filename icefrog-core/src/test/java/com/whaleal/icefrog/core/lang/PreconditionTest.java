package com.whaleal.icefrog.core.lang;

import org.junit.Test;

public class PreconditionTest {

    @Test
    public void isNullTest() {
        String a = null;
        Precondition.isNull(a);
    }

    @Test
    public void notNullTest() {
        String a = null;
        Precondition.isNull(a);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isTrueTest() {
        int i = 0;
        //noinspection ConstantConditions
        Precondition.isTrue(i > 0, IllegalArgumentException::new);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void isTrueTest2() {
        int i = -1;
        //noinspection ConstantConditions
        Precondition.isTrue(i >= 0, IndexOutOfBoundsException::new);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void isTrueTest3() {
        int i = -1;
        //noinspection ConstantConditions
        Precondition.isTrue(i > 0, () -> new IndexOutOfBoundsException("relation message to return"));
    }
}
