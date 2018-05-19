package com.minmin.imemo.model;

import android.graphics.Bitmap;

/**
 * author:minmin
 * email:775846180@qq.com
 * time:2018/01/19
 * desc:侧拉功能
 * version:1.0
 */

public class Func {

    private String type;

    private String font = "";

    private Bitmap bitmap;

    private final static String HEAD = "head";
    private final static String FONT = "font";

    public Func(String type, String font) {

        this.type = type;
        this.font = font;
    }

    public Func(String type, Bitmap bitmap) {

        this.type = type;
        this.bitmap = bitmap;
    }

    public String getType() {
        return type;
    }

    public String getFont() {
        return font;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
