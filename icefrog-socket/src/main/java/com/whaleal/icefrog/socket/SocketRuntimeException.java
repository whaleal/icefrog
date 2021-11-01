package com.whaleal.icefrog.socket;

import com.whaleal.icefrog.core.exceptions.ExceptionUtil;
import com.whaleal.icefrog.core.util.StrUtil;

/**
 * Socket异常
 *
 * @author Looly
 * @author wh
 */
public class SocketRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 8247610319171014183L;

    public SocketRuntimeException( Throwable e ) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public SocketRuntimeException( String message ) {
        super(message);
    }

    public SocketRuntimeException( String messageTemplate, Object... params ) {
        super(StrUtil.format(messageTemplate, params));
    }

    public SocketRuntimeException( String message, Throwable throwable ) {
        super(message, throwable);
    }

    public SocketRuntimeException( String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace ) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public SocketRuntimeException( Throwable throwable, String messageTemplate, Object... params ) {
        super(StrUtil.format(messageTemplate, params), throwable);
    }
}
