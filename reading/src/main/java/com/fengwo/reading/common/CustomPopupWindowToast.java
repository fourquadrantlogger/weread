package com.fengwo.reading.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.fengwo.reading.R;

public class CustomPopupWindowToast {

	private PopupWindow mPopWindow;
	private View mView;

	private boolean is_show = false;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (is_show && mPopWindow != null && mPopWindow.isShowing()) {
				mPopWindow.dismiss();
			}
		};
	};

	public CustomPopupWindowToast(Context context, View view, int imgId,
			String text) {
		super();
		
		if (mPopWindow != null && mPopWindow.isShowing()) {
			mPopWindow.dismiss();
		}
		mView = view;
		
		View viewLayout = LayoutInflater.from(context).inflate(
				R.layout.layout_toast_popupwindow, null);

		mPopWindow = new PopupWindow(viewLayout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mPopWindow.setOutsideTouchable(true);
		mPopWindow.setAnimationStyle(android.R.style.Animation_Dialog);

		mPopWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				is_show = false;
			}
		});

		ImageView imageView = (ImageView) viewLayout
				.findViewById(R.id.iv_toast_img);
		TextView textView = (TextView) viewLayout.findViewById(R.id.tv_toast_txt);
		imageView.setImageResource(imgId);
		textView.setText(text);
	}

	public void show() {
		is_show = true;
		mPopWindow.showAtLocation(mView, Gravity.CENTER, 0, 0);
		handler.sendEmptyMessageDelayed(0, 1000);
	}

}
