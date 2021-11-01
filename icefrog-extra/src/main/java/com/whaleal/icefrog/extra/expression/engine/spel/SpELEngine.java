package com.whaleal.icefrog.extra.expression.engine.spel;

import com.whaleal.icefrog.extra.expression.ExpressionEngine;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * Spring-Expression引擎封装<br>
 * 见：https://github.com/spring-projects/spring-framework/tree/master/spring-expression
 *
 *
 * @author looly
 */
public class SpELEngine implements ExpressionEngine {

	private final ExpressionParser parser;

	/**
	 * 构造
	 */
	public SpELEngine(){
		parser = new SpelExpressionParser();
	}

	@Override
	public Object eval(String expression, Map<String, Object> context) {
		final EvaluationContext evaluationContext = new StandardEvaluationContext();
		context.forEach(evaluationContext::setVariable);
		return parser.parseExpression(expression).getValue(evaluationContext);
	}
}
