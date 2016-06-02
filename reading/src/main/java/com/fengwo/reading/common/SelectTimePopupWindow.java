package com.fengwo.reading.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.fengwo.reading.R;

/**
 * 滚轮
 * 
 * @author lxq
 * 
 */
public class SelectTimePopupWindow extends PopupWindow {

	private TextView tv_location_cancel, tv_location_submit, tv_location_title;
	private TimePicker timePicker;

	/**
	 * context 上下文
	 * 
	 * itemsOnClick 点击事件
	 */
	public SelectTimePopupWindow(Context context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mView = inflater.inflate(R.layout.layout_popupwindow_time, null);

		tv_location_cancel = (TextView) mView
				.findViewById(R.id.tv_location_cancel);
		tv_location_submit = (TextView) mView
				.findViewById(R.id.tv_location_submit);
		tv_location_title = (TextView) mView
				.findViewById(R.id.tv_location_title);

		timePicker = (TimePicker) mView.findViewById(R.id.tp_time_timepicker);

		RelativeLayout relativeLayout = (RelativeLayout) mView
				.findViewById(R.id.rl_popupwindow_layout);
		relativeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});

		// 设置24小时制
		timePicker.setIs24HourView(false);
		// 设置禁止键盘输入
		timePicker
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int mit) {
				System.out.println("=========" + hourOfDay + "--" + mit);
			}
		});

		// 取消按钮
		tv_location_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});
		// 设置按钮监听
		tv_location_submit.setOnClickListener(itemsOnClick);

		// 设置SelectPicPopupWindow的View
		this.setContentView(mView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
	}

	public void setTitle(String string) {
		tv_location_title.setText(string);
	}

	public String getTime() {
		return (timePicker.getCurrentHour()<10?"0"+timePicker.getCurrentHour():timePicker.getCurrentHour()) + ":"
				+ (timePicker.getCurrentMinute()<10?"0"+timePicker.getCurrentMinute():timePicker.getCurrentMinute());
	}

	public void setTime(int hour, int minute) {
		if (timePicker != null) {
			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(minute);
		}
	}

}
