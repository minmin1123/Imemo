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
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memo;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MyCalendar;

public class EditMemoActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private ImageView edit_iv_save;
    private ImageView edit_iv_back;
    private RelativeLayout edit_rv_date;
    private RelativeLayout edit_rv_time_start;
    private RelativeLayout edit_rv_time_finish;
    private TextView edit_tv_date;
    private TextView edit_tv_time_start;
    private TextView edit_tv_time_finish;
    private EditText edit_tv_text;
    private TimePicker edit_tp_start_time;
    private TimePicker edit_tp_finish_time;
    private LinearLayout edit_lv_text;
    private MyCalendar myCalendar = new MyCalendar();
    private String selected_year = myCalendar.getNow_year();
    private String selected_month = myCalendar.getNow_month();
    private String selected_day = myCalendar.getNow_day();
    private String selected_week = myCalendar.getNow_week();
    private String selected_start_hour = "09";
    private String selected_start_minute = "00";
    private String selected_finish_hour = "10";
    private String selected_finish_minute = "00";
    private final int RESULTCODE_EDIT = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmemo);
        initView();
        //判断启动该活动的是大"+"还是小"+"，若是前者日期显示为当天，反之为指定日期
        if (getIntent().getStringExtra("month") != null) {
            String expected_year = getIntent().getStringExtra("year");
            String expected_month = getIntent().getStringExtra("month");
            String expected_day = getIntent().getStringExtra("day");
            String expected_week = getIntent().getStringExtra("week");
            if (selected_year.equals(expected_year) && selected_month.equals(expected_month) && selected_day.equals(expected_day)) {
                edit_tv_date.setText("今天-" + expected_month + "月" + expected_day + "日," + expected_week);
            } else if (DateUtils.isTomorrow(selected_year, selected_month, selected_day, expected_year, expected_month, expected_day)) {
                edit_tv_date.setText("明天-" + expected_month + "月" + expected_day + "日," + expected_week);
            } else {
                edit_tv_date.setText(expected_month + "月" + expected_day + "日," + expected_week);
            }
            selected_year = expected_year;
            selected_month = expected_month;
            selected_day = expected_day;
            selected_week = expected_week;
        } else {
            edit_tv_date.setText("今天-" + selected_month + "月" + selected_day + "日," + selected_week);
        }
    }

    //控件初始化
    public void initView() {
        edit_iv_save = findViewById(R.id.edit_iv_save);
        edit_iv_back = findViewById(R.id.edit_iv_back);
        edit_rv_date = findViewById(R.id.edit_rv_date);
        edit_rv_time_start = findViewById(R.id.edit_rv_time_start);
        edit_rv_time_finish = findViewById(R.id.edit_rv_time_finish);
        edit_tv_date = findViewById(R.id.edit_tv_date);
        edit_tv_time_start = findViewById(R.id.edit_tv_time_start);
        edit_tv_time_finish = findViewById(R.id.edit_tv_time_finish);
        edit_tv_text = findViewById(R.id.edit_tv_text);
        edit_tp_start_time = findViewById(R.id.edit_tp_start_time);
        edit_tp_finish_time = findViewById(R.id.edit_tp_finish_time);
        edit_lv_text = findViewById(R.id.edit_lv_text);
        edit_iv_save.setOnClickListener(this);
        edit_iv_back.setOnClickListener(this);
        edit_rv_date.setOnClickListener(this);
        edit_rv_time_start.setOnClickListener(this);
        edit_rv_time_finish.setOnClickListener(this);
        edit_tp_start_time.setIs24HourView(true);
        edit_tp_start_time.setCurrentHour(9);
        edit_tp_start_time.setCurrentMinute(0);
        edit_tp_finish_time.setIs24HourView(true);
        edit_tp_finish_time.setCurrentHour(10);
        edit_tp_finish_time.setCurrentMinute(0);
        edit_lv_text.setOnTouchListener(this);
        edit_tv_text.setOnTouchListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了返回键
            case R.id.edit_iv_back:
                if (!TextUtils.isEmpty(edit_tv_text.getText())) {
                    newSaveDialog();
                } else {
                    finish();
                }
                break;
            //点击了保存键
            case R.id.edit_iv_save:
                if (TextUtils.isEmpty(edit_tv_text.getText())) {
                    Toast.makeText(this, "什么内容也没有o~", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(selected_start_hour + selected_start_minute) >= Integer.parseInt(selected_finish_hour + selected_finish_minute)) {
                    Toast.makeText(this, "时间不合法,我不会穿越o~", Toast.LENGTH_SHORT).show();
                } else {
                    saveMemo();
                }
                break;
            //点击了选择日期键
            case R.id.edit_rv_date:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String weekOfday = DateUtils.getSelectedWeek(year, month, day);
                        selected_year = String.valueOf(year);
                        selected_month = DateUtils.toNormalTime(month + 1);
                        selected_day = DateUtils.toNormalTime(day);
                        selected_week = weekOfday;
                        if (myCalendar.getNow_year().equals(selected_year) && myCalendar.getNow_month().equals(selected_month) && myCalendar.getNow_day().equals(selected_day)) {
                            edit_tv_date.setText("今天-" + (month + 1) + "月" + day + "日," + weekOfday);
                        } else if (DateUtils.isTomorrow(myCalendar.getNow_year(), myCalendar.getNow_month(), myCalendar.getNow_day(), selected_year, selected_month, selected_day)) {
                            edit_tv_date.setText("明天-" + (month + 1) + "月" + day + "日," + weekOfday);
                        } else {
                            edit_tv_date.setText((month + 1) + "月" + day + "日," + weekOfday);
                        }
                    }
                }, Integer.parseInt(myCalendar.getNow_year()), Integer.parseInt(myCalendar.getNow_month()) - 1, Integer.parseInt(myCalendar.getNow_day())).show();
                break;
            //点击了选择开始时间键
            case R.id.edit_rv_time_start:
                if (edit_tp_finish_time.getVisibility() == View.VISIBLE) {
                    edit_tp_finish_time.setVisibility(View.GONE);
                }
                edit_tp_start_time.setVisibility(View.VISIBLE);
                edit_tp_start_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                        selected_start_hour = DateUtils.toNormalTime(hour);
                        selected_start_minute = DateUtils.toNormalTime(minute);
                        edit_tv_time_start.setText("开始-" + selected_start_hour + ":" + selected_start_minute);

                    }
                });
                break;
            //点击了选择结束时间键
            case R.id.edit_rv_time_finish:
                if (edit_tp_start_time.getVisibility() == View.VISIBLE) {
                    edit_tp_start_time.setVisibility(View.GONE);
                }
                edit_tp_finish_time.setVisibility(View.VISIBLE);
                edit_tp_finish_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                        selected_finish_hour = DateUtils.toNormalTime(hour);
                        selected_finish_minute = DateUtils.toNormalTime(minute);
                        edit_tv_time_finish.setText("结束-" + selected_finish_hour + ":" + selected_finish_minute);
                    }
                });
                break;
            default:
                break;

        }
    }

    @Override//点击了文本框
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (edit_tp_start_time.getVisibility() == View.VISIBLE) {
            edit_tp_start_time.setVisibility(View.GONE);
        } else if (edit_tp_finish_time.getVisibility() == View.VISIBLE) {
            edit_tp_finish_time.setVisibility(View.GONE);
        }
        return false;
    }

    //当选择返回而用户有更改痕迹，弹出是否保存的对话框的提示
    public void newSaveDialog() {
        AlertDialog.Builder builder_isSaveMemo = new AlertDialog.Builder(this);
        builder_isSaveMemo.setMessage("保存更改?");
        builder_isSaveMemo.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveMemo();
            }
        });
        builder_isSaveMemo.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder_isSaveMemo.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder_isSaveMemo.create().show();
    }

    //用户确认保存，插入数据库操作
    public void saveMemo() {
        Memo memo = new Memo();
        memo.setId(selected_year + selected_month + selected_day + selected_start_hour + selected_start_minute + selected_finish_hour + selected_finish_minute + DateUtils.toNormalTime(Integer.parseInt(myCalendar.getNow_hour())) + DateUtils.toNormalTime(Integer.parseInt(myCalendar.getNow_minute())) + DateUtils.toNormalTime(Integer.parseInt(myCalendar.getNow_second())));
        memo.setYear(selected_year);
        memo.setMonth(selected_month);
        memo.setDay(selected_day);
        memo.setWeek(selected_week);
        memo.setStart_hour(selected_start_hour);
        memo.setStart_minute(selected_start_minute);
        memo.setFinish_hour(selected_finish_hour);
        memo.setFinish_minute(selected_finish_minute);
        memo.setText(edit_tv_text.getText().toString().trim());
        memo.setIs_completed(0);
        memo.setIs_first(0);
        memo.setIs_chosen(0);
        MemoDatabase memoDatabase = MemoDatabase.getInstance(this);
        memoDatabase.insertMemo(memo);
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        Intent intent_SaveSuccessful = new Intent();
        setResult(RESULTCODE_EDIT, intent_SaveSuccessful);
        finish();
    }

    @Override//重写返回键事件
    public void onBackPressed() {
        if (!TextUtils.isEmpty(edit_tv_text.getText())) {
            newSaveDialog();
        } else {
            finish();
        }
    }
}
