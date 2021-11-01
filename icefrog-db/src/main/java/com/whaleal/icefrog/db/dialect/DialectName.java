package com.whaleal.icefrog.db.dialect;

import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 方言名<br>
 * 方言枚举列出了icefrog支持的所有数据库方言
 *
 * @author Looly
 * @author wh
 */
public enum DialectName {
    ANSI, MYSQL, ORACLE, POSTGREESQL, SQLITE3, H2, SQLSERVER, SQLSERVER2012, PHOENIX;

    /**
     * 是否为指定数据库方言，检查时不分区大小写
     *
     * @param dialectName 方言名
     * @return 是否时Oracle数据库
     * @since 1.0.0
     */
    public boolean match( String dialectName ) {
        return StrUtil.equalsIgnoreCase(dialectName, name());
    }
}
