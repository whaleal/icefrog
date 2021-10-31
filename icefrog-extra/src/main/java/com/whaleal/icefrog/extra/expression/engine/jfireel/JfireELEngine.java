package com.whaleal.icefrog.extra.expression.engine.jfireel;

import com.jfirer.jfireel.expression.Expression;
import com.whaleal.icefrog.extra.expression.ExpressionEngine;

import java.util.Map;

/**
 * JfireEL引擎封装<br>
 * 见：https://github.com/eric_ds/jfireEL
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class JfireELEngine implements ExpressionEngine {

    /**
     * 构造
     */
    public JfireELEngine() {
    }

    @Override
    public Object eval( String expression, Map<String, Object> context ) {
        return Expression.parse(expression).calculate(context);
    }
}
