package com.whaleal.icefrog.cache;

import com.whaleal.icefrog.cache.impl.FIFOCache;
import com.whaleal.icefrog.cache.impl.LRUCache;
import com.whaleal.icefrog.cache.impl.WeakCache;
import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.thread.ConcurrencyTester;
import com.whaleal.icefrog.core.thread.ThreadUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存单元测试
 *
 * @author Looly
 * @author wh
 */
public class CacheConcurrentTest {

    @Test
    @Ignore
    public void fifoCacheTest() {
        int threadCount = 4000;
        final Cache<String, String> cache = new FIFOCache<>(3);

        // 由于缓存容量只有3，当加入第四个元素的时候，根据FIFO规则，最先放入的对象将被移除

        for (int i = 0; i < threadCount; i++) {
            ThreadUtil.execute(() -> {
                cache.put("key1", "value1", System.currentTimeMillis() * 3);
                cache.put("key2", "value2", System.currentTimeMillis() * 3);
                cache.put("key3", "value3", System.currentTimeMillis() * 3);
                cache.put("key4", "value4", System.currentTimeMillis() * 3);
                ThreadUtil.sleep(1000);
                cache.put("key5", "value5", System.currentTimeMillis() * 3);
                cache.put("key6", "value6", System.currentTimeMillis() * 3);
                cache.put("key7", "value7", System.currentTimeMillis() * 3);
                cache.put("key8", "value8", System.currentTimeMillis() * 3);
                Console.log("put all");
            });
        }

        for (int i = 0; i < threadCount; i++) {
            ThreadUtil.execute(() -> show(cache));
        }

        System.out.println("==============================");
        ThreadUtil.sleep(10000);
    }

    @Test
    @Ignore
    public void lruCacheTest() {
        int threadCount = 40000;
        final Cache<String, String> cache = new LRUCache<>(1000);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            ThreadUtil.execute(() -> {
                cache.put("key1" + index, "value1");
                cache.put("key2" + index, "value2", System.currentTimeMillis() * 3);

                int size = cache.size();
                int capacity = cache.capacity();
                if (size > capacity) {
                    Console.log("{} {}", size, capacity);
                }
                ThreadUtil.sleep(1000);
                size = cache.size();
                capacity = cache.capacity();
                if (size > capacity) {
                    Console.log("## {} {}", size, capacity);
                }
            });
        }

        ThreadUtil.sleep(5000);
    }

    private void show( Cache<String, String> cache ) {

        for (Object tt : cache) {
            Console.log(tt);
        }
    }

    @Test
    public void effectiveTest() {
        // 模拟耗时操作消耗时间
        int delay = 2000;
        AtomicInteger ai = new AtomicInteger(0);
        WeakCache<Integer, Integer> weakCache = new WeakCache<>(60 * 1000);
        ConcurrencyTester concurrencyTester = ThreadUtil.concurrencyTest(32, () -> {
            int i = ai.incrementAndGet() % 4;
            weakCache.get(i, () -> {
                ThreadUtil.sleep(delay);
                return i;
            });
        });
        long interval = concurrencyTester.getInterval();
        // 总耗时应与单次操作耗时在同一个数量级
        Assert.assertTrue(interval < delay * 2);
    }
}
