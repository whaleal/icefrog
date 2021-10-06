package com.whaleal.icefrog.log.dialect.tinylog;

import com.whaleal.icefrog.log.Log;
import com.whaleal.icefrog.log.LogFactory;

/**
 * <a href="http://www.tinylog.org/">TinyLog</a> log.<br>
 *
 * @author Looly
 * @author wh
 *
 */
public class TinyLogFactory extends LogFactory {

	/**
	 * 构造
	 */
	public TinyLogFactory() {
		super("TinyLog");
		checkLogExist(org.pmw.tinylog.Logger.class);
	}

	@Override
	public Log createLog(String name) {
		return new TinyLog(name);
	}

	@Override
	public Log createLog(Class<?> clazz) {
		return new TinyLog(clazz);
	}

}
