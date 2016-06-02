package com.fengwo.reading.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.fengwo.reading.R;
import com.fengwo.reading.umeng.DengLuFragment;
import com.fengwo.reading.utils.ActivityUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_next);

		ActivityUtil.loginActivity = this;

		Bundle bundle = this.getIntent().getExtras();
		int count = 0;
		if (bundle != null) {
			count = bundle.getInt("key", 0);
		}
		replaceFragment(count);
		// 设置新浪SSO handler
		// mController.getConfig().setSsoHandler(new SinaSsoHandler());
	}

	private void replaceFragment(int key) {
		switch (key) {
		case 0:
			break;
		case 1:// 登录
			replaceFragment(DengLuFragment.getInstance());
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

	// 整个平台的Controller,负责管理整个SDK的配置、操作等处理
	final UMSocialService mController = UMServiceFactory
			.getUMSocialService("com.umeng.login");

	// final UMSocialService mController = UMServiceFactory
	// .getUMSocialService("com.umeng.share");

	// 如果有使用任一平台的SSO授权, 则必须在对应的activity中实现onActivityResult方法, 并添加如下代码
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// 根据requestCode获取对应的SsoHandler
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				resultCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, intent);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
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