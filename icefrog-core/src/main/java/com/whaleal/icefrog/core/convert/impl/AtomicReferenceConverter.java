package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.convert.AbstractConverter;
import com.whaleal.icefrog.core.convert.ConverterRegistry;
import com.whaleal.icefrog.core.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link AtomicReference}转换器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public class AtomicReferenceConverter extends AbstractConverter<AtomicReference> {
    private static final long serialVersionUID = 1L;

    @Override
    protected AtomicReference<?> convertInternal( Object value ) {

        //尝试将值转换为Reference泛型的类型
        Object targetValue = null;
        final Type paramType = TypeUtil.getTypeArgument(AtomicReference.class);
        if (false == TypeUtil.isUnknown(paramType)) {
            targetValue = ConverterRegistry.getInstance().convert(paramType, value);
        }
        if (null == targetValue) {
            targetValue = value;
        }

        return new AtomicReference<>(targetValue);
    }

}
