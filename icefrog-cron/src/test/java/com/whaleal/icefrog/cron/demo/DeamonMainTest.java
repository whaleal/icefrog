package com.whaleal.icefrog.cron.demo;

import com.whaleal.icefrog.core.thread.ThreadUtil;
import com.whaleal.icefrog.cron.CronUtil;
import com.whaleal.icefrog.cron.task.InvokeTask;

public class DeamonMainTest {
	public static void main(String[] args) {
		// 测试守护线程是否对作业线程有效
		CronUtil.schedule("*/2 * * * * *", new InvokeTask("TestJob.doWhileTest"));
		// 当为守护线程时，stop方法调用后doWhileTest里的循环输出将终止，表示作业线程正常结束
		// 当非守护线程时，stop方法调用后，不再产生新的作业，原作业正常执行。
		CronUtil.setMatchSecond(true);
		CronUtil.start(true);

		ThreadUtil.sleep(3000);
		CronUtil.stop();
	}
}
