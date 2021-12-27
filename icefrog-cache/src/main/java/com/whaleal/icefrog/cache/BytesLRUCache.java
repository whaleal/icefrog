package com.whaleal.icefrog.cache;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/*

 */

/**
 * 最近最少使用BYTES缓存
 *
 * @author lhp
 */
public class BytesLRUCache {
    /**
     * 总BytesLRUCache的MAP
     */
    private static final ConcurrentMap<BytesLRUCache, Boolean> _activeCaches = new ConcurrentHashMap<>();
    /**
     * 全局的AtomicLong _bytesInGlobalCache
     */
    private static final AtomicLong _bytesInGlobalCache = new AtomicLong(0L);

    private static volatile long _bytesGlobalCapacity = 0L;
    /**
     * 局部LinkedHashMap
     */
    final LinkedHashMap<String, byte[]> _cache;

    /**
     * The Bytes in cache.
     */
    long _bytesInCache;


    public BytesLRUCache(long bytesCapacity) {
        _bytesGlobalCapacity = bytesCapacity;
        this._bytesInCache = 0L;
        this._cache = new LinkedHashMap<>(16, 0.75F, true);
    }

    /**
     * 重制所有的缓存BytesLRUCache
     */
    public static void resetAllCache() {
        _activeCaches.clear();
        _bytesInGlobalCache.set(0L);
        _bytesGlobalCapacity = 0L;
    }

    /**
     * 公平的清除全部BytesLRUCache中的最少使用的数据
     */
    private static void fairlyEvictFromAllActiveCaches() {
        for (BytesLRUCache cache : _activeCaches.keySet()) {
            evictFromCache(cache);
        }
    }

    /**
     * 公平的清除BytesLRUCache中的最少使用的数据
     */
    private static void evictFromCache(BytesLRUCache cache) {
        long targetSize = _bytesGlobalCapacity / _activeCaches.size();
        while (cache._bytesInCache > targetSize) {
            Map.Entry<String, byte[]> oldestEntry = cache._cache.entrySet().iterator().next();
            cache._cache.remove(oldestEntry.getKey());
            _bytesInGlobalCache.addAndGet(-oldestEntry.getValue().length);
            cache._bytesInCache -= oldestEntry.getValue().length;
        }
    }

    /**
     * 根据key获取数据
     *
     * @param key key
     * @return the byte [ ]
     */
    public byte[] get(String key) {
        synchronized (this) {
            return this._cache.get(key);
        }
    }

    /**
     * 放入数据
     *
     * @param key   the key
     * @param value the byte[] value
     */
    public void put(String key, byte[] value) {
        synchronized (this) {
            if (!_activeCaches.containsKey(this)) {
                if (_activeCaches.putIfAbsent(this, Boolean.TRUE) == null) {
                    fairlyEvictFromAllActiveCaches();
                }
            }
            if (value.length <= _bytesGlobalCapacity / _activeCaches.size()) {
                byte[] oldValue = this._cache.put(key, value);
                if (oldValue != null) {
                    _bytesInGlobalCache.addAndGet(-oldValue.length);
                    this._bytesInCache -= oldValue.length;
                }
                _bytesInGlobalCache.addAndGet(value.length);
                this._bytesInCache += value.length;
                evictFromCache(this);
            }
        }
    }

    /**
     * 清除所有的缓存BytesLRUCache
     */
    public void clear() {
        synchronized (this) {
            this._cache.clear();
            _bytesInGlobalCache.addAndGet(-this._bytesInCache);
            this._bytesInCache = 0L;
            _activeCaches.remove(this);
        }
    }

    /**
     * 获取统计信息
     *
     * @return the statistics
     */
    public Statistics getStatistics() {
        synchronized (this) {
            return new Statistics(_activeCaches
                    .size(), this._bytesInCache, _bytesInGlobalCache.get(), _bytesGlobalCapacity);
        }
    }

    /**
     * 内部静态统计类
     */
    public static class Statistics {

        private final int _activeCaches;

        private final long _bytesInCache;

        private final long _bytesInGlobalCache;

        private final long _bytesGlobalCapacity;


        /**
         * Instantiates a new Statistics.
         *
         * @param pActiveCaches        the p active caches
         * @param pBytesInCache        the p bytes in cache
         * @param pBytesInGlobalCache  the p bytes in global cache
         * @param pBytesGlobalCapacity the p bytes global capacity
         */
        Statistics(int pActiveCaches, long pBytesInCache, long pBytesInGlobalCache, long pBytesGlobalCapacity) {
            this._activeCaches = pActiveCaches;
            this._bytesInCache = pBytesInCache;
            this._bytesInGlobalCache = pBytesInGlobalCache;
            this._bytesGlobalCapacity = pBytesGlobalCapacity;
        }

        /**
         * Gets active caches.
         *
         * @return the active caches
         */
        public int getActiveCaches() {
            return this._activeCaches;
        }

        /**
         * Gets bytes in cache.
         *
         * @return the bytes in cache
         */
        public long getBytesInCache() {
            return this._bytesInCache;
        }

        /**
         * Gets bytes capacity.
         *
         * @return the bytes capacity
         */
        public long getBytesCapacity() {
            if (this._activeCaches == 0) {
                return 0L;
            }
            return this._bytesGlobalCapacity / this._activeCaches;
        }

        /**
         * Gets bytes in global cache.
         *
         * @return the bytes in global cache
         */
        public long getBytesInGlobalCache() {
            return this._bytesInGlobalCache;
        }

        /**
         * Gets bytes global capacity.
         *
         * @return the bytes global capacity
         */
        public long getBytesGlobalCapacity() {
            return this._bytesGlobalCapacity;
        }

        /**
         * Gets utilization percent.
         *
         * @return the utilization percent
         */
        public double getUtilizationPercent() {
            long capacityPerCache = getBytesCapacity();
            if (capacityPerCache == 0L) {
                return 0.0D;
            }
            return this._bytesInCache / capacityPerCache * 100.0D;
        }

        /**
         * Gets global utilization percent.
         *
         * @return the global utilization percent
         */
        public double getGlobalUtilizationPercent() {
            if (this._bytesGlobalCapacity == 0L) {
                return 0.0D;
            }
            return this._bytesInGlobalCache / this._bytesGlobalCapacity * 100.0D;
        }


        @Override
        public String toString() {
            return "Cache Statistics: activeCaches=" + this._activeCaches + ", bytesInCache=" + this._bytesInCache + ", bytesCapacity=" +
                    getBytesCapacity() + ", cacheUtilization=" +
                    String.format("%.1f", Double.valueOf(getUtilizationPercent())) + ", globalBytesInCache=" + this._bytesInGlobalCache + ", globalBytesCapacity=" + this._bytesGlobalCapacity + ", globalCacheUtilization=" +
                    String.format("%.1f", Double.valueOf(getGlobalUtilizationPercent()));
        }
    }
}
