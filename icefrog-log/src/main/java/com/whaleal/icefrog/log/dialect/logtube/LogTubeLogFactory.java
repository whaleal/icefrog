package com.whaleal.icefrog.log.dialect.logtube;

import com.whaleal.icefrog.log.Log;
import com.whaleal.icefrog.log.LogFactory;

/**
 * <a href="https://github.com/logtube/logtube-java">LogTube</a> log. 封装<br>
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class LogTubeLogFactory extends LogFactory {

    public LogTubeLogFactory() {
        super("LogTube");
        checkLogExist(io.github.logtube.Logtube.class);
    }

    @Override
    public Log createLog( String name ) {
        return new LogTubeLog(name);
    }

    @Override
    public Log createLog( Class<?> clazz ) {
        return new LogTubeLog(clazz);
    }

}
