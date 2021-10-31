package com.whaleal.icefrog.db.ds.tomcat;

import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.db.ds.AbstractDSFactory;
import com.whaleal.icefrog.setting.Setting;
import com.whaleal.icefrog.setting.dialect.Props;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * Tomcat-Jdbc-Pool数据源工厂类
 *
 * @author Looly
 * @author wh
 */
public class TomcatDSFactory extends AbstractDSFactory {
    public static final String DS_NAME = "Tomcat-Jdbc-Pool";
    private static final long serialVersionUID = 4925514193275150156L;

    /**
     * 构造
     */
    public TomcatDSFactory() {
        this(null);
    }

    /**
     * 构造
     *
     * @param setting Setting数据库配置
     */
    public TomcatDSFactory( Setting setting ) {
        super(DS_NAME, DataSource.class, setting);
    }

    @Override
    protected javax.sql.DataSource createDataSource( String jdbcUrl, String driver, String user, String pass, Setting poolSetting ) {
        final PoolProperties poolProps = new PoolProperties();
        poolProps.setUrl(jdbcUrl);
        poolProps.setDriverClassName(driver);
        poolProps.setUsername(user);
        poolProps.setPassword(pass);

        // remarks等特殊配置，since 5.3.8
        final Props connProps = new Props();
        String connValue;
        for (String key : KEY_CONN_PROPS) {
            connValue = poolSetting.getAndRemoveStr(key);
            if (StrUtil.isNotBlank(connValue)) {
                connProps.setProperty(key, connValue);
            }
        }
        poolProps.setDbProperties(connProps);

        // 连接池相关参数
        poolSetting.toBean(poolProps);

        return new DataSource(poolProps);
    }
}
