package com.fengwo.reading.main.my;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.LoginActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomDeleteDialog;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.service.PushDemoReceiver;
import com.fengwo.reading.umeng.UMengBean;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.CidUtils;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.localdata.FileUtil;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lxq 账户设置
 */
public class SettingFragment extends Fragment implements OnClickListener {
    private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    private ImageView iv_title_left;
    private TextView tv_title_mid, tv_setting_bd_wb, tv_setting_bd_qq, tv_setting_bd_wx, textView_fragmentsetting_cachefoldersize;
    //显示是否使用2G/3G/4G播放或下载的按钮
    private Button bt_remind_switch1, bt_remind_switch2;

    private RelativeLayout rl_setting_remind;
    private RelativeLayout rl_setting_clear;
    private RelativeLayout rl_setting_logout;
    private RelativeLayout rl_setting_with;


    private CustomProgressDialog progressDialog;

    private CustomDeleteDialog dialog;

    private String dsf_type = ""; // qq  wx  weibo
    private String third_id = ""; // 第三方的id，微信的传open_id
    private String union_id = ""; // 微信的union_id

    public boolean isBang = false; //true:绑定 false:解绑

    private View saveView = null;
    public boolean needSaveView = false;

    public SettingFragment() {
    }

    public static SettingFragment fragment = new SettingFragment();

