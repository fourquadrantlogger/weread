package com.fengwo.reading.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 
 * @author lxq EditText
 * 
 */
public class EditTextUtils {
	
	/**
	 * 
	 * 收回软键盘
	 * 
	 */
	public static void hideSoftInput(EditText editText,
			Context context) {
		InputMethodManager imm = (InputMethodManager) (context
				.getSystemService(Context.INPUT_METHOD_SERVICE));
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * 
	 * 弹起软键盘
	 * 
	 */
	public static void showSoftInput(EditText editText,
			Context context) {
		editText.requestFocus();
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
		editText.setSelection(editText.getText().toString().length());
	}

}
