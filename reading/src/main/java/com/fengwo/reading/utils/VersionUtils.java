package com.fengwo.reading.utils;


import android.content.Context;

/**
 * Author：Luo Sheng
 * Date: 2016-05-04
 */
public class VersionUtils {

    /**
     * 获取版本
     */
    public static String getVersion(Context context) {
        String version = "";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }
}  