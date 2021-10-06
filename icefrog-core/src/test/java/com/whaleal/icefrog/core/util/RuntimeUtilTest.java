package com.whaleal.icefrog.core.util;

import com.whaleal.icefrog.core.lang.Console;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 命令行单元测试
 * @author Looly
 * @author wh
 *
 */
public class RuntimeUtilTest {

	@Test
	@Ignore
	public void execTest() {
		String str = RuntimeUtil.execForStr("ipconfig");
		Console.log(str);
	}

	@Test
	@Ignore
	public void execCmdTest() {
		String str = RuntimeUtil.execForStr("cmd /c dir");
		Console.log(str);
	}

	@Test
	@Ignore
	public void execCmdTest2() {
		String str = RuntimeUtil.execForStr("cmd /c", "cd \"C:\\Program Files (x86)\"", "chdir");
		Console.log(str);
	}

	@Test
	public void getUsableMemoryTest(){
		Assert.assertTrue(RuntimeUtil.getUsableMemory() > 0);
	}
}
