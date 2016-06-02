package com.fengwo.reading.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.my.MyInfoFragment;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lxq 引导界面
 * 
 */
public class SetupActivity extends BaseActivity {

	private ViewPager viewPager;
	private TextView tv_setup_go;
	private View view1, view2, view3, view4;

	private List<View> views;
	private List<Fragment> list;

	private int oldPosition = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		ActivityUtil.setupActivity = this;

		viewPager = (ViewPager) findViewById(R.id.vp_setup_show);
		view1 = (View) findViewById(R.id.v_setup_dot1);
		view2 = (View) findViewById(R.id.v_setup_dot2);
		view3 = (View) findViewById(R.id.v_setup_dot3);
		view4 = (View) findViewById(R.id.v_setup_dot4);
		tv_setup_go = (TextView) findViewById(R.id.tv_setup_go);
		tv_setup_go.setVisibility(View.GONE);
		views = new ArrayList<>();
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);

		list = new ArrayList<>();
		list.add(SetupFragment1.getInstance());
		list.add(SetupFragment2.getInstance());
		list.add(SetupFragment3.getInstance());
		list.add(SetupFragment4.getInstance());

		viewPager.setAdapter(new MySetupAdapter(getSupportFragmentManager(),
				list));
		viewPager.setOffscreenPageLimit(4);

		oldPosition = 0;

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				views.get(oldPosition).setBackgroundResource(
						R.drawable.dot_normal);
				views.get(position).setBackgroundResource(
						R.drawable.dot_focused);
				oldPosition = position;
				if(position == 3){
					tv_setup_go.setVisibility(View.VISIBLE);
				}else{
					tv_setup_go.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		tv_setup_go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetupActivity.this,
						LoginActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("key",1);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
				SPUtils.setAppFirst(SetupActivity.this);
			}
		});
		
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

	private class MySetupAdapter extends FragmentPagerAdapter {

		private List<Fragment> list;

		public MySetupAdapter(FragmentManager fm, List<Fragment> list) {
			super(fm);
			this.list = list;
		}

		@Override
		public Fragment getItem(int position) {
			if (list != null) {
				return list.get(position);
			}
			return null;
		}

		@Override
		public int getCount() {
			if (list != null) {
				return list.size();
			}
			return 0;
		}
	}

}
