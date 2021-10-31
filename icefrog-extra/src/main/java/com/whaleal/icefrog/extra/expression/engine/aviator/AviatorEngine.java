package com.whaleal.icefrog.extra.expression.engine.aviator;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.whaleal.icefrog.extra.expression.ExpressionEngine;

import java.util.Map;

/**
 * Aviator引擎封装<br>
 * 见：https://github.com/killme2008/aviatorscript
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class AviatorEngine implements ExpressionEngine {

    private final AviatorEvaluatorInstance engine;

    /**
     * 构造
     */
    public AviatorEngine() {
        engine = AviatorEvaluator.getInstance();
    }

    @Override
    public Object eval( String expression, Map<String, Object> context ) {
        return engine.execute(expression, context);
    }

    /**
     * 获取{@link AviatorEvaluatorInstance}
     *
     * @return {@link AviatorEvaluatorInstance}
     */
    public AviatorEvaluatorInstance getEngine() {
        return this.engine;
    }
}
