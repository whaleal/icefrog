package com.whaleal.icefrog.collections;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author wh
 */
public class RangeExample {
// Range<Integer>   colesedRange = Range.closed(0,10);

    @Test
    public void test(){
        Range<Integer>   colesedRange = Range.closed(0,10);

        System.out.println(colesedRange);

        assertTrue(colesedRange.contains(5));
        assertFalse(colesedRange.contains(11));


        assertEquals(colesedRange.lowerEndpoint(),new Integer(0));
        assertEquals(colesedRange.upperEndpoint(),new Integer(10));

    }


    @Test
    public void tesOpenClosedRange(){
        Range<Integer>   colesedRange = Range.openClosed(0,10);

        System.out.println(colesedRange);

        assertTrue(colesedRange.contains(5));
        assertFalse(colesedRange.contains(11));
        
        assertEquals(colesedRange.lowerEndpoint(),new Integer(0));
        assertEquals(colesedRange.upperEndpoint(),new Integer(10));

    }
}
