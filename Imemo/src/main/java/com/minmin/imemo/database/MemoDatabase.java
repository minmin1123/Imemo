package com.minmin.imemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.minmin.imemo.model.Memo;
import com.minmin.imemo.util.DateUtils;
import com.minmin.imemo.util.MyCalendar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minmin on 2017/10/1.
 */

public class MemoDatabase {

    private MemoDatabaseHelper helper;
    private static MemoDatabase memoDatabase;
    private SQLiteDatabase sqLiteDatabase;

    private MemoDatabase(Context context){
        helper = new MemoDatabaseHelper(context, "IMemo.db", null, 1);
    }

    public static MemoDatabase getInstance(Context context){
        if(memoDatabase==null){
            memoDatabase=new MemoDatabase(context);
        }
        return memoDatabase;
    }

    //新建一条任意日期的备忘录
    public void insertMemo(Memo memo){
        sqLiteDatabase=helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", memo.getId());
        values.put("year",memo.getYear());
        values.put("month",memo.getMonth());
        values.put("day",memo.getDay());
        values.put("week",memo.getWeek());
        values.put("start_hour",memo.getStart_hour());
        values.put("start_minute",memo.getStart_minute());
        values.put("finish_hour",memo.getFinish_hour());
        values.put("finish_minute",memo.getFinish_minute());
        values.put("text",memo.getText());
        values.put("is_completed", memo.getIs_completed());
        values.put("is_first", memo.getIs_first());
        values.put("is_chosen", memo.getIs_chosen());
        sqLiteDatabase.insert("Memo", null, values);
        //之后进行是否是当天第一条的检查
        getEveryDayMemo(memo.getYear(),memo.getMonth(),memo.getDay());
    }

    //遍历当天全部备忘录，对is_first进行判断和重置
    public void getEveryDayMemo(String year,String month,String day){
        sqLiteDatabase=helper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query("Memo", null, "year=? and month=? and day=?", new String[]{year,month,day}, null, null," id asc");
        int hasFirst=0;
        if(cursor!=null){
            while(cursor.moveToNext()){
                String id=cursor.getString(cursor.getColumnIndex("id"));
                if(hasFirst==1){
                    toChangeEveryDayFirstMemo(id,0);
                }else{
                    toChangeEveryDayFirstMemo(id,1);
                }
                hasFirst=1;
            }
            cursor.close();
        }
    }

    public void toChangeEveryDayFirstMemo(String id,int isFirst){
        sqLiteDatabase=helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_first", isFirst);
        sqLiteDatabase.update("Memo",values,"id=?",new String[]{id});
    }

    //新建一条指定日期的备忘录
    public void insertSelectMemoList(List<Memo> memoList,String year,String month,String day){
        sqLiteDatabase=helper.getReadableDatabase();
        for(Memo memo:memoList){
            ContentValues values = new ContentValues();
            values.put("id",year + month + day +  memo.getStart_hour() + memo.getStart_minute() + memo.getFinish_hour() + memo.getFinish_minute() + new MyCalendar().getNow_hour() + new MyCalendar().getNow_minute()+new MyCalendar().getNow_second());
            values.put("year",year);
            values.put("month",month);
            values.put("day",day);
            values.put("week",DateUtils.getSelectedWeek(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day)));
            values.put("is_completed",0);
            values.put("is_first",0);
            values.put("is_chosen",0);
            sqLiteDatabase.update("Memo",values,"id=?",new String[]{memo.getId()});
            insertMemo(memo);
        }
    }

    //删除指定日期的备忘录
    public void deleteMemo(Memo memo){
        sqLiteDatabase=helper.getReadableDatabase();
        String id = memo.getId();
        sqLiteDatabase.delete("Memo","id=?",new String[]{id});
    }

    //批量删除指定日期的备忘录
    public void deleteSelectMemoList(List<Memo> memoList){
        for(Memo memo:memoList){
            deleteMemo(memo);
            getEveryDayMemo(memo.getYear(),memo.getMonth(),memo.getDay());
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
        values.put("is_completed", isCompleted);
        sqLiteDatabase.update("Memo",values,"id=?",new String[]{memo.getId()});
    }

    //更改备忘录的选中状态--对应is_chosen属性
    public void updateMemoChosenStatus(Memo memo,int isChosen){
        sqLiteDatabase=helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_chosen", isChosen);
        sqLiteDatabase.update("Memo",values,"id=?",new String[]{memo.getId()});
    }

    //查询指定年月的所有备忘录
    public List<Memo> quaryEveryMonthMemoList(String year,String month){
        List<Memo> memoList=new ArrayList<>();
        sqLiteDatabase=helper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query("Memo", null, "year=? and month=?", new String[]{year,month}, null, null," id asc");
        if(cursor!=null){
            while(cursor.moveToNext()){
                Memo memo = new Memo();
                memo.setId(cursor.getString(cursor.getColumnIndex("id")));
                memo.setYear(year);
                memo.setMonth(month);
                memo.setDay(cursor.getString(cursor.getColumnIndex("day")));
                memo.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                memo.setStart_hour(cursor.getString(cursor.getColumnIndex("start_hour")));
                memo.setStart_minute(cursor.getString(cursor.getColumnIndex("start_minute")));
                memo.setFinish_hour(cursor.getString(cursor.getColumnIndex("finish_hour")));
                memo.setFinish_minute(cursor.getString(cursor.getColumnIndex("finish_minute")));
                memo.setText(cursor.getString(cursor.getColumnIndex("text")));
                memo.setIs_completed(cursor.getInt(cursor.getColumnIndex("is_completed")));
                memo.setIs_first(cursor.getInt(cursor.getColumnIndex("is_first")));
                memo.setIs_chosen(cursor.getInt(cursor.getColumnIndex("is_chosen")));
                memoList.add(memo);
            }
            cursor.close();
        }
        return memoList;
    }

}
