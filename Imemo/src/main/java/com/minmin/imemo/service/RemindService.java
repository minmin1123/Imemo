package com.minmin.imemo.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.minmin.imemo.R;
import com.minmin.imemo.activity.MainActivity;
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memo;
import com.minmin.imemo.receiver.RemindReceiver;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MyCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/25
 *   desc:实现提醒功能的服务
 *   version:1.0
 * </pre>
 */

public class RemindService extends Service{

    private List<Memo> mMemoList = new ArrayList<>();

    private MemoDatabase mMemoDatabase = MemoDatabase.getInstance(this);

    private int mToDoCount;//今日待办memo个数

    private int mHadDoCount;//今日已办memo个数

    private long mFixedMSecond;//后台定时服务毫秒数

    private String mRemindContent="";//提醒文本内容

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getKeyData();
                getForeSrvice();
                getFixedService();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    //读取数据库关键数据
    public void getKeyData(){
        MyCalendar mCalendar = new MyCalendar();
        int hadDoCount=0;
        int isFirstRemindTimeExist=0;
        String timeCursor="";
        String remindTime = "";
        String remindContent = "";
        mMemoList = mMemoDatabase.quaryEveryDayMemoList(mCalendar.getNow_year(),mCalendar.getNow_month(),mCalendar.getNow_day());
        mToDoCount=mMemoList.size();
        if(mToDoCount!=0){
            for(Memo memo:mMemoList){
                if(memo.getIs_completed()==1){
                    hadDoCount++;
                }
                if(memo.getIs_remind()==1){
                    if(Integer.parseInt(memo.getStart_hour()+memo.getStart_minute())>Integer.parseInt(mCalendar.getNow_hour()+mCalendar.getNow_minute())){
                        if(memo.getIs_completed()==0){
                            //获取最近一条未完成待提醒的memo以及其开始时间距今的时间
                           if(isFirstRemindTimeExist==0){
                               timeCursor=memo.getStart_hour()+memo.getStart_minute();
                               remindTime=memo.getStart_hour()+":"+memo.getStart_minute();
                               remindContent=memo.getStart_hour()+":"+memo.getStart_minute()+" "+memo.getText();
                               isFirstRemindTimeExist=1;
                           }else if((memo.getStart_hour()+memo.getStart_minute()).equals(timeCursor)){
                               remindContent= remindContent+ "," + memo.getText();
                           }
                        }
                    }
                }
            }
            mRemindContent=remindContent;
            mFixedMSecond = DateUtils.turnFixedMSecond(remindTime);
            mHadDoCount=hadDoCount;
        }
    }

    //创建前台服务
    public void getForeSrvice(){
        Notification.Builder foreBuilder = new Notification.Builder(this);
        foreBuilder.setSmallIcon(R.drawable.imemo);
        foreBuilder.setContentTitle("今日待办"+mToDoCount+"项,已完成"+mHadDoCount+"项");
        if(mRemindContent.length()!=0){
            foreBuilder.setContentText("提醒最近一条待完成memo开始于"+mRemindContent);
        }
        Intent mainIntent = new Intent(this, MainActivity.class);
        PendingIntent mainPI=PendingIntent.getActivity(this,0,mainIntent,0);
        foreBuilder.setContentIntent(mainPI);
        startForeground(1,foreBuilder.build());
    }

    //创建后台定时任务
    public void getFixedService(){
        if(mFixedMSecond!=0){
            AlarmManager remindAM= (AlarmManager) getSystemService(ALARM_SERVICE);
            long pastMSecond= SystemClock.elapsedRealtime()+mFixedMSecond;
            Intent i = new Intent(RemindService.this, RemindReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(RemindService.this,0,i,0);
            remindAM.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,pastMSecond,pi);
        }
    }
}
