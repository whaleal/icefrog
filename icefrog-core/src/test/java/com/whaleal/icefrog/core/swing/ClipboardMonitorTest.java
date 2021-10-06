package com.whaleal.icefrog.core.swing;

import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.swing.clipboard.ClipboardUtil;
import org.junit.Ignore;
import org.junit.Test;

public class ClipboardMonitorTest {

	@Test
	@Ignore
	public void monitorTest() {
		// 第一个监听
		ClipboardUtil.listen((clipboard, contents) -> {
			Object object = ClipboardUtil.getStr(contents);
			Console.log("1# {}", object);
			return contents;
		}, false);

		// 第二个监听
		ClipboardUtil.listen((clipboard, contents) -> {
			Object object = ClipboardUtil.getStr(contents);
			Console.log("2# {}", object);
			return contents;
		});

	}
}
