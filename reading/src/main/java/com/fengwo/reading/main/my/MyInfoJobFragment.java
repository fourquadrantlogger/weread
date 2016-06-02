package com.fengwo.reading.main.my;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.EditTextUtils;
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
 * @author lxq - 职业
 * 
 */
public class MyInfoJobFragment extends Fragment implements OnClickListener {

	private LinearLayout ll_myinfoname;

	private ImageView iv_title_left;
	private TextView tv_title_left, tv_title_right, tv_title_mid;

	private LinearLayout ll_myinfo_layout;
	private EditText et_myinfo_name;
	private ImageView iv_myinfo_delete;

	private CustomProgressDialog progressDialog;

	private View saveView = null;
	public boolean needSaveView = false;

	private MyInfoJobFragment() {
	}

	private static MyInfoJobFragment fragment = new MyInfoJobFragment();

	public static MyInfoJobFragment getInstance() {
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (needSaveView && saveView != null) {
			return saveView;
		}
		// needSaveView = true;

		View view = inflater.inflate(R.layout.fragment_myinfo_name, container,
				false);

		findViewById(view);
		onClickListener();

		iv_title_left.setVisibility(View.GONE);

		tv_title_left.setVisibility(View.VISIBLE);

		tv_title_right.setText("保存");
		tv_title_right.setVisibility(View.VISIBLE);

		tv_title_mid.setText("职业");
		tv_title_mid.setVisibility(View.VISIBLE);

		progressDialog = CustomProgressDialog.createDialog(fragment
				.getActivity());

		tv_title_right.setTextColor(fragment.getActivity().getResources()
				.getColor(R.color.text_a8));
		tv_title_right.setEnabled(false);

		et_myinfo_name.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (TextUtils.isEmpty(et_myinfo_name.getText().toString())) {
					iv_myinfo_delete.setVisibility(View.GONE);
				} else {
					iv_myinfo_delete.setVisibility(View.VISIBLE);
				}
				// TODO
				if (TextUtils.isEmpty(et_myinfo_name.getText().toString())
						|| et_myinfo_name.getText().toString()
								.equals(GlobalParams.userInfoBean.job)) {
					tv_title_right.setTextColor(fragment.getActivity()
							.getResources().getColor(R.color.text_a8));
					tv_title_right.setEnabled(false);
				} else {
					tv_title_right.setTextColor(fragment.getActivity()
							.getResources().getColor(R.color.white));
					tv_title_right.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!needSaveView) {
			needSaveView = true;
			et_myinfo_name.setText(GlobalParams.userInfoBean.job);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		saveView = getView();
		MobclickAgent.onPageEnd("MyInfoJobFragment");
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MyInfoJobFragment");
	}

	private void findViewById(View view) {
		ll_myinfoname = (LinearLayout) view.findViewById(R.id.ll_myinfoname);

		ll_myinfo_layout = (LinearLayout) view
				.findViewById(R.id.ll_myinfo_layout);
		iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
		tv_title_left = (TextView) view.findViewById(R.id.tv_title_left);
		tv_title_right = (TextView) view.findViewById(R.id.tv_title_right);
		tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

		et_myinfo_name = (EditText) view.findViewById(R.id.et_myinfo_name);
		iv_myinfo_delete = (ImageView) view.findViewById(R.id.iv_myinfo_delete);
	}

	private void onClickListener() {
		ll_myinfoname.setOnClickListener(this);

		tv_title_left.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);

		ll_myinfo_layout.setOnClickListener(this);
		iv_myinfo_delete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Context context = fragment.getActivity();
		if (context == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.ll_myinfoname:
			EditTextUtils.hideSoftInput(et_myinfo_name, context);
			break;
		case R.id.ll_myinfo_layout:
			EditTextUtils.showSoftInput(et_myinfo_name, context);
			break;
		case R.id.tv_title_left:
			fragment.getActivity().getSupportFragmentManager().popBackStack();
			EditTextUtils.hideSoftInput(et_myinfo_name, context);
			break;
		case R.id.tv_title_right:
			if (TextUtils.isEmpty(et_myinfo_name.getText().toString())) {
				CustomToast.showToast(context, "职业不能为空");
				return;
			}
			EditTextUtils.hideSoftInput(et_myinfo_name, context);
			getData();
			break;
		case R.id.iv_myinfo_delete:
			et_myinfo_name.setText("");
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
		map.put("job", et_myinfo_name.getText().toString());
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
								if (json.user_data != null) {
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
