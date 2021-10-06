package com.whaleal.icefrog.core.swing;

import org.junit.Assert;
import org.junit.Test;

import com.whaleal.icefrog.core.swing.clipboard.ClipboardUtil;

/**
 * 剪贴板工具类单元测试
 *
 * @author Looly
 * @author wh
 *
 */
public class ClipboardUtilTest {

	@Test
	public void setAndGetStrTest() {
		try {
			ClipboardUtil.setStr("test");

			String test = ClipboardUtil.getStr();
			Assert.assertEquals("test", test);
		} catch (java.awt.HeadlessException e) {
			// 忽略 No X11 DISPLAY variable was set, but this program performed an operation which requires it.
			// ignore
		}
	}
}
