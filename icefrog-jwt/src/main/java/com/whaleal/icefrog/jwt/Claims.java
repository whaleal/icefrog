package com.whaleal.icefrog.jwt;

import com.whaleal.icefrog.core.codec.Base64;
import com.whaleal.icefrog.core.lang.Precondition;
import com.whaleal.icefrog.core.map.MapUtil;
import com.whaleal.icefrog.json.JSONConfig;
import com.whaleal.icefrog.json.JSONObject;
import com.whaleal.icefrog.json.JSONUtil;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Claims 认证，简单的JSONObject包装
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class Claims implements Serializable {
    private static final long serialVersionUID = 1L;

    // 时间使用秒级时间戳表示
    private final JSONConfig CONFIG = JSONConfig.create().setDateFormat("#sss").setOrder(true);

    private JSONObject claimJSON;

    /**
     * 增加Claims属性，如果属性值为{@code null}，则移除这个属性
     *
     * @param name  属性名
     * @param value 属性值
     */
    protected void setClaim( String name, Object value ) {
        init();
        Precondition.notNull(name, "Name must be not null!");
        if (value == null) {
            claimJSON.remove(name);
            return;
        }
        claimJSON.set(name, value);
    }

    /**
     * 加入多个Claims属性
     *
     * @param headerClaims 多个Claims属性
     */
    protected void putAll( Map<String, ?> headerClaims ) {
        if (MapUtil.isNotEmpty(headerClaims)) {
            for (Map.Entry<String, ?> entry : headerClaims.entrySet()) {
                setClaim(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 获取指定名称属性
     *
     * @param name 名称
     * @return 属性
     */
    public Object getClaim( String name ) {
        init();
        return this.claimJSON.getObj(name);
    }

    /**
     * 获取Claims的JSON字符串形式
     *
     * @return JSON字符串
     */
    public JSONObject getClaimsJson() {
        init();
        return this.claimJSON;
    }

    /**
     * 解析JWT JSON
     *
     * @param tokenPart JWT JSON
     * @param charset   编码
     */
    public void parse( String tokenPart, Charset charset ) {
        this.claimJSON = JSONUtil.parseObj(Base64.decodeStr(tokenPart, charset), CONFIG);
    }

    @Override
    public String toString() {
        init();
        return this.claimJSON.toString();
    }

    private void init() {
        if (null == this.claimJSON) {
            this.claimJSON = new JSONObject(CONFIG);
        }
    }
}
