package com.whaleal.icefrog.crypto.test.symmetric;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.crypto.Mode;
import com.whaleal.icefrog.crypto.Padding;
import com.whaleal.icefrog.crypto.SecureUtil;
import com.whaleal.icefrog.crypto.symmetric.DES;
import org.junit.Assert;
import org.junit.Test;

/**
 * DES加密解密单元测试
 */
public class DesTest {

	@Test
	public void encryptDecryptTest(){
		String content = "我是一个测试的test字符串123";
		final DES des = SecureUtil.des();

		final String encryptHex = des.encryptHex(content);
		final String result = des.decryptStr(encryptHex);

		Assert.assertEquals(content, result);
	}

	@Test
	public void encryptDecryptWithCustomTest(){
		String content = "我是一个测试的test字符串123";
		final DES des = new DES(
				Mode.CTS,
				Padding.PKCS5Padding,
				StrUtil.bytes("12345678"),
				StrUtil.bytes("11223344")
		);

		final String encryptHex = des.encryptHex(content);
		final String result = des.decryptStr(encryptHex);

		Assert.assertEquals(content, result);
	}
}
