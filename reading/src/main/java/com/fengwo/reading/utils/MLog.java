package com.fengwo.reading.utils;

import android.util.Log;

import com.fengwo.reading.myinterface.GlobalConstant;


/**
 * Created by timeloveboy on 16/3/10.
 */
public class MLog {


    public static void v(String tag, String content) {
        if (GlobalConstant.debug) {
            Log.v(tag, content);
        }
    }
}
