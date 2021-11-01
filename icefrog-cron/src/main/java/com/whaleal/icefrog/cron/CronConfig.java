package com.whaleal.icefrog.cron;

import java.util.TimeZone;

/**
 * 定时任务配置类
 *
 * @author Looly
 * @author wh
 * @since 1.0.0
 */
public class CronConfig {

    /**
     * 时区
     */
    protected TimeZone timezone = TimeZone.getDefault();
    /**
     * 是否支持秒匹配
     */
    protected boolean matchSecond;

    public CronConfig() {
    }

    /**
     * 获得时区，默认为 {@link TimeZone#getDefault()}
     *
     * @return 时区
     */
    public TimeZone getTimeZone() {
        return this.timezone;
    }

    /**
     * 设置时区
     *
     * @param timezone 时区
     * @return this
     */
    public CronConfig setTimeZone( TimeZone timezone ) {
        this.timezone = timezone;
        return this;
    }

    /**
     * 是否支持秒匹配
     *
     * @return <code>true</code>使用，<code>false</code>不使用
     */
    public boolean isMatchSecond() {
        return this.matchSecond;
    }

    /**
     * 设置是否支持秒匹配，默认不使用
     *
     * @param isMatchSecond <code>true</code>支持，<code>false</code>不支持
     * @return this
     */
    public CronConfig setMatchSecond( boolean isMatchSecond ) {
        this.matchSecond = isMatchSecond;
        return this;
    }
}
