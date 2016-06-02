package com.fengwo.reading.main.discover;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
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
 * 发现 - 达人
 * 随笔详情 - 赞过的人
 *
 * @author Luo Sheng
 * @date 2016-3-31
 */
public class ACEFragment extends Fragment implements OnClickListener {

    public CustomProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;

    private List<ACEBean> list;
    private ACEAdapter discoverDaRenAdapter;
    private boolean is_loading;

    public String id = ""; //随笔的id
    public int source = 0;
    // 来源 1:更多达人 2:赞过的人

    private View saveView = null;
    public boolean needSaveView = false;

    public ACEFragment() {
    }

    public static ACEFragment fragment = new ACEFragment();

    public static ACEFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_books, container, false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());

        findViewById(view);
        setTitle();

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView = pullToRefreshListView.getRefreshableView();

        list = new ArrayList<>();
        discoverDaRenAdapter = new ACEAdapter(fragment, list);
        listView.setAdapter(discoverDaRenAdapter);

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
                switch (source) {
                    case 1:
                        getData();
                        break;
                    case 2:
                        getData1();
                        break;
                }
            }
        });

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
                            }, 300);
                            return;
                        }
                        is_loading = true;
                        swipeRefreshLayout.setEnabled(false);
                        switch (source) {
                            case 1:
                                getData();
                                break;
                            case 2:
                                getData1();
                                break;
                        }
                    }
                });

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                is_loading = true;
                switch (source) {
                    case 1:
                        getData();
                        break;
                    case 2:
                        getData1();
                        break;
                }
            }
        }, 300);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                FragmentTransaction transaction = fragment.getActivity()
                        .getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right,
                        R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        OtherUserFragment.getInstance());
                transaction.commit();
                OtherUserFragment.getInstance().source = 2;
                OtherUserFragment.getInstance().needSaveView = false;
                OtherUserFragment.getInstance().ta_user_id = list.get(position - 1).user_id;
            }
        });

        listView.setOnScrollListener(new SwpipeListViewOnScrollListener(
                swipeRefreshLayout));

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_books);
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.srl_books_refresh);

        iv_title_left.setOnClickListener(this);
    }

    private void setTitle() {
        tv_title_mid.setVisibility(View.VISIBLE);
        switch (source) {
            case 1:
                tv_title_mid.setText("每周达人榜");
                break;
            case 2:
                tv_title_mid.setText("赞过的人");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                switch (source) {
                    case 1:
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.in_from_left,
                                R.anim.out_to_right);
                        break;
                    case 2:
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                }
                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 更多达人
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("type", "1");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.find_exportNewer, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        swipeRefreshLayout.setEnabled(true);
                        if (pullToRefreshListView != null) {
                            pullToRefreshListView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        swipeRefreshLayout.setEnabled(true);
                        if (pullToRefreshListView != null) {
                            pullToRefreshListView.onRefreshComplete();
                        }
                        String jsonString = responseInfo.result;
                        try {
                            DiscoverJson json = new Gson().fromJson(jsonString,
                                    DiscoverJson.class);
                            if ("1".equals(json.code)) {
                                list.clear();
                                if (json.data == null
                                        || json.data.size() == 0) {
                                    // 没有数据

                                } else {
                                    list.addAll(json.data);
                                }
                                discoverDaRenAdapter.notifyDataSetChanged();
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 网络请求 - 赞过的人
     */
    private void getData1() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("note_id", id);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.note_diggall, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        swipeRefreshLayout.setEnabled(true);
                        if (pullToRefreshListView != null) {
                            pullToRefreshListView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        swipeRefreshLayout.setEnabled(true);
                        if (pullToRefreshListView != null) {
                            pullToRefreshListView.onRefreshComplete();
                        }
                        String jsonString = responseInfo.result;
                        try {
                            System.out.println("----------11:" + id + ", " + jsonString);
                            DiscoverJson json = new Gson().fromJson(jsonString,
                                    DiscoverJson.class);
                            if ("1".equals(json.code)) {
                                list.clear();
                                if (json.data == null
                                        || json.data.size() == 0) {
                                    // 没有数据

                                } else {
                                    list.addAll(json.data);
                                }
                                discoverDaRenAdapter.notifyDataSetChanged();
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context,
                                context.getString(R.string.network_check),
                                Toast.LENGTH_SHORT).show();
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
        }

        ;
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ACEFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("ACEFragment");
    }
}
