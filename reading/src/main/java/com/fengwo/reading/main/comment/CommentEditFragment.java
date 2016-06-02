package com.fengwo.reading.main.comment;

import android.app.Activity;
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
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.my.Fragment_Suibi;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.EditTextUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
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
 * @author lxq	评论编辑
 * 
 */
public class CommentEditFragment extends Fragment implements OnClickListener {

	private LinearLayout ll_commentedit;

	private ImageView iv_title_left;
	private TextView tv_title_comment_1, tv_title_comment_2;
	private TextView tv_title_right_comment;

	private EditText et_commentedit_content;

	private TextView tv_commentedit_count;

	public int comment_type;// 评论0回复1
	public String name;// 姓名
	public String id;// 拆书包id、笔记id
	public String cid;// 评论id
	public int type;// 来源界面1拆书包,2有书圈详情3我的随笔，我的收藏4话题

	private int count = 10000;

	private CustomProgressDialog progressDialog;

	private View saveView = null;
	public boolean needSaveView = false;

	public CommentEditFragment() {
	}

	public static CommentEditFragment fragment = new CommentEditFragment();

	public static CommentEditFragment getInstance() {
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (needSaveView && saveView != null) {
			return saveView;
		}
		// needSaveView = true;

		View view = inflater.inflate(R.layout.fragment_commentedit, container,
				false);

		findViewById(view);
		onClickListener();

		progressDialog = CustomProgressDialog.createDialog(fragment
				.getActivity());

		tv_title_comment_1.setVisibility(View.VISIBLE);
		tv_title_comment_2.setVisibility(View.VISIBLE);
		tv_title_right_comment.setVisibility(View.VISIBLE);

		switch (comment_type) {
		case 0:
			tv_title_comment_1.setText("发评论");
			et_commentedit_content.setHint("发评论...");
			break;
		case 1:
			tv_title_comment_1.setText("回复评论");
			et_commentedit_content.setHint("回复评论...");
			break;

		default:
			break;
		}

		tv_title_comment_2.setText(name);
		tv_title_right_comment.setText("发送");
		tv_title_right_comment.setTextColor(fragment.getActivity()
				.getResources().getColor(R.color.text_a8));
		tv_title_right_comment
				.setBackgroundResource(R.drawable.btn_comment_white);
		
		try {
			count = Integer.valueOf(NOsqlUtil.get_wordlimit().comment_limit);
		} catch (Exception e) {
		}
		tv_commentedit_count.setText("最多" + count + "字");

		et_commentedit_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				tv_commentedit_count
						.setText("最多输入"
								+ (CommentEditFragment.this.count - et_commentedit_content
										.getText().toString().length()) + "个字");

				if (TextUtils.isEmpty(et_commentedit_content.getText()
						.toString())) {
					tv_title_right_comment.setTextColor(fragment.getActivity()
							.getResources().getColor(R.color.text_a8));
					tv_title_right_comment
							.setBackgroundResource(R.drawable.btn_comment_white);
				} else {
					tv_title_right_comment.setTextColor(fragment.getActivity()
							.getResources().getColor(R.color.white));
					tv_title_right_comment
							.setBackgroundResource(R.drawable.btn_comment_green);
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
			et_commentedit_content.setText("");
		}
	}

	private void findViewById(View view) {
		ll_commentedit = (LinearLayout) view.findViewById(R.id.ll_commentedit);

		iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
		tv_title_comment_1 = (TextView) view
				.findViewById(R.id.tv_title_comment_1);
		tv_title_comment_2 = (TextView) view
				.findViewById(R.id.tv_title_comment_2);
		tv_title_right_comment = (TextView) view
				.findViewById(R.id.tv_title_right_comment);

		et_commentedit_content = (EditText) view
				.findViewById(R.id.et_commentedit_content);

		tv_commentedit_count = (TextView) view
				.findViewById(R.id.tv_commentedit_count);
	}

	private void onClickListener() {
		ll_commentedit.setOnClickListener(this);
		iv_title_left.setOnClickListener(this);
		tv_title_right_comment.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Context context = fragment.getActivity();
		if (context == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.ll_commentedit:
			EditTextUtils.hideSoftInput(et_commentedit_content, context);
			break;
		case R.id.iv_return:
			EditTextUtils.hideSoftInput(et_commentedit_content, context);
			switch (type) {
			case 1:
			case 3:
				fragment.getActivity().getSupportFragmentManager()
						.popBackStack();
				break;
			case 0:
				fragment.getActivity().finish();
				fragment.getActivity().overridePendingTransition(
						R.anim.in_from_left, R.anim.out_to_right);
				break;
			case 2:
				fragment.getActivity().getSupportFragmentManager()
						.popBackStack();
				break;

			default:
				break;
			}
			break;
		case R.id.tv_title_right_comment:
			EditTextUtils.hideSoftInput(et_commentedit_content, context);
			if (TextUtils.isEmpty(et_commentedit_content.getText().toString())) {
				CustomToast.showToast(context, "评论不能为空");
				return;
			}
			if (et_commentedit_content.getText().toString().length() > count) {
				CustomToast.showToast(context,NOsqlUtil.get_wordlimit().err_msg);
				return;
			}
			getData();
			break;

		default:
			break;
		}
	}

	private void getData() {
		Map<String, String> map = new HashMap<>();
		map.put("user_id", GlobalParams.uid);
		map.put("id", id);
		map.put("type", type == 1 ? "bp" : "note");
		map.put("soft", VersionUtils.getVersion(getActivity()));
		map.put("content", et_commentedit_content.getText().toString());
		if (comment_type == 1) {
			map.put("cid", cid);
		}
		HttpParamsUtil.sendData(map, GlobalParams.uid,
				GlobalConstant.comment_add, new RequestCallBack<String>() {

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
					public void onFailure(HttpException arg0, String error) {
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
//						System.out.println("========" + jsonString);
						try {
							final CommentAddJson json = new Gson().fromJson(
									jsonString, CommentAddJson.class);
							if ("1".equals(json.code)) {
								switch (type) {
								case 1:
//									Fragment_Bookpack.getInstance().refresh(
//											json.cid,
//											et_commentedit_content.getText()
//													.toString(),"");
									break;
								case 2:
									GroupDetailsFragment.getInstance()
											.refresh1(
													json.cid,
													et_commentedit_content
															.getText()
															.toString(),"");
									break;
								case 3:
									Fragment_Suibi.getInstance().refresh("");
									break;

								default:
									break;
								}
								fragment.getActivity()
										.getSupportFragmentManager()
										.popBackStack();
							} else {
								Context context = fragment.getActivity();
								if (context != null
										&& !((Activity) context).isFinishing()) {
									CustomToast.showToast(context, json.msg);
								}
							}
						} catch (Exception e) {
							Context context = fragment.getActivity();
							if (context != null
									&& !((Activity) context).isFinishing()) {
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
				if (context != null && !((Activity) context).isFinishing()) {
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

	public void onResume() {
		super.onResume();
		saveView = getView();
		MobclickAgent.onPageStart("CommentEditFragment");
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("CommentEditFragment");
	}

}
