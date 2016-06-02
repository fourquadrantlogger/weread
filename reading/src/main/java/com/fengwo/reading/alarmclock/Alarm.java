package com.fengwo.reading.alarmclock;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Alarm implements Serializable {

    // 访问的数据库名
    public static final String DATABASE_NAME = "clock.db";
    public static final String AUTHORITIES = "com.fengwo.reading.alarmclock.AlarmProvider";
    public static final String TABLE_NAME = "alarms";

    public static class Columns implements BaseColumns {
        // AlarmProvider的访问Uri
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITIES + "/" + TABLE_NAME);
        // AlarmProvider返回的数据类型
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/alarms";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/alarms";

        // 表中的列名
        public static final String HOUR = "hour";
        public static final String MINUTES = "minutes";
        public static final String LABEL = "label";
        public static final String BELL = "bell";
        public static final String REPEAT = "repeat";
        public static final String NEXTMILLIS = "nextMillis";
        public static final String CREATETIME = "createTime";
        public static final String UID = "uid";
        // 默认排序
        public static final String DEFAULT_SORT_ORDER = HOUR + ", " + MINUTES
                + " ASC";
        // 有效闹钟排序
        public static final String ENABLED_SORT_ORDER = NEXTMILLIS + " ASC";

        public static final String[] ALARM_QUERY_COLUMNS = {_ID, HOUR,
                MINUTES, LABEL, BELL, REPEAT, NEXTMILLIS, CREATETIME, UID};

        public static final int ID_INDEX = 0;
        public static final int HOUR_INDEX = 1;
        public static final int MINUTES_INDEX = 2;
        public static final int LABEL_INDEX = 3;
        public static final int BELL_INDEX = 4;
        public static final int REPEAT_INDEX = 5;
        public static final int NEXTMILLIS_INDEX = 6;
        public static final int CREATETIME_INDEX = 7;
        public static final int UID_INDEX = 8;

    }

    // 表中的列名
    public int id;
    public int hour;
    public int minutes;
    public String label;
    public String bell;
    public String repeat;
    public long nextMillis;
    public long createTime;
    public String uid;

    // 默认构造器
    public Alarm() {
    }

    // 构造器，将游标转换为Alarm对象
    public Alarm(Cursor cursor) {
        id = cursor.getInt(Alarm.Columns.ID_INDEX);
        hour = cursor.getInt(Alarm.Columns.HOUR_INDEX);
        minutes = cursor.getInt(Alarm.Columns.MINUTES_INDEX);
        label = cursor.getString(Alarm.Columns.LABEL_INDEX);
        bell = cursor.getString(Alarm.Columns.BELL_INDEX);
        repeat = cursor.getString(Alarm.Columns.REPEAT_INDEX);
        nextMillis = cursor.getLong(Alarm.Columns.NEXTMILLIS_INDEX);
        createTime = cursor.getLong(Alarm.Columns.CREATETIME_INDEX);
        uid = cursor.getString(Alarm.Columns.UID_INDEX);
    }
}