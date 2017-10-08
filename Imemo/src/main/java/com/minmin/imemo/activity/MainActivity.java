package com.minmin.imemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.minmin.imemo.R;
import com.minmin.imemo.adapter.MemoListAdapter;
import com.minmin.imemo.database.MemoDatabase;
import com.minmin.imemo.model.Memo;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MyCalendar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView main_iv_add_any;
    private TextView main_tv_date;
    private ImageView main_iv_left;
    private ImageView main_iv_right;
    private ImageView main_iv_more;
    private TextView main_tv_none;
    private ListView main_lv_list;
    private TextView main_tv_chosen;
    private ImageView main_iv_deal;
    private MyCalendar myCalendar = new MyCalendar();
    private String selected_year = myCalendar.getNow_year();
    private String selected_month = myCalendar.getNow_month();
    private MemoDatabase memoDatabase = MemoDatabase.getInstance(this);
    private List<Memo> memoList = new ArrayList<>();
    private List<Memo> selectedMemoList = new ArrayList<>();
    private MemoListAdapter memoListAdapter;
    private String[] moreList = {"批量删除", "批量复制"};
    private final int REQUESTCODE_MAIN = 1;
    private final int RESULTCODE_EDIT = 2;
    private final int RESULTCODE_DELETE = 3;
    private final int RESULTCODE_UPDATE = 4;
    private String copy_year=selected_year;
    private String copy_month=selected_month;
    private String copy_day=myCalendar.getNow_day();
    private int MORE_STATUS=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化dataList和adapter
        memoList = memoDatabase.quaryEveryMonthMemoList(selected_year, selected_month);
        memoListAdapter= new MemoListAdapter(this, R.layout.item_memo, memoList);
        //页面初始化
        initView();
        updateMemoList();
    }

    //页面初始化
    public void initView() {
        main_iv_add_any = findViewById(R.id.main_iv_add_any);
        main_tv_date = findViewById(R.id.main_tv_date);
        main_tv_none = findViewById(R.id.main_tv_none);
        main_iv_left = findViewById(R.id.main_iv_left);
        main_iv_right = findViewById(R.id.main_iv_right);
        main_iv_more = findViewById(R.id.main_iv_more);
        main_lv_list = findViewById(R.id.main_lv_list);
        main_tv_chosen = findViewById(R.id.main_tv_chosen);
        main_iv_deal = findViewById(R.id.main_iv_deal);
        main_iv_add_any.setOnClickListener(this);
        main_iv_left.setOnClickListener(this);
        main_iv_right.setOnClickListener(this);
        main_iv_deal.setOnClickListener(this);
        main_tv_date.setText(selected_year + "-" + selected_month);
        main_lv_list.setOnItemClickListener(this);
        main_iv_more.setOnClickListener(this);
        main_lv_list.setAdapter(memoListAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //点击了大"+"，新建一条任意日期的备忘录
            case R.id.main_iv_add_any:
                Intent toEditMemo_intent = new Intent(MainActivity.this, EditMemoActivity.class);
                startActivityForResult(toEditMemo_intent, REQUESTCODE_MAIN);
                break;
            //点击了查看前一个月备忘录记录
            case R.id.main_iv_left:
                if (Integer.parseInt(selected_month) == 1) {
                    selected_month = "12";
                    selected_year = String.valueOf(Integer.parseInt(selected_year) - 1);
                } else {
                    selected_month = DateUtils.toNormalTime(Integer.parseInt(selected_month) - 1);
                }
                main_tv_date.setText(selected_year + "-" + selected_month);
                memoList = memoDatabase.quaryEveryMonthMemoList(selected_year, selected_month);
                updateMemoList();
                break;
            //点击了查看后一个月备忘录记录
            case R.id.main_iv_right:
                if (Integer.parseInt(selected_month) == 12) {
                    selected_month = "01";
                    selected_year = String.valueOf(Integer.parseInt(selected_year) + 1);
                } else {
                    selected_month = DateUtils.toNormalTime(Integer.parseInt(selected_month) + 1);
                }
                main_tv_date.setText(selected_year + "-" + selected_month);
                memoList = memoDatabase.quaryEveryMonthMemoList(selected_year, selected_month);
                updateMemoList();
                break;
            //点击了更多（批量删除、批量复制）
            case R.id.main_iv_more:
                AlertDialog.Builder builder_more_memo = new AlertDialog.Builder(this);
                builder_more_memo.setItems(moreList, new DialogInterface.OnClickListener() {
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
                builder_more_memo.create().show();
                break;
            //点击了处理（批量删除、批量复制）
            case R.id.main_iv_deal:
                if (main_iv_deal.getTag().equals("delete")) {
                    if(selectedMemoList.size()==0){
                        Toast.makeText(this, "没有项目可以删除o~", Toast.LENGTH_SHORT).show();
                    }else{
                        newDeleteDialog();
                    }
                } else if (main_iv_deal.getTag().equals("copy")) {
                    if(selectedMemoList.size()==0){
                        Toast.makeText(this, "没有项目可以复制o~", Toast.LENGTH_SHORT).show();
                    }else{
                        newSelectDayDialog();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override//新建、更改、删除三种操作对dataList的更改
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_EDIT) {
            Memo new_memo = (Memo) data.getSerializableExtra("new_memo");
            if(new_memo.getYear().equals(selected_year)&&new_memo.getMonth().equals(selected_month)){
                insertMemo(new_memo);
            }
            MemoDatabase.getInstance(this).insertMemo(new_memo);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            updateMemoList();
        } else if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_UPDATE) {
            Memo update_memo = (Memo) data.getSerializableExtra("update_memo");
            Memo old_memo = (Memo) data.getSerializableExtra("old_memo");
            deleteMemo(old_memo);
            if(update_memo.getYear().equals(selected_year)&&update_memo.getMonth().equals(selected_month)){
                insertMemo(update_memo);
            }
            MemoDatabase.getInstance(this).updateMemo(old_memo,update_memo);
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            updateMemoList();
        } else if (requestCode == REQUESTCODE_MAIN && resultCode == RESULTCODE_DELETE) {
            Memo old_memo = (Memo) data.getSerializableExtra("old_memo");
            deleteMemo(old_memo);
            MemoDatabase.getInstance(this).deleteMemo(old_memo);
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            updateMemoList();
        }
    }

    //向dataList增添一条memo
    public void insertMemo(Memo memo){
        int index = -1;
        for(int i=0;i<memoList.size();i++){
            //如果比前者大后者小记录后者位置
            if(memoList.get(i).getId().compareTo(memo.getId())==1){
                index=i;
                break;
            }
        }
        if(index!=-1){
            memoList.add(index,memo);
        }else{
            memoList.add(memo);
        }
        //每插入一条memo就对dataList的Is_first进行一次重新赋值
        String cursor=null;
        for(Memo eachmemo:memoList){
            if(!eachmemo.getDay().equals(cursor)){
                eachmemo.setIs_first(1);
                cursor = eachmemo.getDay();
            }else{
                eachmemo.setIs_first(0);
            }
        }
    }

    //从dataList删除一条memo
    public void deleteMemo(Memo memo){
        for(Memo eachmemo:memoList){
            if(eachmemo.getId().equals(memo.getId())){
                memoList.remove(eachmemo);
                break;
            }
        }
        //每删除一条memo就对dataList的Is_first进行一次重新赋值
        String cursor=null;
        for(Memo eachmemo:memoList){
            if(!eachmemo.getDay().equals(cursor)){
                eachmemo.setIs_first(1);
                cursor = eachmemo.getDay();
            }else{
                eachmemo.setIs_first(0);
            }
        }
    }

    //更新当前显示的dataList
    public void updateMemoList() {
        Log.i("Main", "当前dataList的个数："+String.valueOf(memoList.size()));
        if (memoList.size() != 0) {
            main_tv_none.setVisibility(View.GONE);
            main_lv_list.setVisibility(View.VISIBLE);
            memoListAdapter.notifyDataSetChanged();
            main_lv_list.setSelection(0);
        } else {
            main_tv_none.setVisibility(View.VISIBLE);
            main_lv_list.setVisibility(View.GONE);
        }
    }

    @Override//ListView的子项点击事件
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
        if(MORE_STATUS==0){
            //非更多（批量删除、批量复制）状态，点击后查看选定子项的备忘录详情
           Intent toCheckMemo_intent = new Intent(MainActivity.this, CheckMemoActivity.class);
           Memo memo = memoList.get(position);
           toCheckMemo_intent.putExtra("memo", memo);
           startActivityForResult(toCheckMemo_intent, REQUESTCODE_MAIN);
       }else{
            //更多（批量删除、批量复制）状态，点击后变化是否选中
            final TextView item_tv_text=view.findViewById(R.id.item_tv_text);
            if(item_tv_text.getTag().equals("unselected")){
                item_tv_text.setTag("selected");
                item_tv_text.setBackgroundResource(R.drawable.paper_select);
                memoList.get(position).setIs_chosen(1);
                MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memoList.get(position), 1);
                selectedMemoList.add(memoList.get(position));
            }else{
                item_tv_text.setTag("unselected");
                item_tv_text.setBackgroundResource(R.drawable.paper);
                memoList.get(position).setIs_chosen(0);
                MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memoList.get(position), 0);
                selectedMemoList.remove(memoList.get(position));
            }
            main_tv_chosen.setText("已选择"+ selectedMemoList.size()+"项");
       }

    }

    //批量删除状态
    public void toDeleteSelectItem() {
        main_tv_chosen.setVisibility(View.VISIBLE);
        main_iv_deal.setVisibility(View.VISIBLE);
        main_iv_deal.setBackgroundResource(R.drawable.delete);
        main_iv_deal.setTag("delete");
    }

    //当点击批量删除按钮，弹出确认是否删除的对话框提示
    public void newDeleteDialog() {
        AlertDialog.Builder builder_isDeleteMemo = new AlertDialog.Builder(this);
        builder_isDeleteMemo.setMessage("选定的备忘录将被删除");
        builder_isDeleteMemo.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(Memo eachmemo:selectedMemoList){
                    deleteMemo(eachmemo);
                }
                MemoDatabase.getInstance(MainActivity.this).deleteSelectMemoList(selectedMemoList);
                selectedMemoList.clear();
                MORE_STATUS=0;
                main_tv_chosen.setVisibility(View.INVISIBLE);
                main_iv_deal.setVisibility(View.INVISIBLE);
                main_tv_chosen.setText("选择项目");
                updateMemoList();
                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder_isDeleteMemo.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for(Memo memo:selectedMemoList){
                    memo.setIs_chosen(0);
                    MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                }
                selectedMemoList.clear();
                MORE_STATUS=0;
                main_tv_chosen.setVisibility(View.INVISIBLE);
                main_iv_deal.setVisibility(View.INVISIBLE);
                main_tv_chosen.setText("选择项目");
                updateMemoList();
            }
        });
        builder_isDeleteMemo.create().show();
    }

    //批量复制状态
    public void toCopySelectItem() {
        main_tv_chosen.setVisibility(View.VISIBLE);
        main_iv_deal.setVisibility(View.VISIBLE);
        main_iv_deal.setBackgroundResource(R.drawable.copy);
        main_iv_deal.setTag("copy");
    }

    //当点击批量复制按钮，先弹出对话框让选择创建日期
    public void newSelectDayDialog() {
        AlertDialog.Builder builder_select_day = new AlertDialog.Builder(this);
        builder_select_day.setTitle("选择这些备忘录的日期：");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_copy, null);
        DatePicker dialog_copy_dp = view.findViewById(R.id.dialog_copy_dp);
        dialog_copy_dp.init(Integer.parseInt(selected_year), Integer.parseInt(selected_month)-1, Integer.parseInt(myCalendar.getNow_day()), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                copy_year = String.valueOf(year);
                copy_month = DateUtils.toNormalTime(month + 1);
                copy_day = DateUtils.toNormalTime(day);
            }
        });
        builder_select_day.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                newCopyDialog();
            }
        });
        builder_select_day.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MORE_STATUS=0;
                for(Memo memo:selectedMemoList){
                    memo.setIs_chosen(0);
                    MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                }
                selectedMemoList.clear();
                main_tv_chosen.setVisibility(View.INVISIBLE);
                main_iv_deal.setVisibility(View.INVISIBLE);
                main_tv_chosen.setText("选择项目");
                updateMemoList();
            }
        });
        builder_select_day.setView(view);
        builder_select_day.create().show();
    }

    //用户选择好日期后，弹出确认是否创建到该日期的对话框提示
    public void  newCopyDialog() {
        AlertDialog.Builder builder_isCopyMemo = new AlertDialog.Builder(this);
        builder_isCopyMemo.setMessage("将被创建至"+copy_year+"年"+copy_month+"月"+copy_day+"日");
        builder_isCopyMemo.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MORE_STATUS=0;
                MemoDatabase.getInstance(MainActivity.this).insertSelectMemoList(selectedMemoList,copy_year,copy_month,copy_day);
                for(Memo memo:selectedMemoList){
                    memo.setIs_chosen(0);
                    MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                    Memo copy_memo = new Memo();
                    copy_memo.setId(copy_year + copy_month + copy_day +  memo.getStart_hour() + memo.getStart_minute() + memo.getFinish_hour() + memo.getFinish_minute() + new MyCalendar().getNow_hour() + new MyCalendar().getNow_minute()+new MyCalendar().getNow_second());
                    copy_memo.setYear(copy_year);
                    copy_memo.setMonth(copy_month);
                    copy_memo.setDay(copy_day);
                    copy_memo.setWeek(DateUtils.getSelectedWeek(Integer.parseInt(copy_year),Integer.parseInt(copy_month),Integer.parseInt(copy_day)));
                    copy_memo.setStart_hour(memo.getStart_hour());
                    copy_memo.setStart_minute(memo.getStart_minute());
                    copy_memo.setFinish_hour(memo.getFinish_hour());
                    copy_memo.setFinish_minute(memo.getFinish_minute());
                    copy_memo.setText(memo.getText());
                    copy_memo.setIs_first(0);
                    copy_memo.setIs_completed(0);
                    copy_memo.setIs_chosen(0);
                    insertMemo(copy_memo);
                }
                selectedMemoList.clear();
                main_tv_chosen.setVisibility(View.INVISIBLE);
                main_iv_deal.setVisibility(View.INVISIBLE);
                main_tv_chosen.setText("选择项目");
                updateMemoList();
                Toast.makeText(MainActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder_isCopyMemo.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MORE_STATUS=0;
                for(Memo memo:selectedMemoList){
                    memo.setIs_chosen(0);
                    MemoDatabase.getInstance(MainActivity.this).updateMemoChosenStatus(memo, 0);
                }
                selectedMemoList.clear();
                main_tv_chosen.setVisibility(View.INVISIBLE);
                main_iv_deal.setVisibility(View.INVISIBLE);
                main_tv_chosen.setText("选择项目");
                updateMemoList();
            }
        });
        builder_isCopyMemo.create().show();
    }

    @Override//重写返回键事件
    public void onBackPressed() {
        if(MORE_STATUS==1){
            //更多（批量删除、批量复制）状态，恢复之前状态
            MORE_STATUS=0;
            for(Memo memo:selectedMemoList){
                memo.setIs_chosen(0);
                MemoDatabase.getInstance(this).updateMemoChosenStatus(memo, 0);
            }
            selectedMemoList.clear();
            main_tv_chosen.setVisibility(View.INVISIBLE);
            main_iv_deal.setVisibility(View.INVISIBLE);
            main_tv_chosen.setText("选择项目");
            updateMemoList();
        }else{
            finish();
        }
    }
}
