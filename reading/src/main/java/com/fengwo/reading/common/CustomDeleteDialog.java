package com.fengwo.reading.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fengwo.reading.R;

public class CustomDeleteDialog extends Dialog {

	private TextView textView;
	private android.view.View.OnClickListener onClickListener;

	public CustomDeleteDialog(Context context,
			android.view.View.OnClickListener onClickListener) {
		super(context, R.style.dialog);
		this.onClickListener = onClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_dialog_delete);

		Button cancel = (Button) findViewById(R.id.bt_dialog_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		textView = (TextView) findViewById(R.id.tv_dialog_title);
		
		Button submit = (Button) findViewById(R.id.bt_dialog_submit);
		submit.setOnClickListener(onClickListener);
		setCanceledOnTouchOutside(false);
	}

	public void setTitle(String title) {
		textView.setText(title);
	}

}
