package com.whaleal.icefrog.core.convert.impl;

import java.util.Currency;

import com.whaleal.icefrog.core.convert.AbstractConverter;

/**
 * 货币{@link Currency} 转换器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class CurrencyConverter extends AbstractConverter<Currency> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Currency convertInternal(Object value) {
		return Currency.getInstance(convertToStr(value));
	}

}
