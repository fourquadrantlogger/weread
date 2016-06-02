package com.fengwo.reading.main.read;


import android.os.Environment;
import android.text.TextUtils;

import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.localdata.FileUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class IndexBean {

    public String id;//拆书包id 例如:303
    public String pb_id;//共读id
    public String book_id;//书id
    public String book_title;//书名
    public String title;//拆书包标题
    public String content;//h5链接
    public String readornot = "0";//改包 是否 读书签到:1读过了。0没读过。
    public String time_type;
    public String pack_abs;
    public String imageurl;//背景cover

    public String timetype_tostring() {
        switch (time_type) {
            case "1":
                return "早读";
            case "2":
                return "晚读";
            case "3":
                return "预告";
            case "4":
                return "美文";
            case "5":
                return "名篇";
            case "6":
                return "有料";
            case "7":
                return "有趣";
            default:
                return "";
        }
    }

    public String pub_time;//时间
    public String getPub_time_week(){
        if (TextUtils.isEmpty(pub_time) || "0000-00-00 00:00:00".equals(pub_time)) {

            return "";
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = format.parse(pub_time);
            calendar.setTime(date);
            String week = "";
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    week = "日";
                    break;
                case 2:
                    week = "一";
                    break;
                case 3:
                    week = "二";
                    break;
                case 4:
                    week = "三";
                    break;
                case 5:
                    week = "四";
                    break;
                case 6:
                    week = "五";
                    break;
                case 7:
                    week = "六";
                    break;
                default:
                    break;
            }
            return week;
        }catch (java.text.ParseException e1){
                e1.printStackTrace();
            return "";
        }
    }
    public String getdate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(pub_time);

            return new SimpleDateFormat("MM-dd").format(new Date(date.getTime()));
        } catch (java.text.ParseException e1) {
            e1.printStackTrace();
            return "";
        }
    }

    public String media;//音频地址
    public boolean Exist(){
        return FileUtil.ExistMedia(book_title, title);
    }
    public String mediaFolder() {
        return Environment.getExternalStorageDirectory().getPath() +  GlobalParams.FolderPath_Media+book_title;
    }
    public String mediaName(){
        return title + ".mp3";
    }
    public String media_localpath(){
        return mediaFolder()+ "/" +mediaName();
    }
    public String media_time;//音频时长

    // 返回 秒
    public int getMaxlength() {
        try {
            String[] strings = media_time.split(":");
            int m = Integer.valueOf(strings[0]);
            int s = Integer.valueOf(strings[1]);
            return m * 60 + s;
        } catch (Exception e) {
            e.printStackTrace();
            return 600;
        }
    }

    public String top_img;//顶部图片

}
