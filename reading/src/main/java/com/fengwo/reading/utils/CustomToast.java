package com.fengwo.reading.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 
 * @author lxq 自定义Toast
 * 
 */
public class CustomToast {

	private static Toast mToast;
	private static Context mContext;

	public static void showToast(Context context, String text) {
		if (mToast != null && mContext != null && mContext == context) {
			mToast.setText(text);
		} else {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		mToast.setGravity(Gravity.CENTER, 0, 0);
		mContext=context;
		mToast.show();
	}

	public static void showToast(Context mContext, int resId) {
		showToast(mContext, mContext.getResources().getString(resId));
	}

}
