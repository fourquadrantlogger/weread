package com.fengwo.reading.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomPopupWindowDialog {

	private RelativeLayout rl_dialog_layout;
	private ImageView iv_dialog_close;
	private ImageView iv_dialog_img;
	private TextView tv_dialog_loading;
	private Dialog dialog;

	public CustomPopupWindowDialog(final Context context, String imgUrl,
			final String url) {
		super();
		dialog = new Dialog(context, R.style.CustomPopupWindowDialog);
		dialog.setContentView(R.layout.layout_dialog_popupwindow);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);

		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.7f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;

		iv_dialog_img = (ImageView) dialog.findViewById(R.id.iv_dialog_img);
		// iv_dialog_img.setAlpha(0.9f);

		// DisplayImageUtils
		// .displayImage(imgUrl, iv_dialog_img, 10, R.drawable.cover);

		tv_dialog_loading = (TextView) dialog
				.findViewById(R.id.tv_dialog_loading);
		tv_dialog_loading.setVisibility(View.VISIBLE);

		// TODO
		final File f = new File(Environment.getExternalStorageDirectory()
				+ "/fengwo_note_reading3",
				(SPUtils.getAppTimeName() + ".jpg"));
		if (SPUtils.getAppTimeFirst()) {
			SPUtils.setAppTimeFirst(false);
			tv_dialog_loading.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage("file://" + f.getPath(),
					iv_dialog_img);
		} else {
			if (f.exists()) {
				tv_dialog_loading.setVisibility(View.GONE);
				ImageLoader.getInstance().displayImage("file://" + f.getPath(),
						iv_dialog_img);
			}else {
				ImageLoader.getInstance().loadImage(imgUrl,
						new SimpleImageLoadingListener() {

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								super.onLoadingComplete(imageUri, view, loadedImage);
								iv_dialog_img.setImageBitmap(loadedImage);
								tv_dialog_loading.setVisibility(View.GONE);
								try {
									FileOutputStream out = new FileOutputStream(f);
									loadedImage.compress(Bitmap.CompressFormat.PNG, 100, out);
									out.flush();
									out.close();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

						});
			}
		}

		iv_dialog_close = (ImageView) dialog.findViewById(R.id.iv_dialog_close);

		rl_dialog_layout = (RelativeLayout) dialog
				.findViewById(R.id.rl_dialog_layout);

		rl_dialog_layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(context, NextActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("fragmentname", WebFragment.class.getSimpleName());
				intent.putExtras(bundle);
				context.startActivity(intent);
				((Activity) context).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				WebFragment.getInstance().needSaveView = false;
				WebFragment.getInstance().source = 1;
				WebFragment.getInstance().url = url;
			}
		});

		iv_dialog_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	public void show() {
		dialog.show();
	}

}
