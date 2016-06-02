package com.fengwo.reading.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Window;

import com.fengwo.reading.R;
import com.fengwo.reading.main.group.PublishFeelingsFragment;
import com.fengwo.reading.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 发布随笔
 */
public class EditTextActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next);

		ActivityUtil.editTextActivity = this;

		Bundle bundle = this.getIntent().getExtras();
		int count = 0;
		if (bundle != null) {
			count = bundle.getInt("key", 0);
		}
		replaceFragment(count);

	}

	private void replaceFragment(int key) {
		switch (key) {
		case 0:
			break;
		case 1:
			// 发表随笔 - 发表话题
			replaceFragment(PublishFeelingsFragment.getInstance());
			break;

		default:
			break;
		}
	}

	/**
	 *  返回键
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			PublishFeelingsFragment.getInstance().finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void replaceFragment(Fragment fragment) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
				.beginTransaction();
		if (fragment.isAdded()) {
			fragmentTransaction.show(fragment);
		} else {
			fragmentTransaction.replace(R.id.ll_activity_next, fragment,
					fragment.getClass().getName());
		}
		fragmentTransaction.commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}