package com.fengwo.reading.umeng;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.utils.OauthHelper;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * @author LS 友盟分享
 */
public class UMShare {

    private static String level_is_up;

    /**
     * 友盟分享
     *
     * @param context
     * @param num      1:微信 2:朋友圈 3:QQ空间 4:微博
     * @param title    标题
     * @param content  内容
     * @param imageUrl 图片url,没有则为空""
     * @param h5Url    跳转地址
     */
    public static void setUMeng(final Context context, int num, String title,
                                String content, String imageUrl, String h5Url, final String id, final String type) {

        // UMeng分享,首先添加如下成员变量
        UMSocialService mController = UMServiceFactory
                .getUMSocialService("com.umeng.share");

        // postShare:参数1为Context类型对象， 参数2为要分享到的目标平台， 参数3为分享操作的回调接口
        switch (num) {
            case 1:// UMeng,微信
                // 添加微信平台
                UMWXHandler wxHandler = new UMWXHandler(context, GlobalConstant.wx_appid,
                        GlobalConstant.wx_appsecret);
                wxHandler.addToSocialSDK();
                // ***** 以上代码必须在弹出分享面板前调用 *****
                // 参数
                WeiXinShareContent weixinContent = new WeiXinShareContent();
                weixinContent.setShareContent(content);
                weixinContent.setTitle(title);
                if (!TextUtils.isEmpty(imageUrl)) {
                    weixinContent.setShareImage(new UMImage(context, imageUrl));
                } else {
                    weixinContent.setShareImage(new UMImage(context, R.drawable.fenxiang_logo));
                }
                weixinContent.setTargetUrl(h5Url);
                mController.setShareMedia(weixinContent);

                mController.postShare(context, SHARE_MEDIA.WEIXIN,
                        new SnsPostListener() {
                            @Override
                            public void onStart() {
                                // System.out.println("----------开始分享.");
                                if (!TextUtils.isEmpty(id)) {
                                    getData(id, type);
                                }
                            }

                            @Override
                            public void onComplete(SHARE_MEDIA platform, int eCode,
                                                   SocializeEntity entity) {
                                if (eCode == 200) {
                                    // System.out.println("----------分享成功.");
                                } else {
                                    String eMsg = "";
                                    if (eCode == -101) {
                                        eMsg = "没有授权";
                                    }
                                    // Toast.makeText(
                                    // getActivity(),
                                    // "分享失败[" + eCode + "] " + eMsg
                                    // + ",请稍后再试",
                                    // Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case 2:// UMeng,朋友圈分享
                // 支持微信朋友圈
                UMWXHandler wxCircleHandler = new UMWXHandler(context,
                        GlobalConstant.wx_appid, GlobalConstant.wx_appsecret);
                wxCircleHandler.setToCircle(true);
                wxCircleHandler.addToSocialSDK();
                // ***** 以上代码必须在弹出分享面板前调用 *****
                // 参数
                CircleShareContent circleMedia = new CircleShareContent();
                circleMedia.setShareContent(content);
                circleMedia.setTitle(title);
                if (!TextUtils.isEmpty(imageUrl)) {
                    circleMedia.setShareImage(new UMImage(context, imageUrl));
                } else {
                    circleMedia.setShareImage(new UMImage(context, R.drawable.fenxiang_logo));
                }
                circleMedia.setTargetUrl(h5Url);
                mController.setShareMedia(circleMedia);

                mController.postShare(context, SHARE_MEDIA.WEIXIN_CIRCLE,
                        new SnsPostListener() {
                            @Override
                            public void onStart() {
                                // System.out.println("----------开始分享.");
                                if (!TextUtils.isEmpty(id)) {
                                    getData(id, type);
                                }
                            }

                            @Override
                            public void onComplete(SHARE_MEDIA platform, int eCode,
                                                   SocializeEntity entity) {
                                if (eCode == 200) {
                                    // System.out.println("----------分享成功.");
                                } else {
                                    String eMsg = "";
                                    if (eCode == -101) {
                                        eMsg = "没有授权";
                                    }
                                    // Toast.makeText(
                                    // getActivity(),
                                    // "分享失败[" + eCode + "] " + eMsg
                                    // + ",请稍后再试",
                                    // Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case 3:// UMeng,QQ空间关联发布
                // 添加QQ支持, 并且设置QQ分享内容的target url
//                UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this,
//                        appId, appKey);
//                qqSsoHandler.setTargetUrl("http://www.umeng.com");
//                qqSsoHandler.addToSocialSDK();

                //添加QZone平台
                QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler((Activity) context, GlobalConstant.qq_appid,
                        GlobalConstant.qq_appkey);
                qZoneSsoHandler.addToSocialSDK();

                //参数
                QZoneShareContent qzone = new QZoneShareContent();
                qzone.setShareContent(content);
                qzone.setTargetUrl(h5Url);
                qzone.setTitle(title);
                //无图片则使用logo,有则使用第一张
                if (!TextUtils.isEmpty(imageUrl)) {
                    qzone.setShareImage(new UMImage(context, imageUrl));
                } else {
                    qzone.setShareImage(new UMImage(context, R.drawable.fenxiang_logo));
                }
                mController.setShareMedia(qzone);

                mController.postShare(context, SHARE_MEDIA.QZONE,
                        new SnsPostListener() {
                            @Override
                            public void onStart() {
                                // System.out.println("----------开始分享.");
                                if (!TextUtils.isEmpty(id)) {
                                    getData(id, type);
                                }
                            }

                            @Override
                            public void onComplete(SHARE_MEDIA platform, int eCode,
                                                   SocializeEntity entity) {
                                if (eCode == 200) {
                                    // System.out.println("----------分享成功.");
                                } else {
                                    String eMsg = "";
                                    if (eCode == -101) {
                                        eMsg = "没有授权";
                                    }
                                    // Toast.makeText(
                                    // getActivity(),
                                    // "分享失败[" + eCode + "] " + eMsg
                                    // + ",请稍后再试",
                                    // Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case 4:// UMeng,微博分享
                //添加新浪sso授权,注意在授权前先检查是否已经授权过，重复授权有可能引起错误
                OauthHelper.isAuthenticated(context, SHARE_MEDIA.SINA);
                //确保未授权，则先调用下面的代码
//                mController.doOauthVerify(context, SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
//                    @Override
//                    public void onStart(SHARE_MEDIA platform) {
//                        Toast.makeText(context, "授权开始", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(SocializeException e, SHARE_MEDIA platform) {
//                        Toast.makeText(context, "授权错误", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
//                        Toast.makeText(context, "授权完成", Toast.LENGTH_SHORT).show();
//                        //获取相关授权信息或者跳转到自定义的分享编辑页面
//                        String uid = value.getString("uid");
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA platform) {
//                        Toast.makeText(context, "授权取消", Toast.LENGTH_SHORT).show();
//                    }
//                });
                //授权成功后可以直接调用分享API接口
                //设置分享内容
                mController.setShareContent(content);
                //设置分享图片
                mController.setShareMedia(new UMImage(context, imageUrl));
                //直接分享
                mController.directShare(context, SHARE_MEDIA.SINA,
                        new SnsPostListener() {
                            @Override
                            public void onStart() {
//                                Toast.makeText(context, "分享开始", Toast.LENGTH_SHORT).show();
                                if (!TextUtils.isEmpty(id)) {
                                    getData(id, type);
                                }
                            }

                            @Override
                            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                                if (eCode == StatusCode.ST_CODE_SUCCESSED) {
                                    Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            default:
                break;
        }
    }

    /**
     * 是否升级   1:升级
     */
    private static void getData(String id, String type) {
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(type)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);
        map.put("type", type);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.note_share,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String error) {
                        level_is_up = "0";
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                level_is_up = json.level_is_up;
                            } else {
                                level_is_up = "0";
                            }
                        } catch (Exception e) {
                        }
                    }
                }, true, null);
    }

    public static String getLevel() {
        return level_is_up;
    }

}
