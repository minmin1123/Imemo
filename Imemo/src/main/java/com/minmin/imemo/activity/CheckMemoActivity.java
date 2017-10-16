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

public class CheckMemoActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private Memo memo;

    private ImageView mBackIv;

    private ImageView mDeleteIv;

    private RelativeLayout mDateRl;

    private RelativeLayout mStartTimeRl;

    private RelativeLayout mFinishTimeRl;

    private LinearLayout mContextLl;

    private ImageView mEditIv;

    private TextView mDateTv;

    private TextView mStartTimeTv;

    private TextView mFinishTimeTv;

    private EditText mContextEt;

    private TimePicker mStartTimeTp;

    private TimePicker mFinishTimeTp;

    private MyCalendar mCalendar = new MyCalendar();

    private String selectedYear;

    private String mSelectedMonth;

    private String mSelectedDay;

    private String mSelectedWeek;

    private String mSelectedStartHour;

    private String mSelectedStartMinute;

    private String mSelectedFinishHour;

    private String mSelectedFinishMinute;

    private final static int START_TIME = 1;

    private final static int FINISH_TIME = 2;

    private final int RESULTCODE_DELETE = 3;

    private final int RESULTCODE_UPDATE = 4;

    private final static String DELETE = "delete";

    private final static String SAVE = "save";

    private final static String RETURN_MEMO = "memo";

    private final static String RETURN_OLDMEMO = "oldMemo";

    private final static String RETURN_UPDATEMEMO = "updateMemo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmemo);
        //获取选定memo对象，并进行页面初始化
        memo = (Memo) getIntent().getSerializableExtra(RETURN_MEMO);
        initView();
        selectedYear = memo.getYear();
        mSelectedMonth = memo.getMonth();
        mSelectedDay = memo.getDay();
        mSelectedWeek = memo.getWeek();
        mSelectedStartHour = memo.getStart_hour();
        mSelectedStartMinute = memo.getStart_minute();
        mSelectedFinishHour = memo.getFinish_hour();
        mSelectedFinishMinute = memo.getFinish_minute();
    }

    public void initView() {
        mDeleteIv = findViewById(R.id.deleteIv);
        mBackIv = findViewById(R.id.backIv);
        mDateRl = findViewById(R.id.dateRl);
        mStartTimeRl = findViewById(R.id.startTimeRl);
        mFinishTimeRl = findViewById(R.id.finishTimeRl);
        mDateTv = findViewById(R.id.dateTv);
        mEditIv = findViewById(R.id.editIv);
        if (mCalendar.getNow_year().equals(memo.getYear()) && mCalendar.getNow_month().equals(memo.getMonth()) && mCalendar.getNow_day().equals(memo.getDay())) {
            mDateTv.setText("今天-" + memo.getMonth() + "月" + memo.getDay() + "日," + memo.getWeek());
        } else if (DateUtils.isTomorrow(mCalendar.getNow_year(), mCalendar.getNow_month(), mCalendar.getNow_day(), memo.getYear(), memo.getMonth(), memo.getDay())) {
            mDateTv.setText("明天-" + memo.getMonth() + "月" + memo.getDay() + "日," + memo.getWeek());
        } else {
            mDateTv.setText(memo.getMonth() + "月" + memo.getDay() + "日," + memo.getWeek());
        }
        mStartTimeTv = findViewById(R.id.startTimeTv);
        mStartTimeTv.setText("开始-" + memo.getStart_hour() + ":" + memo.getStart_minute());
        mFinishTimeTv = findViewById(R.id.finishTimeTv);
        mFinishTimeTv.setText("结束-" + memo.getFinish_hour() + ":" + memo.getFinish_minute());
        mContextEt = findViewById(R.id.contextEt);
        mContextEt.setText(memo.getText());
        mStartTimeTp = findViewById(R.id.startTimeTp);
        mStartTimeTp.setIs24HourView(true);
        mStartTimeTp.setCurrentHour(Integer.parseInt(memo.getStart_hour()));
        mStartTimeTp.setCurrentMinute(Integer.parseInt(memo.getStart_minute()));
        mFinishTimeTp = findViewById(R.id.finishTimeTp);
        mFinishTimeTp.setIs24HourView(true);
        mFinishTimeTp.setCurrentHour(Integer.parseInt(memo.getFinish_hour()));
        mFinishTimeTp.setCurrentMinute(Integer.parseInt(memo.getFinish_minute()));
        mContextLl = findViewById(R.id.contextLl);
        mDeleteIv.setOnClickListener(this);
        mBackIv.setOnClickListener(this);
        mDateRl.setOnClickListener(this);
        mStartTimeRl.setOnClickListener(this);
        mFinishTimeRl.setOnClickListener(this);
        mEditIv.setOnClickListener(this);
        mContextLl.setOnTouchListener(this);
        mContextEt.setOnTouchListener(this);
        mDateRl.setClickable(false);
        mStartTimeRl.setClickable(false);
        mFinishTimeRl.setClickable(false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了编辑
            case R.id.editIv:
                controlChange();
                break;
            //点击了删除
            case R.id.deleteIv:
                if (mDeleteIv.getTag().equals(DELETE)) {
                    newDeleteDialog();
                } else if (mDeleteIv.getTag().equals(SAVE)) {
                    checkText();
                }
                break;
            //点击了返回
            case R.id.backIv:
                if (isUpdate()) {
                    newUpdateDialog();
                } else {
                    finish();
                }
                break;
            //点击了选择日期
            case R.id.dateRl:
                newDatePickerDialog();
                break;
            //点击了选择开始时间
            case R.id.startTimeRl:
                if (mFinishTimeTp.getVisibility() == View.VISIBLE) {
                    mFinishTimeTp.setVisibility(View.GONE);
                }
                mStartTimeTp.setVisibility(View.VISIBLE);
                timeChange(START_TIME);
                break;
            //点击了选择结束时间
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

    //点击编辑之后的控件变化
    public void controlChange(){
        mDateRl.setClickable(true);
        mStartTimeRl.setClickable(true);
        mFinishTimeRl.setClickable(true);
        mContextEt.setEnabled(true);
        mContextEt.setSelection(memo.getText().length());
        mEditIv.setVisibility(View.INVISIBLE);
        mDeleteIv.setBackgroundResource(R.drawable.save);
        mDeleteIv.setTag(DELETE);
    }

    //检查文本内容是否合法
    public void checkText(){
        if (Integer.parseInt(mSelectedStartHour + mSelectedStartMinute) >= Integer.parseInt(mSelectedFinishHour + mSelectedFinishMinute)) {
            Toast.makeText(this, R.string.time_error, Toast.LENGTH_SHORT).show();
        } else if (isUpdate()) {
            updateMemo();
        } else {
            finish();
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
    //弹出日历选择器
    public void newDatePickerDialog(){
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String weekOfday = DateUtils.getSelectedWeek(year, month, day);
                selectedYear = String.valueOf(year);
                mSelectedMonth = DateUtils.toNormalTime(month + 1);
                mSelectedDay = DateUtils.toNormalTime(day);
                mSelectedWeek = weekOfday;
                if (mCalendar.getNow_year().equals(selectedYear) && mCalendar.getNow_month().equals(mSelectedMonth) && mCalendar.getNow_day().equals(mSelectedDay)) {
                    mDateTv.setText("今天-" + (month + 1) + "月" + day + "日," + weekOfday);
                } else if (DateUtils.isTomorrow(mCalendar.getNow_year(), mCalendar.getNow_month(), mCalendar.getNow_day(), selectedYear, mSelectedMonth, mSelectedDay)) {
                    mDateTv.setText("明天-" + (month + 1) + "月" + day + "日," + weekOfday);
                } else {
                    mDateTv.setText((month + 1) + "月" + day + "日," + weekOfday);
                }
            }
        }, Integer.parseInt(mCalendar.getNow_year()), Integer.parseInt(mCalendar.getNow_month()) - 1, Integer.parseInt(mCalendar.getNow_day())).show();
    }

    //当点击删除按钮，弹出确认是否删除的对话框提示
    public void newDeleteDialog() {
        AlertDialog.Builder isDeleteMemoBuilder = new AlertDialog.Builder(this);
        isDeleteMemoBuilder.setMessage(R.string.ensure_delete);
        isDeleteMemoBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMemo();
            }
        });
        isDeleteMemoBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        isDeleteMemoBuilder.create().show();
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

    //用户确认删除，数据库删除操作
    public void deleteMemo() {
        Intent deleteSuccessfulIntent = new Intent();
        deleteSuccessfulIntent.putExtra(RETURN_OLDMEMO, memo);
        setResult(RESULTCODE_DELETE, deleteSuccessfulIntent);
        finish();
    }

    //当选择返回而用户有更改痕迹，弹出是否保存的对话框的提示
    public void newUpdateDialog() {
        AlertDialog.Builder isUpdateMemoBuilder = new AlertDialog.Builder(this);
        isUpdateMemoBuilder.setMessage(R.string.ensure_save);
        isUpdateMemoBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                updateMemo();
            }
        });
        isUpdateMemoBuilder.setNegativeButton(R.string.give_up, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        isUpdateMemoBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        isUpdateMemoBuilder.create().show();
    }

    //用户确认更改，数据库更改操作
    public void updateMemo() {
        Memo updateMemo = new Memo();
        updateMemo.setId(selectedYear + mSelectedMonth + mSelectedDay + mSelectedStartHour + mSelectedStartMinute + mSelectedFinishHour + mSelectedFinishMinute + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_hour())) + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_minute())) + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_second())));
        updateMemo.setYear(selectedYear);
        updateMemo.setMonth(mSelectedMonth);
        updateMemo.setDay(mSelectedDay);
        updateMemo.setWeek(mSelectedWeek);
        updateMemo.setStart_hour(mSelectedStartHour);
        updateMemo.setStart_minute(mSelectedStartMinute);
        updateMemo.setFinish_hour(mSelectedFinishHour);
        updateMemo.setFinish_minute(mSelectedFinishMinute);
        updateMemo.setText(mContextEt.getText().toString().trim());
        updateMemo.setIs_completed(0);
        updateMemo.setIs_first(0);
        updateMemo.setIs_chosen(0);
        Intent updateSuccessfulIntent = new Intent();
        updateSuccessfulIntent.putExtra(RETURN_UPDATEMEMO, updateMemo);
        updateSuccessfulIntent.putExtra(RETURN_OLDMEMO, memo);
        setResult(RESULTCODE_UPDATE, updateSuccessfulIntent);
        finish();
    }

    //判断当前memo是否被修改
    public boolean isUpdate() {
        if (selectedYear.equals(memo.getYear()) && mSelectedMonth.equals(memo.getMonth()) && mSelectedDay.equals(memo.getDay())
                && mSelectedWeek.equals(memo.getWeek()) && mSelectedStartHour.equals(memo.getStart_hour())
                && mSelectedStartMinute.equals(memo.getStart_minute()) && mSelectedFinishHour.equals(memo.getFinish_hour())
                && mSelectedFinishMinute.equals(memo.getFinish_minute()) && mContextEt.getText().toString().trim().equals(memo.getText())) {

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
