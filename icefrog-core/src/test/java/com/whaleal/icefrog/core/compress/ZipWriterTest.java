package com.whaleal.icefrog.core.compress;

import com.whaleal.icefrog.core.util.ZipUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class ZipWriterTest {

	@Test
	@Ignore
	public void zipDirTest() {
		ZipUtil.zip(new File("d:/test"));
	}
}
