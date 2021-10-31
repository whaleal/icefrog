package com.whaleal.icefrog.core.date;

import com.whaleal.icefrog.core.date.format.FastDateFormat;
import org.junit.Assert;
import org.junit.Test;

import java.util.TimeZone;

public class TimeZoneTest {

    @Test
    public void timeZoneConvertTest() {
        DateTime dt = DateUtil.parse("2018-07-10 21:44:32", //
                FastDateFormat.getInstance(DatePattern.NORM_DATETIME_PATTERN, TimeZone.getTimeZone("GMT+8:00")));
        Assert.assertEquals("2018-07-10 21:44:32", dt.toString());

        dt.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        int hour = dt.getField(DateField.HOUR_OF_DAY);
        Assert.assertEquals(14, hour);
        Assert.assertEquals("2018-07-10 14:44:32", dt.toString());
    }
}
