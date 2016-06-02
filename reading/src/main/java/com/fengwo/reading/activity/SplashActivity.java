package com.fengwo.reading.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.alarmclock.AlarmNotificationManager;
import com.fengwo.reading.alarmclock.AlarmUtils;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.player.Playlist_Now;
import com.fengwo.reading.task.config.Bean_ad;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.ImageUtils;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.umeng.analytics.MobclickAgent;

/**
 * @author lxq 闪屏界面
 */
public class SplashActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_splsah_logo, rl_splsah_ad;
    private ImageView iv_splsah_img;
    private TextView tv_splsah_time;
    private boolean isOne = false;
    private Bean_ad bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splsah);
        ActivityUtil.splashActivity = this;

        findViewById();

        SPUtils.getUMeng(this);
        SPUtils.getUserId(this);
        GlobalParams.userInfoBean = NOsqlUtil.get_userInfoBean();
        SPUtils.getUserCid(this);

        AlarmUtils.closeMusic();
        AlarmNotificationManager.cancelNotification(this);
        MLog.v("reading", "开启后台下载");
//        startService(new Intent(this, BackgroundService.class));

        isOne = false;
        time = 3;
        bean = NOsqlUtil.getConfig_ad();

        new BitmapUtils(this).display(iv_splsah_img, bean.img, new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadCompleted(ImageView imageView, String s, Bitmap srcBmp, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                Drawable drawable = new BitmapDrawable(getResources(), ImageUtils.getBitmap(SplashActivity.this, srcBmp));
                if (Build.VERSION.SDK_INT >= 16) {
                    iv_splsah_img.setImageDrawable(drawable);
                }
            }

            @Override
            public void onLoadFailed(ImageView linearLayout, String s, Drawable drawable) {
            }
        });

        handler.sendEmptyMessageDelayed(0, 1500);

    }

    private void findViewById() {
        rl_splsah_logo = (RelativeLayout) findViewById(R.id.rl_splsah_logo);
        rl_splsah_ad = (RelativeLayout) findViewById(R.id.rl_splsah_ad);
        iv_splsah_img = (ImageView) findViewById(R.id.iv_splsah_img);
        tv_splsah_time = (TextView) findViewById(R.id.tv_splsah_time);
        rl_splsah_logo.setVisibility(View.VISIBLE);
        rl_splsah_ad.setVisibility(View.GONE);
        iv_splsah_img.setOnClickListener(this);
        tv_splsah_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_splsah_img:
                //广告跳转
                isOne = true;
                timeHandler.removeCallbacks(mRunnable);
                time = 0;

                Intent intent = new Intent(this, NextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("fragmentname", WebFragment.class.getSimpleName());
                WebFragment.getInstance().needSaveView = false;
                WebFragment.getInstance().source = 1;
                WebFragment.getInstance().url = bean.href;
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                break;
            case R.id.tv_splsah_time:
                //跳过
                notNewVersionDlgShow();
                break;
        }
    }

    private int time;// 倒计时的整个时间数
    private Handler timeHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (time > 0) {
                tv_splsah_time.setText("跳过 " + time + "s");
                time--;
                timeHandler.postDelayed(mRunnable, 1000);
            } else {
                notNewVersionDlgShow();
            }
        }
    };

    public void notNewVersionDlgShow() {
        timeHandler.removeCallbacks(mRunnable);
        time = 0;
        if (SPUtils.getAppFirst(SplashActivity.this)) {
            //第一次使用,引导界面
            startActivity(new Intent(SplashActivity.this,
                    SetupActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in,
                    android.R.anim.fade_out);
        } else {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            if (GlobalParams.uid.equals(GlobalConstant.ISLOGINUID)) {
                //登录界面
                intent.setClass(this, LoginActivity.class);
                bundle.putInt("key", 1);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            } else {
                //共读首页
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (TextUtils.isEmpty(bean.href) || TextUtils.isEmpty(bean.img)) {
                notNewVersionDlgShow();
            } else {
                rl_splsah_logo.setVisibility(View.GONE);
                rl_splsah_ad.setVisibility(View.VISIBLE);
                timeHandler.post(mRunnable);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isOne) {
            isOne = false;
            notNewVersionDlgShow();
        }
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