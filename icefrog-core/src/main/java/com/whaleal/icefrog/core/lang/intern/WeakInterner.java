package com.whaleal.icefrog.core.lang.intern;

import com.whaleal.icefrog.core.lang.SimpleCache;

/**
 * 使用WeakHashMap(线程安全)存储对象的规范化对象，注意此对象需单例使用！<br>
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class WeakInterner<T> implements Interner<T>{

	private final SimpleCache<T, T> cache = new SimpleCache<>();

	@Override
	public T intern(T sample) {
		if(null == sample){
			return null;
		}
		return cache.get(sample, ()->sample);
	}
}
