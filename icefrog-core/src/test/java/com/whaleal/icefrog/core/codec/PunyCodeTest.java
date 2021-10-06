package com.whaleal.icefrog.core.codec;

import org.junit.Assert;
import org.junit.Test;

public class PunyCodeTest {

	@Test
	public void encodeDecodeTest(){
		String text = "icefrog编码器";
		String strPunyCode = PunyCode.encode(text);
		Assert.assertEquals("icefrog-ux9js33tgln", strPunyCode);
		String decode = PunyCode.decode("icefrog-ux9js33tgln");
		Assert.assertEquals(text, decode);
		decode = PunyCode.decode("xn--icefrog-ux9js33tgln");
		Assert.assertEquals(text, decode);
	}
}
