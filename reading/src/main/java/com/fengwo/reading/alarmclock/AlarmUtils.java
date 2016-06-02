package com.fengwo.reading.alarmclock;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;

public class AlarmUtils {

	public static String getRepeatTime(String repeatTime, int type) {
		if (TextUtils.isEmpty(repeatTime)) {
			return "";
		}
		if (repeatTime.equals("0,1,2,3,4,5,6")) {
			return "每天";
		}
		if (repeatTime.equals("0,1,2,3,4")) {
			return "工作日";
		}
		String[] strings = repeatTime.split(",");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			switch (strings[i]) {
			case "0":
				if (type == 0) {
					builder.append("星期一 ");
				} else if (type == 1) {
					builder.append("周一 ");
				}
				break;
			case "1":
				if (type == 0) {
					builder.append("星期二 ");
				} else if (type == 1) {
					builder.append("周二 ");
				}
				break;
			case "2":
				if (type == 0) {
					builder.append("星期三 ");
				} else if (type == 1) {
					builder.append("周三 ");
				}
				break;
			case "3":
				if (type == 0) {
					builder.append("星期四 ");
				} else if (type == 1) {
					builder.append("周四 ");
				}
				break;
			case "4":
				if (type == 0) {
					builder.append("星期五 ");
				} else if (type == 1) {
					builder.append("周五 ");
				}
				break;
			case "5":
				if (type == 0) {
					builder.append("星期六 ");
				} else if (type == 1) {
					builder.append("周六 ");
				}
				break;
			case "6":
				if (type == 0) {
					builder.append("星期天 ");
				} else if (type == 1) {
					builder.append("周日  ");
				}
				break;

			default:
				break;
			}
		}
		return builder.toString().substring(0, builder.toString().length() - 1);
	}

	public static String getNameByPath(String path) {
		String name = "";
		try {
			String temp[] = path.split("/");
			name = temp[temp.length - 1].split("\\.")[0];
		} catch (Exception e) {
		}
		return name;
	}

	public static MediaPlayer mp;

	public static void showMusic(Context context, Alarm alarm) {
		closeMusic();
		if (alarm == null) {
			mp = MediaPlayer
					.create(context, Uri.parse(getDefaultbell(context)));
		} else {
			mp = MediaPlayer.create(context, Uri.parse(alarm.bell));
		}
		if (mp != null) {
			mp.setLooping(true); // 设置循环播放音乐
			mp.start(); // 开始播放音乐
			handler.sendEmptyMessageDelayed(0, 5000);
		}
	}

	private static Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			closeMusic();
		};
	};

	public static void closeMusic() {
		if (mp != null && mp.isPlaying()) {
			mp.stop();
			mp.reset();
			mp.release();
			mp = null;
		}
	}

	public static String getDefaultbell(Context context) {
		String ret = "";
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null,
				null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				ret = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
			}
			cursor.close();
		}
		return ret;
	}

}
