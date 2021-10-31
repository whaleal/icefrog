package com.whaleal.icefrog.core.swing;

import com.whaleal.icefrog.core.io.FileUtil;
import org.junit.Ignore;
import org.junit.Test;

public class RobotUtilTest {

    @Test
    @Ignore
    public void captureScreenTest() {
        RobotUtil.captureScreen(FileUtil.file("e:/screen.jpg"));
    }
}
