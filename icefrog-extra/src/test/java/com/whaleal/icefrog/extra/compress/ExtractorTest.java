package com.whaleal.icefrog.extra.compress;

import com.whaleal.icefrog.core.io.FileUtil;
import com.whaleal.icefrog.core.util.CharsetUtil;
import com.whaleal.icefrog.extra.compress.extractor.Extractor;
import org.junit.Ignore;
import org.junit.Test;

public class ExtractorTest {

	@Test
	@Ignore
	public void zipTest(){
		Extractor extractor = CompressUtil.createExtractor(
				CharsetUtil.defaultCharset(),
				FileUtil.file("d:/test/c_1344112734760931330_20201230104703032.zip"));

		extractor.extract(FileUtil.file("d:/test/compress/test2/"));
	}

	@Test
	@Ignore
	public void sevenZTest(){
		Extractor extractor = CompressUtil.createExtractor(
				CharsetUtil.defaultCharset(),
				FileUtil.file("d:/test/compress/test.7z"));

		extractor.extract(FileUtil.file("d:/test/compress/test2/"));
	}
}
