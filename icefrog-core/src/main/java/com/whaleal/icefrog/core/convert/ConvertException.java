package com.whaleal.icefrog.core.convert;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 转换异常
 *
 * @author Looly
 * @author wh
 */
public class ConvertException extends RuntimeException {
    private static final long serialVersionUID = 4730597402855274362L;

    public ConvertException( Throwable e ) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public ConvertException( String message ) {
        super(message);
    }

    public ConvertException( String messageTemplate, Object... params ) {
        super(StrUtil.format(messageTemplate, params));
    }

    public ConvertException( String message, Throwable throwable ) {
        super(message, throwable);
    }

    public ConvertException( Throwable throwable, String messageTemplate, Object... params ) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
