package com.whaleal.icefrog.core.swing;

import org.junit.Ignore;
import org.junit.Test;

import com.whaleal.icefrog.core.io.FileUtil;

public class RobotUtilTest {

	@Test
	@Ignore
	public void captureScreenTest() {
		RobotUtil.captureScreen(FileUtil.file("e:/screen.jpg"));
	}
}
