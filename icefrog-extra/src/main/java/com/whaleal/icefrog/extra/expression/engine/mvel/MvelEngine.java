package com.whaleal.icefrog.extra.expression.engine.mvel;

import com.whaleal.icefrog.extra.expression.ExpressionEngine;
import org.mvel2.MVEL;

import java.util.Map;

/**
 * MVEL (MVFLEX Expression Language)引擎封装<br>
 * 见：https://github.com/mvel/mvel
 *
 *
 * @author looly
 */
public class MvelEngine implements ExpressionEngine {

	/**
	 * 构造
	 */
	public MvelEngine(){
	}

	@Override
	public Object eval(String expression, Map<String, Object> context) {
		return MVEL.eval(expression, context);
	}
}
