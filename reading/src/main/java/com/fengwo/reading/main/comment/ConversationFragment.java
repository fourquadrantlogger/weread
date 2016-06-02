package com.fengwo.reading.main.comment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.view.SwpipeListViewOnScrollListener;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author lxq	查看对话
 * 
 */
public class ConversationFragment extends Fragment implements OnClickListener {

	private ImageView iv_title_left;
	private TextView tv_title_mid;

	private SwipeRefreshLayout swipeRefreshLayout;

	private ListView listView;
	private ConversationAdapter adapter;
	private List<CommentTalkBean> list;

	public CustomProgressDialog progressDialog;

	private boolean is_loading;
	private int page;

	public String id;
	public String cuser_id;
	public String ruser_id;
	public int type;//0拆书包,1笔记

	private View saveView = null;
	public boolean needSaveView = false;

	public ConversationFragment() {
	}

	public static ConversationFragment fragment = new ConversationFragment();

	public static ConversationFragment getInstance() {
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (needSaveView && saveView != null) {
			return saveView;
		}
		needSaveView = true;

		View view = inflater.inflate(R.layout.fragment_conversation, container,
				false);

		findViewById(view);
		onClickListener();

		progressDialog = CustomProgressDialog.createDialog(getActivity());

		list = new ArrayList<CommentTalkBean>();
		// adapter = new ConversationAdapter(fragment, list, null,null);
		// listView.setAdapter(adapter);

		listView.setOnScrollListener(new SwpipeListViewOnScrollListener(
				swipeRefreshLayout));

		tv_title_mid.setVisibility(View.VISIBLE);
		tv_title_mid.setText("查看对话");
		is_loading = false;
		page = 1;

		// 控件的颜色
		swipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		// 下拉控件的监听
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (is_loading) {
					return;
				}
				is_loading = true;
				page = 1;
				getData();
			}
		});

		// 延时请求网络
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				swipeRefreshLayout.setRefreshing(true);
				is_loading = true;
				getData();
			}
		}, 500);

		return view;
	}

	private void findViewById(View view) {
		iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
		tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

		swipeRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.srl_conversation_refresh);
		listView = (ListView) view.findViewById(R.id.lv_conversation_show);
	}

	private void onClickListener() {
		iv_title_left.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Context context = fragment.getActivity();
		if (context == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.iv_return:
			fragment.getActivity().getSupportFragmentManager().popBackStack();
			break;

		default:
			break;
		}
	}

	public void onResume() {
		super.onResume();
		saveView = getView();
		MobclickAgent.onPageStart("ConversationFragment");
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ConversationFragment");
	}

	private void getData() {
		Map<String, String> map = new HashMap<>();
		map.put("user_id", GlobalParams.uid);
		map.put("id", id);
		map.put("cuser_id", cuser_id);
		map.put("ruser_id", ruser_id);
		map.put("type", type == 0?"bp":"note");
		map.put("soft", VersionUtils.getVersion(getActivity()));
		
		System.out.println("========"+map.get("cuser_id"));
		System.out.println("========"+map.get("ruser_id"));
		
		HttpParamsUtil.sendData(map, GlobalParams.uid,
				GlobalConstant.comment_talk, new RequestCallBack<String>() {

					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onFailure(HttpException arg0, String error) {
						Context context = fragment.getActivity();
						if (context != null
								&& !((Activity) context).isFinishing()) {
							CustomToast.showToast(context,
									context.getString(R.string.network_check));
						}
						swipeRefreshLayout.setRefreshing(false);
						is_loading = false;
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						swipeRefreshLayout.setRefreshing(false);
						is_loading = false;
						String jsonString = responseInfo.result;
						System.out.println("===========" + jsonString);
						try {
							final CommentTalkJson json = new Gson().fromJson(
									jsonString, CommentTalkJson.class);
							if ("1".equals(json.code)) {
								if (json.talk_list != null
										&& json.talk_list.size() != 0) {
									list.clear();
									list.addAll(json.talk_list);
									for (int i = 0; i < list.size(); i++) {
										if (list.get(i).user_id.equals(json.meuser_data.user_id)) {
											list.get(i).type = 0;
										}else {
											list.get(i).type = 1;
										}
									}
									adapter = new ConversationAdapter(fragment,
											list, json.meuser_data,
											json.tauser_data);
									listView.setAdapter(adapter);
								} else {
									list.clear();
									if (adapter!=null) {
										adapter.notifyDataSetChanged();
									}
									Context context = fragment.getActivity();
									if (context != null
											&& !((Activity) context)
													.isFinishing()) {
										CustomToast.showToast(context, "暂无数据");
									}
								}
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

}
