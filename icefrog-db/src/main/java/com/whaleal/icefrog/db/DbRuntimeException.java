package com.whaleal.icefrog.db;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 数据库异常
 * @author Looly
 * @author wh
 */
public class DbRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 3624487785708765623L;

	public DbRuntimeException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public DbRuntimeException(String message) {
		super(message);
	}

	public DbRuntimeException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public DbRuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public DbRuntimeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public DbRuntimeException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
