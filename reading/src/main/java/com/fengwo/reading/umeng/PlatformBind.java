package com.fengwo.reading.umeng;


import android.content.Context;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.bean.BaseJson;

import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;

import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by timeloveboy on 16/3/17.
 */
public class PlatformBind {

    public PlatformBind(Context context) {
        this.context = context;
        handler = new CommonHandler(context, null);
    }

    private CommonHandler handler;
    // 整个平台的Controller,负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.login");
    private String third_id = ""; // 第三方的id，微信的传open_id
    private String union_id = ""; // 微信的union_id
    private String dsf_type = ""; // qq/weixin/weibo
    public boolean isBang = false; //true:绑定 false:解绑
    private Context context;

    //region授权。如果授权成功，则获取用户信息
    public void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(context, platform,
                new SocializeListeners.UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    @Override
                    public void onError(SocializeException e,
                                        SHARE_MEDIA platform) {
                        Toast.makeText(context, "授权错误", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
                        if (value != null
                                && !TextUtils.isEmpty(value.getString("uid"))) {
                            dsf_type = "weibo";
                            // uid不为空，获取相关授权信息
                            getUserInfo(platform);
                        } else {
                            Toast.makeText(context, "授权失败...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        // Log.e("TAG", "-----授权取消");
                    }
                });
    }

    //region获取用户信息  param platform 平台标签
    public void getUserInfo(SHARE_MEDIA platform) {
        mController.getPlatformInfo(context, platform,
                new SocializeListeners.UMDataListener() {

                    @Override
                    public void onStart() {
                        // Log.e("TAG", "-----获取平台数据开始");
                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        String showText = "";
                        if (status == StatusCode.ST_CODE_SUCCESSED) {
                            third_id = "";
                            union_id = "";
                            switch (dsf_type) {
                                case "weixin":
                                    third_id = info.get("openid").toString();
                                    union_id = info.get("unionid").toString();
                                    break;
                                case "weibo":
                                    third_id = info.get("uid").toString();
                                    SPUtils.setUserWeiboAccess_Token(context, info.get("access_token").toString());
                                    break;
                                case "qq":
                                    third_id = info.get("openid").toString();
                                    break;
                                default:
                                    break;
                            }
                            // 获取信息成功,绑定帐号
                            getData();
                            showText = "获取用户信息成功";
                        } else {
                            showText = "获取用户信息失败";
                        }
//						 Log.e("TAG", "-----showText: " + showText);
                        if (info != null) {
                            Log.e("TAG", "-----info: " + info.toString());
                        }
                    }
                });
    }

    //region帐号绑定(解绑) - 请求网络
    private void getData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("type", dsf_type);

        if (isBang) {
            // 解绑不传
            map.put("third_id", third_id);
            if (!TextUtils.isEmpty(union_id)) {
                map.put("union_id", union_id);
            }
        }

        HttpParamsUtil.sendData(map, null, GlobalConstant.third_bind,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(0);
                            }
                        }.start();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(2);
                            }
                        }.start();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }.start();
                        String jsonString = responseInfo.result;
                        try {
                            BaseJson json = new Gson().fromJson(
                                    jsonString, BaseJson.class);
//                            System.out.println("----jsonString:" + jsonString);
                            if ("1".equals(json.code)) {
                                UMengBean bean = new UMengBean();
                                bean.wx = GlobalParams.wx;
                                bean.weibo = GlobalParams.weibo;
                                bean.qq = GlobalParams.qq;
                                bean.type = GlobalParams.type;
                                //绑定成功,变为 解绑
                                switch (dsf_type) {
                                    case "weixin":
                                        GlobalParams.wx = "1";
                                        bean.wx = GlobalParams.wx;
                                        break;
                                    case "weibo":
                                        GlobalParams.weibo = "1";
                                        bean.wx = GlobalParams.weibo;
                                        break;
                                    case "qq":
                                        GlobalParams.qq = "1";
                                        bean.wx = GlobalParams.qq;
                                        break;
                                    default:
                                        break;
                                }
                                SPUtils.setUMeng(context, bean);
                            } else if ("2".equals(json.code)) {
                                UMengBean bean = new UMengBean();
                                bean.wx = GlobalParams.wx;
                                bean.weibo = GlobalParams.weibo;
                                bean.qq = GlobalParams.qq;
                                bean.type = GlobalParams.type;
                                //解绑成功,变为 绑定
                                switch (dsf_type) {
                                    case "weixin":
                                        GlobalParams.wx = "0";
                                        bean.wx = GlobalParams.wx;
                                        break;
                                    case "weibo":
                                        GlobalParams.weibo = "0";
                                        bean.weibo = GlobalParams.weibo;
                                        break;
                                    case "qq":
                                        GlobalParams.qq = "0";
                                        bean.qq = GlobalParams.qq;
                                        break;
                                    default:
                                        break;
                                }
                                SPUtils.setUMeng(context, bean);
                            } else {
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

}
