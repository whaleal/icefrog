package com.whaleal.icefrog.db.meta;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import com.whaleal.icefrog.core.collection.CollectionUtil;
import com.whaleal.icefrog.core.util.StrUtil;
import com.whaleal.icefrog.db.ds.DSFactory;

/**
 * 元数据信息单元测试
 *
 * @author Looly
 * @author wh
 *
 */
public class MetaUtilTest {
	DataSource ds = DSFactory.get("test");

	@Test
	public void getTablesTest() {
		List<String> tables = MetaUtil.getTables(ds);
		Assert.assertEquals("user", tables.get(0));
	}

	@Test
	public void getTableMetaTest() {
		Table table = MetaUtil.getTableMeta(ds, "user");
		Assert.assertEquals(CollectionUtil.newHashSet("id"), table.getPkNames());
	}

	@Test
	public void getColumnNamesTest() {
		String[] names = MetaUtil.getColumnNames(ds, "user");
		Assert.assertArrayEquals(StrUtil.splitToArray("id,name,age,birthday,gender", ','), names);
	}
}
