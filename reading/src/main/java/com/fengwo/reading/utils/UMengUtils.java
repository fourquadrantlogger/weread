package com.fengwo.reading.utils;

import android.content.Context;

import com.fengwo.reading.myinterface.GlobalConstant;
import com.umeng.analytics.MobclickAgent;

public class UMengUtils {

    // eventId为当前统计的事件ID
    public static void onCountListener(Context context, String eventId) {
        if (!GlobalConstant.debug) {
            MobclickAgent.onEvent(context, eventId);
        }
    }

}