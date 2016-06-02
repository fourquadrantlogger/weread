package com.fengwo.reading.common;

import android.app.ProgressDialog;
import android.content.Context;

public class MyProgressDialog extends ProgressDialog {

	public MyProgressDialog(Context context) {
		super(context);
		// setTitle("提示");
		setMessage("正在加载中...");
		setCanceledOnTouchOutside(false);
	}

}
