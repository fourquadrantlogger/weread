package com.fengwo.reading.main.read;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.discover.PrivilegeFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.view.MyListView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 往期书单
 *
 * @author Luo Sheng
 * @date 2016-3-31
 */
public class Fragment_BookList extends Fragment implements OnClickListener {
    public CustomProgressDialog progressDialog;

    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private RelativeLayout rl_books_all;
    private PullToRefreshScrollView scrollview;
    private MyListView listView;

    private List<Bean_BookList> list;
    private Adapter_BookList_Month partBooksAdapter;
    private boolean is_loading;
    private int page;

    public String book_id = "";
    public String book_title = "";

    private View saveView = null;
    public boolean needSaveView = false;

    public Fragment_BookList() {
    }

    public static Fragment_BookList fragment = new Fragment_BookList();

    public static Fragment_BookList getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partbooks1, container, false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());

        findViewById(view);
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText("往期共读");
        page = 0;

        scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        list = new ArrayList<>();
        partBooksAdapter = new Adapter_BookList_Month(fragment, list);
        listView.setAdapter(partBooksAdapter);

        scrollview
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        // 执行刷新函数
                        is_loading = true;
                        page++;

                        getData();
                    }
                });

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                is_loading = true;
                getData();
            }
        }, 300);


        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        scrollview = (PullToRefreshScrollView) view.findViewById(R.id.sv_books);
        listView = (MyListView) view.findViewById(R.id.lv_books);
        rl_books_all = (RelativeLayout) view.findViewById(R.id.rl_books_all);

        iv_title_left.setOnClickListener(this);
        rl_books_all.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                UMengUtils.onCountListener(getActivity(), "GD_02_04_01");
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_to_right);
                break;
            case R.id.rl_books_all:
                //解锁 跳转特权
                UMengUtils.onCountListener(getActivity(), "GD_02_04_02");
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left,
                        R.anim.out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, PrivilegeFragment.getInstance());
                transaction.commit();
                PrivilegeFragment.getInstance().needSaveView = false;
                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 发现 - 书单
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("page", page + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.find_bookList, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                        is_loading = false;
                        scrollview.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        is_loading = false;
                        scrollview.onRefreshComplete();
                        String jsonString = responseInfo.result;
                        try {
                            Json_BookList js = new Gson().fromJson(jsonString, Json_BookList.class);
                            if ("1".equals(js.code)) {
                                jsonData = js;
                                setData();
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, js.msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                Toast.makeText(context, context.getString(R.string.json_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

    Json_BookList jsonData;

    private void setData() {
        if ("1".equals(jsonData.is_unlock)) {
            rl_books_all.setVisibility(View.GONE);
        } else {
            Toast.makeText(getActivity(), "请点击下方 [解锁更多] 获得特权", Toast.LENGTH_SHORT).show();
            rl_books_all.setVisibility(View.VISIBLE);
        }
        if (page == 0) {
            list.clear();
            if (jsonData.book == null || jsonData.book.size() == 0) {
                // 没有数据

            } else {
                list.addAll(jsonData.book);
            }
        } else {
            if (jsonData.book == null
                    || jsonData.book.size() == 0) {
                page--;
            } else {
                list.addAll(jsonData.book);
            }
        }
        partBooksAdapter.notifyDataSetChanged();
    }

    /**
     * 解锁成功
     */
    public void refresh() {
        rl_books_all.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment_BookList");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("Fragment_BookList");
    }
}
