package com.whaleal.icefrog.extra.expression.engine.rhino;

import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.extra.expression.ExpressionEngine;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.Map;

/**
 * rhino引擎封装<br>
 * 见：https://github.com/mozilla/rhino
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class RhinoEngine implements ExpressionEngine {

    @Override
    public Object eval( String expression, Map<String, Object> context ) {
        final Context ctx = Context.enter();
        final Scriptable scope = ctx.initStandardObjects();
        if (MapUtil.isNotEmpty(context)) {
            context.forEach(( key, value ) -> {
                // 将java对象转为js对象后放置于JS的作用域中
                ScriptableObject.putProperty(scope, key, Context.javaToJS(value, scope));
            });
        }
        final Object result = ctx.evaluateString(scope, expression, "rhino.js", 1, null);
        Context.exit();
        return result;
    }
}
