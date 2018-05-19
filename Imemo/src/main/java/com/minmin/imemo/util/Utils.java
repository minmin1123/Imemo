package com.minmin.imemo.util;

import android.content.Context;

/**
 * author:minmin
 * email:775846180@qq.com
 * time:2018/01/15
 * desc:工具类
 * version:1.0
 */

public class Utils {
    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
