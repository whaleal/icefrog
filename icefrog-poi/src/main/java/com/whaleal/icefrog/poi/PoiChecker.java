package com.whaleal.icefrog.poi;

import com.whaleal.icefrog.core.exceptions.DependencyException;
import com.whaleal.icefrog.core.util.ClassLoaderUtil;

/**
 * POI引入检查器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class PoiChecker {

    /**
     * 没有引入POI的错误消息
     */
    public static final String NO_POI_ERROR_MSG = "You need to add dependency of 'poi-ooxml' to your project, and version >= 4.1.2";

    /**
     * 检查POI包的引入情况
     */
    public static void checkPoiImport() {
        try {
            Class.forName("org.apache.poi.ss.usermodel.Workbook", false, ClassLoaderUtil.getClassLoader());
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            throw new DependencyException(e, NO_POI_ERROR_MSG);
        }
    }
}
