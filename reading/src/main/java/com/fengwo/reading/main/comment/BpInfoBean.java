package com.fengwo.reading.main.comment;

import android.os.Environment;

import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.localdata.FileUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BpInfoBean implements Serializable {
    public String id;//笔记 id
    public String book_id;//书id
    public String book_title;//书名
    public String title;//标题
    public String content;
    public String is_digg;//是否点赞(0未点赞,1点过赞)
    public String is_fav;//是否收藏(0未点赞,1点过赞)
    public String read_count;//阅读量
    public String digg_count;//点赞数
    public String comment_count;//评论数
    public String is_qian;//是否签到，1已签到
    public String pub_time;//时间

    public String Getweek_zh() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date;
        try {
            date = format.parse(pub_time);
        } catch (Exception e) {
            throw new NullPointerException("日期错误");
        }
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
        return "周" + week;

    }

    public String show_check;//是否显示签到按钮，1显示，0不显示
    public String share_url;//拆书包分享地址，只限于微信分享
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
    public String name;//主播的姓名
    public String top_img;////置顶图片地址


    public String time_type;//1早读，2晚读，3预告，4美文，5名篇，6有料，7有趣
    public String timetype_tostring() {
        switch (time_type) {
            case "1":
                return "早读";
            case "2":
                return "晚读";
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
}
