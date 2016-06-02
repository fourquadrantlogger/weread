package com.fengwo.reading.main.discover.hottopics;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.group.PublishFeelingsFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.view.SwpipeListViewOnScrollListener;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 话题标签 lxq
 */
public class LabelFragment extends Fragment implements OnClickListener {

	private ImageView iv_title_left;
	private TextView tv_title_left, tv_title_mid;

	private SwipeRefreshLayout swipeRefreshLayout;
	private PullToRefreshListView pullToRefreshListView;
	private ListView listView;
	private List<String> list;
	private LabelAdapter adapter;
	private boolean is_loading;
	private int page;

	private CustomProgressDialog progressDialog;

	private View saveView = null;
	public boolean needSaveView = false;

	public LabelFragment() {
	}

	public static LabelFragment fragment = new LabelFragment();

	public static LabelFragment getInstance() {
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (needSaveView && saveView != null) {
			return saveView;
		}
		needSaveView = true;

		View view = inflater.inflate(R.layout.fragment_label, container, false);

		findViewById(view);
		onClickListener();

		iv_title_left.setVisibility(View.GONE);
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("取消");
		tv_title_mid.setVisibility(View.VISIBLE);
		tv_title_mid.setText("添加标签");

		pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
		listView = pullToRefreshListView.getRefreshableView();

		progressDialog = CustomProgressDialog.createDialog(fragment
				.getActivity());

		list = new ArrayList<>();
		adapter = new LabelAdapter(fragment, list);
		listView.setAdapter(adapter);

		// 下拉控件调用此方法解决滑动的冲突
		listView.setOnScrollListener(new SwpipeListViewOnScrollListener(
				swipeRefreshLayout));

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
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							swipeRefreshLayout.setRefreshing(false);
						}
					}, 100);
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

		pullToRefreshListView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (is_loading) {
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									pullToRefreshListView.onRefreshComplete();
								}
							}, 100);
							return;
						}
						is_loading = true;
						page++;
						swipeRefreshLayout.setRefreshing(false);
						swipeRefreshLayout.setEnabled(false);
						getData();
					}
				});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PublishFeelingsFragment.getInstance().refresh(
						list.get(position - 1));
				fragment.getActivity().getSupportFragmentManager()
						.popBackStack();
			}
		});

		return view;
	}

	private void findViewById(View view) {
		iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
		tv_title_left = (TextView) view.findViewById(R.id.tv_title_left);
		tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

		swipeRefreshLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.srl_addlabel_refresh);
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.lv_addlabel_show);
	}

	private void onClickListener() {
		tv_title_left.setOnClickListener(this);
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

		default:
			break;
		}
	}

	private void getData() {
		Map<String, String> map = new HashMap<>();
		map.put("user_id", GlobalParams.uid);
		map.put("page", page + "");
		map.put("soft", VersionUtils.getVersion(getActivity()));

		HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.hot_list,
				new RequestCallBack<String>() {

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
						swipeRefreshLayout.setEnabled(true);
						pullToRefreshListView.onRefreshComplete();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						swipeRefreshLayout.setRefreshing(false);
						is_loading = false;
						swipeRefreshLayout.setEnabled(true);
						pullToRefreshListView.onRefreshComplete();
						String jsonString = responseInfo.result;
						// System.out.println("===========" + jsonString);
						try {
							HotListJson json = new Gson().fromJson(jsonString,
									HotListJson.class);
							if ("1".equals(json.code)) {
								if (json.data != null && json.data.size() != 0) {
									if (page == 1) {
										list.clear();
									} else {
									}
									for (int i = 0; i < json.data.size(); i++) {
										list.add(json.data.get(i).topic_title);
									}
									adapter.notifyDataSetChanged();
								} else {
									if (page == 1) {
										list.clear();
										adapter.notifyDataSetChanged();
									} else {
										page--;
										Context context = fragment
												.getActivity();
										if (context != null
												&& !((Activity) context)
														.isFinishing()) {
											CustomToast.showToast(context,
													"暂无更多的数据");
										}
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

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LabelFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		saveView = getView();
		MobclickAgent.onPageEnd("LabelFragment");
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

}
