package com.whaleal.icefrog.core.lang;

import com.whaleal.icefrog.core.exceptions.UtilException;
import com.whaleal.icefrog.core.thread.ThreadUtil;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class SingletonTest {

    @Test
    public void getTest() {
        // 此测试中使用1000个线程获取单例对象，其间对象只被创建一次
        ThreadUtil.concurrencyTest(1000, () -> Singleton.get(TestBean.class));
    }

    /**
     * 测试单例构建属性锁死问题
     * C构建单例时候，同时构建B，此时在SimpleCache中会有写锁竞争（写入C时获取了写锁，此时要写入B，也要获取写锁）
     */
    @Test(timeout = 1000L)
    public void reentrantTest() {
        final C c = Singleton.get(C.class);
        Assert.assertEquals("aaa", c.getB().getA());
    }

    @Data
    static class TestBean {
        private static volatile TestBean testSingleton;
        private String name;
        private String age;
        public TestBean() {
            if (null != testSingleton) {
                throw new UtilException("单例测试中，对象被创建了两次！");
            }
            testSingleton = this;
        }
    }

    @Data
    static class B {
        private String a = "aaa";
    }

    @Data
    static class C {
        private B b = Singleton.get(B.class);
    }
}
