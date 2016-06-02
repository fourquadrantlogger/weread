package com.fengwo.reading.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author lxq 本地数据库
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private final static String DBNAME = "db_reading.db";
    private final static int VERSION = 1;
    private final static String TABLE_NAME = "tb_notify";

    public MySQLiteOpenHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + "uid text not null ,"
                + "id text not null ,"
                + "notify_user_id text not null ,"
                + "source text not null ,"
                + "type text not null ,"
                + "name text not null ,"
                + "avatar text not null ,"
                + "sex text not null ,"
                + "right text not null ,"
                + "content text not null ,"
                + "create_time text not null ,"
                + "is_read text not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

}
