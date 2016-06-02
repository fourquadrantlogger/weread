package com.fengwo.reading.main.my;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.BaseActivity;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * 关于有书
 * author song
 */
public class SettingForWithYouShu extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_update,rl_setting_server,rl_new_version_num;
    private TextView tv_now_version_num,tv_new_version_num;

    private ImageView iv_title_left,iv_new_version_num;
    private TextView tv_title_mid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_for_with_you_shu);
        init();
    }
    //初始化
    private void init() {
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        rl_setting_server = (RelativeLayout) findViewById(R.id.rl_setting_server);
        tv_now_version_num = (TextView) findViewById(R.id.tv_now_version_num);
        tv_new_version_num = (TextView) findViewById(R.id.tv_new_version_num);

        iv_title_left = (ImageView) findViewById(R.id.iv_return);
        tv_title_mid = (TextView) findViewById(R.id.tv_title_mid);
        rl_new_version_num = (RelativeLayout) findViewById(R.id.rl_new_version_num);
        iv_new_version_num = (ImageView) findViewById(R.id.iv_new_version_num);
        iv_title_left.setVisibility(View.VISIBLE);
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText("关于有书");

        iv_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        tv_now_version_num.setText(getPackageInfo(this).versionName);
        //判断当前版本是否是最新版本
        if ((getPackageInfo(this).versionName).equals(getRecentlyVersionName())){
            tv_new_version_num.setText("最新版");
            iv_new_version_num.setVisibility(View.GONE);
//            rl_update.setClickable(false);
        }else{
            tv_new_version_num.setText("新版本");
            iv_new_version_num.setVisibility(View.VISIBLE);
            DisplayImageUtils.displayImage(null,
                    iv_new_version_num, 100, 0);
//            rl_update.setClickable(true);
        }

        //点击事件
        rl_update.setOnClickListener(this);
        rl_new_version_num.setOnClickListener(this);
        rl_setting_server.setOnClickListener(this);
    }
    //获取最新版本号
    public String getRecentlyVersionName(){
        return "1.1.0";
    }
    //获取当前apk包信息
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_update:
                //下载最新版apk
                if ((getPackageInfo(this).versionName).equals(getRecentlyVersionName())) {
                    //提示已经是最新版本，不执行下载
                    Toast.makeText(SettingForWithYouShu.this, "亲，您已经是最新版本了哦", Toast.LENGTH_SHORT).show();
                }else{
                    //执行下载
                    downLoadApp();
                }
                break;
            case R.id.rl_setting_server:
                //服务使用协议
                Intent intent = new Intent(this,WithYouShuAgreement.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                break;
            case R.id.rl_new_version_num:
                //下载最新版apk
                if ((getPackageInfo(this).versionName).equals(getRecentlyVersionName())) {
                    //提示已经是最新版本，不执行下载
                    Toast.makeText(SettingForWithYouShu.this, "亲，您已经是最新版本了哦", Toast.LENGTH_SHORT).show();
                }else{
                    //执行下载
                    downLoadApp();
                }
                break;
        }
    }

    //下载apk
    public void downLoadApp(){
        HttpUtils http=new HttpUtils();
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/YOUSHU/com.fengwo.reading_041411.apk";
        http.download("http://apk.hiapk.com/appdown/com.fengwo.reading", path,true,true,new RequestCallBack<File>() {
            @Override
            public void onStart() {
                Toast.makeText(SettingForWithYouShu.this, "正在连接...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(ResponseInfo<File> fileResponseInfo) {
                //自安装
                installApk(path);
                Toast.makeText(SettingForWithYouShu.this, "新版本APK已经安装到本地SD卡中", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(HttpException e, String s) {

                Toast.makeText(SettingForWithYouShu.this,"更新失败",Toast.LENGTH_SHORT).show();
            }
        });


    }
    //自安装
    public void installApk(String path){
        Toast.makeText(SettingForWithYouShu.this, "正在连接...", Toast.LENGTH_SHORT).show();
        Intent intent =new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
        startActivity(intent);
    }

}
