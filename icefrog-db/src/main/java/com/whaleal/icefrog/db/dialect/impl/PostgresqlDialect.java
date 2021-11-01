package com.whaleal.icefrog.db.dialect.impl;

import com.whaleal.icefrog.db.dialect.DialectName;
import com.whaleal.icefrog.db.sql.Wrapper;


/**
 * Postgree方言
 *
 * @author Looly
 * @author wh
 */
public class PostgresqlDialect extends AnsiSqlDialect {
    private static final long serialVersionUID = 3889210427543389642L;

    public PostgresqlDialect() {
        wrapper = new Wrapper('"');
    }

    @Override
    public String dialectName() {
        return DialectName.POSTGREESQL.name();
    }
}
