package com.fengwo.reading.main.discover;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fengwo.reading.myinterface.GlobalParams;

/**
 * 往期书单特权解锁
 */
public class PrivilegeService extends Service {

    public static final String ACTION = "com.fengwo.reading.main.discover.PrivilegeService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //服务第一次创建的时候调用
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //服务销毁的时候调用
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
        //每次服务启动都会调用
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if ((System.currentTimeMillis() / 1000) - GlobalParams.time >= 300) {
            PrivilegeFragment.getInstance().refresh1();
        } else {
            new PollingThread().start();
        }
    }

    int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            count++;
            //当计数能被2整除时(秒)
            if (count % 2 == 0) {
                PrivilegeFragment.getInstance().refresh();
            }
        }
    }
}