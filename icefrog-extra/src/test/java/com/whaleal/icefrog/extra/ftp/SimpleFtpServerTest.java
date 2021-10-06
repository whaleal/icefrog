package com.whaleal.icefrog.extra.ftp;

public class SimpleFtpServerTest {

	public static void main(String[] args) {
		SimpleFtpServer
				.create()
				.addAnonymous("d:/test/ftp/")
				.start();
	}
}
