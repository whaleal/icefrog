package com.whaleal.icefrog.core.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * 按照GBK拼音顺序对给定的汉字字符串排序
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class PinyinComparator implements Comparator<String>, Serializable {
	private static final long serialVersionUID = 1L;

	final Collator collator;

	/**
	 * 构造
	 */
	public PinyinComparator() {
		collator = Collator.getInstance(Locale.CHINESE);
	}

	@Override
	public int compare(String o1, String o2) {
		return collator.compare(o1, o2);
	}

}
