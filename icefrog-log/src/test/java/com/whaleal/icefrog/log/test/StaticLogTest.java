package com.whaleal.icefrog.log.test;

import com.whaleal.icefrog.log.StaticLog;
import org.junit.Test;

public class StaticLogTest {
    @Test
    public void test() {
        StaticLog.debug("This is static {} log", "debug");
        StaticLog.info("This is static {} log", "info");
    }
}
