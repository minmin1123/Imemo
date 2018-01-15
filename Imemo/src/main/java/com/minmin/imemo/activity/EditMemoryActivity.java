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
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.minmin.imemo.R;
import com.minmin.imemo.model.Memory;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MyCalendar;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:新建一条纪念日界面
 *   version:1.0
 * </pre>
 */

public class EditMemoryActivity extends Activity implements View.OnClickListener {

    private ImageView mSaveIv;

    private ImageView mBackIv;

    private EditText mTextET;

    private TextView mCountTv;

    private TextView mArrivedTv;

    private TextView mDateTv;

    private AlertDialog.Builder mIsSaveMemoryBuilder;
    
    private DatePickerDialog mDatePD;

    private MyCalendar mCalendar = new MyCalendar();

    private String mSelectedYear = mCalendar.getNow_year();

    private String mSelectedMonth = mCalendar.getNow_month();

    private String mSelectedDay = mCalendar.getNow_day();

    private final static String RETURN_NEWMEMORY = "newMemory";

    private final int RESULTCODE_EDIT = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editmemory);
        initView();
    }

    //控件初始化
    public void initView() {
        mSaveIv = findViewById(R.id.saveIv);
        mBackIv = findViewById(R.id.backIv);
        mTextET = findViewById(R.id.textET);
        mCountTv = findViewById(R.id.countTv);
        mArrivedTv = findViewById(R.id.arrivedTv);
        mDateTv = findViewById(R.id.dateTv);
        mDateTv.setText("目标日："+mSelectedYear+"年"+mSelectedMonth+"月"+mSelectedDay+"日");
        mSaveIv.setOnClickListener(this);
        mBackIv.setOnClickListener(this);
        mDateTv.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了返回键
            case R.id.backIv:
                if (!TextUtils.isEmpty(mTextET.getText())) {
                    showSaveDialog();
                } else {
                    finish();
                }
                break;
            //点击了保存键
            case R.id.saveIv:
                if (TextUtils.isEmpty(mTextET.getText())) {
                    Toast.makeText(this, R.string.none_content, Toast.LENGTH_SHORT).show();
                } else {

                    saveMemory();
                }
                break;
            case R.id.dateTv:
                showDatePickerDialog();
                break;

        }
    }

    
    //弹出日历选择器
    public void showDatePickerDialog() {

        if (mDatePD == null) {
            mDatePD = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mSelectedYear = year + "";
                    mSelectedMonth = DateUtils.toNormalTime(month + 1);
                    mSelectedDay = DateUtils.toNormalTime(day);
                    mDateTv.setText("目标日："+mSelectedYear+"年"+mSelectedMonth+"月"+mSelectedDay+"日");
                    mCountTv.setText(DateUtils.countSpanDays(mSelectedYear,mSelectedMonth,mSelectedDay)+"");
                    mArrivedTv.setText(DateUtils.isArrived(mSelectedYear + mSelectedMonth +mSelectedDay)?"已经":"还有");
                }
            }, Integer.parseInt(mSelectedYear), Integer.parseInt(mSelectedMonth) - 1, Integer.parseInt(mSelectedDay));
        }
        mDatePD.show();
    }

    //当选择返回而用户有更改痕迹，弹出是否保存的对话框的提示
    public void showSaveDialog() {

        if (mIsSaveMemoryBuilder == null) {
            mIsSaveMemoryBuilder = new AlertDialog.Builder(this);
            mIsSaveMemoryBuilder.setMessage(R.string.ensure_save);
            mIsSaveMemoryBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveMemory();
                }
            });
            mIsSaveMemoryBuilder.setNegativeButton(R.string.give_up, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            mIsSaveMemoryBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        }
        mIsSaveMemoryBuilder.create().show();
    }

    //用户确认保存，插入数据库操作
    public void saveMemory() {
        Memory memory = new Memory();
        memory.setId(mSelectedYear + mSelectedMonth + mSelectedDay + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_hour())) + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_minute())) + DateUtils.toNormalTime(Integer.parseInt(mCalendar.getNow_second())));
        memory.setYear(mSelectedYear);
        memory.setMonth(mSelectedMonth);
        memory.setDay(mSelectedDay);
        memory.setText(mTextET.getText().toString().trim());
        Intent saveSuccessfulIntent = new Intent();
        saveSuccessfulIntent.putExtra(RETURN_NEWMEMORY, memory);
        setResult(RESULTCODE_EDIT, saveSuccessfulIntent);
        finish();
    }

    @Override//重写返回键事件
    public void onBackPressed() {
        if (!TextUtils.isEmpty(mTextET.getText())) {
            showSaveDialog();
        } else {
            finish();
        }
    }
}
