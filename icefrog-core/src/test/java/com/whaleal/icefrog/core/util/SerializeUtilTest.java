package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.annotation.CombinationAnnotationElement;
import com.whaleal.icefrog.core.thread.lock.NoLock;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.locks.Lock;

/**
 * @author Looly
 * @author wh
 * @date 2021/10/4 10:18 下午
 */
public class SerializeUtilTest {


    @Test
    public void testserialize() {
        Person p = new Person("001", "wh", new Date());

        byte[] serialize = SerializeUtil.serialize(p);

        Assert.assertNotNull(serialize);

    }


    @Test
    public void testdeserialize() {
        Person p = new Person("001", "wh", new Date());

        byte[] serialize = SerializeUtil.serialize(p);


        Person pp = (Person) SerializeUtil.deserialize(serialize);

        Assert.assertNotNull(pp);
    }


    @Test
    public void testserialize01() {

        CombinationAnnotationElement combinationAnnotationElement = new CombinationAnnotationElement(Person.class);

        byte[] serialize = SerializeUtil.serialize(combinationAnnotationElement);

        Assert.assertNotNull(serialize);

    }


    @Test
    public void testdeserialize01() {
        CombinationAnnotationElement combinationAnnotationElement = new CombinationAnnotationElement(Person.class);

        byte[] serialize = SerializeUtil.serialize(combinationAnnotationElement);

        Assert.assertNotNull(serialize);

        CombinationAnnotationElement deserialize = SerializeUtil.deserialize(serialize);

        Assert.assertNotNull(deserialize);

    }

    @Test
    public void testserialize02() {

        Lock lock = new NoLock();

        byte[] serialize = SerializeUtil.serialize(lock);

        Assert.assertNotNull(serialize);

    }


    @Test
    public void testdeserialize02() {
        CombinationAnnotationElement combinationAnnotationElement = new CombinationAnnotationElement(Person.class);

        byte[] serialize = SerializeUtil.serialize(combinationAnnotationElement);

        Assert.assertNotNull(serialize);

        CombinationAnnotationElement deserialize = SerializeUtil.deserialize(serialize);

        Assert.assertNotNull(deserialize);

    }


}


class Person implements Serializable {
    String id;
    String name;
    Date birthday;

    public Person( String id, String name, Date birthday ) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday( Date birthday ) {
        this.birthday = birthday;
    }


}
