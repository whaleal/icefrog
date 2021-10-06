package com.whaleal.icefrog.core.io;

import com.whaleal.icefrog.core.io.watch.SimpleWatcher;
import com.whaleal.icefrog.core.io.watch.WatchMonitor;
import com.whaleal.icefrog.core.io.watch.Watcher;
import com.whaleal.icefrog.core.io.watch.watchers.DelayWatcher;
import com.whaleal.icefrog.core.lang.Console;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 文件监听单元测试
 *
 * @author Looly
 * @author wh
 */
public class WatchMonitorTest {

	public static void main(String[] args) {
		Watcher watcher = new SimpleWatcher() {
			@Override
			public void onCreate(WatchEvent<?> event, Path currentPath) {
				Object obj = event.context();
				Console.log("创建：{}-> {}", currentPath, obj);
			}

			@Override
			public void onModify(WatchEvent<?> event, Path currentPath) {
				Object obj = event.context();
				Console.log("修改：{}-> {}", currentPath, obj);
			}

			@Override
			public void onDelete(WatchEvent<?> event, Path currentPath) {
				Object obj = event.context();
				Console.log("删除：{}-> {}", currentPath, obj);
			}

			@Override
			public void onOverflow(WatchEvent<?> event, Path currentPath) {
				Object obj = event.context();
				Console.log("Overflow：{}-> {}", currentPath, obj);
			}
		};

		WatchMonitor monitor = WatchMonitor.createAll("d:/test/aaa.txt", new DelayWatcher(watcher, 500));

		monitor.setMaxDepth(0);
		monitor.start();
	}


}