    public static SettingFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_setting, container,
                false);

        findViewById(view);
        // 配置相关平台
        configPlatforms();

        progressDialog = CustomProgressDialog.createDialog(fragment
                .getActivity());

        bt_remind_switch1
                .setBackgroundResource(SPUtils.getBoFang() ? R.drawable.switchbutton_on
                        : R.drawable.switchbutton_off);
        bt_remind_switch2
                .setBackgroundResource(SPUtils.getXiaZai() ? R.drawable.switchbutton_on
                        : R.drawable.switchbutton_off);

        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText("账户设置");
        textView_fragmentsetting_cachefoldersize.setText(FileUtil.getFolderSize() + "");

        dialog = new CustomDeleteDialog(fragment.getActivity(),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        GlobalParams.uid = GlobalConstant.ISLOGINUID;
                        GlobalParams.userInfoBean = null;
                        SPUtils.setUserId(fragment.getActivity(),
                                GlobalConstant.ISLOGINUID);
                        NOsqlUtil.set_userInfoBean(null);
                        //
                        SPUtils.setAppTime("");

                        NOsqlUtil.set_naoling(null);
                        NOsqlUtil.set_wordlimit(null);

                        SPUtils.setAppConfig_act("");
                        // TODO
                        Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("key", 1);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        fragment.getActivity().finish();
                        ActivityUtil.mainActivity.finish();
                        CidUtils.clearCid();
                        PushDemoReceiver.payloadData.delete(0,
                                PushDemoReceiver.payloadData.length());
                        UMServiceFactory
                                .getUMSocialService("com.umeng.login").deleteOauth(getActivity(), SHARE_MEDIA.WEIXIN, null);
                        UMServiceFactory
                                .getUMSocialService("com.umeng.login").deleteOauth(getActivity(), SHARE_MEDIA.SINA, null);
                        UMServiceFactory
                                .getUMSocialService("com.umeng.login").deleteOauth(getActivity(), SHARE_MEDIA.QQ, null);
                    }
                });

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

        tv_setting_bd_wx = (TextView) view.findViewById(R.id.tv_setting_bd_wx);
        tv_setting_bd_qq = (TextView) view.findViewById(R.id.tv_setting_bd_qq);
        tv_setting_bd_wb = (TextView) view.findViewById(R.id.tv_setting_bd_wb);

        bt_remind_switch1 = (Button) view.findViewById(R.id.bt_remind_switch1);
        bt_remind_switch2 = (Button) view.findViewById(R.id.bt_remind_switch2);
        textView_fragmentsetting_cachefoldersize = (TextView) view.findViewById(R.id.textView_fragmentsetting_cachefoldersize);
        //已绑定帐号
        if ("1".equals(GlobalParams.wx)) {
            tv_setting_bd_wx.setText("解绑");
            tv_setting_bd_wx.setTextColor(fragment.getActivity().getResources()
                    .getColor(R.color.text_32));
            tv_setting_bd_wx.setBackgroundResource(R.drawable.setting_gray);
        }
        if ("1".equals(GlobalParams.qq)) {
            tv_setting_bd_qq.setText("解绑");
            tv_setting_bd_qq.setTextColor(fragment.getActivity().getResources()
                    .getColor(R.color.text_32));
            tv_setting_bd_qq.setBackgroundResource(R.drawable.setting_gray);
        }
        if ("1".equals(GlobalParams.weibo)) {
            tv_setting_bd_wb.setText("解绑");
            tv_setting_bd_wb.setTextColor(fragment.getActivity().getResources()
                    .getColor(R.color.text_32));
            tv_setting_bd_wb.setBackgroundResource(R.drawable.setting_gray);
        }
        rl_setting_remind = (RelativeLayout) view
                .findViewById(R.id.rl_setting_remind);
        rl_setting_clear = (RelativeLayout) view.findViewById(R.id.rl_setting_clear);
        rl_setting_logout = (RelativeLayout) view
                .findViewById(R.id.rl_setting_logout);
        rl_setting_with = (RelativeLayout) view.findViewById(R.id.rl_setting_with);

        iv_title_left.setOnClickListener(this);
        rl_setting_remind.setOnClickListener(this);
        rl_setting_clear.setOnClickListener(this);
        rl_setting_logout.setOnClickListener(this);
        rl_setting_with.setOnClickListener(this);
        tv_setting_bd_wb.setOnClickListener(this);
        tv_setting_bd_qq.setOnClickListener(this);
        tv_setting_bd_wx.setOnClickListener(this);
        bt_remind_switch1.setOnClickListener(this);
        bt_remind_switch2.setOnClickListener(this);
    }

    private void configPlatforms() {
        // 设置新浪SSO handler
        // mController.getConfig().setSsoHandler(new SinaSsoHandler());
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

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_return:
                fragment.getActivity().finish();
                fragment.getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case R.id.rl_setting_remind:
                //早晚读设置 - 闹铃提醒
                UMengUtils.onCountListener(getActivity(), "GD_05_08_04");
                FragmentTransaction transaction = fragment.getActivity()
                        .getSupportFragmentManager().beginTransaction();
                transaction
                        .setCustomAnimations(R.anim.in_from_bottom,
                                R.anim.out_to_top, R.anim.in_from_top,
                                R.anim.out_to_bottom);
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        RemindFragment.getInstance());
                transaction.commit();
                RemindFragment.getInstance().type = 0;
                RemindFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_setting_clear:
                //清楚缓存
                UMengUtils.onCountListener(getActivity(), "GD_05_08_06");
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                dialogBuilder.setTitle("缓存音频");
                dialogBuilder.setMessage("是否删除所有缓存的音频?");
                dialogBuilder.setCancelable(true);
                dialogBuilder.setPositiveButton("清除", new DialogInterface.OnClickListener() {
                    //确认按钮的点击事件
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (FileUtil.clearMediaFile()) {
                            CustomToast.showToast(fragment.getActivity(), "删除成功");
                            textView_fragmentsetting_cachefoldersize.setText("0MB");
                        } else {
                            CustomToast.showToast(fragment.getActivity(), "没有缓存文件");
                        }
                        MLog.v("reading", "clearMediaFile");
                    }
                });
                dialogBuilder.setNegativeButton("查看文件夹", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(Environment.getExternalStorageDirectory() + GlobalParams.FolderPath_Media);

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setDataAndType(Uri.fromFile(file), "*/*");
                        startActivity(intent);
                    }
                });
                AlertDialog mdialog = dialogBuilder.create();
