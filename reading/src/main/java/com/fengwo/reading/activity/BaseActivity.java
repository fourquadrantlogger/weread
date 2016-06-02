package com.fengwo.reading.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 基类
 */
public class BaseActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try{
            super.onCreate(savedInstanceState);
            /*if (GlobalParams.uid==null||GlobalParams.uid==""){
                GlobalParams.uid = SPUtils.getUserId(BaseActivity.this);
                GlobalParams.userInfoBean = NOsqlUtil.get_userInfoBean();
            }*/
        }catch (Exception e){
           /* Intent k = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);*/
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, intent,
                    0);
            //退出程序
            AlarmManager mgr = (AlarmManager)getApplication().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
                    restartIntent); // 1秒钟后重启应用
            System.exit(0);
//            ActivityUtil.exit();
        }
	}

    @Override
    protected void onStart() {

        /*try{

        }catch (NullPointerException e){
           *//* //        if (savedInstanceState==null){
            Intent i = getPackageManager()
                    .getLaunchIntentForPackage(getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);
//        }
//        Intent intent = new Intent(this, LoginActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, i,0
            );
            //退出程序
            AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
                    restartIntent); // 1秒钟后重启应用
            System.exit(0);
//            startActivity(i);*//*
        }
        MLog.v("reading", getClass().getName() + ":onStart");
        SPUtils.getUserId(BaseActivity.this);*/

        try{
            super.onStart();
           /* if (GlobalParams.uid==null||GlobalParams.uid==""){
                GlobalParams.uid = SPUtils.getUserId(BaseActivity.this);
                GlobalParams.userInfoBean = NOsqlUtil.get_userInfoBean();
            }*/
        }catch (Exception e){
            /*Intent k = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);*/
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, intent,
                    0);
            //退出程序
            AlarmManager mgr = (AlarmManager)getApplication().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
                    restartIntent); // 1秒钟后重启应用
            System.exit(0);
//            ActivityUtil.exit();
        }
    }

    @Override
    protected void onResume() {

        /*try{
            super.onResume();
        }catch(Exception e){
//            String packageName = getPackageInfo(getBaseContext()).packageName;
//            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(packageName);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getBaseContext().startActivity(intent);
//            System.exit(0);
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        MobclickAgent.onResume(this);
        MLog.v("reading", getClass().getName() + ":onResume");*/

        try{
            super.onResume();
            /*if (GlobalParams.uid==null||GlobalParams.uid==""){
                GlobalParams.uid = SPUtils.getUserId(BaseActivity.this);
                GlobalParams.userInfoBean = NOsqlUtil.get_userInfoBean();
            }*/
        }catch (Exception e){
           /* Intent k = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);*/

            /*application = (CatchExcep)getApplication();
            application.init();
            application.addActivity(this);*/

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, intent,
                    0);
            //退出程序
            AlarmManager mgr = (AlarmManager)getApplication().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
                    restartIntent); // 1秒钟后重启应用
            System.exit(0);
//            ActivityUtil.exit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MLog.v("reading", getClass().getName() + ":onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        MLog.v("reading", getClass().getName() + ":onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.v("reading", getClass().getName() + ":onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("uid",GlobalParams.uid);
        SPUtils.setUserId(BaseActivity.this, GlobalParams.uid);
        NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
        MLog.v("reading", getClass().getName() + ":onSaveInstanceState");

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
////        if (savedInstanceState==null){
//            Intent i = getPackageManager()
//                    .getLaunchIntentForPackage(getPackageName());
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            startActivity(i);
////        }
////        Intent intent = new Intent(this, LoginActivity.class);
//        PendingIntent restartIntent = PendingIntent.getActivity(
//                getApplicationContext(), 0, i,0
//              );
//        //退出程序
//        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
//                restartIntent); // 1秒钟后重启应用
//        System.exit(0);
//        MLog.v("reading", getClass().getName() + ":onRestoreInstanceState");
//
        try{
            /*if (GlobalParams.uid==null||GlobalParams.uid==""){
                GlobalParams.uid = SPUtils.getUserId(BaseActivity.this);
                GlobalParams.userInfoBean = NOsqlUtil.get_userInfoBean();
            }*/

        }catch (Exception e){
            /*Intent k = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            k.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(k);*/
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, intent,
                    0);
            //退出程序
            AlarmManager mgr = (AlarmManager)getApplication().getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
                    restartIntent); // 1秒钟后重启应用
           System.exit(0);
//            ActivityUtil.exit();
        }
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



   /* public class UnCeHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler mDefaultHandler;
        public static final String TAG = "CatchExcep";
        CatchExcep application;

        public UnCeHandler(CatchExcep application){
            //获取系统默认的UncaughtException处理器
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            this.application = application;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if(!handleException(ex) && mDefaultHandler != null){
                //如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, ex);
            }else{
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    Log.e(TAG, "error : ", e);
                }
                Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
                PendingIntent restartIntent = PendingIntent.getActivity(
                        application.getApplicationContext(), 0, intent,
                        0);
                //退出程序
                AlarmManager mgr = (AlarmManager)application.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                        restartIntent); // 1秒钟后重启应用
                application.finishActivity();
            }
        }

        *//**
         * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
         *
         * @param ex
         * @return true:如果处理了该异常信息;否则返回false.
         *//*
        private boolean handleException(Throwable ex) {
            if (ex == null) {
                return false;
            }
            //使用Toast来显示异常信息
            new Thread(){
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(application.getApplicationContext(), "很抱歉,程序出现异常,即将退出.",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }.start();
            return true;
        }
    }



    public class CatchExcep extends Application {

        ArrayList<Activity> list = new ArrayList<Activity>();

        public void init(){
            //设置该CrashHandler为程序的默认处理器
            UnCeHandler catchExcep = new UnCeHandler(this);
            Thread.setDefaultUncaughtExceptionHandler(catchExcep);
        }

        *//**
         * Activity关闭时，删除Activity列表中的Activity对象*//*
        public void removeActivity(Activity a){
            list.remove(a);
        }

        *//**
         * 向Activity列表中添加Activity对象*//*
        public void addActivity(Activity a){
            list.add(a);
        }

        *//**
         * 关闭Activity列表中的所有Activity*//*
        public void finishActivity(){
            for (Activity activity : list) {
                if (null != activity) {
                    activity.finish();
                }
            }
            //杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }*/
}

