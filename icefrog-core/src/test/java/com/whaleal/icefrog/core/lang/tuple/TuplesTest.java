package com.whaleal.icefrog.core.lang.tuple;

import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

import static com.whaleal.icefrog.core.lang.tuple.TupleUtil.tuple;


/**
 * @author wh
 *
 */
public class TuplesTest {


    @Test
    public void testTuple0() {
        Tuple0 tuple = tuple();

    }

    @Test
    public void testTuple1() {
        Tuple1<String> tuple = tuple("test");

    }

    @Test
    public void testTuple2() {
        Tuple2<String, Integer> tuple = tuple("test", 123);
    }

    @Test
    public void testTuple3() {
        Tuple3<String, Integer, Boolean> tuple = tuple("test", 123, true);
    }

    @Test
    public void testTuple4() {
        Tuple4<String, Integer, Boolean, Double> tuple = tuple("test", 123, true, 186.5);
    }

    @Test
    public void testTuple5() {
        Tuple5<String, Integer, Boolean, Double, Character> tuple = tuple("test", 123, true, 186.5, 'A');
    }

    @Test
    public void testArrayTupleN() {
        Object[] array = new Object[2];
        array[0] = "hello";
        array[1] = 456;
        TupleN tuple = tuple(array);
    }

    @Test
    public void testSort() {
        List<Tuple2> list = new ArrayList<>();
        list.add(tuple(5, "5"));
        list.add(tuple(2, "2"));
        list.add(tuple(3, "3"));
        list.add(tuple(1, "1"));
        list.add(tuple(4, "4"));

        Tuple2[] array = new Tuple2[5];
        array[0] = tuple("5", 5);
        array[1] = tuple("2", 2);
        array[2] = tuple("3", 3);
        array[3] = tuple("1", 1);
        array[4] = tuple("4", 4);


        List<Tuple2> list2 = new ArrayList<>();
        //空List传入
        list2.add(tuple(5, "5"));
        //size=1的List传入

        Tuple2[] array2 = new Tuple2[0];
        //空数组传入
        array2 = new Tuple2[1];
        array2[0] = tuple("5", 5);
        //length=1的数组传入
        try {
        } catch (Exception e) {
        }

    }
}
