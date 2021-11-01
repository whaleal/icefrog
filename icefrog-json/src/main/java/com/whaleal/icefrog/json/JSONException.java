package com.whaleal.icefrog.json;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * JSON异常
 *
 * @author looly   wh
 *
 */
public class JSONException extends RuntimeException {
	private static final long serialVersionUID = 0;

	public JSONException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public JSONException(String message) {
		super(message);
	}

	public JSONException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public JSONException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSONException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public JSONException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
