package com.fengwo.reading.utils;

import android.content.Intent;

import com.fengwo.reading.activity.BaseActivity;
import com.fengwo.reading.activity.EditTextActivity;
import com.fengwo.reading.activity.HotActivity;
import com.fengwo.reading.activity.LoginActivity;
import com.fengwo.reading.activity.MainActivity;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.activity.SetupActivity;
import com.fengwo.reading.activity.SplashActivity;
import com.fengwo.reading.activity.UpdateActivity;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.application.MyApplication;
import com.fengwo.reading.main.discover.hottopics.TopicsActivity;
import com.fengwo.reading.main.my.SettingForWithYouShu;
import com.fengwo.reading.main.my.WithYouShuAgreement;
import com.fengwo.reading.player.PlayerService;
import com.fengwo.reading.zxing.activity.CaptureActivity;

public class ActivityUtil {

    public static MainActivity mainActivity = null;
    public static NextActivity nextActivity = null;
    public static LoginActivity loginActivity = null;
    public static EditTextActivity editTextActivity = null;
    public static HotActivity hotActivity = null;
    public static SetupActivity setupActivity = null;
    public static SplashActivity splashActivity = null;
    public static TopicsActivity topicsActivity = null;
    public static UpgradeActivity upgradeActivity = null;
    public static UpdateActivity updateActivity = null;
    public static CaptureActivity captureActivity = null;
    public static BaseActivity baseActivity = null;
    public static SettingForWithYouShu settingForWithYouShu = null;
    public static WithYouShuAgreement withYouShuAgreement = null;

    /**
     * 清除所有Activity
     */
    public static void exit() {
        Intent intent = new Intent(MyApplication.getContext(), PlayerService.class);
        MyApplication.getContext().stopService(intent);
        if (mainActivity != null && !mainActivity.isFinishing()) {
            mainActivity.finish();
        }
        if (nextActivity != null && !nextActivity.isFinishing()) {
            nextActivity.finish();
        }
        if (loginActivity != null && !loginActivity.isFinishing()) {
            loginActivity.finish();
        }
        if (editTextActivity != null && !editTextActivity.isFinishing()) {
            editTextActivity.finish();
        }
        if (hotActivity != null && !hotActivity.isFinishing()) {
            hotActivity.finish();
        }
        if (setupActivity != null && !setupActivity.isFinishing()) {
            setupActivity.finish();
        }
        if (splashActivity != null && !splashActivity.isFinishing()) {
            splashActivity.finish();
        }
        if (topicsActivity != null && !topicsActivity.isFinishing()) {
            topicsActivity.finish();
        }
        if (upgradeActivity != null && !upgradeActivity.isFinishing()) {
            upgradeActivity.finish();
        }
        if (updateActivity != null && !updateActivity.isFinishing()) {
            updateActivity.finish();
        }
        if (captureActivity != null && !captureActivity.isFinishing()) {
            captureActivity.finish();
        }
        if (settingForWithYouShu!= null && !settingForWithYouShu.isFinishing()){
            settingForWithYouShu.finish();
        }
        if (withYouShuAgreement !=null && !withYouShuAgreement.isFinishing()){
            withYouShuAgreement.finish();
        }
        if (baseActivity !=null &&!baseActivity.isFinishing()){
            baseActivity.finish();
        }

    }

    /**
     * 清除所有 Service
     */
    public static void stopService() {
        Intent intent = new Intent(MyApplication.getContext(), PlayerService.class);
        MyApplication.getContext().stopService(intent);

    }

}
