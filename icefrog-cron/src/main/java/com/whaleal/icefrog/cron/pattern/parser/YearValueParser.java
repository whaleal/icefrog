package com.whaleal.icefrog.cron.pattern.parser;

/**
 * 年值处理
 *
 * @author Looly
 * @author wh
 */
public class YearValueParser extends SimpleValueParser {

    public YearValueParser() {
        super(1970, 2099);
    }

}
