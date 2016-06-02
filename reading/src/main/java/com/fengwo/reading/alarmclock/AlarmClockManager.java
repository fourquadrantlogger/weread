package com.fengwo.reading.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmClockManager {

    // 日历
    private static Calendar calendar = Calendar.getInstance();
    // 闹铃管理
    private static AlarmManager alarmManager;

    /**
     * 设置提示信息
     *
     * @param context 上下文
     * @param hour    小时
     * @param minute  分钟
     */
    public static void setAlarm(Context context, Alarm alarm, boolean b) {
        long timeMillis = time2Millis(alarm.hour, alarm.minutes, alarm.repeat);
        // 将下次响铃时间的毫秒数存到数据库
        ContentValues values = new ContentValues();
        values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
        AlarmHandle.updateAlarm(context, values, alarm.id);
        if (b) {
            Toast.makeText(context, fomatTip(timeMillis), Toast.LENGTH_SHORT)
                    .show();
        }
        // 设置闹钟
        setNextAlarm(context);
    }

    /**
     * 设置闹钟
     *
     * @param context 上下文
     */
    public static void setNextAlarm(Context context) {
        Alarm alarm = AlarmHandle.getNextAlarm(context);
        if (alarm != null) {
            Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
            intent.putExtra(Alarm.Columns._ID, alarm.id);
            PendingIntent pi = PendingIntent.getBroadcast(context, alarm.id,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.nextMillis, pi);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),5000,pi);
            // Intent intent = new Intent(context,
            // AlarmDealActivity.class); // 创建一个Intent对象
            // PendingIntent pendingIntent = PendingIntent.getActivity(
            // context, 0, intent, 0); // 获取显示闹钟的PendingIntent对象
            // alarmManager = (AlarmManager)
            // context.getSystemService(Context.ALARM_SERVICE); //
            // 获取AlarmManager对象
            // alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.nextMillis,
            // pendingIntent); // 设置一个闹钟

            // 显示通知
            // AlarmNotificationManager.showNotification(context, alarm);
        } else {
            AlarmNotificationManager.cancelNotification(context);
        }
    }

    public static void cancelAlarm(Context context, int id) {
        Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
        setNextAlarm(context);
    }

    private static String fomatTip(long timeMillis) {
        long delta = timeMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" : days + "天";

        String hourSeq = (hours == 0) ? "" : hours + "小时";

        String minSeq = (minutes == 0) ? "1分钟" : minutes + "分钟";

        return "已将闹钟设置为从现在起" + daySeq + hourSeq + minSeq + "后提醒";
    }

    public static Long time2Millis(int hour, int minute, String repeat) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // 闹钟重复模式为 只响一次或每天

        if (repeat.equals("0,1,2,3,4,5,6")) {

            // 若时间已经过去，则推迟一天
            if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
                System.out.println("====过时延迟一天");
                calendar.roll(Calendar.DATE, 1);
            } else {
                System.out.println("====没有延迟，响铃");
            }
        } else if (repeat.equals("0,1,2,3,4")) {
            // 闹钟重复模式为 周一到周五
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
                // 周五若时间已经过去，则推迟3天
                if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
                    calendar.roll(Calendar.DATE, 3);
                }
            } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                // 周六
                calendar.roll(Calendar.DATE, 2);
            } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                // 周日
                calendar.roll(Calendar.DATE, 1);
            } else {
                // 若时间已经过去，则推迟一天
                if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
                    System.out.println("过时延迟一天");
                    calendar.roll(Calendar.DATE, 1);
                }
            }
        } else {
            // if (repeat.length() == 1) {
            // // 若时间已经过去，则推迟一天
            // if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0)
            // {
            // System.out.println("过时延迟七天");
            // calendar.roll(Calendar.DATE, 7);
            // }
            // } else {
            // if (repeat.contains(getDayOfWeek())) {
            //
            // }
            // if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0)
            // {
            // // System.out.println("过时延迟七天");
            // calendar.roll(Calendar.DATE, 1);
            // }
            // }
            int nDays = 0;
            int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
            dayWeek = (dayWeek - 1) == 0 ? 7 : (dayWeek - 1);
            String[] repeatArr = repeat.split(",");

            int flag = 0;
            for (int i = 0; i < repeatArr.length; i++) {
                if (dayWeek < Integer.valueOf(repeatArr[i]) + 1) {
                    flag = 1;
                    nDays = Integer.valueOf(repeatArr[i]) + 1 - dayWeek;
                    break;
                }
                if (dayWeek == Integer.valueOf(repeatArr[i]) + 1) {
                    if (calendar.getTimeInMillis() - System.currentTimeMillis() <= 0) {
                        continue;
                    }
                    flag = 1;
                    nDays = 0;
                    break;
                }
            }
            if (flag == 0) {
                nDays = 7 - dayWeek + Integer.valueOf(repeatArr[0]) + 1;
            }
            if (nDays != 0) {
                calendar.roll(Calendar.DATE, nDays);
            }
        }
        return calendar.getTimeInMillis();
    }

    private static String getDayOfWeek() {
        String string = "0";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                string = "6";
                break;
            case 2:
                string = "0";
                break;
            case 3:
                string = "1";
                break;
            case 4:
                string = "2";
                break;
            case 5:
                string = "3";
                break;
            case 6:
                string = "4";
                break;
            case 7:
                string = "5";
                break;
            default:
                break;
        }
        return string;
    }

}