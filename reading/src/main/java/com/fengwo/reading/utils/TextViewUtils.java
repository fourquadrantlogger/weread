package com.fengwo.reading.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.fengwo.reading.R;
import com.fengwo.reading.main.discover.hottopics.TopicsActivity;

/**
 * TextView变色设置、点击事件
 * 
 * @author Luo Sheng
 * @date 2016-3-2
 * 
 */
public class TextViewUtils extends ClickableSpan {

	String string;
	Context context;

	public TextViewUtils(Context context, String str) {
		super();
		this.string = str;
		this.context = context;
	}

	/**
	 * 特殊字符需要改变的颜色
	 */
	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(context.getResources().getColor(R.color.green_17));
	}

	/**
	 * 特殊字符的点击事件
	 */
	@Override
	public void onClick(View widget) {
		// 跳转 话题详情
		Intent intent = new Intent(context, TopicsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("name", string);
		intent.putExtras(bundle);
		context.startActivity(intent);
		((Activity) context).overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	// #话题内容# 为主色,其余为32 截取#与#中字符正则表达式: #([^\\#|.]+)#

	// public static SpannableStringBuilder getTextView111(Context context,
	// String str1, String str2, String str3) {
	// StringTokenizer token = new StringTokenizer(str1, str3);
	// int i = 0;
	// while (token.hasMoreTokens()) {
	// if (str2.equals(token.nextToken() + "")) {
	// i++;
	// }
	// }
	// SpannableStringBuilder style = new SpannableStringBuilder(str1);
	// int index[] = new int[i];
	// for (int j = 0; j < i; j++) {
	// if (j == 0) {
	// index[j] = str1.indexOf(str2);
	// } else {
	// index[j] = str1.indexOf(str2, index[j - 1] + str2.length());
	// }
	// style.setSpan(new ForegroundColorSpan(context.getResources()
	// .getColor(R.color.green_36)), index[j] - 1,
	// index[j] + str2.length() + 1,
	// Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
	// }
	// return style;
	// }

}
