package com.whaleal.icefrog.core.convert.impl;

import com.whaleal.icefrog.core.convert.AbstractConverter;

import java.util.TimeZone;

/**
 * TimeZone转换器
 *
 * @author Looly
 * @author wh
 */
public class TimeZoneConverter extends AbstractConverter<TimeZone> {
    private static final long serialVersionUID = 1L;

    @Override
    protected TimeZone convertInternal( Object value ) {
        return TimeZone.getTimeZone(convertToStr(value));
    }

}
