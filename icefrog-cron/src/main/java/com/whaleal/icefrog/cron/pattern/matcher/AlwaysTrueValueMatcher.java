package com.whaleal.icefrog.cron.pattern.matcher;

import com.whaleal.icefrog.core.util.StrUtil;

/**
 * 值匹配，始终返回<code>true</code>
 *
 * @author Looly
 * @author wh
 */
public class AlwaysTrueValueMatcher implements ValueMatcher {

    @Override
    public boolean match( Integer t ) {
        return true;
    }

    @Override
    public String toString() {
        return StrUtil.format("[Matcher]: always true.");
    }
}
