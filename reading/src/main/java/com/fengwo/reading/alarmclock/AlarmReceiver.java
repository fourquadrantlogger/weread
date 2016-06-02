package com.fengwo.reading.alarmclock;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // 开机
            // Intent service = new Intent(context, AlarmService.class);
            // context.startService(service);
            AlarmClockManager.setNextAlarm(context);
        } else {
            // 转到闹铃界面
            // Intent deal = new Intent(context, AlarmDealActivity.class);
            // deal.putExtra(Alarm.Columns._ID,
            // intent.getIntExtra(Alarm.Columns._ID, 0));
            // deal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//必须增加，否则报错
            // context.startActivity(deal);

            int id = intent.getIntExtra(Alarm.Columns._ID, 0);
            if (id != 0) {
                // 根据ID获得闹钟的详细信息
                Alarm alarm = AlarmHandle.getAlarm(context, id);
                // 通知
                if (alarm == null) {
                    return;
                }
                AlarmNotificationManager.showNotification(context, alarm);
                AlarmUtils.showMusic(context, alarm);
                // if
                // (BackgroundUtils.isApplicationBroughtToBackground(context)) {
                // 音乐
                // AlarmUtils.showMusic(context, alarm);
                // }
                // 刷新数据
                long timeMillis = AlarmClockManager.time2Millis(alarm.hour,
                        alarm.minutes, alarm.repeat);
                // 将下次响铃时间的毫秒数存到数据库
                ContentValues values = new ContentValues();
                values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
                AlarmHandle.updateAlarm(context, values, alarm.id);
                // 设置下一个有效的闹铃
                AlarmClockManager.setNextAlarm(context);
            }
        }
    }
}