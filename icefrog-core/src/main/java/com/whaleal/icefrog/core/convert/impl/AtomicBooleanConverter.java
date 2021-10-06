package com.whaleal.icefrog.core.convert.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import com.whaleal.icefrog.core.convert.AbstractConverter;
import com.whaleal.icefrog.core.util.BooleanUtil;

/**
 * {@link AtomicBoolean}转换器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class AtomicBooleanConverter extends AbstractConverter<AtomicBoolean> {
	private static final long serialVersionUID = 1L;

	@Override
	protected AtomicBoolean convertInternal(Object value) {
		if (value instanceof Boolean) {
			return new AtomicBoolean((Boolean) value);
		}
		final String valueStr = convertToStr(value);
		return new AtomicBoolean(BooleanUtil.toBoolean(valueStr));
	}

}
