package com.whaleal.icefrog.http.server.filter;

import com.sun.net.httpserver.Filter;

/**
 * 匿名简单过滤器，跳过了描述
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public abstract class SimpleFilter extends Filter {

	@Override
	public String description() {
		return "Anonymous Filter";
	}
}
