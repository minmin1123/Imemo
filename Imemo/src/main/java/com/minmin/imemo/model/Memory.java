package com.minmin.imemo.model;

import java.io.Serializable;

/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:Memory实体类
 *   version:1.0
 */


public class Memory implements Serializable{

    private String id;//唯一id

    private String year;//年（4位）

    private String month;//月（2位）

    private String day;//日（2位）

    private String text;//文本内容

    private int is_arrived=0;//是否到达，"还有"/"已经"

    private long count;//天数

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

    public String getText() {
        return text;
    }

    public long getCount() {
        return count;
    }

    public int getIs_arrived() {
        return is_arrived;
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

    public void setText(String text) {
        this.text = text;
    }

    public void setIs_arrived(int is_arrived) {
        this.is_arrived = is_arrived;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
