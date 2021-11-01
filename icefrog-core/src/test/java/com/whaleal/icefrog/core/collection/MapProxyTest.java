package com.whaleal.icefrog.core.collection;

import com.whaleal.icefrog.core.map.MapProxy;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MapProxyTest {

    @Test
    public void mapProxyTest() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "1");
        map.put("b", "2");

        MapProxy mapProxy = new MapProxy(map);
        Integer b = mapProxy.getInt("b");
        Assert.assertEquals(new Integer(2), b);

        Set<Object> keys = mapProxy.keySet();
        Assert.assertFalse(keys.isEmpty());

        Set<Entry<Object, Object>> entrys = mapProxy.entrySet();
        Assert.assertFalse(entrys.isEmpty());
    }

    @Test
    public void classProxyTest() {
        Student student = MapProxy.create(new HashMap<>()).toProxyBean(Student.class);
        student.setName("小明").setAge(18);
        Assert.assertEquals(student.getAge(), 18);
        Assert.assertEquals(student.getName(), "小明");
    }

    private interface Student {
        String getName();

        Student setName( String name );

        int getAge();

        Student setAge( int age );
    }
}
