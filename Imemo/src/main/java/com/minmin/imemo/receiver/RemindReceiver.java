package com.minmin.imemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.minmin.imemo.service.RemindService;

/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/27
 *   desc:后台定时任务的广播接收器
 *   version:1.0
 */

public class RemindReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, RemindService.class);
        context.startService(i);
    }
}
