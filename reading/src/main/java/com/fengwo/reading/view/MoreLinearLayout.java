package com.fengwo.reading.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fengwo.reading.R;

/**
 * 
 * @author lxq 加载更多
 * 
 */
public class MoreLinearLayout extends LinearLayout {

	private View view;
	private ProgressBar pb_refresh_more;
	private TextView tv_refresh_more;

	private OnLoadingListener onLoadingListener;
	private boolean isLoading;

	// private int size1;
	// private int size2;
	// private int color1;
	// private int color2;
	private String text1;
	private String text2;

	public MoreLinearLayout(Context context) {
		super(context);
		init(context);
	}

	public MoreLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		view = LayoutInflater.from(context).inflate(
				R.layout.layout_refresh_more, null);
		addView(view);
		setGravity(Gravity.CENTER);
		initViews();
		isLoading = false;
		// size1 = 15;
		// size2 = 15;
		// color1 = Color.parseColor("#bfbfbf");
		// color2 = Color.parseColor("#bfbfbf");
		text1 = "加载更多的数据";
		text2 = "loading";

		pb_refresh_more.setVisibility(View.GONE);
		tv_refresh_more.setText(text1);
		onClick();
	}

	private void initViews() {
		pb_refresh_more = (ProgressBar) view.findViewById(R.id.pb_refresh_more);
		tv_refresh_more = (TextView) view.findViewById(R.id.tv_refresh_more);
	}

	private void onClick() {
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isLoading) {
					isLoading = true;
					pb_refresh_more.setVisibility(View.VISIBLE);
					tv_refresh_more.setText(text2);
					if (onLoadingListener != null) {
						onLoadingListener.onLoading();
					}
				}
			}
		});
	}

	public interface OnLoadingListener {
		public void onLoading();
	}

	public void setOnLoadingListener(OnLoadingListener listener) {
		onLoadingListener = listener;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoadCompleted() {
		isLoading = false;
		pb_refresh_more.setVisibility(View.GONE);
		tv_refresh_more.setText(text1);
	}

	// public void setSize1(int size1) {
	// this.size1 = size1;
	// }
	//
	// public void setSize2(int size2) {
	// this.size2 = size2;
	// }
	//
	// public void setColor1(int color1) {
	// this.color1 = color1;
	// }
	//
	// public void setColor2(int color2) {
	// this.color2 = color2;
	// }

	public void setText1(String text1) {
		this.text1 = text1;
	}

	public void setText2(String text2) {
		this.text2 = text2;
	}

}
