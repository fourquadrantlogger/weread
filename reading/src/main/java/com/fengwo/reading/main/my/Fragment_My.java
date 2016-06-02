package com.fengwo.reading.main.my;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.my.achieve.MyAchieveFragment;
import com.fengwo.reading.main.my.myfav.Fragment_MyFav;
import com.fengwo.reading.main.read.Fragment_Local;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.DengLuFragment;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.MySQLiteOpenHelper;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.VipImageUtil;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
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
 * @author lxq 我的
 */
public class Fragment_My extends Fragment implements OnClickListener {

    // 整个平台的Controller,负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");

    private SelectSharePopupWindow sharePopupWindow;
    private ImageView iv_title_left;
    private TextView tv_title_mid;

    private RelativeLayout rl_title_right_notify;
    private ImageView iv_title_right_notify_point;
    //endregion
    private ImageView iv_my_avatar, iv_my_vip, iv_my_xunzhang;
    private TextView tv_my_name, tv_my_intro;
    private RelativeLayout rl_my_info, rl_my_0, rl_my_1, rl_my_2,
            rl_my_3, rl_my_4, rl_my_offline,
            rl_my_5, rl_my_6, rl_my_7, rl_my_8;

    private SQLiteDatabase database;

    public boolean is_notify = false;

    public static Fragment_My fragment = new Fragment_My();

    public static Fragment_My getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        findViewById(view);
        onClickListener();

        iv_title_left.setVisibility(View.GONE);
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText("我的");

        rl_title_right_notify.setVisibility(View.VISIBLE);
        iv_title_right_notify_point.setVisibility(View.VISIBLE);

        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(
                fragment.getActivity());
        database = helper.getReadableDatabase();

