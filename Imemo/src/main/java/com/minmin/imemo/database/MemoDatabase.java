package com.minmin.imemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.minmin.imemo.model.Memo;
import com.minmin.imemo.model.Memory;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MyCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:数据库
 *   version:1.0
 */

public class MemoDatabase {

    private MemoDatabaseHelper helper;

    private static MemoDatabase memoDatabase;

    private SQLiteDatabase sqLiteDatabase;

    private final static String DATABASE_NAME="IMemo.db";

    private final static int VERSION = 1;

    private final static String ID = "id";
    private final static String YEAR = "year";
    private final static String MONTH = "month";
    private final static String DAY = "day";
    private final static String WEEK = "week";
    private final static String START_HOUR = "start_hour";
    private final static String START_MINUTE = "start_minute";
    private final static String FINISH_HOUR = "finish_hour";
    private final static String FINISH_MINUTE = "finish_minute";
    private final static String TEXT = "text";
    private final static String IS_CHOSEN = "is_chosen";
    private final static String IS_COMPLETE = "is_completed";
    private final static String IS_REMIND = "is_remind";
    private final static String TABLE_MEMO = "Memo";
    private final static String TABLE_MEMORY = "Memory";

    private MemoDatabase(Context context){
        helper = new MemoDatabaseHelper(context, DATABASE_NAME, null, VERSION);
    }

    public static MemoDatabase getInstance(Context context){
        if(memoDatabase==null){
            memoDatabase=new MemoDatabase(context);
        }
        return memoDatabase;
    }

    public static MemoDatabase getInstance(){

        return memoDatabase;
    }

    //新建一条任意日期的备忘录
    public void insertMemo(Memo memo){
        sqLiteDatabase=helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, memo.getId());
        values.put(YEAR,memo.getYear());
        values.put(MONTH,memo.getMonth());
        values.put(DAY,memo.getDay());
        values.put(WEEK,memo.getWeek());
        values.put(START_HOUR,memo.getStart_hour());
        values.put(START_MINUTE,memo.getStart_minute());
        values.put(FINISH_HOUR,memo.getFinish_hour());
        values.put(FINISH_MINUTE,memo.getFinish_minute());
        values.put(TEXT,memo.getText());
        values.put(IS_COMPLETE,memo.getIs_completed());
        values.put(IS_REMIND, memo.getIs_remind());
        values.put(IS_CHOSEN, memo.getIs_chosen());
        sqLiteDatabase.insert(TABLE_MEMO, null, values);
        //之后进行是否是当天第一条的检查
//        getEveryDayMemo(memo.getYear(),memo.getMonth(),memo.getDay());
    }

    //遍历当天全部备忘录（按顺序），对is_first进行重新赋值
