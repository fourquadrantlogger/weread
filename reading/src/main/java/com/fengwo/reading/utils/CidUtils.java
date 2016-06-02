package com.fengwo.reading.utils;

import android.os.Handler;
import android.text.TextUtils;

import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class CidUtils {
    /**
     * 绑定个推cid false 标示没有获取到 cid 需要重新执行
     */
    public static void setCid() {
        if (GlobalConstant.ISLOGINUID.equals(GlobalParams.uid)) {
            LogUtils.e("=====注册uid=null");
            return;
        }
        if (TextUtils.isEmpty(GlobalParams.cid)) {
            LogUtils.e("=====注册cid=null");
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("cid", GlobalParams.cid);
        map.put("os", "1");
        map.put("soft", VersionUtils.getVersion(MyApplication.getContext()));

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.SETCID,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        MLog.v("reading",jsonString);
                        try {
                            new JsonParser().parse(jsonString);
                        } catch (JsonParseException e) {
                            return;
                        }
                        try {
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            String code = json.code;

                            if ("1".equals(code)) {
                                LogUtils.e("=====cid注册成功");
                            } else {
                                LogUtils.e("=====cid注册不成功");
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                }, true, null);
    }

    private static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            setCid();
        }

        ;
    };

    /**
     * 个推解除绑定cid
     */
    public static void clearCid() {
        if (GlobalConstant.ISLOGINUID.equals(GlobalParams.uid)) {
            return;
        }
        if (TextUtils.isEmpty(GlobalParams.cid)) {
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("cid", GlobalParams.cid);
        map.put("soft", VersionUtils.getVersion(MyApplication.getContext()));

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.CLEARCID,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {
                            new JsonParser().parse(jsonString);
                        } catch (JsonParseException e) {
                            return;
                        }
                        try {
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            String code = json.code;
                            if ("1".equals(code)) {
                                LogUtils.e("=====cid解绑成功");
                            } else {
                                LogUtils.e("=====cid解绑不成功");
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                }, true, null);
    }
}
