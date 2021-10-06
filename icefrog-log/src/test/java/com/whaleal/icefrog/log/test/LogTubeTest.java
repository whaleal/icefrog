package com.whaleal.icefrog.log.test;

import com.whaleal.icefrog.log.Log;
import com.whaleal.icefrog.log.LogFactory;
import com.whaleal.icefrog.log.dialect.logtube.LogTubeLogFactory;
import org.junit.Test;

public class LogTubeTest {

	@Test
	public void logTest(){
		LogFactory factory = new LogTubeLogFactory();
		LogFactory.setCurrentLogFactory(factory);
		Log log = LogFactory.get();
		log.debug("LogTube debug test.");
	}
}
