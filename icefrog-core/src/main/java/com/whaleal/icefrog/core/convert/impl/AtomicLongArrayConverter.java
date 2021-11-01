package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.convert.AbstractConverter;
import com.whaleal.icefrog.core.convert.Convert;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * {@link AtomicLongArray}转换器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class AtomicLongArrayConverter extends AbstractConverter<AtomicLongArray> {
    private static final long serialVersionUID = 1L;

    @Override
    protected AtomicLongArray convertInternal( Object value ) {
        return new AtomicLongArray(Convert.convert(long[].class, value));
    }

}
