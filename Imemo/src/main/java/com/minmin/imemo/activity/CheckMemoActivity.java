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
import android.support.v4.media.MediaMetadataCompat;
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

import static com.minmin.imemo.R.id.edit_tv_text;

/**
 * Created by minmin on 2017/10/3.
 */

public class CheckMemoActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private Memo memo;
    private ImageView check_iv_back;
    private ImageView check_iv_delete;
    private RelativeLayout check_rv_date;
    private RelativeLayout check_rv_time_start;
    private RelativeLayout check_rv_time_finish;
    private LinearLayout check_lv_text;
    private ImageView check_iv_edit;
    private TextView check_tv_date;
    private TextView check_tv_time_start;
    private TextView check_tv_time_finish;
    private EditText check_tv_text;
    private TimePicker check_tp_start_time;
    private TimePicker check_tp_finish_time;
    private MyCalendar myCalendar = new MyCalendar();
    private String selected_year;
    private String selected_month;
    private String selected_day;
    private String selected_week;
    private String selected_start_hour;
    private String selected_start_minute;
    private String selected_finish_hour;
    private String selected_finish_minute;
    private final int RESULTCODE_DELETE = 3;
    private final int RESULTCODE_UPDATE = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmemo);
        //获取选定memo对象，并进行页面初始化
        memo = (Memo) getIntent().getSerializableExtra("memo");
        initView();
        selected_year = memo.getYear();
        selected_month = memo.getMonth();
        selected_day = memo.getDay();
        selected_week = memo.getWeek();
        selected_start_hour = memo.getStart_hour();
        selected_start_minute = memo.getStart_minute();
        selected_finish_hour = memo.getFinish_hour();
        selected_finish_minute = memo.getFinish_minute();
    }

    public void initView() {
        check_iv_delete = findViewById(R.id.check_iv_delete);
        check_iv_back = findViewById(R.id.check_iv_back);
        check_rv_date = findViewById(R.id.check_rv_date);
        check_rv_time_start = findViewById(R.id.check_rv_time_start);
        check_rv_time_finish = findViewById(R.id.check_rv_time_finish);
        check_tv_date = findViewById(R.id.check_tv_date);
        check_iv_edit = findViewById(R.id.check_iv_edit);
        if (myCalendar.getNow_year().equals(memo.getYear()) && myCalendar.getNow_month().equals(memo.getMonth()) && myCalendar.getNow_day().equals(memo.getDay())) {
            check_tv_date.setText("今天-" + memo.getMonth() + "月" + memo.getDay() + "日," + memo.getWeek());
        } else if (DateUtils.isTomorrow(myCalendar.getNow_year(), myCalendar.getNow_month(), myCalendar.getNow_day(), memo.getYear(), memo.getMonth(), memo.getDay())) {
            check_tv_date.setText("明天-" + memo.getMonth() + "月" + memo.getDay() + "日," + memo.getWeek());
        } else {
            check_tv_date.setText(memo.getMonth() + "月" + memo.getDay() + "日," + memo.getWeek());
        }
        check_tv_time_start = findViewById(R.id.check_tv_time_start);
        check_tv_time_start.setText("开始-" + memo.getStart_hour() + ":" + memo.getStart_minute());
        check_tv_time_finish = findViewById(R.id.check_tv_time_finish);
        check_tv_time_finish.setText("结束-" + memo.getFinish_hour() + ":" + memo.getFinish_minute());
        check_tv_text = findViewById(R.id.check_tv_text);
        check_tv_text.setText(memo.getText());
        check_tp_start_time = findViewById(R.id.check_tp_start_time);
        check_tp_start_time.setIs24HourView(true);
        check_tp_start_time.setCurrentHour(Integer.parseInt(memo.getStart_hour()));
        check_tp_start_time.setCurrentMinute(Integer.parseInt(memo.getStart_minute()));
        check_tp_finish_time = findViewById(R.id.check_tp_finish_time);
        check_tp_finish_time.setIs24HourView(true);
        check_tp_finish_time.setCurrentHour(Integer.parseInt(memo.getFinish_hour()));
        check_tp_finish_time.setCurrentMinute(Integer.parseInt(memo.getFinish_minute()));
        check_lv_text = findViewById(R.id.check_lv_text);
        check_iv_delete.setOnClickListener(this);
        check_iv_back.setOnClickListener(this);
        check_rv_date.setOnClickListener(this);
        check_rv_time_start.setOnClickListener(this);
        check_rv_time_finish.setOnClickListener(this);
        check_iv_edit.setOnClickListener(this);
        check_lv_text.setOnTouchListener(this);
        check_tv_text.setOnTouchListener(this);
        check_rv_date.setClickable(false);
        check_rv_time_start.setClickable(false);
        check_rv_time_finish.setClickable(false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了编辑
            case R.id.check_iv_edit:
                check_rv_date.setClickable(true);
                check_rv_time_start.setClickable(true);
                check_rv_time_finish.setClickable(true);
                check_tv_text.setEnabled(true);
                check_tv_text.setSelection(memo.getText().length());
                check_iv_edit.setVisibility(View.INVISIBLE);
                check_iv_delete.setBackgroundResource(R.drawable.save);
                check_iv_delete.setTag("save");
                break;
            //点击了删除
            case R.id.check_iv_delete:
                if (check_iv_delete.getTag().equals("delete")) {
                    newDeleteDialog();
                } else if (check_iv_delete.getTag().equals("save")) {
                    if (Integer.parseInt(selected_start_hour + selected_start_minute) >= Integer.parseInt(selected_finish_hour + selected_finish_minute)) {
                        Toast.makeText(this, "时间不合法,我不会穿越o~", Toast.LENGTH_SHORT).show();
                    } else if (isUpdate()) {
                        updateMemo();
                    } else {
                        finish();
                    }
                }
                break;
            //点击了返回
            case R.id.check_iv_back:
                if (isUpdate()) {
                    newUpdateDialog();
                } else {
                    finish();
                }
                break;
            //点击了选择日期
            case R.id.check_rv_date:
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
                            check_tv_date.setText("今天-" + (month + 1) + "月" + day + "日," + weekOfday);
                        } else if (DateUtils.isTomorrow(myCalendar.getNow_year(), myCalendar.getNow_month(), myCalendar.getNow_day(), selected_year, selected_month, selected_day)) {
                            check_tv_date.setText("明天-" + (month + 1) + "月" + day + "日," + weekOfday);
                        } else {
                            check_tv_date.setText((month + 1) + "月" + day + "日," + weekOfday);
                        }
                    }
                }, Integer.parseInt(myCalendar.getNow_year()), Integer.parseInt(myCalendar.getNow_month()) - 1, Integer.parseInt(myCalendar.getNow_day())).show();
                break;
            //点击了选择开始时间
            case R.id.check_rv_time_start:
                if (check_tp_finish_time.getVisibility() == View.VISIBLE) {
                    check_tp_finish_time.setVisibility(View.GONE);
                }
                check_tp_start_time.setVisibility(View.VISIBLE);
                check_tp_start_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                        selected_start_hour = DateUtils.toNormalTime(hour);
                        selected_start_minute = DateUtils.toNormalTime(minute);
                        check_tv_time_start.setText("开始-" + selected_start_hour + ":" + selected_start_minute);

                    }
                });
                break;
            //点击了选择结束时间
            case R.id.check_rv_time_finish:
                if (check_tp_start_time.getVisibility() == View.VISIBLE) {
                    check_tp_start_time.setVisibility(View.GONE);
                }
                check_tp_finish_time.setVisibility(View.VISIBLE);
                check_tp_finish_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
                        selected_finish_hour = DateUtils.toNormalTime(hour);
                        selected_finish_minute = DateUtils.toNormalTime(minute);
                        check_tv_time_finish.setText("结束-" + selected_finish_hour + ":" + selected_finish_minute);
                    }
                });
                break;
            default:
                break;

        }
    }

    @Override//点击了文本框
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (check_tp_start_time.getVisibility() == View.VISIBLE) {
            check_tp_start_time.setVisibility(View.GONE);
        } else if (check_tp_finish_time.getVisibility() == View.VISIBLE) {
            check_tp_finish_time.setVisibility(View.GONE);
        }
        return false;
    }

    //当点击删除按钮，弹出确认是否删除的对话框提示
    public void newDeleteDialog() {
        AlertDialog.Builder builder_isDeleteMemo = new AlertDialog.Builder(this);
        builder_isDeleteMemo.setMessage("备忘录将被删除");
        builder_isDeleteMemo.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMemo();
            }
        });
        builder_isDeleteMemo.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder_isDeleteMemo.create().show();
    }

    //用户确认删除，数据库删除操作
    public void deleteMemo() {
        Intent intent_deleteSuccessful = new Intent();
        intent_deleteSuccessful.putExtra("old_memo", memo);
        setResult(RESULTCODE_DELETE, intent_deleteSuccessful);
        finish();
    }

    //当选择返回而用户有更改痕迹，弹出是否保存的对话框的提示
    public void newUpdateDialog() {
        AlertDialog.Builder builder_isUpdateMemo = new AlertDialog.Builder(this);
        builder_isUpdateMemo.setMessage("保存更改?");
        builder_isUpdateMemo.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateMemo();
            }
        });
        builder_isUpdateMemo.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder_isUpdateMemo.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder_isUpdateMemo.create().show();
    }

    //用户确认更改，数据库更改操作
    public void updateMemo() {
        Memo update_memo = new Memo();
        update_memo.setId(selected_year + selected_month + selected_day + selected_start_hour + selected_start_minute + selected_finish_hour + selected_finish_minute + DateUtils.toNormalTime(Integer.parseInt(myCalendar.getNow_hour())) + DateUtils.toNormalTime(Integer.parseInt(myCalendar.getNow_minute())) + DateUtils.toNormalTime(Integer.parseInt(myCalendar.getNow_second())));
        update_memo.setYear(selected_year);
        update_memo.setMonth(selected_month);
        update_memo.setDay(selected_day);
        update_memo.setWeek(selected_week);
        update_memo.setStart_hour(selected_start_hour);
        update_memo.setStart_minute(selected_start_minute);
        update_memo.setFinish_hour(selected_finish_hour);
        update_memo.setFinish_minute(selected_finish_minute);
        update_memo.setText(check_tv_text.getText().toString().trim());
        update_memo.setIs_completed(0);
        update_memo.setIs_first(0);
        update_memo.setIs_chosen(0);
        Intent intent_updateSuccessful = new Intent();
        intent_updateSuccessful.putExtra("update_memo", update_memo);
        intent_updateSuccessful.putExtra("old_memo", memo);
        setResult(RESULTCODE_UPDATE, intent_updateSuccessful);
        finish();
    }

    //判断当前memo是否被修改
    public boolean isUpdate() {
        if (selected_year.equals(memo.getYear()) && selected_month.equals(memo.getMonth()) && selected_day.equals(memo.getDay())
                && selected_week.equals(memo.getWeek()) && selected_start_hour.equals(memo.getStart_hour())
                && selected_start_minute.equals(memo.getStart_minute()) && selected_finish_hour.equals(memo.getFinish_hour())
                && selected_finish_minute.equals(memo.getFinish_minute()) && check_tv_text.getText().toString().trim().equals(memo.getText())) {

            return false;
        }
        return true;
    }

    @Override//重写返回键事件
    public void onBackPressed() {
        if (isUpdate()) {
            newUpdateDialog();
        } else {
            finish();
        }
    }
}
