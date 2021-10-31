package com.whaleal.icefrog.db.ds.jndi;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.db.DbRuntimeException;
import com.whaleal.icefrog.db.DbUtil;
import com.whaleal.icefrog.db.ds.AbstractDSFactory;
import com.whaleal.icefrog.setting.Setting;

import javax.sql.DataSource;

/**
 * JNDI数据源工厂类<br>
 * Setting配置样例：<br>
 * ---------------------<br>
 * [group]<br>
 * jndi = jdbc/TestDB<br>
 * ---------------------<br>
 *
 * @author Looly
 * @author wh
 */
public class JndiDSFactory extends AbstractDSFactory {
    public static final String DS_NAME = "JNDI DataSource";
    private static final long serialVersionUID = 1573625812927370432L;

    public JndiDSFactory() {
        this(null);
    }

    public JndiDSFactory( Setting setting ) {
        super(DS_NAME, null, setting);
    }

    @Override
    protected DataSource createDataSource( String jdbcUrl, String driver, String user, String pass, Setting poolSetting ) {
        String jndiName = poolSetting.getStr("jndi");
        if (StrUtil.isEmpty(jndiName)) {
            throw new DbRuntimeException("No setting name [jndi] for this group.");
        }
        return DbUtil.getJndiDs(jndiName);
    }
}
