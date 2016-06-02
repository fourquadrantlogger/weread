package com.fengwo.reading.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.my.achieve.AchieveInfoJson;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.utils.VipImageUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 升级
 */
public class UpgradeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_privilege_cancel, iv_upgrade_vip;
    private TextView tv_upgrade_vip, tv_upgrade_day, tv_upgrade_books, tv_upgrade_suibi, tv_upgrade_fenxiang, tv_upgrade_num;

    // 分享的信息
    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        ActivityUtil.upgradeActivity = this;

        findViewById();

        sharePopupWindow = new SelectSharePopupWindow(UpgradeActivity.this,
                itemsOnClick);

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 300);

    }

    private void findViewById() {
        iv_privilege_cancel = (ImageView) findViewById(R.id.iv_privilege_cancel);
        iv_upgrade_vip = (ImageView) findViewById(R.id.iv_upgrade_vip);
        tv_upgrade_vip = (TextView) findViewById(R.id.tv_upgrade_vip);
        tv_upgrade_day = (TextView) findViewById(R.id.tv_upgrade_day);
        tv_upgrade_books = (TextView) findViewById(R.id.tv_upgrade_books);
        tv_upgrade_suibi = (TextView) findViewById(R.id.tv_upgrade_suibi);
        tv_upgrade_fenxiang = (TextView) findViewById(R.id.tv_upgrade_fenxiang);
        tv_upgrade_num = (TextView) findViewById(R.id.tv_upgrade_num);

        iv_privilege_cancel.setOnClickListener(this);
        tv_upgrade_fenxiang.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_privilege_cancel:
                finish();
                break;
            case R.id.tv_upgrade_fenxiang:
                //分享
                sharePopupWindow.showAtLocation(findViewById(R.id.rl_upgrade),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
        }
    }

    /**
     * 我的成就
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.user_cj,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {
                            AchieveInfoJson json = new Gson().fromJson(jsonString,
                                    AchieveInfoJson.class);
                            if ("1".equals(json.code)) {
                                // 设置数据
                                VipImageUtil.getExp(json.data.exp);
                                VipImageUtil.getVipGrade(UpgradeActivity.this, iv_upgrade_vip, VipImageUtil.getGrade(), 4);
                                tv_upgrade_vip.setText("V" + VipImageUtil.getGrade());
                                GlobalParams.userInfoBean.exp = json.data.exp;
                                tv_upgrade_day.setText(json.data.qd_sum);
                                tv_upgrade_books.setText(json.data.book_sum);
                                tv_upgrade_suibi.setText(json.data.note_sum);
                                tv_upgrade_num.setText("与\t" + json.data.shuyou + "\t名书友一起读书");

                                imageUrl = GlobalParams.userInfoBean.avatar;
                                h5Url = GlobalConstant.ServerDomain + "share/level?user_id=" + GlobalParams.uid;
                                title = "我的有书等级已达到【V" + VipImageUtil.getGrade() + "】";
                                content = "参加有书共读计划第【" + json.data.qd_sum
                                        + "】天，已读完【" + json.data.book_sum + "】本书，想想还有点小激动呢";
                            } else {
                                Toast.makeText(UpgradeActivity.this, json.msg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(UpgradeActivity.this,
                                    getString(R.string.json_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, true, null);
    }

    // 分享的弹出窗体类
    private SelectSharePopupWindow sharePopupWindow;
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int num = 0;
            switch (v.getId()) {
                case R.id.ll_popupwindow_wx:
                    num = 1;
                    break;
                case R.id.ll_popupwindow_pyq:
                    num = 2;
                    break;
                case R.id.ll_popupwindow_qq:
                    num = 3;
                    break;
                case R.id.ll_popupwindow_wb:
                    num = 4;
                    content = title + content + "!来自@有书共读" + h5Url;
                    break;
                default:
                    break;
            }
            UMShare.setUMeng(UpgradeActivity.this, num, title, content, imageUrl, h5Url, "", "");
            sharePopupWindow.dismiss();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}