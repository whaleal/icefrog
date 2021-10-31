package com.whaleal.icefrog.http.server;

import com.whaleal.icefrog.core.swing.DesktopUtil;
import com.whaleal.icefrog.http.ContentType;
import com.whaleal.icefrog.http.HttpUtil;

public class BlankServerTest {
    public static void main( String[] args ) {
        HttpUtil.createServer(8888)
                .addAction("/", ( req, res ) -> res.write("Hello icefrog Server", ContentType.JSON.getValue()))
                .start();

        DesktopUtil.browse("http://localhost:8888/");
    }
}
