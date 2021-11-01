package com.whaleal.icefrog.db.dialect.impl;

import com.whaleal.icefrog.db.Page;
import com.whaleal.icefrog.db.dialect.DialectName;
import com.whaleal.icefrog.db.sql.SqlBuilder;

/**
 * H2数据库方言
 *
 * @author Looly
 * @author wh
 */
public class H2Dialect extends AnsiSqlDialect {
    private static final long serialVersionUID = 1490520247974768214L;

    public H2Dialect() {
//		wrapper = new Wrapper('"');
    }

    @Override
    public String dialectName() {
        return DialectName.H2.name();
    }

    @Override
    protected SqlBuilder wrapPageSql( SqlBuilder find, Page page ) {
        // limit A , B 表示：A就是查询的起点位置，B就是你需要多少行。
        return find.append(" limit ").append(page.getStartPosition()).append(" , ").append(page.getPageSize());
    }
}
