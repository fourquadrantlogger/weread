package com.fengwo.reading.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.read.Fragment_WeRead;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.player.PlayerService;
import com.fengwo.reading.umeng.PhoneJson;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 强制更新
 */
public class UpdateActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_update_version, tv_update_version2, tv_update_gengxin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ActivityUtil.updateActivity = this;

        findViewById();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 300);

//        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
//        builder.setCancelable(false);

    }

    private void findViewById() {
        tv_update_version = (TextView) findViewById(R.id.tv_update_version);
        tv_update_version2 = (TextView) findViewById(R.id.tv_update_version2);
        tv_update_gengxin = (TextView) findViewById(R.id.tv_update_gengxin);

        tv_update_gengxin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_update_gengxin:
                Uri uri = Uri.parse("http://android.myapp.com/myapp/detail.htm?apkName=com.fengwo.reading");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
                break;
        }
    }

    /**
     * 我的成就
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.is_pass,
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
                            PhoneJson json = new Gson().fromJson(jsonString,
                                    PhoneJson.class);
                            if ("1".equals(json.code)) {
                                tv_update_version.setText("致歉!");
                                tv_update_version2.setText("尊敬的书友:");
                            } else if ("999".equals(json.code)) {
                                tv_update_version.setText(json.msg);
                                tv_update_version2.setText(json.msg);
                            } else {
                                tv_update_version.setText("致歉!");
                                tv_update_version2.setText("尊敬的书友:");
                            }
                        } catch (Exception e) {
                            tv_update_version.setText("致歉!");
                            tv_update_version2.setText("尊敬的书友:");
                        }
                    }
                }, true, null);
    }

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
        Intent intent = new Intent(this, PlayerService.class);
        stopService(intent);
        stopService(Fragment_WeRead.getInstance().serviceIntent);
        ActivityUtil.exit();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}