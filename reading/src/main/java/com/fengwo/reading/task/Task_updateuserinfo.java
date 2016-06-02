package com.fengwo.reading.task;

import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by timeloveboy on 16/5/4.
 * 从服务端 更新用户信息
 */
public class Task_updateuserinfo {
    class Json_userinfo extends BaseJson {
        public UserInfoBean user_data;
    }

    public Task_updateuserinfo() {
        getData();
    }

    //region getdata
    public void getData() {
        Map<String, String> map = new HashMap<String, String>();
        if ("1".equals(GlobalParams.uid))
            return;
        map.put("user_id", GlobalParams.uid);
        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.SERVERURL + "userinfo/get", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonString = responseInfo.result;
                try {
                    Json_userinfo json_userinfo = new Gson().fromJson(jsonString, Json_userinfo.class);
                    NOsqlUtil.set_userInfoBean(json_userinfo.user_data);
                    GlobalParams.userInfoBean = json_userinfo.user_data;
                    MLog.v("reading", "用户信息得到更新" + jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
            }
        }, true, null);

    }

}
