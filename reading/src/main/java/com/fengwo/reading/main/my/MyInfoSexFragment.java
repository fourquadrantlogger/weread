package com.fengwo.reading.main.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author lxq - 性别
 * 
 */
public class MyInfoSexFragment extends Fragment implements OnClickListener {

	private ImageView iv_title_left;
	private TextView tv_title_left, tv_title_right, tv_title_mid;

	private RelativeLayout rl_myinfo_nan, rl_myinfo_nv;
	private ImageView iv_myinfo_nan, iv_myinfo_nv;

	private CustomProgressDialog progressDialog;

	private String sex;

	private View saveView = null;
	public boolean needSaveView = false;

	private MyInfoSexFragment() {
	}

	private static MyInfoSexFragment fragment = new MyInfoSexFragment();

	public static MyInfoSexFragment getInstance() {
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (needSaveView && saveView != null) {
			return saveView;
		}
		needSaveView = true;

		View view = inflater.inflate(R.layout.fragment_myinfo_sex, container,
				false);

		findViewById(view);
		onClickListener();

		iv_title_left.setVisibility(View.GONE);

		tv_title_left.setVisibility(View.VISIBLE);

		tv_title_right.setText("保存");
		tv_title_right.setVisibility(View.VISIBLE);

		tv_title_mid.setText("性别");
		tv_title_mid.setVisibility(View.VISIBLE);

		progressDialog = CustomProgressDialog.createDialog(fragment
				.getActivity());

		sex = GlobalParams.userInfoBean.sex;
		if (sex.equals("2")) {
			iv_myinfo_nan.setVisibility(View.GONE);
			iv_myinfo_nv.setVisibility(View.VISIBLE);
		} else if (sex.equals("1")) {
			iv_myinfo_nan.setVisibility(View.VISIBLE);
			iv_myinfo_nv.setVisibility(View.GONE);
		} else {
			iv_myinfo_nan.setVisibility(View.GONE);
			iv_myinfo_nv.setVisibility(View.GONE);
		}

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		saveView = getView();
		MobclickAgent.onPageEnd("MyInfoSexFragment");
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MyInfoSexFragment");
	}

	private void findViewById(View view) {
		iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
		tv_title_left = (TextView) view.findViewById(R.id.tv_title_left);
		tv_title_right = (TextView) view.findViewById(R.id.tv_title_right);
		tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

		rl_myinfo_nan = (RelativeLayout) view.findViewById(R.id.rl_myinfo_nan);
		rl_myinfo_nv = (RelativeLayout) view.findViewById(R.id.rl_myinfo_nv);
		iv_myinfo_nan = (ImageView) view.findViewById(R.id.iv_myinfo_nan);
		iv_myinfo_nv = (ImageView) view.findViewById(R.id.iv_myinfo_nv);
	}

	private void onClickListener() {
		tv_title_left.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);

		rl_myinfo_nan.setOnClickListener(this);
		rl_myinfo_nv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Context context = fragment.getActivity();
		if (context == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.tv_title_left:
			fragment.getActivity().getSupportFragmentManager().popBackStack();
			break;
		case R.id.tv_title_right:
			if (TextUtils.isEmpty(sex)||sex.equals("0")) {
				CustomToast.showToast(context, "选择性别");
				return;
			}
			getData();
			break;
		case R.id.rl_myinfo_nan:
			if (sex.equals("1")) {
				return;
			}
			iv_myinfo_nan.setVisibility(View.VISIBLE);
			iv_myinfo_nv.setVisibility(View.GONE);
			sex = "1";
			break;
		case R.id.rl_myinfo_nv:
			if (sex.equals("2")) {
				return;
			}
			iv_myinfo_nan.setVisibility(View.GONE);
			iv_myinfo_nv.setVisibility(View.VISIBLE);
			sex = "2";
			break;

		default:
			break;
		}
	}

	/**
	 * 获取数据(个人信息保存)
	 */
	private void getData() {
		Map<String, String> map = new HashMap<>();
		map.put("user_id", GlobalParams.uid);
		map.put("sex", sex);
		map.put("soft", VersionUtils.getVersion(getActivity()));

		HttpParamsUtil.sendData(map, GlobalParams.uid,
				GlobalConstant.userinfo_save, new RequestCallBack<String>() {

					@Override
					public void onStart() {
						super.onStart();
						new Thread() {
							@Override
							public void run() {
								handler.sendEmptyMessage(0);
							}
						}.start();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						new Thread() {
							@Override
							public void run() {
								handler.sendEmptyMessage(1);
								handler.sendEmptyMessage(2);
							}
						}.start();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						new Thread() {
							@Override
							public void run() {
								handler.sendEmptyMessage(1);
							}
						}.start();
						String jsonString = responseInfo.result;
						try {
							UserinfoSaveJson json = new Gson().fromJson(
									jsonString, UserinfoSaveJson.class);
							if ("1".equals(json.code)) {
								if (json.user_data!=null) {
									GlobalParams.userInfoBean = json.user_data;
									NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
								}
								fragment.getActivity()
										.getSupportFragmentManager()
										.popBackStack();
								MyInfoFragment.getInstance().refresh();
							} else {
								Context context = fragment.getActivity();
								if (context != null) {
									CustomToast.showToast(context, json.msg);
								}
							}
						} catch (Exception e) {
							Context context = fragment.getActivity();
							if (context != null) {
								CustomToast.showToast(context,
										context.getString(R.string.json_error));
							}
						}
					}
				}, true, null);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2:
				Context context = fragment.getActivity();
				if (context != null) {
					CustomToast.showToast(context,
							context.getString(R.string.network_check));
				}
				break;
			case 1:
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				break;
			case 0:
				if (progressDialog != null && !progressDialog.isShowing()) {
					progressDialog.show();
				}
				break;

			default:
				break;
			}
		};
	};

}
