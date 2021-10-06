package com.whaleal.icefrog.http.server;

import com.whaleal.icefrog.core.util.CharsetUtil;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;

import java.nio.charset.Charset;

/**
 * HttpServer公用对象，提供HttpExchange包装和公用方法
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class HttpServerBase {

	final static Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;

	final HttpExchange httpExchange;

	/**
	 * 构造
	 *
	 * @param httpExchange {@link HttpExchange}
	 */
	public HttpServerBase(HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
	}

	/**
	 * 获取{@link HttpExchange}对象
	 *
	 * @return {@link HttpExchange}对象
	 */
	public HttpExchange getHttpExchange() {
		return this.httpExchange;
	}

	/**
	 * 获取{@link HttpContext}
	 *
	 * @return {@link HttpContext}
	 * @since 1.0.0
	 */
	public HttpContext getHttpContext() {
		return getHttpExchange().getHttpContext();
	}
}
