package com.minmin.imemo.model;

import java.io.Serializable;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:
 *   version:1.0
 * </pre>
 */


public class Memo implements Serializable{

    private String id;//唯一id=年+月+日+开始时钟+开始分钟+结束时钟+结束分钟+当前时钟+当前分钟+当前秒数（共22位）

    private String year;//年（4位）

    private String month;//月（2位）

    private String day;//日（2位）

    private String week;//星期:周*

    private String start_hour;//开始时钟（2位）

    private String start_minute;//开始分钟（2位）

    private String finish_hour;//结束时钟（2位）

    private String finish_minute;//结束分钟（2位）

    private String text;//文本内容

    private int is_completed=0;//是否完成--对应圆圈状态

    private int is_first=0;//是否是当天第一个备忘录--对应是否有日期头

    private int is_chosen=0;//是否被选中--对应处于编辑状态的item选中状态

    public String getId() {
        return id;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getWeek() {
        return week;
    }

    public String getStart_hour() {
        return start_hour;
    }

    public String getStart_minute() {
        return start_minute;
    }

    public String getFinish_hour() {
        return finish_hour;
    }

    public String getFinish_minute() {
        return finish_minute;
    }

    public String getText() {
        return text;
    }

    public int getIs_completed() {
        return is_completed;
    }

    public int getIs_first() {
        return is_first;
    }

    public int getIs_chosen() {
        return is_chosen;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setStart_hour(String start_hour) {
        this.start_hour = start_hour;
    }

    public void setStart_minute(String start_minute) {
        this.start_minute = start_minute;
    }

    public void setFinish_hour(String finish_hour) {
        this.finish_hour = finish_hour;
    }

    public void setFinish_minute(String finish_minute) {
        this.finish_minute = finish_minute;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setIs_completed(int is_completed) {
        this.is_completed = is_completed;
    }

    public void setIs_first(int is_first) {
        this.is_first = is_first;
    }

    public void setIs_chosen(int is_chosen) {
        this.is_chosen = is_chosen;
    }
}
