package com.terran4j.common.util;

import com.terran4j.commons.util.DateTimes;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateTimesTest {

    @Test
    public void testCutHour() {
        Date date = DateTimes.toDate("2017-03-02 18:41:59");
        Date cutDate = DateTimes.cutHour(date);
        String dateText = DateTimes.toString(cutDate);
        Assert.assertEquals("2017-03-02 00:00:00", dateText);

        date = DateTimes.toDate("2017-03-02 00:00:00");
        cutDate = DateTimes.cutHour(date);
        dateText = DateTimes.toString(cutDate);
        Assert.assertEquals("2017-03-02 00:00:00", dateText);
    }

    @Test
    public void testCutDay() {
        Date date = DateTimes.toDate("2017-03-12 18:41:59");
        Date cutDate = DateTimes.cutDay(date);
        String dateText = DateTimes.toString(cutDate);
        Assert.assertEquals("2017-03-01 00:00:00", dateText);

        date = DateTimes.toDate("2017-03-01 00:00:00");
        cutDate = DateTimes.cutDay(date);
        dateText = DateTimes.toString(cutDate);
        Assert.assertEquals("2017-03-01 00:00:00", dateText);
    }

    @Test
    public void testCutMonth() {
        Date date = DateTimes.toDate("2017-12-31 23:59:59");
        Date cutDate = DateTimes.cutMonth(date);
        String dateText = DateTimes.toString(cutDate);
        Assert.assertEquals("2017-01-01 00:00:00", dateText);
    }
}
