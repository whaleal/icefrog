package com.whaleal.icefrog.db.dialect.impl;

import com.whaleal.icefrog.db.dialect.DialectName;
import com.whaleal.icefrog.db.sql.Wrapper;

/**
 * SqlLite3方言
 *
 * @author Looly
 * @author wh
 */
public class Sqlite3Dialect extends AnsiSqlDialect {
    private static final long serialVersionUID = -3527642408849291634L;

    public Sqlite3Dialect() {
        wrapper = new Wrapper('[', ']');
    }

    @Override
    public String dialectName() {
        return DialectName.SQLITE3.name();
    }
}
