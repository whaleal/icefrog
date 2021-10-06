package com.whaleal.icefrog.system;

import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.system.oshi.OshiUtil;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class OshiPrintTest {

	@Test
	public void printCpuInfo(){
		Console.log(OshiUtil.getCpuInfo());
	}
}
