package com.whaleal.icefrog.core.io;

import com.whaleal.icefrog.core.io.file.FileCopier;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 文件拷贝单元测试
 *
 * @author Looly
 * @author wh
 */
public class FileCopierTest {

    @Test
    @Ignore
    public void dirCopyTest() {
        FileCopier copier = FileCopier.create("D:\\Java", "e:/eclipse/eclipse2.zip");
        copier.copy();
    }

    @Test
    @Ignore
    public void dirCopyTest2() {
        //测试带.的文件夹复制
        FileCopier copier = FileCopier.create("D:\\workspace\\java\\.metadata", "D:\\workspace\\java\\.metadata\\temp");
        copier.copy();

        FileUtil.copy("D:\\workspace\\java\\looly\\icefrog\\.git", "D:\\workspace\\java\\temp", true);
    }

    @Test(expected = IORuntimeException.class)
    public void dirCopySubTest() {
        //测试父目录复制到子目录报错
        FileCopier copier = FileCopier.create("D:\\workspace\\java\\.metadata", "D:\\workspace\\java\\.metadata\\temp");
        copier.copy();
    }

    @Test
    @Ignore
    public void copyFileToDirTest() {
        FileCopier copier = FileCopier.create("d:/GReen_Soft/XshellXftpPortable.zip", "c:/hp/");
        copier.copy();
    }
}
