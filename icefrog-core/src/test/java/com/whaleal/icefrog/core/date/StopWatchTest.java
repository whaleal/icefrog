package com.whaleal.icefrog.core.date;

import org.junit.Before;
import org.junit.Test;

/**
 * @author wh
 *
 */
public class StopWatchTest {

    StopWatch state1 ;
    @Before
    public void init(){
        state1=  StopWatch.create("state1");
    }


    @Test
    public void testTime(){
        state1.start();

        for(int i= 0 ;i<9999;i++){
            System.out.println(i);
        }
        state1.stop();

        long totalTimeMillis = state1.getTotalTimeMillis();
        System.out.println(totalTimeMillis);
    }
}
