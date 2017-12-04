package com.minmin.imemo.util;

import java.util.Calendar;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:自定义日历类
 *   version:1.0
 * </pre>
 */

public class MyCalendar{//该类用于获取当前年月日时分秒星期，并化为标准形式

    private Calendar calendar;

    private String now_year;

    private String now_month;

    private String now_day;

    private String now_week;

    private String now_hour;

    private String now_minute;

    private String now_second;

    public MyCalendar() {
        calendar = Calendar.getInstance();
    }

    public String getNow_year() {
        now_year=calendar.get(Calendar.YEAR)+"";
        return now_year;
    }

    public String getNow_month() {
        now_month=DateUtils.toNormalTime(calendar.get(Calendar.MONTH)+1);
        return now_month;
    }

    public String getNow_day() {
        now_day=DateUtils.toNormalTime(calendar.get(Calendar.DAY_OF_MONTH));
        return now_day;
    }

    public String getNow_week() {
        now_week=calendar.get(Calendar.DAY_OF_WEEK)+"";
        if("1".equals(now_week)){
            now_week ="周日";
        }else if("2".equals(now_week)){
            now_week ="周一";
        }else if("3".equals(now_week)){
            now_week ="周二";
        }else if("4".equals(now_week)){
            now_week ="周三";
        }else if("5".equals(now_week)){
            now_week ="周四";
        }else if("6".equals(now_week)){
            now_week ="周五";
        }else if("7".equals(now_week)){
            now_week ="周六";
        }
        return now_week;
    }

    public String getNow_hour() {
        now_hour=DateUtils.toNormalTime(calendar.get(Calendar.HOUR_OF_DAY));
        return now_hour;
    }

    public String getNow_minute() {
        now_minute=DateUtils.toNormalTime(calendar.get(Calendar.MINUTE));
        return now_minute;
    }

    public String getNow_second() {
        now_second = DateUtils.toNormalTime(calendar.get(Calendar.SECOND));
        return now_second;
    }
}
