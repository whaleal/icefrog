package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.convert.AbstractConverter;

import java.util.UUID;

/**
 * UUID对象转换器转换器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class UUIDConverter extends AbstractConverter<UUID> {
    private static final long serialVersionUID = 1L;

    @Override
    protected UUID convertInternal( Object value ) {
        return UUID.fromString(convertToStr(value));
    }

}
