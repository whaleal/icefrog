package com.whaleal.icefrog.log.test;

import com.whaleal.icefrog.log.Log;
import com.whaleal.icefrog.log.LogFactory;
import com.whaleal.icefrog.log.dialect.commons.ApacheCommonsLogFactory;
import com.whaleal.icefrog.log.dialect.console.ConsoleLogFactory;
import com.whaleal.icefrog.log.dialect.jboss.JbossLogFactory;
import com.whaleal.icefrog.log.dialect.jdk.JdkLogFactory;
import com.whaleal.icefrog.log.dialect.log4j.Log4jLogFactory;
import com.whaleal.icefrog.log.dialect.log4j2.Log4j2LogFactory;
import com.whaleal.icefrog.log.dialect.slf4j.Slf4jLogFactory;
import com.whaleal.icefrog.log.dialect.tinylog.TinyLog2Factory;
import com.whaleal.icefrog.log.dialect.tinylog.TinyLogFactory;
import org.junit.Test;

/**
 * 日志门面单元测试
 *
 * @author Looly
 * @author wh
 */
public class CustomLogTest {

    private static final String LINE = "----------------------------------------------------------------------";
    
    @Test
    public void consoleLogTest() {
        LogFactory factory = new ConsoleLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    public void consoleLogNullTest() {
        LogFactory factory = new ConsoleLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String) null);
    }

    @Test
    public void icefrogLogTest() {
        LogFactory factory = new ApacheCommonsLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String) null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    public void tinyLogTest() {
        LogFactory factory = new TinyLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String) null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    public void tinyLog2Test() {
        LogFactory factory = new TinyLog2Factory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String) null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    public void log4j2LogTest() {
        LogFactory factory = new Log4j2LogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.debug(null);
        log.debug("This is custom '{}' log\n{}", factory.getName(), LINE);
        log.info(null);
        log.info((String) null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    public void log4jLogTest() {
        LogFactory factory = new Log4jLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String) null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);

    }

    @Test
    public void jbossLogTest() {
        LogFactory factory = new JbossLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String) null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    public void jdkLogTest() {
        LogFactory factory = new JdkLogFactory();
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String) null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }

    @Test
    public void slf4jTest() {
        LogFactory factory = new Slf4jLogFactory(false);
        LogFactory.setCurrentLogFactory(factory);
        Log log = LogFactory.get();

        log.info(null);
        log.info((String) null);
        log.info("This is custom '{}' log\n{}", factory.getName(), LINE);
    }
}
