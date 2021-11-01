package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.convert.AbstractConverter;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.core.util.ObjectUtil;

import java.util.Map;

/**
 * {@link StackTraceElement} 转换器<br>
 * 只支持Map方式转换
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class StackTraceElementConverter extends AbstractConverter<StackTraceElement> {
    private static final long serialVersionUID = 1L;

    @Override
    protected StackTraceElement convertInternal( Object value ) {
        if (value instanceof Map) {
            final Map<?, ?> map = (Map<?, ?>) value;

            final String declaringClass = MapUtil.getStr(map, "className");
            final String methodName = MapUtil.getStr(map, "methodName");
            final String fileName = MapUtil.getStr(map, "fileName");
            final Integer lineNumber = MapUtil.getInt(map, "lineNumber");

            return new StackTraceElement(declaringClass, methodName, fileName, ObjectUtil.defaultIfNull(lineNumber, 0));
        }
        return null;
    }

}
