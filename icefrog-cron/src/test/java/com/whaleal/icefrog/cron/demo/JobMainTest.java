package com.whaleal.icefrog.cron.demo;

import com.whaleal.icefrog.cron.CronUtil;

/**
 * 定时任务样例
 */
public class JobMainTest {

    public static void main( String[] args ) {
        CronUtil.setMatchSecond(true);
        CronUtil.start(false);
    }
}
