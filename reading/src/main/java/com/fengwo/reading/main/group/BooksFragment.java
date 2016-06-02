package com.fengwo.reading.main.group;

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
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.my.UserReadBean;
import com.fengwo.reading.main.my.UserReadJson;
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
 * 随笔 - 相关书籍
 *
 * @author Luo Sheng
 * @date 2016-2-1
 */
public class BooksFragment extends Fragment implements OnClickListener {

    public CustomProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;

    private List<UserReadBean> list;
    private BooksAdapter booksAdapter;
    private boolean is_loading;
    private int page;

    public String book_id = "";

    private View saveView = null;
    public boolean needSaveView = false;

    public BooksFragment() {
    }

    public static BooksFragment fragment = new BooksFragment();

    public static BooksFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // if (needSaveView && saveView != null) {
        // return saveView;
        // }
        // needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_books, container, false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());

        findViewById(view);
        setTitle();
        page = 1;

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = pullToRefreshListView.getRefreshableView();

        list = new ArrayList<>();
        booksAdapter = new BooksAdapter(fragment, list, book_id);
        listView.setAdapter(booksAdapter);

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
//                        page++;
                        page = 1;
                        swipeRefreshLayout.setEnabled(false);
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
        }, 300);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                PublishFeelingsFragment.getInstance().setBookName(
                        list.get(position - 1).book_title);
                PublishFeelingsFragment.getInstance().book_id = list
                        .get(position - 1).book_id;

                getActivity().getSupportFragmentManager().popBackStack();
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
        tv_title_mid.setText("相关书籍");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 有书圈筛选书籍列表
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.get_pbook, new RequestCallBack<String>() {

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
                            UserReadJson json = new Gson().fromJson(jsonString,
                                    UserReadJson.class);
                            if ("1".equals(json.code)) {
                                if (page == 1) {
                                    list.clear();
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        // 没有数据

                                    } else {
                                        list.addAll(json.data);
                                    }
                                } else {
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        page--;
                                    } else {
                                        list.addAll(json.data);
                                    }
                                }
                                booksAdapter.notifyDataSetChanged();
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
        MobclickAgent.onPageStart("BooksFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("BooksFragment");
    }
}
