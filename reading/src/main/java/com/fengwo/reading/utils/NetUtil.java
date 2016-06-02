package com.fengwo.reading.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetUtil {
	/**
	 * 检查网络
	 */
	public static boolean checkNet(Context context) {
		// 判断是否使用WIFI通信
		// 判断是否使用基站通信

		// 如果都没有使用，提示用户无网络

		// 如果用户在使用基站通信
		// WAP 还是 NET
		// WAP 需要在代码中设置代理数据：代理ip和端口

		boolean isWifi = isWifiConnection(context);
		boolean isMobile = isMobileConnection(context);

		if (!isWifi && !isMobile) {
			return false;
		}

//		if (isMobile) {
//			// 获取代理ip
//			String ip = android.net.Proxy.getDefaultHost();
//			if (StringUtils.isNotBlank(ip)) {
//				// 用户在使用WAP方式通信
//				return true;
//			}
//		}
		return true;

	}

	/**
	 * 判断是否使用基站通信
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isMobileConnection(Context content) {
		ConnectivityManager manager = (ConnectivityManager) content
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取Mobile连接的描述信息
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 判断是否使用WIFI通信
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isWifiConnection(Context content) {
		ConnectivityManager manager = (ConnectivityManager) content
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取WIFI连接的描述信息
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 根据框架自身判断网络是什么样的问题---依据的是String msg
	 */
	public static boolean dealWithFailHttpException(Context context, String msg) {
		// 没有网络时
		if (msg.startsWith("java.net.UnknownHostException:")) {
			if (context != null) {
				Toast.makeText(context, "加载失败，请检查网络", Toast.LENGTH_SHORT)
						.show();
			}
			return true;
		}
		// 请求的网络连接地址有误时
		if (msg.startsWith("java.io.IOException:")) {
			if (context != null) {
				Toast.makeText(context, "无法请求有书数据，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			}
			return true;
		}
		// 服务器崩溃，返回的数据有误时
		if (msg.startsWith("<!DOCTYPE html>")) {
			if (context != null) {
				Toast.makeText(context, "返回数据有误，请稍后再试", Toast.LENGTH_SHORT)
						.show();
			}
			return true;
		}
		return false;
	}
	
	/** Network type is unknown */
	private static final int NETWORK_TYPE_UNKNOWN = 0;
	/** Current network is GPRS */
	private static final int NETWORK_TYPE_GPRS = 1;
	/** Current network is EDGE */
	private static final int NETWORK_TYPE_EDGE = 2;
	/** Current network is UMTS */
	private static final int NETWORK_TYPE_UMTS = 3;
	/** Current network is CDMA: Either IS95A or IS95B */
	private static final int NETWORK_TYPE_CDMA = 4;
	/** Current network is EVDO revision 0 */
	private static final int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A */
	private static final int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is 1xRTT */
	private static final int NETWORK_TYPE_1xRTT = 7;
	/** Current network is HSDPA */
	private static final int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	private static final int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	private static final int NETWORK_TYPE_HSPA = 10;
	/** Current network is iDen */
	private static final int NETWORK_TYPE_IDEN = 11;
	/** Current network is EVDO revision B */
	private static final int NETWORK_TYPE_EVDO_B = 12;
	/** Current network is LTE */
	private static final int NETWORK_TYPE_LTE = 13;
	/** Current network is eHRPD */
	private static final int NETWORK_TYPE_EHRPD = 14;
	/** Current network is HSPA+ */
	private static final int NETWORK_TYPE_HSPAP = 15;
	/** Current network is GSM {@hide} */
	private static final int NETWORK_TYPE_GSM = 16;
	/** Current network is TD_SCDMA {@hide} */
	private static final int NETWORK_TYPE_TD_SCDMA = 17;
	/** Current network is IWLAN {@hide} */
	private static final int NETWORK_TYPE_IWLAN = 18;
	
	public static String getNetworkType(Context context) {
		String type = "no_network";
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null) {
			switch (info.getType()) {
			case ConnectivityManager.TYPE_WIFI:
				type = "wifi";
				break;
			case ConnectivityManager.TYPE_MOBILE:
				switch (info.getSubtype()) {
				case NETWORK_TYPE_GPRS:
				case NETWORK_TYPE_EDGE:
				case NETWORK_TYPE_CDMA:
				case NETWORK_TYPE_1xRTT:
				case NETWORK_TYPE_IDEN:
					type = "2G";
					break;
				case NETWORK_TYPE_UMTS:
				case NETWORK_TYPE_EVDO_0:
				case NETWORK_TYPE_EVDO_A:
				case NETWORK_TYPE_HSDPA:
				case NETWORK_TYPE_HSUPA:
				case NETWORK_TYPE_HSPA:
				case NETWORK_TYPE_EVDO_B:
				case NETWORK_TYPE_EHRPD:
				case NETWORK_TYPE_HSPAP:
					type = "3G";
					break;
				case NETWORK_TYPE_LTE:
					type = "4G";
					break;
					
				default:
					break;
				}
				break;
				
			default:
				break;
			}
		}
		return type;
	}
	
}
