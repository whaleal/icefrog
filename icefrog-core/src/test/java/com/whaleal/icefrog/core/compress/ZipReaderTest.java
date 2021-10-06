package com.whaleal.icefrog.core.compress;

import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.util.ZipUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class ZipReaderTest {

	@Test
	@Ignore
	public void unzipTest() {
		File unzip = ZipUtil.unzip("d:/java.zip", "d:/test/java");
		Console.log(unzip);
	}
}
