package com.whaleal.icefrog.core.net;

import com.whaleal.icefrog.core.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Test;

public class UrlDecoderTest {
	@Test
	public void decodeForPathTest() {
		Assert.assertEquals("+", URLDecoder.decodeForPath("+", CharsetUtil.CHARSET_UTF_8));
	}
}
