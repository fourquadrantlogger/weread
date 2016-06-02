package com.fengwo.reading.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.fengwo.reading.R;
import com.fengwo.reading.main.discover.hottopics.HotFragment;
import com.fengwo.reading.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 热门话题
 */
public class HotActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next);

		ActivityUtil.hotActivity = this;

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
			// 热门话题
			replaceFragment(HotFragment.getInstance());
			break;

		default:
			break;
		}
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