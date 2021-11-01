package com.whaleal.icefrog.poi.word;

import java.io.File;

/**
 * Word工具类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class WordUtil {
    /**
     * 创建Word 07格式的生成器
     *
     * @return {@link Word07Writer}
     */
    public static Word07Writer getWriter() {
        return new Word07Writer();
    }

    /**
     * 创建Word 07格式的生成器
     *
     * @param destFile 目标文件
     * @return {@link Word07Writer}
     */
    public static Word07Writer getWriter( File destFile ) {
        return new Word07Writer(destFile);
    }
}