        //分享的弹出窗体类
        sharePopupWindow = new SelectSharePopupWindow(getActivity(),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentActivity activity = getActivity();
                        if (activity == null) {
                            return;
                        }
                        sharePopupWindow.imageUrl = "";
                        sharePopupWindow.h5Url = GlobalConstant.ServerDomain + "/share/app";
                        sharePopupWindow.title = "有书共读";
                        int num = 0;
                        switch (v.getId()) {
                            case R.id.ll_popupwindow_wx:
                                num = 1;
                                sharePopupWindow.content = "我正在使用有书共读APP，推荐给你。 每周共读一本书，组队对抗惰性。";
                                break;
                            case R.id.ll_popupwindow_pyq:
                                num = 2;
                                sharePopupWindow.content = "有书共读";
                                break;
                            case R.id.ll_popupwindow_qq:
                                num = 3;
                                sharePopupWindow.content = "我正在使用有书共读APP，推荐给你。 每周共读一本书，组队对抗惰性。";
                                break;
                            case R.id.ll_popupwindow_wb:
                                num = 4;
                                sharePopupWindow.content = "我正在使用有书共读APP，推荐给你。 每周共读一本书，组队对抗惰性。来自@有书共读" + sharePopupWindow.h5Url;
                                break;
                            default:
                                break;
                        }
//                        UMengUtils.onCountListener(getActivity(), "shouye_CSB_FX");
                        UMShare.setUMeng(activity, num, sharePopupWindow.title, sharePopupWindow.content, sharePopupWindow.imageUrl, sharePopupWindow.h5Url, "", "");
//                        if ("1".equals(UMShare.getLevel())) {
//                            startActivity(new Intent(getActivity(), UpgradeActivity.class));
//                        }
                        sharePopupWindow.dismiss();
                    }
                });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (GlobalParams.userInfoBean == null) {
            iv_my_avatar.setImageResource(R.drawable.avatar);
            tv_my_name.setText("");
            tv_my_intro.setText("");
        } else {
            DisplayImageUtils.displayImage(GlobalParams.userInfoBean.avatar, iv_my_avatar, 100, R.drawable.avatar);

            refresh_userinfo();
        }
        is_notify = true;
        refresh();
    }

    public void refresh_userinfo() {
        tv_my_name.setText(GlobalParams.userInfoBean.name);
        //等级
        VipImageUtil.getExp(GlobalParams.userInfoBean.exp);
        VipImageUtil.getVipGrade(getActivity(), iv_my_vip, VipImageUtil.getGrade(), 1);
        //勋章
        if (!TextUtils.isEmpty(GlobalParams.userInfoBean.badge_id)) {
            try {
                iv_my_xunzhang.setImageDrawable(GlobalParams.userInfoBean.badge_Drawable(0.3f, 0.3f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tv_my_intro.setText(GlobalParams.userInfoBean.intro);
    }

    public void refresh() {
        if (iv_title_right_notify_point == null) {
            return;
        }
        Cursor cursor = database.rawQuery(
                "select * from tb_notify where uid = ? and is_read = ?",
                new String[]{GlobalParams.uid, "0"});
        if (cursor.getCount() == 0) {
            iv_title_right_notify_point.setVisibility(View.GONE);
        } else {
            iv_title_right_notify_point.setVisibility(View.VISIBLE);
        }
        cursor.close();
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

        rl_title_right_notify = (RelativeLayout) view
                .findViewById(R.id.rl_title_right_notify);
        iv_title_right_notify_point = (ImageView) view
                .findViewById(R.id.iv_title_right_notify_point);

        iv_my_avatar = (ImageView) view.findViewById(R.id.iv_my_avatar);
        iv_my_vip = (ImageView) view.findViewById(R.id.iv_my_vip);
        iv_my_xunzhang = (ImageView) view.findViewById(R.id.iv_my_xunzhang);

        tv_my_name = (TextView) view.findViewById(R.id.tv_my_name);
        tv_my_intro = (TextView) view.findViewById(R.id.tv_my_intro);

        rl_my_0 = (RelativeLayout) view.findViewById(R.id.rl_my_0);
        SPUtils.getUMeng(getActivity());
        if ("qq".equals(GlobalParams.type)) {
            rl_my_0.setVisibility(View.VISIBLE);
        } else {
            rl_my_0.setVisibility(View.GONE);
        }
        rl_my_0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //找回数据
                getInfo();
            }
        });

        rl_my_info = (RelativeLayout) view.findViewById(R.id.rl_my_info);
        rl_my_1 = (RelativeLayout) view.findViewById(R.id.rl_my_1);
        rl_my_2 = (RelativeLayout) view.findViewById(R.id.rl_my_2);

        rl_my_3 = (RelativeLayout) view.findViewById(R.id.rl_my_3);
        rl_my_4 = (RelativeLayout) view.findViewById(R.id.rl_my_4);
        rl_my_offline = (RelativeLayout) view.findViewById(R.id.rl_my_offline);

        rl_my_5 = (RelativeLayout) view.findViewById(R.id.rl_my_5);
        rl_my_6 = (RelativeLayout) view.findViewById(R.id.rl_my_6);
        rl_my_7 = (RelativeLayout) view.findViewById(R.id.rl_my_7);

        rl_my_8 = (RelativeLayout) view.findViewById(R.id.rl_my_8);
    }

    private void onClickListener() {
        rl_my_info.setOnClickListener(this);
        rl_my_1.setOnClickListener(this);
        rl_my_2.setOnClickListener(this);

        rl_my_3.setOnClickListener(this);
        rl_my_4.setOnClickListener(this);
        rl_my_offline.setOnClickListener(this);

        rl_my_5.setOnClickListener(this);
        rl_my_6.setOnClickListener(this);
        rl_my_7.setOnClickListener(this);

        //分享有书APP
        rl_my_8.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UMengUtils.onCountListener(getActivity(), "GD_05_09");
                FragmentActivity activity = getActivity();
                sharePopupWindow.showAtLocation(activity.findViewById(R.id.ll_my),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        rl_title_right_notify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, NextActivity.class);
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.rl_my_info:
                //个人信息
                UMengUtils.onCountListener(getActivity(), "GD_05_02");
                bundle.putString("fragmentname", MyInfoFragment.class.getSimpleName());
                MyInfoFragment.getInstance().needSaveView = false;
                MyInfoFragment.getInstance().source = 0;
                break;
            case R.id.rl_my_1:
                //我的成就
                UMengUtils.onCountListener(getActivity(), "GD_05_03");
                bundle.putString("fragmentname", MyAchieveFragment.class.getSimpleName());
                MyAchieveFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_my_2:
                //我的阅历
                UMengUtils.onCountListener(getActivity(), "GD_05_04");
                bundle.putString("fragmentname", ProgressFragment.class.getSimpleName());
                ProgressFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_my_3:
                //我的随笔
                UMengUtils.onCountListener(getActivity(), "GD_05_05");
                bundle.putString("fragmentname", Fragment_Suibi.class.getSimpleName());
                Fragment_Suibi.getInstance().needSaveView = false;
                break;
            case R.id.rl_my_4:
                //我的收藏
                UMengUtils.onCountListener(getActivity(), "GD_05_06");
                bundle.putString("fragmentname", Fragment_MyFav.class.getSimpleName());
                Fragment_MyFav.getInstance().needSaveView = false;
                break;
            case R.id.rl_my_offline:
                //我的离线
//                UMengUtils.onCountListener(getActivity(), "");
                bundle.putString("fragmentname", Fragment_Local.class.getSimpleName());
                Fragment_Local.getInstance().needSaveView = false;
                break;
            case R.id.rl_my_5:
                //账户设置
                UMengUtils.onCountListener(getActivity(), "GD_05_08");
                bundle.putString("fragmentname", SettingFragment.class.getSimpleName());
                SettingFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_my_6:
                //使用帮助
                UMengUtils.onCountListener(getActivity(), "GD_05_10");
                bundle.putString("fragmentname", WebFragment.class.getSimpleName());
                WebFragment.getInstance().needSaveView = false;
                WebFragment.getInstance().source = 1;
                WebFragment.getInstance().url = GlobalConstant.SERVERURL.equals("http://api.fengwo.com/m/") ? "http://api.fengwo.com/"
                        : "http://gongdu.youshu.cc/" + "userguide?uid="
                        + GlobalParams.uid;
                break;
            case R.id.rl_my_7:
                //意见反馈
                UMengUtils.onCountListener(getActivity(), "GD_05_11");
                bundle.putString("fragmentname", SuggestFragment.class.getSimpleName());
                SuggestFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_title_right_notify:
                //消息通知
                UMengUtils.onCountListener(getActivity(), "GD_05_01");
                bundle.putString("fragmentname", MyNotifyFragment.class.getSimpleName());
                MyNotifyFragment.getInstance().needSaveView = false;
                break;

            default:
                break;
        }
        intent.putExtras(bundle);
        fragment.getActivity().startActivity(intent);
        fragment.getActivity().overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }

    /**
     * 找回数据
     */
    public void getInfo() {
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("确认找回历史数据?")
                .setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                configPlatforms();
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
    }

    private void configPlatforms() {
        // 添加QQ支持
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
                "100424468", "c7394704798a158208a74ab60104f0ba");
        qqSsoHandler.setTargetUrl("http://www.umeng.com");
        qqSsoHandler.addToSocialSDK();

        mController.doOauthVerify(getActivity(), SHARE_MEDIA.QQ,
                new SocializeListeners.UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA platform) {
                        // Log.e("TAG", "-----授权开始");
                    }

                    @Override
                    public void onError(SocializeException e,
                                        SHARE_MEDIA platform) {
                        Toast.makeText(getActivity(), "授权错误",
                                Toast.LENGTH_SHORT).show();
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
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA platform) {
                        // Log.e("TAG", "-----授权取消");
                    }
                });
    }

    /**
     * 获取 id
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
                        if (status == StatusCode.ST_CODE_SUCCESSED) {
                            String qq_id = info.get("openid").toString();
                            // 获取信息成功
                            Toast.makeText(getActivity(), "获取用户信息成功", Toast.LENGTH_SHORT).show();
                            getData(qq_id);
                        } else {
                            Toast.makeText(getActivity(), "获取用户信息失败", Toast.LENGTH_SHORT).show();
                        }
                        if (info != null) {
//							 Log.e("TAG", "-----info: " + info.toString());
                        }
                    }
                }
        );
    }

    //请求网络 - 找回数据
    public void getData(String qq_id) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("qq_id", qq_id);

        HttpParamsUtil.sendData(map, null, GlobalConstant.user_oldata,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Toast.makeText(getActivity(), "找回数据出现错误,请稍后重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {
//                            System.out.println("-----3423:" + jsonString);
                            UserinfoSaveJson json = new Gson().fromJson(jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                Toast.makeText(getActivity(), "恭喜,数据找回成功", Toast.LENGTH_SHORT).show();
                                GlobalParams.uid = json.user_data.user_id;
                                GlobalParams.userInfoBean = json.user_data;
                                SPUtils.setUserId(getActivity(),
                                        json.user_data.user_id);
                                NOsqlUtil.set_userInfoBean(json.user_data);
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment_My");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Fragment_My");
    }

}
