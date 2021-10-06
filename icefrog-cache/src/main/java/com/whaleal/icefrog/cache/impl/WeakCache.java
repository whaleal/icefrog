package com.whaleal.icefrog.cache.impl;

import java.util.WeakHashMap;

/**
 * 弱引用缓存<br>
 * 对于一个给定的键，其映射的存在并不阻止垃圾回收器对该键的丢弃，这就使该键成为可终止的，被终止，然后被回收。<br>
 * 丢弃某个键时，其条目从映射中有效地移除。<br>
 *
 * @author Looly
 * @author wh
 *
 * @param <K> 键
 * @param <V> 值
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class WeakCache<K, V> extends TimedCache<K, V>{
	private static final long serialVersionUID = 1L;

	public WeakCache(long timeout) {
		super(timeout, new WeakHashMap<>());
	}

}
