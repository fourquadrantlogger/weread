package com.fengwo.reading.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 特定的时间工具
 *
 * @author Luo Sheng
 * @date 2016-1-26
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {
    public static String getTime(String time, int type) {
        // time = String.valueOf(System.currentTimeMillis()/1000);
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        String strTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        switch (type) {
            case 1:
                sdf = new SimpleDateFormat("yyyy.MM.dd");
                break;
            case 2:
                sdf = new SimpleDateFormat("MM月dd日 HH:mm");
                break;
            case 3:
                sdf = new SimpleDateFormat("MM-dd HH:mm");
                break;
            case 4:
                sdf = new SimpleDateFormat("MM-dd");
                break;
            case 5:
                sdf = new SimpleDateFormat("MM/dd");
                break;
            case 6:
                sdf = new SimpleDateFormat("HH:mm");
                break;
            case 7:
                sdf = new SimpleDateFormat("yyyy/MM/dd");
                break;
            case 8:
                sdf = new SimpleDateFormat("yyyy");
                break;
            case 9:
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case 10:
                sdf = new SimpleDateFormat("MM月dd日");
                break;
            default:
                break;
        }
        try {
            long longTime = Long.valueOf(time) * 1000;
            strTime = sdf.format(new Date(longTime));
        } catch (Exception e) {
            strTime = "";
        }
        return strTime;
    }

    public static String getTimeY(String time) {
        String result = "";
        try {
            if (getTime(String.valueOf(System.currentTimeMillis() / 1000), 8)
                    .equals(getTime(time, 8))) {
                result = getTime(time, 5);
            } else {
                result = getTime(time, 7);
            }
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 动态时间定义： 1小时内：显示 XX分钟前； 1-24小时内：显示 XX小时内； 一天以上：显示 年/月/日 小时/分
     * 如：2015.3.20_9：20
     */
    public static String getTime(String time) {
        String resultTime = "";
        if (TextUtils.isEmpty(time) || "0".equals(time)) {
            return resultTime;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            long now = System.currentTimeMillis();// 现在的时间(毫秒)
            long sta = format.parse(time).getTime();// 传过来的时间
            long minutes = (now - sta) / (1000 * 60);// 计算出来的时间(分钟)
            if (minutes <= 0) {
                // 小于一分钟
                resultTime = "刚刚";
            } else if (minutes < 60 && minutes > 0) {
                // 小于一小时
                resultTime = minutes + "分钟前";
            } else {
                long hours = (long) Math.floor(minutes / (60 * 1.0));// 小时
                if (hours < 24) {
                    // 小于一天
                    resultTime = hours + "小时前";
                } else {
                    // 一天以上(根据需要的格式设置)
                    String newStr = time.substring(5, time.indexOf(" "));
                    resultTime = newStr;
                    // if (getTime(
                    // String.valueOf(System.currentTimeMillis() / 1000),
                    // 8).equals(getTime(time, 8))) {
                    // resultTime = getTime(time, 4);
                    // } else {
                    // resultTime = getTime(time, 9);
                    // }
                }
            }
        } catch (Exception e) {
        }

        return resultTime;
    }




}
