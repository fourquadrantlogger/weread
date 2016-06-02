package com.fengwo.reading.common;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import com.fengwo.reading.R;

public class CustomProgressDialog extends Dialog {

	private static CustomProgressDialog dialog = null;

	public CustomProgressDialog(Context context) {
		super(context);
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static CustomProgressDialog createDialog(Context context) {
		dialog = new CustomProgressDialog(context,
				R.style.CustomProgressDialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.layout_dialog_progress);
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		dialog.setCancelable(false);
		return dialog;
	}

}
