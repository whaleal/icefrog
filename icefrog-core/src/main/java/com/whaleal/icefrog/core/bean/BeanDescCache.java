package com.whaleal.icefrog.core.bean;

import com.whaleal.icefrog.core.lang.SimpleCache;
import com.whaleal.icefrog.core.lang.func.Func0;

/**
 * Bean属性缓存<br>
 * 缓存用于防止多次反射造成的性能问题
 * @author Looly
 * @author wh
 *
 */
public enum BeanDescCache {
	INSTANCE;

	private final SimpleCache<Class<?>, BeanDesc> bdCache = new SimpleCache<>();

	/**
	 * 获得属性名和{@link BeanDesc}Map映射
	 * @param beanClass Bean的类
	 * @param supplier 对象不存在时创建对象的函数
	 * @return 属性名和{@link BeanDesc}映射
	 * @since 1.0.0
	 */
	public BeanDesc getBeanDesc(Class<?> beanClass, Func0<BeanDesc> supplier){
		return bdCache.get(beanClass, supplier);
	}
}