//    public void getEveryDayMemo(String year,String month,String day){
//        sqLiteDatabase=helper.getReadableDatabase();
//        Cursor cursor=sqLiteDatabase.query(TABLE_MEMO, null, YEAR+"=? and "+MONTH+"=? and "+DAY+"=?", new String[]{year,month,day}, null, null,ID+" asc");
//        int hasFirst=0;
//        if(cursor!=null){
//            while(cursor.moveToNext()){
//                String id=cursor.getString(cursor.getColumnIndex(ID));
//                Log.i("Main","hasFirst="+hasFirst+"");
//                if(hasFirst==1){
//                    toChangeEveryDayFirstMemo(id,0);
//                }else{
//                    toChangeEveryDayFirstMemo(id,1);
//                }
//                hasFirst=1;
//            }
//            cursor.close();
//        }
//    }
//
//    public void toChangeEveryDayFirstMemo(String id,int isFirst){
//        sqLiteDatabase=helper.getReadableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(IS_REMIND, isFirst);
//        sqLiteDatabase.update(TABLE_MEMO,values,ID+"=?",new String[]{id});
//    }

    //新建指定日期的备忘录列表--先将被复制备忘录改为指定日期备忘录，再插入一条被复制备忘录
    public void insertSelectMemoList(List<Memo> memoList,String year,String month,String day){
        sqLiteDatabase=helper.getReadableDatabase();
        for(Memo memo:memoList){
            ContentValues values = new ContentValues();
            values.put(ID,year + month + day +  memo.getStart_hour() + memo.getStart_minute() + memo.getFinish_hour() + memo.getFinish_minute() + new MyCalendar().getNow_hour()+memo.getId().substring(18,22));
            values.put(YEAR,year);
            values.put(MONTH,month);
            values.put(DAY,day);
            values.put(WEEK,DateUtils.getSelectedWeek(Integer.parseInt(year),Integer.parseInt(month)-1,Integer.parseInt(day)));
            values.put(IS_COMPLETE,0);
            values.put(IS_REMIND,0);
            values.put(IS_CHOSEN,0);
            sqLiteDatabase.update(TABLE_MEMO,values,ID+"=?",new String[]{memo.getId()});
            insertMemo(memo);
        }
        //对指定日期的备忘录Is_first进行重新赋值
//        getEveryDayMemo(year,month,day);
    }

    //删除指定日期的备忘录
    public void deleteMemo(Memo memo){
        sqLiteDatabase=helper.getReadableDatabase();
        String id = memo.getId();
        sqLiteDatabase.delete(TABLE_MEMO,ID+"=?",new String[]{id});
    }

    //批量删除指定日期的备忘录
    public void deleteSelectMemoList(List<Memo> memoList){
        for(Memo memo:memoList){
            deleteMemo(memo);
//            getEveryDayMemo(memo.getYear(),memo.getMonth(),memo.getDay());
        }

    }

    //更改备忘录
    public void updateMemo(Memo old_memo,Memo update_memo){
        //删除原来备忘录，增添新的备忘录
        sqLiteDatabase=helper.getReadableDatabase();
        deleteMemo(old_memo);
        insertMemo(update_memo);
    }

    //更改备忘录的完成状态--对应is_completed属性
    public void updateMemoCompleteStatus(Memo memo,int isCompleted){
        sqLiteDatabase=helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(IS_COMPLETE, isCompleted);
        sqLiteDatabase.update(TABLE_MEMO,values,ID+"=?",new String[]{memo.getId()});
    }

    //更改备忘录的选中状态--对应is_chosen属性
    public void updateMemoChosenStatus(Memo memo,int isChosen){
        sqLiteDatabase=helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(IS_CHOSEN, isChosen);
        sqLiteDatabase.update(TABLE_MEMO,values,ID+"=?",new String[]{memo.getId()});
    }

    //查询指定年月的所有备忘录
    public List<Memo> quaryEveryMonthMemoList(String year,String month){
        List<Memo> memoList=new ArrayList<>();
        sqLiteDatabase=helper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query(TABLE_MEMO, null, YEAR+"=? and "+MONTH+"=?", new String[]{year,month}, null, null,ID+" asc");
        if(cursor!=null) {
            while (cursor.moveToNext()) {
                Memo memo = new Memo();
                memo.setId(cursor.getString(cursor.getColumnIndex(ID)));
                memo.setYear(year);
                memo.setMonth(month);
                memo.setDay(cursor.getString(cursor.getColumnIndex(DAY)));
                memo.setWeek(cursor.getString(cursor.getColumnIndex(WEEK)));
                memo.setStart_hour(cursor.getString(cursor.getColumnIndex(START_HOUR)));
                memo.setStart_minute(cursor.getString(cursor.getColumnIndex(START_MINUTE)));
                memo.setFinish_hour(cursor.getString(cursor.getColumnIndex(FINISH_HOUR)));
                memo.setFinish_minute(cursor.getString(cursor.getColumnIndex(FINISH_MINUTE)));
                memo.setText(cursor.getString(cursor.getColumnIndex(TEXT)));
                memo.setIs_completed(cursor.getInt(cursor.getColumnIndex(IS_COMPLETE)));
                memo.setIs_remind(cursor.getInt(cursor.getColumnIndex(IS_REMIND)));
                memo.setIs_chosen(cursor.getInt(cursor.getColumnIndex(IS_CHOSEN)));
                memoList.add(memo);
            }
            cursor.close();
        }
        return memoList;
    }

    //查询当天的所有备忘录
    public List<Memo> quaryEveryDayMemoList(String year,String month,String day){
        List<Memo> memoList=new ArrayList<>();
        sqLiteDatabase=helper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query(TABLE_MEMO, null, YEAR+"=? and "+MONTH+"=? and "+DAY+"=?", new String[]{year,month,day}, null, null,ID+" asc");
        if(cursor!=null){
            while(cursor.moveToNext()){
                Memo memo = new Memo();
                memo.setId(cursor.getString(cursor.getColumnIndex(ID)));
                memo.setYear(year);
                memo.setMonth(month);
                memo.setDay(cursor.getString(cursor.getColumnIndex(DAY)));
                memo.setWeek(cursor.getString(cursor.getColumnIndex(WEEK)));
                memo.setStart_hour(cursor.getString(cursor.getColumnIndex(START_HOUR)));
                memo.setStart_minute(cursor.getString(cursor.getColumnIndex(START_MINUTE)));
                memo.setFinish_hour(cursor.getString(cursor.getColumnIndex(FINISH_HOUR)));
                memo.setFinish_minute(cursor.getString(cursor.getColumnIndex(FINISH_MINUTE)));
                memo.setText(cursor.getString(cursor.getColumnIndex(TEXT)));
                memo.setIs_completed(cursor.getInt(cursor.getColumnIndex(IS_COMPLETE)));
                memo.setIs_remind(cursor.getInt(cursor.getColumnIndex(IS_REMIND)));
                memo.setIs_chosen(cursor.getInt(cursor.getColumnIndex(IS_CHOSEN)));
                memoList.add(memo);
            }
            cursor.close();
        }
        return memoList;
    }


    //新建一条纪念日
    public void insertMemory(Memory memory){
        sqLiteDatabase=helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID,memory.getId());
        values.put(YEAR,memory.getYear());
        values.put(MONTH,memory.getMonth());
        values.put(DAY,memory.getDay());
        values.put(TEXT,memory.getText());
        sqLiteDatabase.insert(TABLE_MEMORY, null, values);
    }

    //删除指定纪念日
    public void deleteMemory(Memory memory){
        sqLiteDatabase=helper.getReadableDatabase();
        String id = memory.getId();
        sqLiteDatabase.delete(TABLE_MEMORY,ID+"=?",new String[]{id});
    }


    //更改指定纪念日
    public void updateMemory(Memory old_memory,Memory update_memory){
        //删除原来备忘录，增添新的备忘录
        sqLiteDatabase=helper.getReadableDatabase();
        deleteMemory(old_memory);
        insertMemory(update_memory);
    }

    //查询所有纪念日
    public List<Memory> quaryAllMemoryList(){
        List<Memory> memoList=new ArrayList<>();
        sqLiteDatabase=helper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query(TABLE_MEMORY, null, null,null,null, null,ID+" desc");
        if(cursor!=null){
            while(cursor.moveToNext()){
                Memory memory = new Memory();
                memory.setId(cursor.getString(cursor.getColumnIndex(ID)));
                memory.setYear(cursor.getString(cursor.getColumnIndex(YEAR)));
                memory.setMonth(cursor.getString(cursor.getColumnIndex(MONTH)));
                memory.setDay(cursor.getString(cursor.getColumnIndex(DAY)));
                memory.setText(cursor.getString(cursor.getColumnIndex(TEXT)));
                memoList.add(memory);
            }
            cursor.close();
        }
        return memoList;
    }

}
