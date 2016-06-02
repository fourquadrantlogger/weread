package com.fengwo.reading.alarmclock;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.SplashActivity;

public class AlarmNotificationManager {

    private static NotificationManager notificationManager;

    /*
     * 显示状态栏通知图标
     */
    public static void showNotification(Context context, Alarm alarm) {
        if (alarm == null) {
            return;
        }
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();
        // 设置图标
        notification.icon = R.drawable.youshu;
        // notification.when = 0;
        // 表明在点击了通知栏中的"清除通知"后，此通知不清除， 经常与FLAG_ONGOING_EVENT一起使用
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // 将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        String title = context.getResources().getString(R.string.app_name);
        String str = alarm.label;
        notification.setLatestEventInfo(context, title, str, pi);
        notificationManager.notify(alarm.id, notification);

        System.out.println("showNotification=========" + alarm.id);
    }

    /*
     * 取消状态栏通知图标
     */
    public static void cancelNotification(Context context) {
        // notificationManager = (NotificationManager)
        // context.getSystemService(context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

}