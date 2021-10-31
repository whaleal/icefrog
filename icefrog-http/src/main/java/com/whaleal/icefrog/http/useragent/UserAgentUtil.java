package com.whaleal.icefrog.http.useragent;

/**
 * User-Agent工具类
 *
 * @author Looly
 * @author wh
 */
public class UserAgentUtil {

    /**
     * 解析User-Agent
     *
     * @param userAgentString User-Agent字符串
     * @return {@link UserAgent}
     */
    public static UserAgent parse( String userAgentString ) {
        return UserAgentParser.parse(userAgentString);
    }

}
