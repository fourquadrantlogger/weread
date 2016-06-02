package com.fengwo.reading.main.group;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 随笔 - 谁可以看
 * 
 * @author Luo Sheng
 * @date 2016-2-1
 * 
 */
public class StateFragment extends Fragment implements OnClickListener {

	private ImageView iv_title_left, iv_state_gk, iv_state_sr;
	private TextView tv_title_mid;
	private RelativeLayout rl_state_gk, rl_state_sr;

	private View saveView = null;
	public boolean needSaveView = false;

	public int is_pub = 0;// 谁可以看,0公开，1私有
	
	public StateFragment() {
	}

	public static StateFragment fragment = new StateFragment();

	public static StateFragment getInstance() {
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (needSaveView && saveView != null) {
			return saveView;
		}
		needSaveView = true;

		View view = inflater.inflate(R.layout.fragment_state, container, false);

		findViewById(view);
		setTitle();
		
		switch (is_pub) {
		case 0:
			iv_state_gk.setVisibility(View.VISIBLE);
			iv_state_sr.setVisibility(View.INVISIBLE);
			break;
		case 1:
			iv_state_gk.setVisibility(View.INVISIBLE);
			iv_state_sr.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}

		return view;
	}

	private void findViewById(View view) {
		iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
		iv_state_gk = (ImageView) view.findViewById(R.id.iv_state_gk);
		iv_state_sr = (ImageView) view.findViewById(R.id.iv_state_sr);
		tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
		rl_state_gk = (RelativeLayout) view.findViewById(R.id.rl_state_gk);
		rl_state_sr = (RelativeLayout) view.findViewById(R.id.rl_state_sr);

		iv_title_left.setOnClickListener(this);
		rl_state_gk.setOnClickListener(this);
		rl_state_sr.setOnClickListener(this);
	}

	private void setTitle() {
		tv_title_mid.setVisibility(View.VISIBLE);
		tv_title_mid.setText("谁可以看");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_return:
			getActivity().getSupportFragmentManager().popBackStack();
			break;
		case R.id.rl_state_gk:
			PublishFeelingsFragment.getInstance().setWho("公开");
			PublishFeelingsFragment.getInstance().is_pub = 0;
			getActivity().getSupportFragmentManager().popBackStack();
			break;
		case R.id.rl_state_sr:
			PublishFeelingsFragment.getInstance().setWho("秘密");
			PublishFeelingsFragment.getInstance().is_pub = 1;
			getActivity().getSupportFragmentManager().popBackStack();
			break;

		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("StateFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		saveView = getView();
		MobclickAgent.onPageEnd("StateFragment");
	}
}
