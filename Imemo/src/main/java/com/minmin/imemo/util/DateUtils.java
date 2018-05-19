package com.minmin.imemo.util;

import android.os.Build;

import java.util.Calendar;

/**
 * author:minmin
 * email:775846180@qq.com
 * time:2017/10/11
 * desc:有关日期和时间的类
 * version:1.0
 */

public class DateUtils {

    //把月日、时间变成标准形式（均为两位数）
    public static String toNormalTime(int oldTime) {
        String newTime;
        if (oldTime >= 0 && oldTime <= 9) {
            newTime = "0" + oldTime;
        } else {
            newTime = oldTime + "";
        }
        return newTime;
    }

    //根据年月日推算星期
    public static String getSelectedWeek(int year, int month, int day) {
        int week;
        String weekOfday = null;
        if (month == 0) {
            week = (int) (year + (year - 2000) / 4 + day - 2001);
        } else if (month == 1) {
            week = (int) (year + (year - 2000) / 4 + day - 1998);
        } else {
            week = (int) (year + (year - 2000) / 4 - 2036 + 26 * (month + 2) / 10 + day);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (Math.floorMod(week, 7) == 0) {
                weekOfday = "周日";
            } else if (Math.floorMod(week, 7) == 1) {
                weekOfday = "周一";
            } else if (Math.floorMod(week, 7) == 2) {
                weekOfday = "周二";
            } else if (Math.floorMod(week, 7) == 3) {
                weekOfday = "周三";
            } else if (Math.floorMod(week, 7) == 4) {
                weekOfday = "周四";
            } else if (Math.floorMod(week, 7) == 5) {
                weekOfday = "周五";
            } else if (Math.floorMod(week, 7) == 6) {
                weekOfday = "周六";
            }
        }
        return weekOfday;
    }

    //判断给定时间是否是明天
    public static boolean isTomorrow(String old_year, String old_month, String old_day, String new_year, String new_month, String new_day) {
        int now_year = Integer.parseInt(old_year);
        int now_month = Integer.parseInt(old_month);
        int now_day = Integer.parseInt(old_day);
        int selected_year = Integer.parseInt(new_year);
        int selected_month = Integer.parseInt(new_month);
        int selected_day = Integer.parseInt(new_day);
        if (now_month == 1 || now_month == 3 || now_month == 5 || now_month == 7 || now_month == 8 || now_month == 10) {
            if (now_day == 31) {
                if ((selected_day == 1) && (selected_month == now_month + 1) && (selected_year == now_year)) {
                    return true;
                }
            } else {
                if ((selected_day == now_day + 1) && (selected_month == now_month) && (selected_year == now_year)) {
                    return true;
                }
            }
        } else if (now_month == 4 || now_month == 6 || now_month == 9 || now_month == 11) {
            if (now_day == 30) {
                if ((selected_day == 1) && (selected_month == now_month + 1) && (selected_year == now_year)) {
                    return true;
                }
            } else {
                if ((selected_day == now_day + 1) && (selected_month == now_month) && (selected_year == now_year)) {
                    return true;
                }
            }
        } else if (now_month == 2) {
            if (now_year % 4 == 0) {
                if (now_day == 29) {
                    if ((selected_day == 1) && (selected_month == now_month + 1) && (selected_year == now_year)) {
                        return true;
                    }
                } else {
                    if ((selected_day == now_day + 1) && (selected_month == now_month) && (selected_year == now_year)) {
                        return true;
                    }
                }
            } else {
                if (now_day == 28) {
                    if ((selected_day == 1) && (selected_month == now_month + 1) && (selected_year == now_year)) {
                        return true;
                    }
                } else {
                    if ((selected_day == now_day + 1) && (selected_month == now_month) && (selected_year == now_year)) {
                        return true;
                    }
                }
            }
        } else if (now_month == 12) {
            if (now_day == 31) {
                if ((selected_day == 1) && (selected_month == 1) && (selected_year == now_year + 1)) {
                    return true;
                }
            } else {
                if ((selected_day == now_day + 1) && (selected_month == now_month) && (selected_year == now_year)) {
                    return true;
                }
            }
        }
        return false;
    }

    //判断给定时间是当天之前还是之后
    public static boolean isArrived(String date) {
        String now = MyCalendar.getNow_year() + MyCalendar.getNow_month() + MyCalendar.getNow_day();
        if (date.compareTo(now) < 0) {
            return true;
        } else {
            return false;
        }

    }

    //距离上一次时间过去的毫秒数
    public static long turnFixedMSecond(String time) {
        if (time.length() != 0) {
            long pastMSecond;
            int hour = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            int lastHour = Integer.parseInt(MyCalendar.getNow_hour());
            int lastMinute = Integer.parseInt(MyCalendar.getNow_minute());
            pastMSecond = ((hour - lastHour) * 60 * 60 + (minute - lastMinute) * 60) * 1000;
            return pastMSecond;
        }
        return 0;
    }

    //计算距离当日的天数
    public static long countSpanDays(String year, String month, String day) {
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();
        then.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
        long t1 = Math.abs(then.getTimeInMillis() - now.getTimeInMillis());
        long t2 = 1000 * 60 * 60 * 24;
        long days = (int) (t1 / t2);
        return days;
    }


}
