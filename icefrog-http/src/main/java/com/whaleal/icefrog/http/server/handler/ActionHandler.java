package com.whaleal.icefrog.http.server.handler;

import com.whaleal.icefrog.http.server.HttpServerRequest;
import com.whaleal.icefrog.http.server.HttpServerResponse;
import com.whaleal.icefrog.http.server.action.Action;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Action处理器，用于将HttpHandler转换为Action形式
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class ActionHandler implements HttpHandler {

	private final Action action;

	/**
	 * 构造
	 *
	 * @param action Action
	 */
	public ActionHandler(Action action) {
		this.action = action;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		action.doAction(
				new HttpServerRequest(httpExchange),
				new HttpServerResponse(httpExchange)
		);
	}
}
