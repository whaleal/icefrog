package com.whaleal.icefrog.core.lang.generator;

import com.whaleal.icefrog.core.util.IdUtil;

/**
 * UUID生成器
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class UUIDGenerator implements Generator<String> {
	@Override
	public String next() {
		return IdUtil.fastUUID();
	}
}
