package com.whaleal.icefrog.log.dialect.console;

import com.whaleal.icefrog.core.date.DateUtil;
import com.whaleal.icefrog.core.lang.Preconditions;
import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.lang.Dict;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.log.AbstractLog;
import com.whaleal.icefrog.log.level.Level;

/**
 * 利用System.out.println()打印日志
 *
 * @author Looly 
 * @author wh
 */
public class ConsoleLog extends AbstractLog {
	private static final long serialVersionUID = -6843151523380063975L;

	private static final String logFormat = "[{date}] [{level}] {name}: {msg}";
	private static Level currentLevel = Level.DEBUG;

	private final String name;

	//------------------------------------------------------------------------- Constructor

	/**
	 * 构造
	 *
	 * @param clazz 类
	 */
	public ConsoleLog(Class<?> clazz) {
		this.name = (null == clazz) ? StrUtil.NULL : clazz.getName();
	}

	/**
	 * 构造
	 *
	 * @param name 类名
	 */
	public ConsoleLog(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * 设置自定义的日志显示级别
	 *
	 * @param customLevel 自定义级别
	 * @since 1.0.0
	 */
	public static void setLevel(Level customLevel) {
		Preconditions.notNull(customLevel);
		currentLevel = customLevel;
	}

	//------------------------------------------------------------------------- Trace
	@Override
	public boolean isTraceEnabled() {
		return isEnabled(Level.TRACE);
	}

	@Override
	public void trace(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.TRACE, t, format, arguments);
	}

	//------------------------------------------------------------------------- Debug
	@Override
	public boolean isDebugEnabled() {
		return isEnabled(Level.DEBUG);
	}

	@Override
	public void debug(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.DEBUG, t, format, arguments);
	}

	//------------------------------------------------------------------------- Info
	@Override
	public boolean isInfoEnabled() {
		return isEnabled(Level.INFO);
	}

	@Override
	public void info(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.INFO, t, format, arguments);
	}

	//------------------------------------------------------------------------- Warn
	@Override
	public boolean isWarnEnabled() {
		return isEnabled(Level.WARN);
	}

	@Override
	public void warn(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.WARN, t, format, arguments);
	}

	//------------------------------------------------------------------------- Error
	@Override
	public boolean isErrorEnabled() {
		return isEnabled(Level.ERROR);
	}

	@Override
	public void error(String fqcn, Throwable t, String format, Object... arguments) {
		log(fqcn, Level.ERROR, t, format, arguments);
	}

	//------------------------------------------------------------------------- Log
	@Override
	public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
		// fqcn 无效
		if (false == isEnabled(level)) {
			return;
		}


		final Dict dict = Dict.create()
				.set("date", DateUtil.now())
				.set("level", level.toString())
				.set("name", this.name)
				.set("msg", StrUtil.format(format, arguments));

		final String logMsg = StrUtil.format(logFormat, dict);

		//WARN以上级别打印至System.err
		if (level.ordinal() >= Level.WARN.ordinal()) {
			Console.error(t, logMsg);
		} else {
			Console.log(t, logMsg);
		}
	}

	@Override
	public boolean isEnabled(Level level) {
		return currentLevel.compareTo(level) <= 0;
	}
}
