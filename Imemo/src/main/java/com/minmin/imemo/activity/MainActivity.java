package com.minmin.imemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.minmin.imemo.R;
import com.minmin.imemo.adapter.MemoListAdapter;
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memo;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MemoListManager;
import com.minmin.imemo.util.MyCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:
 *   version:1.0
 * </pre>
 */

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mAddAnyIv;

    private TextView mDateTv;

    private ImageView mLeftIv;

    private ImageView mRightIv;

    private ImageView mMoreIv;

    private TextView mNothingTv;

    private ListView mListLv;

    private TextView mChosenTv;

    private ImageView mDealIv;

    private MyCalendar mCalendar = new MyCalendar();

    private String mSelectedYear = mCalendar.getNow_year();

    private String mSelectedMonth = mCalendar.getNow_month();

    private MemoDatabase mMemoDatabase = MemoDatabase.getInstance(this);

    private List<Memo> mMemoList = new ArrayList<>();

    private List<Memo> mSelectedMemoList = new ArrayList<>();

    private MemoListAdapter mMemoListAdapter;

    private String[] moreList;

    private final static int REQUESTCODE_MAIN = 1;

    private final static int RESULTCODE_EDIT = 2;

    private final static int RESULTCODE_DELETE = 3;

    private final static int RESULTCODE_UPDATE = 4;
    
    private final static int LEFT = 5;

    private final static int RIGHT = 6;

    private String mCopyYear=mSelectedYear;

    private String mCopyMonth=mSelectedMonth;

    private String mCopyDay=mCalendar.getNow_day();

    private int MORE_STATUS=0;

    private final static String DELETE = "delete";

    private final static String COPY= "copy";

    private final static String UNSELECTED="unselected";

    private final static String SELECTED="selected";

    private final static String RETURN_MEMO = "memo";

    private final static String RETURN_NEWMEMO = "newMemo";

    private final static String RETURN_OLDMEMO = "oldMemo";

    private final static String RETURN_UPDATEMEMO = "updateMemo";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moreList = new String[]{getResources().getString(R.string.bulk_delete), getResources().getString(R.string.bulk_copy)};
        //初始化dataList和adapter
        mMemoList = mMemoDatabase.quaryEveryMonthMemoList(mSelectedYear, mSelectedMonth);
        mMemoListAdapter= new MemoListAdapter(this, R.layout.item_memo, mMemoList);
        //页面初始化
        initView();
        updateMemoList();
    }

    //页面初始化
    public void initView() {
        mAddAnyIv = findViewById(R.id.addAnyIv);
        mDateTv = findViewById(R.id.dateTv);
        mNothingTv = findViewById(R.id.nothingTv);
        mLeftIv = findViewById(R.id.leftIv);
        mRightIv = findViewById(R.id.rightIv);
        mMoreIv = findViewById(R.id.moreIv);
        mListLv = findViewById(R.id.listLv);
        mChosenTv = findViewById(R.id.chosenTv);
        mDealIv = findViewById(R.id.dealIv);
        mAddAnyIv.setOnClickListener(this);
        mLeftIv.setOnClickListener(this);
        mRightIv.setOnClickListener(this);
        mDealIv.setOnClickListener(this);
        mDateTv.setText(mSelectedYear + "-" + mSelectedMonth);
        mListLv.setOnItemClickListener(this);
        mMoreIv.setOnClickListener(this);
        mListLv.setAdapter(mMemoListAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了大"+"，新建一条任意日期的备忘录
            case R.id.addAnyIv:
                Intent toEditMemoIntent = new Intent(MainActivity.this, EditMemoActivity.class);
                startActivityForResult(toEditMemoIntent, REQUESTCODE_MAIN);
                break;
            //点击了查看前一个月备忘录记录
            case R.id.leftIv:
                changeDateTV(LEFT);
                mMemoList.clear();
                mMemoList.addAll(mMemoDatabase.quaryEveryMonthMemoList(mSelectedYear, mSelectedMonth));
                updateMemoList();
                break;
            //点击了查看后一个月备忘录记录
            case R.id.rightIv:
                changeDateTV(RIGHT);
                mMemoList.clear();
                mMemoList.addAll(mMemoDatabase.quaryEveryMonthMemoList(mSelectedYear, mSelectedMonth));
                updateMemoList();
                break;
            //点击了更多（批量删除、批量复制）
            case R.id.moreIv:
                newMoreDialog();
                break;
            //点击了处理（批量删除、批量复制）
            case R.id.dealIv:
                checkMoreSelect();
                break;
            default:
                break;
        }
    }

    //更改标题日期的内容
    public void changeDateTV(int flag){
        if(flag==LEFT){
            if (Integer.parseInt(mSelectedMonth) == 1) {
                mSelectedMonth = "12";
                mSelectedYear = Integer.parseInt(mSelectedYear) - 1+"";
            } else {
                mSelectedMonth = DateUtils.toNormalTime(Integer.parseInt(mSelectedMonth) - 1);
            }
            mDateTv.setText(mSelectedYear + "-" + mSelectedMonth);
        }else if(flag==RIGHT){
            if (Integer.parseInt(mSelectedMonth) == 12) {
                mSelectedMonth = "01";
                mSelectedYear = Integer.parseInt(mSelectedYear) + 1+"";
            } else {
                mSelectedMonth = DateUtils.toNormalTime(Integer.parseInt(mSelectedMonth) + 1);
            }
            mDateTv.setText(mSelectedYear + "-" + mSelectedMonth);
        }
    }

    @Override//新建、更改、删除三种操作对dataList的更改
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_EDIT) {
            Memo newMemo = (Memo) data.getSerializableExtra(RETURN_NEWMEMO);
            if(newMemo.getYear().equals(mSelectedYear)&&newMemo.getMonth().equals(mSelectedMonth)){
                mMemoList=MemoListManager.insertMemo(mMemoList,newMemo);
            }
            MemoDatabase.getInstance(this).insertMemo(newMemo);
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
            updateMemoList();
        } else if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_UPDATE) {
            Memo updateMemo = (Memo) data.getSerializableExtra(RETURN_UPDATEMEMO);
            Memo oldMemo = (Memo) data.getSerializableExtra(RETURN_OLDMEMO);
            mMemoList=MemoListManager.deleteMemo(mMemoList,oldMemo);
            if(updateMemo.getYear().equals(mSelectedYear)&&updateMemo.getMonth().equals(mSelectedMonth)){
                mMemoList=MemoListManager.insertMemo(mMemoList,updateMemo);
            }
            MemoDatabase.getInstance(this).updateMemo(oldMemo,updateMemo);
            Toast.makeText(this, R.string.update_success, Toast.LENGTH_SHORT).show();
            updateMemoList();
        } else if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_DELETE) {
            Memo oldMemo = (Memo) data.getSerializableExtra(RETURN_OLDMEMO);
            mMemoList=MemoListManager.deleteMemo(mMemoList,oldMemo);
            MemoDatabase.getInstance(this).deleteMemo(oldMemo);
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
            updateMemoList();
        }
    }

    //更新当前显示的dataList
    public void updateMemoList() {
        if (mMemoList.size() != 0) {
            mNothingTv.setVisibility(View.GONE);
            mListLv.setVisibility(View.VISIBLE);
            mMemoListAdapter.notifyDataSetChanged();
            mListLv.setSelection(0);
        } else {
            mNothingTv.setVisibility(View.VISIBLE);
            mListLv.setVisibility(View.GONE);
        }
    }

    @Override//ListView的子项点击事件
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        if(MORE_STATUS==0){
            //非更多（批量删除、批量复制）状态，点击后查看选定子项的备忘录详情
           Intent toCheckMemoIntent = new Intent(MainActivity.this, CheckMemoActivity.class);
           Memo memo = mMemoList.get(position);
           toCheckMemoIntent.putExtra(RETURN_MEMO, memo);
           startActivityForResult(toCheckMemoIntent, REQUESTCODE_MAIN);
       }else{
            //更多（批量删除、批量复制）状态，点击后变化是否选中
            final TextView mItemContextTv=view.findViewById(R.id.contextTv);
            if(mItemContextTv.getTag().equals(UNSELECTED)){
                mItemContextTv.setTag(SELECTED);
                mItemContextTv.setBackgroundResource(R.drawable.paper_select);
                mMemoList.get(position).setIs_chosen(1);
                MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(mMemoList.get(position), 1);
                mSelectedMemoList.add(mMemoList.get(position));
            }else{
                mItemContextTv.setTag(UNSELECTED);
                mItemContextTv.setBackgroundResource(R.drawable.paper);
                mMemoList.get(position).setIs_chosen(0);
                MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(mMemoList.get(position), 0);
                mSelectedMemoList.remove(mMemoList.get(position));
                for(Memo selectMemo:mSelectedMemoList){
                    if(selectMemo.getId().equals(mMemoList.get(position).getId())){
                        mSelectedMemoList.remove(selectMemo);
                        break;
                    }
                }
            }
            mChosenTv.setText("已选择"+ mSelectedMemoList.size()+"项");
       }

    }

    //更多选项的对话框
    public void newMoreDialog(){
        AlertDialog.Builder moreMemoBuilder = new AlertDialog.Builder(this);
        moreMemoBuilder.setItems(moreList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                MORE_STATUS=1;
                if (position == 0) {
                    toDeleteSelectItem();
                } else if (position == 1) {
                    toCopySelectItem();
                }
            }
        });
        moreMemoBuilder.create().show();
    }

    //批量删除状态
    public void toDeleteSelectItem() {
        mChosenTv.setVisibility(View.VISIBLE);
        mDealIv.setVisibility(View.VISIBLE);
        mDealIv.setBackgroundResource(R.drawable.delete);
        mDealIv.setTag(DELETE);
    }

    //当点击批量删除按钮，弹出确认是否删除的对话框提示
    public void newDeleteDialog() {
        AlertDialog.Builder isDeleteMemoBuilder = new AlertDialog.Builder(this);
        isDeleteMemoBuilder.setMessage(R.string.ensure_delete_selected);
        isDeleteMemoBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(Memo eachMemo:mSelectedMemoList){
                    mMemoList=MemoListManager.deleteMemo(mMemoList,eachMemo);
                }
                MemoDatabase.getInstance(MainActivity.this).deleteSelectMemoList(mSelectedMemoList);
                mSelectedMemoList.clear();
                MORE_STATUS=0;
                mChosenTv.setVisibility(View.INVISIBLE);
                mDealIv.setVisibility(View.INVISIBLE);
                mChosenTv.setText(R.string.select_item);
                updateMemoList();
                Toast.makeText(MainActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
            }
        });
        isDeleteMemoBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(Memo memo:mSelectedMemoList){
                    memo.setIs_chosen(0);
                    MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                }
                mSelectedMemoList.clear();
                MORE_STATUS=0;
                mChosenTv.setVisibility(View.INVISIBLE);
                mDealIv.setVisibility(View.INVISIBLE);
                mChosenTv.setText(R.string.select_item);
                updateMemoList();
            }
        });
        isDeleteMemoBuilder.create().show();
    }

    //批量复制状态
    public void toCopySelectItem() {
        mChosenTv.setVisibility(View.VISIBLE);
        mDealIv.setVisibility(View.VISIBLE);
        mDealIv.setBackgroundResource(R.drawable.copy);
        mDealIv.setTag(COPY);
    }

    //当点击批量复制按钮，先弹出对话框让选择创建日期
    public void newSelectDayDialog() {
        AlertDialog.Builder selectDayBuilder = new AlertDialog.Builder(this);
        selectDayBuilder.setTitle(R.string.select_date);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_copy, null);
        DatePicker dialog_copy_dp = view.findViewById(R.id.dialog_copy_dp);
        dialog_copy_dp.init(Integer.parseInt(mSelectedYear), Integer.parseInt(mSelectedMonth)-1, Integer.parseInt(mCalendar.getNow_day()), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                mCopyYear = String.valueOf(year);
                mCopyMonth = DateUtils.toNormalTime(month + 1);
                mCopyDay = DateUtils.toNormalTime(day);
            }
        });
        selectDayBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newCopyDialog();
            }
        });
        selectDayBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MORE_STATUS=0;
                for(Memo memo:mSelectedMemoList){
                    memo.setIs_chosen(0);
                    MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                }
                mSelectedMemoList.clear();
                mChosenTv.setVisibility(View.INVISIBLE);
                mDealIv.setVisibility(View.INVISIBLE);
                mChosenTv.setText(R.string.select_item);
                updateMemoList();
            }
        });
        selectDayBuilder.setView(view);
        selectDayBuilder.create().show();
    }

    //用户选择好日期后，弹出确认是否创建到该日期的对话框提示
    public void  newCopyDialog() {
        AlertDialog.Builder isCopyMemoBuilder = new AlertDialog.Builder(this);
        isCopyMemoBuilder.setMessage("将被创建至"+mCopyYear+"年"+mCopyMonth+"月"+mCopyDay+"日");
        isCopyMemoBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MORE_STATUS=0;
                MemoDatabase.getInstance(MainActivity.this).insertSelectMemoList(mSelectedMemoList,mCopyYear,mCopyMonth,mCopyDay);
                for(Memo memo:mSelectedMemoList){
                    memo.setIs_chosen(0);
                    MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                }
                if(mCopyYear.equals(mSelectedYear)&&mCopyMonth.equals(mSelectedMonth)){
                    for(Memo memo:mSelectedMemoList){
                        memo.setIs_chosen(0);
                        MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                        Memo copyMemo = new Memo();
                        copyMemo.setId(mCopyYear + mCopyMonth + mCopyDay +  memo.getStart_hour() + memo.getStart_minute() + memo.getFinish_hour() + memo.getFinish_minute() + new MyCalendar().getNow_hour() + new MyCalendar().getNow_minute()+new MyCalendar().getNow_second());
                        copyMemo.setYear(mCopyYear);
                        copyMemo.setMonth(mCopyMonth);
                        copyMemo.setDay(mCopyDay);
                        copyMemo.setWeek(DateUtils.getSelectedWeek(Integer.parseInt(mCopyYear),Integer.parseInt(mCopyMonth),Integer.parseInt(mCopyDay)));
                        copyMemo.setStart_hour(memo.getStart_hour());
                        copyMemo.setStart_minute(memo.getStart_minute());
                        copyMemo.setFinish_hour(memo.getFinish_hour());
                        copyMemo.setFinish_minute(memo.getFinish_minute());
                        copyMemo.setText(memo.getText());
                        copyMemo.setIs_first(0);
                        copyMemo.setIs_completed(0);
                        copyMemo.setIs_chosen(0);
                        mMemoList=MemoListManager.insertMemo(mMemoList,copyMemo);
                    }
                }
                mSelectedMemoList.clear();
                mChosenTv.setVisibility(View.INVISIBLE);
                mDealIv.setVisibility(View.INVISIBLE);
                mChosenTv.setText(R.string.select_item);
                updateMemoList();
                Toast.makeText(MainActivity.this, R.string.copy_success, Toast.LENGTH_SHORT).show();
            }
        });
        isCopyMemoBuilder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MORE_STATUS=0;
                for(Memo memo:mSelectedMemoList){
                    memo.setIs_chosen(0);
                    MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                }
                mSelectedMemoList.clear();
                mChosenTv.setVisibility(View.INVISIBLE);
                mDealIv.setVisibility(View.INVISIBLE);
                mChosenTv.setText(R.string.select_item);
                updateMemoList();
            }
        });
        isCopyMemoBuilder.create().show();
    }

    //对更多操作的合法性检验
    public void checkMoreSelect(){
        if (mDealIv.getTag().equals(DELETE)) {
            if(mSelectedMemoList.size()==0){
                Toast.makeText(this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
            }else{
                newDeleteDialog();
            }
        } else if (mDealIv.getTag().equals(COPY)) {
            if(mSelectedMemoList.size()==0){
                Toast.makeText(this, R.string.nothing_to_copy, Toast.LENGTH_SHORT).show();
            }else{
                newSelectDayDialog();
            }
        }
    }

    @Override//重写返回键事件
    public void onBackPressed() {
        if(MORE_STATUS==1){
            //更多（批量删除、批量复制）状态，恢复之前状态
            MORE_STATUS=0;
            for(Memo memo:mSelectedMemoList){
                memo.setIs_chosen(0);
                MemoDatabase.getInstance(this).updateMemoChosenStatus(memo, 0);
            }
            mSelectedMemoList.clear();
            mChosenTv.setVisibility(View.INVISIBLE);
            mDealIv.setVisibility(View.INVISIBLE);
            mChosenTv.setText(R.string.select_item);
            updateMemoList();
        }else{
            finish();
        }
    }
}
