package com.whaleal.icefrog.extra.template;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 模板异常
 *
 * @author Looly
 * @author wh
 */
public class TemplateException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public TemplateException( Throwable e ) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public TemplateException( String message ) {
        super(message);
    }

    public TemplateException( String messageTemplate, Object... params ) {
        super(StrUtil.format(messageTemplate, params));
    }

    public TemplateException( String message, Throwable throwable ) {
        super(message, throwable);
    }

    public TemplateException( String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace ) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public TemplateException( Throwable throwable, String messageTemplate, Object... params ) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
