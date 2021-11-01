package com.whaleal.icefrog.http.useragent;

import com.whaleal.icefrog.core.collection.CollUtil;
import com.whaleal.icefrog.core.util.ReUtil;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 引擎对象
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class Engine extends UserAgentInfo {
    /**
     * 未知
     */
    public static final Engine Unknown = new Engine(NameUnknown, null);
    /**
     * 支持的引擎类型
     */
    public static final List<Engine> engines = CollUtil.newArrayList(//
            new Engine("Trident", "trident"), //
            new Engine("Webkit", "webkit"), //
            new Engine("Chrome", "chrome"), //
            new Engine("Opera", "opera"), //
            new Engine("Presto", "presto"), //
            new Engine("Gecko", "gecko"), //
            new Engine("KHTML", "khtml"), //
            new Engine("Konqeror", "konqueror"), //
            new Engine("MIDP", "MIDP")//
    );
    private static final long serialVersionUID = 1L;
    private final Pattern versionPattern;

    /**
     * 构造
     *
     * @param name  引擎名称
     * @param regex 关键字或表达式
     */
    public Engine( String name, String regex ) {
        super(name, regex);
        this.versionPattern = Pattern.compile(name + "[/\\- ]([\\d\\w.\\-]+)", Pattern.CASE_INSENSITIVE);
    }

    /**
     * 获取引擎版本
     *
     * @param userAgentString User-Agent字符串
     * @return 版本
     * @since 1.0.0
     */
    public String getVersion( String userAgentString ) {
        if (isUnknown()) {
            return null;
        }
        return ReUtil.getGroup1(this.versionPattern, userAgentString);
    }
}