//                         dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设置AlertDialog类型，保证在广播接收器中可以正常弹出
                mdialog.show();
                break;
            case R.id.rl_setting_logout:
                //退出登录
                UMengUtils.onCountListener(getActivity(), "GD_05_08_07");
                dialog.show();
                break;
            case R.id.bt_remind_switch1:
                //是否使用网络播放
                if (SPUtils.getBoFang()) {
                    SPUtils.setBoFang(false);
                    bt_remind_switch1.setBackgroundResource(R.drawable.switchbutton_off);
                } else {
                    SPUtils.setBoFang(true);
                    bt_remind_switch1.setBackgroundResource(R.drawable.switchbutton_on);
                }
                break;
            case R.id.bt_remind_switch2:
                //是否使用网络下载
                if (SPUtils.getXiaZai()) {
                    SPUtils.setXiaZai(false);
                    bt_remind_switch2.setBackgroundResource(R.drawable.switchbutton_off);
                } else {
                    SPUtils.setXiaZai(true);
                    bt_remind_switch2.setBackgroundResource(R.drawable.switchbutton_on);
                }
                break;
            case R.id.rl_setting_with:
                //关于有书
                UMengUtils.onCountListener(getActivity(), "GD_05_08_05");
                Intent intent = new Intent();
                intent.setClass(getActivity(), SettingForWithYouShu.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                break;
            case R.id.tv_setting_bd_wb:
                //绑定 - 新浪微博
                UMengUtils.onCountListener(getActivity(), "GD_05_08_01");
                dsf_type = "weibo";
                if ("1".equals(GlobalParams.weibo)) {
                    isBang = false;
                    logout(SHARE_MEDIA.SINA);
                } else {
                    isBang = true;
                    login(SHARE_MEDIA.SINA);
                }
                break;
            case R.id.tv_setting_bd_qq:
                //绑定 - QQ
                UMengUtils.onCountListener(getActivity(), "GD_05_08_02");
                dsf_type = "qq";
                if ("1".equals(GlobalParams.qq)) {
                    isBang = false;
                    logout(SHARE_MEDIA.QQ);
                } else {
                    isBang = true;
                    login(SHARE_MEDIA.QQ);
                }
                break;
            case R.id.tv_setting_bd_wx:
                //绑定 - 微信
                UMengUtils.onCountListener(getActivity(), "GD_05_08_03");
                dsf_type = "wx";
                if ("1".equals(GlobalParams.wx)) {
                    isBang = false;
                    logout(SHARE_MEDIA.WEIXIN);
                } else {
                    isBang = true;
                    login(SHARE_MEDIA.WEIXIN);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 授权。如果授权成功，则获取用户信息
     *
     * @param platform 平台标签
     */
    private void login(final SHARE_MEDIA platform) {
        UMengUtils.onCountListener(getActivity(), "My_ZHSZ_BD");
        mController.doOauthVerify(getActivity(), platform,
                new SocializeListeners.UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                    }

                    @Override
                    public void onError(SocializeException e,
                                        SHARE_MEDIA platform) {
                        Toast.makeText(getActivity(), "授权错误",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA platform) {
                        if (value != null
                                && !TextUtils.isEmpty(value.getString("uid"))) {
                            // uid不为空，获取相关授权信息
                            getUserInfo(platform);
                        } else {
                            Toast.makeText(getActivity(), "授权失败...",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        // Log.e("TAG", "-----授权取消");
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
                                case "wx":
                                    third_id = info.get("openid").toString();
                                    union_id = info.get("unionid").toString();
                                    break;
                                case "weibo":
                                    third_id = info.get("uid").toString();
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
//                            Log.e("TAG", "-----info: " + info.toString());
                        }
                    }
                });
    }

    /**
     * 帐号绑定(解绑) - 请求网络
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
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
                            if ("1".equals(json.code)) {
                                UMengBean bean = new UMengBean();
                                bean.wx = GlobalParams.wx;
                                bean.weibo = GlobalParams.weibo;
                                bean.qq = GlobalParams.qq;
                                bean.type = GlobalParams.type;
                                //绑定成功,变为 解绑
                                switch (dsf_type) {
                                    case "wx":
                                        GlobalParams.wx = "1";
                                        bean.wx = GlobalParams.wx;
                                        tv_setting_bd_wx.setText("解绑");
                                        tv_setting_bd_wx.setTextColor(fragment.getActivity().getResources()
                                                .getColor(R.color.text_32));
                                        tv_setting_bd_wx.setBackgroundResource(R.drawable.setting_gray);
                                        break;
                                    case "weibo":
                                        GlobalParams.weibo = "1";
                                        bean.weibo = GlobalParams.weibo;
                                        tv_setting_bd_wb.setText("解绑");
                                        tv_setting_bd_wb.setTextColor(fragment.getActivity().getResources()
                                                .getColor(R.color.text_32));
                                        tv_setting_bd_wb.setBackgroundResource(R.drawable.setting_gray);
                                        break;
                                    case "qq":
                                        GlobalParams.qq = "1";
                                        bean.qq = GlobalParams.qq;
                                        tv_setting_bd_qq.setText("解绑");
                                        tv_setting_bd_qq.setTextColor(fragment.getActivity().getResources()
                                                .getColor(R.color.text_32));
                                        tv_setting_bd_qq.setBackgroundResource(R.drawable.setting_gray);
                                        break;
                                    default:
                                        break;
                                }
                                SPUtils.setUMeng(getActivity(), bean);
                            } else if ("2".equals(json.code)) {
                                UMengBean bean = new UMengBean();
                                bean.wx = GlobalParams.wx;
                                bean.weibo = GlobalParams.weibo;
                                bean.qq = GlobalParams.qq;
                                bean.type = GlobalParams.type;
                                //解绑成功,变为 绑定
                                switch (dsf_type) {
                                    case "wx":
                                        GlobalParams.wx = "0";
                                        bean.wx = GlobalParams.wx;
                                        tv_setting_bd_wx.setText("绑定");
                                        tv_setting_bd_wx.setTextColor(fragment.getActivity().getResources()
                                                .getColor(R.color.white));
                                        tv_setting_bd_wx.setBackgroundResource(R.drawable.setting_green);
                                        break;
                                    case "weibo":
                                        GlobalParams.weibo = "0";
                                        bean.weibo = GlobalParams.weibo;
                                        tv_setting_bd_wb.setText("绑定");
                                        tv_setting_bd_wb.setTextColor(fragment.getActivity().getResources()
                                                .getColor(R.color.white));
                                        tv_setting_bd_wb.setBackgroundResource(R.drawable.setting_green);
                                        break;
                                    case "qq":
                                        GlobalParams.qq = "0";
                                        bean.qq = GlobalParams.qq;
                                        tv_setting_bd_qq.setText("绑定");
                                        tv_setting_bd_qq.setTextColor(fragment.getActivity().getResources()
                                                .getColor(R.color.white));
                                        tv_setting_bd_qq.setBackgroundResource(R.drawable.setting_green);
                                        break;
                                    default:
                                        break;
                                }
                                SPUtils.setUMeng(getActivity(), bean);
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
     * 注销本次登陆,(退出登录)
     *
     * @param platform
     */
    public void logout(final SHARE_MEDIA platform) {
        mController.deleteOauth(
                getActivity(), platform, new SocializeListeners.SocializeClientListener() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(int status, SocializeEntity entity) {
                        if (status != StatusCode.ST_CODE_SUCCESSED) {
                            Toast.makeText(getActivity(), "解除" + platform.toString() + "平台授权失败["
                                            + status + "]",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getData();
                    }
                });
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    Context context = fragment.getActivity();
                    if (context != null && !((Activity) context).isFinishing()) {
                        CustomToast.showToast(context,
                                context.getString(R.string.network_check));
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

    public void onResume() {
        super.onResume();
        saveView = getView();
        MobclickAgent.onPageStart("SettingFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingFragment");
    }

}
