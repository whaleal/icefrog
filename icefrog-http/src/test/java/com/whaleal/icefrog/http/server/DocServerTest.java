package com.whaleal.icefrog.http.server;

import com.whaleal.icefrog.core.swing.DesktopUtil;
import com.whaleal.icefrog.http.HttpUtil;

public class DocServerTest {

	public static void main(String[] args) {
		HttpUtil.createServer(80)
				// 设置默认根目录，
				.setRoot("D:\\workspace\\site\\icefrog-site")
				.start();

		DesktopUtil.browse("http://localhost/");
	}
}
