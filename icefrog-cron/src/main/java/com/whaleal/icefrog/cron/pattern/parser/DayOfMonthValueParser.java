package com.whaleal.icefrog.cron.pattern.parser;

import com.whaleal.icefrog.cron.CronException;

/**
 * 每月的几号值处理<br>
 * 每月最多31天，32和“L”都表示最后一天
 *
 * @author Looly
 * @author wh
 *
 */
public class DayOfMonthValueParser extends SimpleValueParser {

	public DayOfMonthValueParser() {
		super(1, 31);
	}

	@Override
	public int parse(String value) throws CronException {
		if ("L".equalsIgnoreCase(value) || "32".equals(value)) {// 每月最后一天
			return 32;
		} else {
			return super.parse(value);
		}
	}
}
