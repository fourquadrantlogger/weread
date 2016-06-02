package com.fengwo.reading.utils;

import android.content.Context;

import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.handmark.pulltorefresh.library.internal.Utils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * 是否使用网络进行播放、下载
 */
public class InternetUtil {

    /**
     * @param context
     * @param type    1:播放  2:下载
     */
    public static boolean getInternet(Context context, int type) {
        switch (type) {
            case 1:
                //是否可以播放
                if (SPUtils.getBoFang()) {
                    return true;
                } else if ("wifi".equals(NetUtil.getNetworkType(context))) {
                    return true;
                } else {
                    return false;
                }
            case 2:
                //是否可以下载
                if (SPUtils.getXiaZai()) {
                    return true;
                } else if ("wifi".equals(NetUtil.getNetworkType(context))) {
                    return true;
                } else {
                    return false;
                }
        }
        return false;
    }
}