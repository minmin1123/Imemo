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
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2018/1/13
 *   desc:查看单条纪念日界面
 *   version:1.0
 */

public class CheckMemoryActivity extends Activity implements View.OnClickListener{

    private Memory memory;

    private ImageView mEditIv;

    private ImageView mBackIv;

    private EditText mTextEt;

    private TextView mCountTv;

    private TextView mArrivedTv;

    private TextView mDateTv;

    private String mSelectedYear;

    private String mSelectedMonth;

    private String mSelectedDay;

    private final int RESULTCODE_UPDATE = 3;

    private final static String EDIT = "edit";
    private final static String SAVE = "save";
    private final static String RETURN_MEMORY = "memory_body";
    private final static String RETURN_OLDMEMORY = "oldMemory";
    private final static String RETURN_UPDATEMEMORY = "updateMemory";

    private AlertDialog.Builder mIsUpdateMemoryBuilder;

    private DatePickerDialog mDatePD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmemory);
        //获取选定memo对象，并进行页面初始化
        memory = (Memory) getIntent().getSerializableExtra(RETURN_MEMORY);
        mSelectedYear = memory.getYear();
        mSelectedMonth = memory.getMonth();
        mSelectedDay = memory.getDay();
        initView();

    }

    public void initView() {
        mEditIv = findViewById(R.id.editIv);
        mBackIv = findViewById(R.id.backIv);
        mTextEt = findViewById(R.id.textEt);
        mTextEt.setText(memory.getText());
        mCountTv = findViewById(R.id.countTv);
        mCountTv.setText(memory.getCount()+"");
        mArrivedTv = findViewById(R.id.arrivedTv);
        mArrivedTv.setText(memory.getIs_arrived()==0?"还有":"已经");
        mDateTv = findViewById(R.id.dateTv);
        mDateTv.setText("目标日："+mSelectedYear+"年"+mSelectedMonth+"月"+mSelectedDay+"日");
        mEditIv.setOnClickListener(this);
        mBackIv.setOnClickListener(this);
        mDateTv.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了编辑/完成
            case R.id.editIv:
                doEditOrSave();
                break;
            //点击了返回
            case R.id.backIv:
                if (isUpdate()) {
                    showUpdateDialog();
                } else {
                    finish();
                }
                break;
            //点击了日期栏
            case R.id.dateTv:
                if(mDateTv.getTag().equals(EDIT)){
                    showDatePickerDialog();
                }
                break;
        }
    }

    //点击编辑&保存键的响应时间
    public void doEditOrSave(){
        if(mEditIv.getTag().equals(EDIT)){
            controlChange();
        }else if(mEditIv.getTag().equals(SAVE)){
            if (TextUtils.isEmpty(mTextEt.getText())) {
                Toast.makeText(this, R.string.none_content, Toast.LENGTH_SHORT).show();
            } else{
                if (isUpdate()) {
                    updateMemory();
                } else {
                    finish();
                }
            }
        }
    }
    
    //点击编辑之后的控件变化
    public void controlChange(){
        
        mTextEt.setEnabled(true);
        mTextEt.setSelection(memory.getText().length());
        mDateTv.setTag(EDIT);
        mEditIv.setBackgroundResource(R.drawable.save);
        mEditIv.setTag(SAVE);
    }


    //弹出日历选择器
    public void showDatePickerDialog(){

        if(mDatePD==null){
            mDatePD=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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
            }, Integer.parseInt(mSelectedYear),  Integer.parseInt(mSelectedMonth)- 1, Integer.parseInt(mSelectedDay));
        }
        mDatePD.show();
    }

    //当选择返回而用户有更改痕迹，弹出是否保存的对话框的提示
    public void showUpdateDialog() {

        if(mIsUpdateMemoryBuilder==null){
            mIsUpdateMemoryBuilder = new AlertDialog.Builder(this);
            mIsUpdateMemoryBuilder.setMessage(R.string.ensure_save);
            mIsUpdateMemoryBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateMemory();
                }
            });
            mIsUpdateMemoryBuilder.setNegativeButton(R.string.give_up, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            mIsUpdateMemoryBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        mIsUpdateMemoryBuilder.create().show();
    }

    //用户确认更改，数据库更改操作
    public void updateMemory() {
        Memory updateMemory = new Memory();
        updateMemory.setId(mSelectedYear + mSelectedMonth + mSelectedDay + DateUtils.toNormalTime(Integer.parseInt(MyCalendar.getNow_hour())) + DateUtils.toNormalTime(Integer.parseInt(MyCalendar.getNow_minute())) + DateUtils.toNormalTime(Integer.parseInt(MyCalendar.getNow_second())));
        updateMemory.setYear(mSelectedYear);
        updateMemory.setMonth(mSelectedMonth);
        updateMemory.setDay(mSelectedDay);
        updateMemory.setText(mTextEt.getText().toString().trim());
        Intent updateSuccessfulIntent = new Intent();
        updateSuccessfulIntent.putExtra(RETURN_UPDATEMEMORY, updateMemory);
        updateSuccessfulIntent.putExtra(RETURN_OLDMEMORY, memory);
        setResult(RESULTCODE_UPDATE, updateSuccessfulIntent);
        finish();
    }

    //判断当前memo是否被修改
    public boolean isUpdate() {
        if (mSelectedYear.equals(memory.getYear()) && mSelectedMonth.equals(memory.getMonth()) && mSelectedDay.equals(memory.getDay())
                && mTextEt.getText().toString().trim().equals(memory.getText())) {

            return false;
        }
        return true;
    }

    @Override//重写返回键事件
    public void onBackPressed() {
        if (isUpdate()) {
            showUpdateDialog();
        } else {
            finish();
        }
    }
}
