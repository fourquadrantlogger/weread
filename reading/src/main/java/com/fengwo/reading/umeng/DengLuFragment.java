package com.fengwo.reading.umeng;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.MainActivity;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.my.UserinfoSaveJson;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.EditTextUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.SmsObserver;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 手机、第三方登录 (微信,QQ,新浪微博)
 *
 * @author Luo Sheng
 * @date 2016-1-26
 */

public class DengLuFragment extends Fragment implements OnClickListener {
    // 整个平台的Controller,负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    private RelativeLayout rl_denglu_qq, rl_denglu_wb, rl_denglu_wx;
    private TextView tv_denglu_sj;

    private CustomProgressDialog progressDialog;

    private String Phone = ""; // 手机号
    private String third_id = ""; // 第三方的id，微信的传open_id
    private String dsf_type = ""; // qq/weixin/weibo
    private String union_id = ""; // 微信的union_id

    private boolean isOK = false;

    private String name = ""; //
    private String avatar = ""; //
    private String sex = ""; //
    private String intro = ""; // 简介
    private String province = ""; // 省
    private String city = ""; // 市

    public DengLuFragment() {
    }

    public static DengLuFragment fragment = new DengLuFragment();

    public static DengLuFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_denglu, container, false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());
        // 点击消失
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);

        findViewById(view);
        setPopupWindow();
        // 配置相关平台
        configPlatforms();

        isOK = false;

        // 验证码自动填入
        mObserver = new SmsObserver(fragment.getActivity(), smsHandler);
        fragment.getActivity()
                .getContentResolver()
                .registerContentObserver(Uri.parse("content://sms"), true,
                        mObserver);

        return view;
    }

    private SmsObserver mObserver;

    private Handler smsHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1 && type == 2) {
                String code = (String) msg.obj;
                et_denglu_num.setText(code);
            }
        }

        ;
    };

    private void findViewById(View view) {
        rl_denglu_qq = (RelativeLayout) view.findViewById(R.id.rl_denglu_qq);
        rl_denglu_wb = (RelativeLayout) view.findViewById(R.id.rl_denglu_wb);
        rl_denglu_wx = (RelativeLayout) view.findViewById(R.id.rl_denglu_wx);
        tv_denglu_sj = (TextView) view.findViewById(R.id.tv_denglu_sj);

        rl_denglu_qq.setOnClickListener(this);
        rl_denglu_wb.setOnClickListener(this);
        rl_denglu_wx.setOnClickListener(this);
        tv_denglu_sj.setOnClickListener(this);
    }

    private void setPopupWindow() {
        pop = new PopupWindow(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_denglu, null);
        et_denglu_num = (EditText) mView.findViewById(R.id.et_denglu_num);
        tv_denglu_tishi = (TextView) mView.findViewById(R.id.tv_denglu_tishi);
        tv_denglu_lift = (TextView) mView.findViewById(R.id.tv_denglu_lift);
        tv_denglu_right = (TextView) mView.findViewById(R.id.tv_denglu_right);
        pop.setContentView(mView);
        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setFocusable(true);
        pop.setAnimationStyle(R.style.DengLuTop);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        pop.setBackgroundDrawable(dw);

        tv_denglu_tishi.setOnClickListener(itemsOnClick);
        tv_denglu_lift.setOnClickListener(itemsOnClick);
        tv_denglu_right.setOnClickListener(itemsOnClick);

        // 输入框的监听
        et_denglu_num.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                setIsEnabled();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        pop.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                if (time != 0) {
                    timeHandler.removeCallbacks(mRunnable);
                    time = 0;
                }
            }
        });
    }

    /**
     * 确定是否可点击
     */
    private void setIsEnabled() {
        if (!TextUtils.isEmpty(et_denglu_num.getText().toString().trim())) {
            tv_denglu_right.setTextColor(fragment.getActivity().getResources()
                    .getColor(R.color.green));
            tv_denglu_right.setEnabled(true);
        } else {
            tv_denglu_right.setTextColor(fragment.getActivity().getResources()
                    .getColor(R.color.text_98));
            tv_denglu_right.setEnabled(false);
        }
    }

    private void configPlatforms() {
        // 设置新浪SSO handler
//         mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加QQ支持
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                GlobalConstant.qq_appid, GlobalConstant.qq_appkey);
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(getActivity(),
                GlobalConstant.wx_appid, GlobalConstant.wx_appsecret);
        wxHandler.addToSocialSDK();
    }

    private int type = 0;// 1,2
    private PopupWindow pop;
    private EditText et_denglu_num;
    private TextView tv_denglu_tishi, tv_denglu_lift, tv_denglu_right;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_denglu_sj:
                if (time != 0) {
                    timeHandler.removeCallbacks(mRunnable);
                    time = 0;
                }
                type = 1;
                tv_denglu_tishi.setTextColor(Color.GRAY);
                et_denglu_num.setHint("手机号码");
                et_denglu_num.setText("");
                et_denglu_num.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                tv_denglu_tishi.setText("");
                tv_denglu_tishi.setEnabled(false);
                tv_denglu_lift.setText("取消");
                tv_denglu_right.setText("下一步");
                pop.showAtLocation(getActivity().findViewById(R.id.ll_activity_next), Gravity.TOP
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                UMengUtils.onCountListener(getActivity(), "denglu_shouji");
                break;
            case R.id.rl_denglu_wx:
                // 微信登录
                dsf_type = "weixin";
                login(SHARE_MEDIA.WEIXIN);
                UMengUtils.onCountListener(getActivity(), "GD_01_01");
                break;
            case R.id.rl_denglu_wb:
                // 新浪微博登录
                dsf_type = "weibo";
                login(SHARE_MEDIA.SINA);
                UMengUtils.onCountListener(getActivity(), "GD_01_02");
                break;
            case R.id.rl_denglu_qq:
                // QQ登录
                dsf_type = "qq";
                login(SHARE_MEDIA.QQ);
                UMengUtils.onCountListener(getActivity(), "GD_01_03");
                break;
            default:
                break;
        }
    }

    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentActivity activity = fragment.getActivity();
            if (activity == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.tv_denglu_tishi:
                    getData();
                    break;
                case R.id.tv_denglu_lift:
                    switch (type) {
                        case 2:
                            if (time != 0) {
                                timeHandler.removeCallbacks(mRunnable);
                                time = 0;
                            }
                            type = 1;
                            et_denglu_num.setHint("手机号码");
                            et_denglu_num.setText("");
                            et_denglu_num
                                    .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                            11)});
                            tv_denglu_tishi.setText("");
                            tv_denglu_tishi.setEnabled(false);
                            tv_denglu_lift.setText("取消");
                            tv_denglu_right.setText("下一步");
                            break;
                        case 1:
                            EditTextUtils.hideSoftInput(et_denglu_num, getActivity());
                            pop.dismiss();
                            break;
                        default:
                            break;
                    }
                    break;
                case R.id.tv_denglu_right:
                    switch (type) {
                        case 2:
                            getData2();
                            break;
                        case 1:
                            String str = "我们将发送验证码短信到这个号码:\n"
                                    + et_denglu_num.getText().toString();
                            Dialog dialog = new AlertDialog.Builder(getActivity())
                                    .setTitle("确认手机号码")
                                    .setMessage(str)
                                            // 设置内容
                                    .setPositiveButton("好",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    Phone = et_denglu_num.getText()
                                                            .toString().trim();
                                                    getData();
                                                }
                                            })
                                    .setNegativeButton("取消",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int whichButton) {

                                                }
                                            }).create();
                            dialog.show();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 手机号请求验证码
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", Phone);

        HttpParamsUtil.sendData(map, null, GlobalConstant.sign_msg,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        new Thread() {
                            @Override
                            public void run() {
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
                            UserinfoSaveJson json = new Gson().fromJson(
                                    jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                type = 2;
                                Toast.makeText(getActivity(), "验证码已发送,请稍等",
                                        Toast.LENGTH_SHORT).show();
                                et_denglu_num.setHint("验证码");
                                et_denglu_num.setText("");
                                et_denglu_num
                                        .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                                6)});
                                tv_denglu_lift.setText("上一步");
                                tv_denglu_right.setText("验证");
                                tv_denglu_tishi.setEnabled(false);
                                tv_denglu_tishi.setTextColor(Color.GRAY);
                                // new Thread(new ClassCut()).start();// 开启倒计时
                                // TODO
                                time = 60;
                                timeHandler.post(mRunnable);
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

    // TODO
    private Handler timeHandler = new Handler();

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (time > 0) {
                tv_denglu_tishi.setText("接受短信大约需要" + time + "秒");// 显示剩余时间
                time--;
                timeHandler.postDelayed(mRunnable, 1000);
            } else {
                tv_denglu_tishi.setText("收不到验证码?");// 一轮倒计时结束 修改剩余时间为一分钟
                tv_denglu_tishi.setTextColor(Color.BLACK);
                tv_denglu_tishi.setEnabled(true);
            }
        }
    };

    private int time = 0;// 倒计时的整个时间数

    /**
     * 验证登录
     */
    private void getData2() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", Phone);
        map.put("code", et_denglu_num.getText().toString().trim());
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, null, GlobalConstant.check_code,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        new Thread() {
                            @Override
                            public void run() {
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
                            UserinfoSaveJson json = new Gson().fromJson(
                                    jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                // TODO
                                GlobalParams.uid = json.user_data.user_id;
                                GlobalParams.userInfoBean = json.user_data;
                                SPUtils.setUserId(getActivity(),
                                        json.user_data.user_id);
                                NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
                                SPUtils.setAppTime("");
                                startActivity(new Intent().setClass(
                                        getActivity(), MainActivity.class));
                                getActivity().finish();
                                getActivity().overridePendingTransition(
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out);
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }


    /**
     * 授权。如果授权成功，则获取用户信息
     *
     * @param platform 平台标签
     */
    private void login(final SHARE_MEDIA platform) {
        mController.doOauthVerify(getActivity(), platform,
                new UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                        // Log.e("TAG", "-----授权开始");
                        isOK = true;
                    }

                    @Override
                    public void onError(SocializeException e,
                                        SHARE_MEDIA platform) {
                        Toast.makeText(getActivity(), "授权错误",
                                Toast.LENGTH_SHORT).show();
                        isOK = false;
                    }

                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
                        // Log.e("TAG", "-----onComplete");
                        // 授权成功，获取uid
                        if (value != null
                                && !TextUtils.isEmpty(value.getString("uid"))) {
                            // uid不为空，获取相关授权信息
                            getUserInfo(platform);
                        } else {
                            Toast.makeText(getActivity(), "授权失败...",
                                    Toast.LENGTH_LONG).show();
                            isOK = false;
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        // Log.e("TAG", "-----授权取消");
                        isOK = false;
                    }
                });
    }

    /**
     * 获取用户信息
     *
     * @param platform 平台标签
     */
    private void getUserInfo(SHARE_MEDIA platform) {
        mController.getPlatformInfo(getActivity(), platform,
                new UMDataListener() {

                    @Override
                    public void onStart() {
                        // Log.e("TAG", "-----获取平台数据开始");
                    }

                    @Override
                    public void onComplete(int status, Map<String, Object> info) {
                        String showText = "";
                        if (status == StatusCode.ST_CODE_SUCCESSED) {
                            switch (dsf_type) {
                                case "weixin":
                                    third_id = info.get("openid").toString();
                                    union_id = info.get("unionid").toString();
                                    name = info.get("nickname").toString();
                                    avatar = info.get("headimgurl").toString();
                                    sex = info.get("sex").toString();
                                    // intro = info.get("").toString();
                                    province = info.get("province").toString();
                                    city = info.get("city").toString();
                                    break;
                                case "weibo":
                                    third_id = info.get("uid").toString();
                                    name = info.get("screen_name").toString();
                                    avatar = info.get("profile_image_url")
                                            .toString().trim();
                                    sex = info.get("gender").toString();
//                                    intro = info.get("description").toString();// 简介
                                    // province = info.get("").toString(); //省
                                    city = info.get("location").toString(); // 市
                                    SPUtils.setUserWeiboAccess_Token(getActivity(),
                                            info.get("access_token").toString());
                                    GlobalParams.weiboaccess_token = info.get("access_token").toString();
                                    MLog.v("reading", "access_token");
                                    break;
                                case "qq":
                                    third_id = info.get("openid").toString();
                                    name = info.get("screen_name").toString();
                                    avatar = info.get("profile_image_url")
                                            .toString();
                                    // intro = info.get("").toString();
                                    province = info.get("province").toString();
                                    city = info.get("city").toString();
                                    if ("男".equals(info.get("gender").toString())) {
                                        sex = "1";
                                    } else if ("女".equals(info.get("gender")
                                            .toString())) {
                                        sex = "2";
                                    } else {
                                        sex = "0";
                                    }
                                    break;
                                default:
                                    break;
                            }
                            // 获取信息成功,第三方登录网络验证
                            getData_third();
                            showText = "获取用户信息成功";
                        } else {
                            showText = "获取用户信息失败";
                        }
//						 Log.e("TAG", "-----showText: " + showText);
                        if (info != null) {
//							 Log.e("TAG", "-----info: " + info.toString());
                        }
                    }
                });
    }
    //TODO
    //第三方登录 - 请求网络
    private void getData_third() {
        Map<String, String> map = new HashMap<>();
        map.put("third_id", third_id);
        map.put("type", dsf_type);
        // 微信需要传 union_id
        if (!TextUtils.isEmpty(union_id)) {
            map.put("union_id", union_id);
        }

        HttpParamsUtil.sendData(map, null, GlobalConstant.third_login,
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
                        isOK = false;
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
                        MLog.v("reading", jsonString);
                        try {
//                            System.out.println("-----3423:" + jsonString);
                            UserinfoSaveJson json = new Gson().fromJson(jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                isOK = false;
                                GlobalParams.uid = json.user_data.user_id;
                                GlobalParams.userInfoBean = json.user_data;
                                GlobalParams.wx = json.bind.wx;
                                GlobalParams.qq = json.bind.qq;
                                GlobalParams.weibo = json.bind.weibo;
                                json.bind.type = dsf_type;
                                SPUtils.setUMeng(getActivity(), json.bind);
                                SPUtils.setThirdId(getActivity(), third_id);
                                if (!TextUtils.isEmpty(json.user_data.name)
                                        || !TextUtils
                                        .isEmpty(json.user_data.sex)
                                        && !TextUtils
                                        .isEmpty(json.user_data.avatar)) {
                                    SPUtils.setUserId(getActivity(),
                                            json.user_data.user_id);
                                    NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
                                    SPUtils.setAppTime("");
                                    startActivity(new Intent().setClass(
                                            getActivity(), MainActivity.class));
                                    getActivity().finish();
                                    getActivity().overridePendingTransition(
                                            android.R.anim.fade_in,
                                            android.R.anim.fade_out);
                                } else {
                                    // 下载的图片放在fengwo_denglu_avatar这个文件夹里面
                                    File file = new File(Environment
                                            .getExternalStorageDirectory()
                                            + "/fengwo_denglu_avatar");
                                    if (!file.exists()) {
                                        // 为空
                                        if (file.mkdir()) {
                                            saveFile();
                                        }
                                    } else {
                                        // 不为空,全部清除
                                        File files[] = file.listFiles();
                                        for (int i = 0; i < files.length; i++) {
                                            deleteFile(files[i]);
                                        }
                                        saveFile();
                                    }
                                }
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 赋予个人信息
     */
    private void getData4(File file) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("name", name);
        map.put("sex", sex);
        map.put("intro", intro);
        map.put("province", province);
        map.put("city", city);
        // 头像
        Map<String, File> filemap = new HashMap<>();
        filemap.put("img", file);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.userinfo_save, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        new Thread() {
                            @Override
                            public void run() {
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
                            UserinfoSaveJson json = new Gson().fromJson(jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                GlobalParams.uid = json.user_data.user_id;
                                GlobalParams.userInfoBean = json.user_data;
                                SPUtils.setUserId(getActivity(), json.user_data.user_id);
                                NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
                                SPUtils.setAppTime("");
                                startActivity(new Intent().setClass(
                                        getActivity(), MainActivity.class));
                                getActivity().finish();
                                getActivity().overridePendingTransition(
                                        android.R.anim.fade_in,
                                        android.R.anim.fade_out);
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, filemap);
    }

    /**
     * 注销本次登陆,(退出登录)
     *
     * @param platform
     */
    public void logout(final SHARE_MEDIA platform) {
        UMServiceFactory.getUMSocialService("com.umeng.login").deleteOauth(
                getActivity(), platform, new SocializeClientListener() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(int status, SocializeEntity entity) {
                        String showText = "解除" + platform.toString() + "平台授权成功";
                        if (status != StatusCode.ST_CODE_SUCCESSED) {
                            showText = "解除" + platform.toString() + "平台授权失败["
                                    + status + "]";
                        }
                        // Toast.makeText(getActivity(), showText,
                        // Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 下载图片，然后存在本地
     */
    private void saveFile() {
        new Thread() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }.start();
        final File f = new File(Environment.getExternalStorageDirectory() + "/fengwo_denglu_avatar", ("avatar.jpg"));
        ImageLoader.getInstance().loadImage(avatar, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeResource(fragment
                            .getActivity().getResources(), R.drawable.cover);
                    FileOutputStream out = new FileOutputStream(f);
                    boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                            out);
                    out.flush();
                    out.close();
                    if (b) {
                        // 图片下载成功,请求网络
                        getData4(f);
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(2);
                            }
                        }.start();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    boolean b = loadedImage.compress(Bitmap.CompressFormat.PNG,
                            100, out);
                    out.flush();
                    out.close();
                    if (b) {
                        // 图片下载成功,请求网络
                        getData4(f);
                    } else {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(2);
                            }
                        }.start();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    /**
     * 删除
     */
    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    Context context = fragment.getActivity();
                    if (context != null) {
                        Toast.makeText(context,
                                context.getString(R.string.network_check),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                case 0:
                    if (progressDialog != null && !progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onStart() {
        super.onStart();
        // Log.e("TAG", "-----onStart");
        // 延时
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOK) {
                    handler.sendEmptyMessage(0);
                    isOK = false;
                } else {
                    handler.sendEmptyMessage(1);
                }
            }
        }, 300);
    }

    public void onStop() {
        super.onStop();
        // Log.e("TAG", "-----onStop");
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // getActivity().unregisterReceiver(smsReceiver);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("DengLuFragment");
    }

    public void onPause() {
        super.onPause();
        // Log.e("TAG", "-----onPause");
        MobclickAgent.onPageEnd("DengLuFragment");
        fragment.getActivity().getContentResolver()
                .unregisterContentObserver(mObserver);
    }

}