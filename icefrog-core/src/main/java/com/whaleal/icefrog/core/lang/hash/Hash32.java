package com.whaleal.icefrog.core.lang.hash;

/**
 * Hash计算接口
 *
 * @param <T> 被计算hash的对象类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
@FunctionalInterface
public interface Hash32<T> {
	/**
	 * 计算Hash值
	 *
	 * @param t 对象
	 * @return hash
	 */
	int hash32(T t);
}
