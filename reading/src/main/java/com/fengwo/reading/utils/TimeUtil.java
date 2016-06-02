package com.fengwo.reading.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * (有毫秒的,有秒的)
 * 
 * @author lxq
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	/**
	 * (秒)的
	 */
	public static String getTimeMsgMiao(String time) {
		String result = getTimeChaMiao(time);
		if (result.equals("")) {
			return result;
		}
		String[] strs = result.split("/");
		if (strs[0].equals("0")) {
			if (strs[1].equals("0")) {
				if (strs[2].equals("0")) {
					result = "刚刚";
				} else {
					result = strs[2] + "分钟前";
				}
			} else {
				result = strs[1] + "小时前";
			}
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
			long l = Long.valueOf(time);
			result = sdf.format(new Date(l * 1000));
		}
		return result;
	}

	/**
	 * 将秒转换成日期
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeMsgOnlyDate(String time) {
		String result = getTimeCha(time);
		if (result.equals("")) {
			return result;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long l = Long.valueOf(time);
		result = sdf.format(new Date(l * 1000));

		return result;
	}

	/**
	 * 将秒转换成日期,中间无分隔符
	 * 
	 * @param time
	 * @return
	 */
	public static int getTimeMsgOnlyDateNoGang(String time) {
		String result = getTimeCha(time);
		if (result.equals("")) {
			return 0;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		long l = Long.valueOf(time);
		result = sdf.format(new Date(l * 1000));

		return Integer.parseInt(result);
	}

	/**
	 * 计算时间差
	 */
	private static String getTimeChaMiao(String time) {
		String result = "";

		Date date = new Date(System.currentTimeMillis() / 1000
				- Long.valueOf(time));
		long diff = date.getTime();

		long days = diff / (60 * 60 * 24);
		long hours = (diff - days * (60 * 60 * 24)) / (60 * 60);
		long minutes = (diff - days * (60 * 60 * 24) - hours * (60 * 60)) / 60;
		result = "" + days + "/" + hours + "/" + minutes;
		return result;
	}

	/**
	 * 字符串转成时间戳(秒)
	 */
	private static String getTimeMiao(String time) {
		String re_time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = sdf.parse(time);
			long l = date.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}

	/**
	 * 字符串日期转成时间戳(秒)
	 */
	public static String getDateMiao(String time) {
		String re_time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(time);
			long l = date.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}

	/**
	 * 字符串转日期成时间戳(秒)没有中间分隔符
	 */
	public static String getDateMiaoNoGang(String time) {
		String re_time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(time);
			long l = date.getTime();
			String str = String.valueOf(l);
			re_time = str.substring(0, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re_time;
	}

	/**
	 * 时间戳转成字符串(秒)
	 */
	public static String getStrTimeMiao(String time) {
		String re_time = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long l = Long.valueOf(time);
		re_time = sdf.format(new Date(l * 1000));
		return re_time;
	}

	/**
	 * 1.标准格式化时间 1.与当前时间差小于60秒，显示"刚刚" 2.与当前时间差小于60分钟，则显示"x分钟前"
	 * 3.与当前时间差小于24小时，则显示"x小时前" 4.与当前时间差大于等于24小时，则显示"年-月-日 时:分"
	 * 
	 * 传入时间戳，按上述要求格式化时间输出，这个方法是对外调用的方法 显示"年-月-日 时:分"
	 * 
	 * (毫秒)
	 */
	public static String getTimeMsg(String time) {
		String result = getTimeCha(time);
		if (result.equals("")) {
			return result;
		}
		String[] strs = result.split("/");
		if (strs[0].equals("0")) {
			if (strs[1].equals("0")) {
				if (strs[2].equals("0")) {
					result = "刚刚";
				} else {
					result = strs[2] + "分钟前";
				}
			} else {
				result = strs[1] + "小时前";
			}
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			long lcc_time = Long.valueOf(time);
			result = sdf.format(new Date(lcc_time));
		}
		return result;
	}

	/**
	 * 一天内显示时或者分 超过一天显示年月日
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeMsgNoneMM(String time) {
		if (TextUtils.isEmpty(time)) {
			return "";
		}
		Date curDate = new Date();
		long diff = curDate.getTime() - Long.parseLong(time);// 这样得到的差值是微秒级别

		long days = diff / (1000 * 60 * 60 * 24);
		long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
				* (1000 * 60 * 60))
				/ (1000 * 60);
		String result = days + "/" + hours + "/" + minutes;
		String[] strs = result.split("/");
		if (strs[0].equals("0")) {
			if (strs[1].equals("0")) {
				if (strs[2].equals("0")) {
					result = "刚刚";
				} else {
					result = strs[2] + "分钟前";
				}
			} else {
				result = strs[1] + "小时前";
			}
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			long lcc_time = Long.valueOf(time);
			result = format.format(new Date(lcc_time));
		}
		return result;
	}

	/**
	 * 本类内使用，外部用的时候改为public 传入时间戳,显示"年-月-日 时:分:秒"
	 */
	private static String getStrTime(String time) {
		String strTime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long longTime = Long.valueOf(time);
		strTime = sdf.format(new Date(longTime));
		return strTime;
	}

	/**
	 * 本类内使用，外部用的时候改为public 计算时间差
	 */
	public static String getTimeCha(String time) {
		String result = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			// SimpleDateFormat format = new SimpleDateFormat(
			// "yyyy-MM-dd HH:mm:ss");
			// Date curDate = new Date(System.currentTimeMillis());
			Date curDate = new Date();
			Date data = format.parse(getStrTime(time + "000"));
			long diff = curDate.getTime() - data.getTime();// 这样得到的差值是微秒级别

			long days = diff / (1000 * 60 * 60 * 24);
			long hours = (diff - days * (1000 * 60 * 60 * 24))
					/ (1000 * 60 * 60);
			long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
					* (1000 * 60 * 60))
					/ (1000 * 60);
			if (days > 0) {
				result = days + "天前";
			} else {
				if (hours > 0) {
					result = hours + "小时前";
				} else {
					if (minutes > 0) {

						result = minutes + "分钟前";
					} else {
						result = "刚刚";
					}
				}
			}
			// result = "" + days + "/" + hours + "/" + minutes;
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 现实时间 一天内显示时分,超过一天显示月日时分
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeMsg(long time) {
		String result = getTimeCha("" + time);
		if (result.equals("")) {
			return result;
		}
		String[] strs = result.split("/");
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if (strs[0].equals("0")
				&& (Integer.parseInt("" + c.get(Calendar.HOUR_OF_DAY)
						+ c.get(Calendar.MINUTE))
						- Integer.parseInt(strs[1] + strs[2]) >= 0)) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			long lcc_time = Long.valueOf(time);
			result = sdf.format(new Date(lcc_time));
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
			long lcc_time = Long.valueOf(time);
			result = sdf.format(new Date(lcc_time));
		}
		return result;
	}

	public static String getDaysByMiao(String times) {
		return (int) (Math.ceil(Double.parseDouble(times) / 3600 / 24)) + "";
	}
	
	public static String getReadTime(String time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String week = "?";
		try {
			Date date = format.parse(time);
			calendar.setTime(date);
			switch (calendar.get(Calendar.DAY_OF_WEEK)) {
			case 1:
				week = "日";
				break;
			case 2:
				week = "一";
				break;
			case 3:
				week = "二";
				break;
			case 4:
				week = "三";
				break;
			case 5:
				week = "四";
				break;
			case 6:
				week = "五";
				break;
			case 7:
				week = "六";
				break;

			default:
				break;
			}
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return week;
	}
	

}