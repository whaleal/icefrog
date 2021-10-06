package com.whaleal.icefrog.log.dialect.jboss;

import com.whaleal.icefrog.log.Log;
import com.whaleal.icefrog.log.LogFactory;

/**
 * <a href="https://github.com/jboss-logging">Jboss-Logging</a> log.
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class JbossLogFactory extends LogFactory {

	/**
	 * 构造
	 */
	public JbossLogFactory() {
		super("JBoss Logging");
		checkLogExist(org.jboss.logging.Logger.class);
	}

	@Override
	public Log createLog(String name) {
		return new JbossLog(name);
	}

	@Override
	public Log createLog(Class<?> clazz) {
		return new JbossLog(clazz);
	}

}
