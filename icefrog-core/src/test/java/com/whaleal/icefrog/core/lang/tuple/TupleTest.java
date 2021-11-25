package com.whaleal.icefrog.core.lang.tuple;

import org.junit.Test;

import java.util.*;

/**
 * @author wh
 *
 */
public class TupleTest {


    @Test
    public void testToString() {
        TupleN tuple = TupleN.of("hello", 123, true, 186.5, null);
        String one = tuple.get(0);
        int two = tuple.get(1);
    }

    @Test
    public void testCount() {
        TupleN tuple = TupleN.of("hello", 123, true, 186.5, null);
        final long nullCount = tuple.count(null);
        final long dataCount = tuple.count(123);
    }

    @Test
    public void testForeach() {
        TupleN tuple = TupleN.of("hello", 123, true, 186.5, null);
        for (Object object : tuple) {
        }
    }

    @Test
    public void testAdd() {
        Tuple1 tuple1 = Tuple1.of("hello");
        Tuple2 tuple2 = Tuple2.of("world", "!");
        Tuple3 tuple3 = Tuple3.of(1, 2, null);
    }

    @Test
    public void testSwap() {
        TupleN tuple = TupleN.of("hello", 123, true, null, 186.5);
    }

    @Test
    public void testTuple0() {
        Tuple0 tuple0 = Tuple0.of();
        Tuple0 tuple01 = Tuple0.of();
        Tuple1 tuple1 = Tuple1.of("123");
    }

    @Test
    public void testRepeat() {
        Tuple2 tuple2 = Tuple2.of("a", null);

        try {
            tuple2.repeat(-1);
        } catch (Exception e) {
        }
    }

    @Test
    public void testTuple2() {
        Tuple2 tuple2 = Tuple2.of("test", 123);
    }

    @Test
    public void testNull() {
        Tuple2 tuple2 = Tuple2.of("test", null);
    }

    @Test
    public void testSub() {
        TupleN tupleN = TupleN.of(0, 1, 2, 3, 4, 5, 6);
        try {
            tupleN.subTuple(-1, 0);
        } catch (Exception e) {
        }

        try {
            tupleN.subTuple(0, -1);
        } catch (Exception e) {
        }

        try {
            tupleN.subTuple(0, 1000);
        } catch (Exception e) {
        }

        try {
            tupleN.subTuple(5, 3);
        } catch (Exception e) {
        }
    }

    @Test
    public void testStream() {
        TupleN tupleN = TupleN.of("hello", 123, true, null, 186.5);
        tupleN.stream().forEach(o -> System.out.printf("元素:{}", o));
        tupleN.parallelStream().forEach(o -> System.out.printf("元素:{}", o));
    }

    @Test
    public void testToList() {
        Tuple2 tuple2 = Tuple2.of("hello", 123);
    }

    @Test
    public void testToArray() {
        Tuple2 tuple2 = Tuple2.of("hello", 123);
    }

    @Test
    public void testContains() {
        Tuple2 tuple2 = Tuple2.of("hello", 123);
    }

    @Test
    public void testIterator() {
        Tuple2 tuple2 = Tuple2.of("hello", 123);
        Iterator<Object> iterator = tuple2.iterator();
        while (iterator.hasNext()) {
            System.out.printf("value:{}", iterator.next());
        }
    }

    @Test
    public void testEquals() {
        Tuple2 tuple2 = Tuple2.of("hello", 123);
        System.out.printf("equals null:{}", tuple2.equals(null));
        Tuple0 tuple0 = Tuple0.of();
        Tuple0 tuple01 = Tuple0.of();
        System.out.printf("equals same:{}", tuple0.equals(tuple01));
        System.out.printf("equals not same:{}", tuple2.equals(tuple0));
        System.out.printf("equals not same class:{}", tuple2.equals("123"));
    }

    @Test
    public void testHashCode() {
        Tuple2 tuple2 = Tuple2.of("hello", 123);
        System.out.printf("hashCode:{}", tuple2.hashCode());
    }
}
