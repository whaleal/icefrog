package com.whaleal.icefrog.log.dialect.console;

import com.whaleal.icefrog.log.Log;
import com.whaleal.icefrog.log.LogFactory;

/**
 * 利用System.out.println()打印日志
 * @author Looly
 * @author wh
 *
 */
public class ConsoleLogFactory extends LogFactory {

	public ConsoleLogFactory() {
		super("icefrog Console Logging");
	}

	@Override
	public Log createLog(String name) {
		return new ConsoleLog(name);
	}

	@Override
	public Log createLog(Class<?> clazz) {
		return new ConsoleLog(clazz);
	}

}
