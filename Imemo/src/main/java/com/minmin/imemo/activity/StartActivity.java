package com.minmin.imemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.minmin.imemo.R;
import com.minmin.imemo.view.launcherview.LauncherView;

/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2018/01/17
 *   desc:开场动画
 *   version:1.0
 */

public class StartActivity extends Activity{

    private LauncherView launcherView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //开机动画
        launcherView = findViewById(R.id.loadView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launcherView.start();
            }
        }, 100);
    }
}
