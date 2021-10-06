package com.whaleal.icefrog.log.test;

import org.junit.Test;

import com.whaleal.icefrog.log.StaticLog;

public class StaticLogTest {
	@Test
	public void test() {
		StaticLog.debug("This is static {} log", "debug");
		StaticLog.info("This is static {} log", "info");
	}
}
