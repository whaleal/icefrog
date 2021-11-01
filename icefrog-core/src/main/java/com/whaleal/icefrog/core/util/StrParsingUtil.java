package com.whaleal.icefrog.core.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.whaleal.icefrog.core.lang.Precondition.notNull;

/**
 * Utility methods for {@link String} parsing.
 * <p>
 * String 相关的 转换工具类
 *
 * @author wh
 * @since 1.1
 */
public class StrParsingUtil extends StrUtil {

    private static final String UPPER = "\\p{Lu}|\\P{InBASIC_LATIN}";
    private static final String LOWER = "\\p{Ll}";
    private static final String CAMEL_CASE_REGEX = "(?<!(^|[%u_$]))(?=[%u])|(?<!^)(?=[%u][%l])". //
            replace("%u", UPPER).replace("%l", LOWER);

    private static final Pattern CAMEL_CASE = Pattern.compile(CAMEL_CASE_REGEX);


    /**
     * Splits up the given camel-case {@link String}.
     * 将 驼峰字符串 进行切分为多个字符串
     *
     * @param source must not be {@literal null}.
     * @return
     */
    public static List<String> splitCamelCase( String source ) {
        return split(source, false);
    }

    /**
     * Splits up the given camel-case {@link String} and returns the parts in lower case.
     * 将 驼峰字符串 进行切分为多个字符串 ，并转为小写
     *
     * @param source must not be {@literal null}.
     * @return
     */
    public static List<String> splitCamelCaseToLower( String source ) {
        return split(source, true);
    }

    /**
     * Reconcatenates the given camel-case source {@link String} using the given delimiter. Will split up the camel-case
     * {@link String} and use an uncapitalized version of the parts.
     * <p>
     * 将字符串从新连接 生成新的字符串
     *
     * @param source    must not be {@literal null}.
     * @param delimiter must not be {@literal null}.
     * @return
     */
    public static String reconcatenateCamelCase( String source, String delimiter ) {

        notNull(source, "Source string must not be null!");
        notNull(delimiter, "Delimiter must not be null!");

        return StrUtil.collectionToDelimitedString(splitCamelCaseToLower(source), delimiter);
    }

    private static List<String> split( String source, boolean toLower ) {

        notNull(source, "Source string must not be null!");

        String[] parts = CAMEL_CASE.split(source);
        List<String> result = new ArrayList<>(parts.length);

        for (String part : parts) {
            result.add(toLower ? part.toLowerCase() : part);
        }

        return Collections.unmodifiableList(result);
    }
}
