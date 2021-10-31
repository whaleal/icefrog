package com.whaleal.icefrog.db.dialect.impl;

import com.whaleal.icefrog.db.Page;
import com.whaleal.icefrog.db.dialect.DialectName;
import com.whaleal.icefrog.db.sql.SqlBuilder;
import com.whaleal.icefrog.db.sql.Wrapper;

/**
 * MySQL方言
 *
 * @author Looly
 * @author wh
 */
public class MysqlDialect extends AnsiSqlDialect {
    private static final long serialVersionUID = -3734718212043823636L;

    public MysqlDialect() {
        wrapper = new Wrapper('`');
    }

    @Override
    protected SqlBuilder wrapPageSql( SqlBuilder find, Page page ) {
        return find.append(" LIMIT ").append(page.getStartPosition()).append(", ").append(page.getPageSize());
    }

    @Override
    public String dialectName() {
        return DialectName.MYSQL.toString();
    }
}
