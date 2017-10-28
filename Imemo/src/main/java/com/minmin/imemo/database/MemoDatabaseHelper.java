package com.minmin.imemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <pre>
 *   author:minmin
 *   email:775846180@qq.com
 *   time:2017/10/11
 *   desc:
 *   version:1.0
 * </pre>
 */

public class MemoDatabaseHelper extends SQLiteOpenHelper{

    private final static String CREATE_MEMO = "create table Memo(id text," +
            "year text," +
            "month text," +
            "day text," +
            "week text," +
            "start_hour text," +
            "start_minute text," +
            "finish_hour text," +
            "finish_minute text," +
            "text text," +
            "is_completed integer," +
            "is_remind integer," +
            "is_chosen integer)";

    public MemoDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MEMO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
