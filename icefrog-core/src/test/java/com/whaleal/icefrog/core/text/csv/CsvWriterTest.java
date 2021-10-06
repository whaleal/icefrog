package com.whaleal.icefrog.core.text.csv;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.util.CharsetUtil;
import org.junit.Ignore;
import org.junit.Test;

public class CsvWriterTest {

	@Test
	@Ignore
	public void writeWithAliasTest(){
		final CsvWriteConfig csvWriteConfig = CsvWriteConfig.defaultConfig()
				.addHeaderAlias("name", "姓名")
				.addHeaderAlias("gender", "性别");

		final CsvWriter writer = CsvUtil.getWriter(
				FileUtil.file("d:/test/csvAliasTest.csv"),
				CharsetUtil.CHARSET_GBK, false, csvWriteConfig);

		writer.writeHeaderLine("name", "gender", "address");
		writer.close();
	}
}
