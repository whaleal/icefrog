package com.whaleal.icefrog.core.io;

import com.whaleal.icefrog.core.io.file.FileReader;
import org.junit.Assert;
import org.junit.Test;

/**
 * 文件读取测试
 *
 * @author Looly
 * @author wh
 */
public class FileReaderTest {

    @Test
    public void fileReaderTest() {
        FileReader fileReader = new FileReader("test.properties");
        String result = fileReader.readString();
        Assert.assertNotNull(result);
    }
}
