package com.whaleal.icefrog.cron;

import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 定时任务异常
 *
 * @author Looly
 * @author wh
 */
public class CronException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CronException(Throwable e) {
		super(e.getMessage(), e);
	}

	public CronException(String message) {
		super(message);
	}

	public CronException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public CronException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public CronException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
