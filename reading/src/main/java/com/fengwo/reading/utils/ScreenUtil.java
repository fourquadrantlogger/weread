package com.fengwo.reading.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

/**
 * dp、sp 转换为 px 的工具类
 * 
 * @author fxsky 2012.11.12
 *
 */ 
public class ScreenUtil {
	
    /**
     * 
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * 
     */ 
    public static int px2dip(Context context, float pxValue) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (pxValue / scale + 0.5f); 
    } 
   
    /**
     * 
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * 
     */ 
    public static int dip2px(Context context, float dipValue) { 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int) (dipValue * scale + 0.5f); 
    } 
   
    /**
     * 
     * 将px值转换为sp值，保证文字大小不变
     * 
     */ 
    public static int px2sp(Context context, float pxValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (pxValue / fontScale + 0.5f); 
    } 
   
    /**
     * 
     * 将sp值转换为px值，保证文字大小不变
     * 
     */ 
    public static int sp2px(Context context, float spValue) { 
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
        return (int) (spValue * fontScale + 0.5f); 
    }
    
    /**
     * 
     * 获取屏幕的宽度
     * 
     */
    public static int getWidthPixels(Context context){
    	DisplayMetrics dm = new DisplayMetrics();
    	((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    	return dm.widthPixels;
    }
    
    /**
     * 
     * 获取屏幕的高度
     * 
     */
    public static int getHeightPixels(Context context){
    	DisplayMetrics dm = new DisplayMetrics();
    	((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    	return dm.heightPixels;
    }
    
    /**
     * 
	 * 获取 android 通知栏高度
	 * 
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}
    
}