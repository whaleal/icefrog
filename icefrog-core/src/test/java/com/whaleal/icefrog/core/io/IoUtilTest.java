package com.whaleal.icefrog.core.io;

import com.whaleal.icefrog.core.io.resource.ResourceUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class IoUtilTest {

    @Test
    public void readBytesTest() {
        final byte[] bytes = IoUtil.readBytes(ResourceUtil.getStream("icefrog.jpg"));
        Assert.assertEquals(64668, bytes.length);
    }

    @Test
    public void readLinesTest() {
        try (BufferedReader reader = ResourceUtil.getUtf8Reader("test_lines.csv");) {
            IoUtil.readLines(reader, (LineHandler) Assert::assertNotNull);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Test
    public void copyTest(){
        String file = new File("").getAbsoluteFile().getParent()+"/LICENSE";
        String file2 = new File("").getAbsoluteFile().getParent()+"/LICENSE_bak";

        try {
            InputStream  inputStream =new FileInputStream(new File(file));

            OutputStream outputStream  = new FileOutputStream(new File(file2));

            IoUtil.copy(inputStream,outputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            new File(file2).deleteOnExit();

        }

    }
}
