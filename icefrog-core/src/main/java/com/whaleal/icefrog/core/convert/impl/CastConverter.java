package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.convert.AbstractConverter;
import com.whaleal.icefrog.core.convert.ConvertException;

/**
 * 强转转换器
 *
 * @param <T> 强制转换到的类型
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class CastConverter<T> extends AbstractConverter<T> {
    private static final long serialVersionUID = 1L;

    private Class<T> targetType;

    protected CastConverter( Class<T> type ) {
        this.targetType = type;

    }

    @Override
    protected T convertInternal( Object value ) {
        // 由于在AbstractConverter中已经有类型判断并强制转换，因此当在上一步强制转换失败时直接抛出异常
        throw new ConvertException("Can not cast value to [{}]", this.targetType);
    }

    @Override
    public Class<T> getTargetType() {
        return this.targetType;
    }
}
