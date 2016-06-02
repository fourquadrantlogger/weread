package com.fengwo.reading.main.read;

import android.os.Environment;

import com.fengwo.reading.myinterface.GlobalParams;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by timeloveboy on 16/5/19.
 */
public class Bean_Book {
    public String author;// "杨绛"
    public String id;// //共读pb_id
    public String book_title;// 书名
    public String book_cover;// 图片

//    public String time_type;//1早读，2晚读，3预告，4美文，5名篇，6有料，7有趣

    public String bookfolder() {
        String mediaFolder = Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Media + book_title;
        return mediaFolder;
    }

    public String book_cover_local() {
        //  本地图片
        return bookfolder() + "/book_cover.jpg";
    }
    public boolean book_cover_exist(){
        return new File(book_cover_local()).exists();
    }
    public String qi;//期
    public String book_id;// 书Id
    public String start_time;//"2016-05-01",
    public String end_time;//"2016-05-10",

    public String wereadtime_data_to_date() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd");// HH:mm:ss

            Date date1 = simpleDateFormat
                    .parse(start_time);
            Date date2 = simpleDateFormat
                    .parse(end_time);

            SimpleDateFormat format = new SimpleDateFormat("MM月dd日");

            String create = format.format(date1);
            String end = format.format(date2);

            return create + " 至" + end;

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
 
}
