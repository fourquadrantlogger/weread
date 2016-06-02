package com.fengwo.reading.task;

import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.main.my.achieve.Json_wodexunzhang;
import com.fengwo.reading.main.my.achieve.Xunzhang;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.localdata.FileUtil;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 从服务端 下载勋章图片,并存储勋章数据到snappdb
 * Created by timeloveboy on 16/5/4.
 */
public class Task_updatexunzhang {
    public Task_updatexunzhang() {
        getData(true);
    }

    //region getdata
    public void getData(final boolean downloadimg) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("banben", SPUtils.get_xunzhang(MyApplication.getContext()));
        map.put("soft", VersionUtils.getVersion(MyApplication.getContext()));
        Json_wodexunzhang json_wodexunzhang;
        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.SERVERURL + "badge/data", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonString = responseInfo.result;
                MLog.v("reading", jsonString);
                try {
                    Json_wodexunzhang json_wodexunzhang = new Gson().fromJson(jsonString, Json_wodexunzhang.class);
                    if (json_wodexunzhang.data != null) {
                        NOsqlUtil.set_json_wodexunzhang(json_wodexunzhang);
                    }
                    //如果版本不一致,根据downloading选择是否下载
                    if (downloadimg && !json_wodexunzhang.banben.equals(SPUtils.get_xunzhang(MyApplication.getContext()))) {
                        MLog.v("reading", " 勋章img版本不一致,开启后台下载");
                        downloadxunzhangimg(json_wodexunzhang);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        }, true, null);

    }

    public void downloadxunzhangimg(Json_wodexunzhang json_wodexunzhang) {
        FileUtil.delFolder(Json_wodexunzhang.xunzhangFolder());
        for (int i = 0; i < json_wodexunzhang.data.size(); i++) {
            HttpUtils httpUtils = new HttpUtils();
            final Xunzhang xunzhang = json_wodexunzhang.data.get(i);
            httpUtils.download(xunzhang.limg_url, xunzhang.localPath(), new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    MLog.v("reading", "我的勋章:" + xunzhang.limg_url);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    e.printStackTrace();
                }
            });
        }

        SPUtils.set_xunzhang(MyApplication.getContext(), json_wodexunzhang.banben);
    }
    //endregion
}
