package com.whaleal.icefrog.aop.proxy;

import com.whaleal.icefrog.aop.ProxyUtil;
import com.whaleal.icefrog.aop.aspects.Aspect;
import com.whaleal.icefrog.aop.interceptor.JdkInterceptor;

/**
 * JDK实现的切面代理
 *
 * @author Looly
 * @author wh
 */
public class JdkProxyFactory extends ProxyFactory {
	private static final long serialVersionUID = 1L;

	@Override
	public <T> T proxy(T target, Aspect aspect) {
		return ProxyUtil.newProxyInstance(//
				target.getClass().getClassLoader(), //
				new JdkInterceptor(target, aspect), //
				target.getClass().getInterfaces());
	}
}
