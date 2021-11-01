package com.whaleal.icefrog.core.compiler;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 编译异常
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class CompilerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CompilerException( Throwable e ) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public CompilerException( String message ) {
        super(message);
    }

    public CompilerException( String messageTemplate, Object... params ) {
        super(StrUtil.format(messageTemplate, params));
    }

    public CompilerException( String message, Throwable throwable ) {
        super(message, throwable);
    }

    public CompilerException( Throwable throwable, String messageTemplate, Object... params ) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
