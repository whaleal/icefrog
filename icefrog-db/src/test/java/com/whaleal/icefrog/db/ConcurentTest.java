package com.whaleal.icefrog.db;

import com.whaleal.icefrog.core.collection.CollectionUtil;
import com.whaleal.icefrog.core.lang.Console;
import com.whaleal.icefrog.core.thread.ThreadUtil;
import com.whaleal.icefrog.db.handler.EntityListHandler;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

/**
 * SqlRunner线程安全测试
 *
 * @author Looly
 * @author wh
 *
 */
@Ignore
public class ConcurentTest {

	private Db db;

	@Before
	public void init() {
		db = Db.use("test");
	}

	@Test
	public void findTest() {
		for(int i = 0; i < 10000; i++) {
			ThreadUtil.execute(() -> {
				List<Entity> find;
				try {
					find = db.find(CollectionUtil.newArrayList("name AS name2"), Entity.create("user"), new EntityListHandler());
				} catch (SQLException e) {
					throw new DbRuntimeException(e);
				}
				Console.log(find);
			});
		}

		//主线程关闭会导致连接池销毁，sleep避免此情况引起的问题
		ThreadUtil.sleep(5000);
	}
}
