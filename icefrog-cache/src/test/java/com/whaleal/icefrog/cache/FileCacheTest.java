package com.whaleal.icefrog.cache;

import org.junit.Assert;
import org.junit.Test;

import com.whaleal.icefrog.cache.file.LFUFileCache;

/**
 * 文件缓存单元测试
 * @author Looly 
 * @author wh
 *
 */
public class FileCacheTest {
	@Test
	public void lfuFileCacheTest() {
		LFUFileCache cache = new LFUFileCache(1000, 500, 2000);
		Assert.assertNotNull(cache);
	}
}
