package com.minmin.imemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.minmin.imemo.R;
import com.minmin.imemo.model.Memo;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MyCalendar;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:
 *   version:1.0
 * </pre>
 */

public class EditMemoActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private ImageView mSaveIv;

    private ImageView mBackIv;

    private RelativeLayout mDateRl;

    private RelativeLayout mStartTimeRl;

    private RelativeLayout mFinishTimeRl;

    private TextView mDateTv;

    private TextView mStartTimeTv;

    private TextView mFinishTimeTv;

    private EditText mContextEt;

    private TimePicker mStartTimeTp;

    private TimePicker mFinishTimeTp;

    private LinearLayout mContextLl;

    private MyCalendar mCalendar = new MyCalendar();

    private String mSelectedYear = mCalendar.getNow_year();

    private String mSelectedMonth = mCalendar.getNow_month();

    private String mSelectedDay = mCalendar.getNow_day();

    private String mSelectedWeek = mCalendar.getNow_week();

    private String mSelectedStartHour = "09";

    private String mSelectedStartMinute = "00";

    private String mSelectedFinishHour = "10";

    private String mSelectedFinishMinute = "00";

    private final int RESULTCODE_EDIT = 2;

    private final static String YEAR = "year";

    private final static String MONTH = "month";

    private final static String DAY = "day";

    private final static String WEEK = "week";

    private final static String RETURN_NEWMEMO = "newMemo";

    private final static int START_TIME = 1;

    private final static int FINISH_TIME = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmemo);
        initView();
        //判断启动该活动的是大"+"还是小"+"，若是前者日期显示为当天，反之为指定日期
        if (getIntent().getStringExtra(MONTH) != null) {
            String expectedYear = getIntent().getStringExtra(YEAR);
            String expectedMonth = getIntent().getStringExtra(MONTH);
            String expectedDay = getIntent().getStringExtra(DAY);
            String expectedWeek = getIntent().getStringExtra(WEEK);
            if (mSelectedYear.equals(expectedYear) && mSelectedMonth.equals(expectedMonth) && mSelectedDay.equals(expectedDay)) {
                mDateTv.setText("今天-" + expectedMonth + "月" + expectedDay + "日," + expectedWeek);
            } else if (DateUtils.isTomorrow(mSelectedYear, mSelectedMonth, mSelectedDay, expectedYear, expectedMonth, expectedDay)) {
                mDateTv.setText("明天-" + expectedMonth + "月" + expectedDay + "日," + expectedWeek);
            } else {
                mDateTv.setText(expectedMonth + "月" + expectedDay + "日," + expectedWeek);
            }
            mSelectedYear = expectedYear;
            mSelectedMonth = expectedMonth;
            mSelectedDay = expectedDay;
            mSelectedWeek = expectedWeek;
        } else {
            mDateTv.setText("今天-" + mSelectedMonth + "月" + mSelectedDay + "日," + mSelectedWeek);
        }
    }

    //控件初始化
    public void initView() {
        mSaveIv = findViewById(R.id.saveIv);
        mBackIv = findViewById(R.id.backIv);
        mDateRl = findViewById(R.id.dateRl);
        mStartTimeRl = findViewById(R.id.startTimeRl);
        mFinishTimeRl = findViewById(R.id.finishTimeRl);
        mDateTv = findViewById(R.id.dateTv);
        mStartTimeTv = findViewById(R.id.startTimeTv);
        mFinishTimeTv = findViewById(R.id.finishTimeTv);
        mContextEt = findViewById(R.id.contextEt);
        mStartTimeTp = findViewById(R.id.startTimeTp);
        mFinishTimeTp = findViewById(R.id.finishTimeTp);
        mContextLl = findViewById(R.id.contextLl);
        mSaveIv.setOnClickListener(this);
        mBackIv.setOnClickListener(this);
        mDateRl.setOnClickListener(this);
        mStartTimeRl.setOnClickListener(this);
        mFinishTimeRl.setOnClickListener(this);
        mStartTimeTp.setIs24HourView(true);
        mStartTimeTp.setCurrentHour(9);
        mStartTimeTp.setCurrentMinute(0);
        mFinishTimeTp.setIs24HourView(true);
        mFinishTimeTp.setCurrentHour(10);
        mFinishTimeTp.setCurrentMinute(0);
        mContextLl.setOnTouchListener(this);
        mContextEt.setOnTouchListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了返回键
            case R.id.backIv:
                if (!TextUtils.isEmpty(mContextEt.getText())) {
                    newSaveDialog();
                } else {
                    finish();
                }
                break;
            //点击了保存键
            case R.id.saveIv:
                checkText();
                break;
            //点击了选择日期键
            case R.id.dateRl:
                newDatePickerDialog();
                break;
            //点击了选择开始时间键
            case R.id.startTimeRl:
                if (mFinishTimeTp.getVisibility() == View.VISIBLE) {
                    mFinishTimeTp.setVisibility(View.GONE);
                }
                mStartTimeTp.setVisibility(View.VISIBLE);
                timeChange(START_TIME);
                break;
            //点击了选择结束时间键
            case R.id.finishTimeRl:
                if (mStartTimeTp.getVisibility() == View.VISIBLE) {
                    mStartTimeTp.setVisibility(View.GONE);
                }
                mFinishTimeTp.setVisibility(View.VISIBLE);
                timeChange(FINISH_TIME);
                break;
            default:
                break;

        }
    }

    @Override//点击了文本框
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (mStartTimeTp.getVisibility() == View.VISIBLE) {
            mStartTimeTp.setVisibility(View.GONE);
        } else if (mFinishTimeTp.getVisibility() == View.VISIBLE) {
            mFinishTimeTp.setVisibility(View.GONE);
        }
        return false;
    }

    //检查文本内容是否合法
    public void checkText(){
        if (TextUtils.isEmpty(mContextEt.getText())) {
            Toast.makeText(this, R.string.none_content, Toast.LENGTH_SHORT).show();
        } else if (Integer.parseInt(mSelectedStartHour + mSelectedStartMinute) >= Integer.parseInt(mSelectedFinishHour + mSelectedFinishMinute)) {
            Toast.makeText(this, R.string.time_error, Toast.LENGTH_SHORT).show();
        } else {
            saveMemo();
        }
    }

    //弹出日历选择器
    public void newDatePickerDialog(){
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String weekOfday = DateUtils.getSelectedWeek(year, month, day);
                mSelectedYear = year+"";
                mSelectedMonth = DateUtils.toNormalTime(month + 1);
                mSelectedDay = DateUtils.toNormalTime(day);
                mSelectedWeek = weekOfday;
                if (mCalendar.getNow_year().equals(mSelectedYear) && mCalendar.getNow_month().equals(mSelectedMonth) && mCalendar.getNow_day().equals(mSelectedDay)) {
                    mDateTv.setText("今天-" + (month + 1) + "月" + day + "日," + weekOfday);
                } else if (DateUtils.isTomorrow(mCalendar.getNow_year(), mCalendar.getNow_month(), mCalendar.getNow_day(), mSelectedYear, mSelectedMonth, mSelectedDay)) {
                    mDateTv.setText("明天-" + (month + 1) + "月" + day + "日," + weekOfday);
                } else {
                    mDateTv.setText((month + 1) + "月" + day + "日," + weekOfday);
                }
            }
        }, Integer.parseInt(mCalendar.getNow_year()), Integer.parseInt(mCalendar.getNow_month()) - 1, Integer.parseInt(mCalendar.getNow_day())).show();
    }

    //时间选择器变化时文本也跟着变化
    public void timeChange(int flag){
        if(flag==START_TIME){
            mStartTimeTp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                    mSelectedStartHour = DateUtils.toNormalTime(hour);
                    mSelectedStartMinute = DateUtils.toNormalTime(minute);
                    mStartTimeTv.setText("开始-" + mSelectedStartHour + ":" + mSelectedStartMinute);

                }
            });
        }else if(flag==FINISH_TIME){
            mFinishTimeTp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                    mSelectedFinishHour = DateUtils.toNormalTime(hour);
                    mSelectedFinishMinute = DateUtils.toNormalTime(minute);
                    mFinishTimeTv.setText("结束-" + mSelectedFinishHour + ":" + mSelectedFinishMinute);
                }
            });
        }
    }

    //当选择返回而用户有更改痕迹，弹出是否保存的对话框的提示
    public void newSaveDialog() {
        AlertDialog.Builder isSaveMemoBuilder_ = new AlertDialog.Builder(this);
        isSaveMemoBuilder_.setMessage(R.string.ensure_save);
        isSaveMemoBuilder_.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveMemo();
            }
        });
        isSaveMemoBuilder_.setNegativeButton(R.string.give_up, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        isSaveMemoBuilder_.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        isSaveMemoBuilder_.create().show();
    }

    //用户确认保存，插入数据库操作
    public void saveMemo() {
        Memo memo = new Memo();
        memo.setId(mSelectedYear + mSelectedMonth + mSelectedDay + mSelectedStartHour + mSelectedStartMinute + mSelectedFinishHour + mSelectedFinishMinute + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_hour())) + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_minute())) + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_second())));
        memo.setYear(mSelectedYear);
        memo.setMonth(mSelectedMonth);
        memo.setDay(mSelectedDay);
        memo.setWeek(mSelectedWeek);
        memo.setStart_hour(mSelectedStartHour);
        memo.setStart_minute(mSelectedStartMinute);
        memo.setFinish_hour(mSelectedFinishHour);
        memo.setFinish_minute(mSelectedFinishMinute);
        memo.setText(mContextEt.getText().toString().trim());
        memo.setIs_completed(0);
//        memo.setIs_first(0);
        memo.setIs_chosen(0);
        Intent saveSuccessfulIntent = new Intent();
        saveSuccessfulIntent.putExtra(RETURN_NEWMEMO, memo);
        setResult(RESULTCODE_EDIT, saveSuccessfulIntent);
        finish();
    }

    @Override//重写返回键事件
    public void onBackPressed() {
        if (!TextUtils.isEmpty(mContextEt.getText())) {
            newSaveDialog();
        } else {
            finish();
        }
    }
}
