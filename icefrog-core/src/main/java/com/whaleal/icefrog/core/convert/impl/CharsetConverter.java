package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.convert.AbstractConverter;
import com.whaleal.icefrog.core.util.CharsetUtil;

import java.nio.charset.Charset;

/**
 * 编码对象转换器
 *
 * @author Looly
 * @author wh
 */
public class CharsetConverter extends AbstractConverter<Charset> {
    private static final long serialVersionUID = 1L;

    @Override
    protected Charset convertInternal( Object value ) {
        return CharsetUtil.charset(convertToStr(value));
    }

}
