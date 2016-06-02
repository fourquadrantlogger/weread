package com.fengwo.reading.main.discover;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.group.GroupAdapter;
import com.fengwo.reading.main.group.GroupBean;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
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
 * 发现 - 查看更多精选随笔
 *
 * @author Luo Sheng
 * @date 2016-3-31
 */
public class ChoicenessBooksDetailsFragment extends Fragment implements OnClickListener {

    public CustomProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;

    private List<GroupBean> list;
    private GroupAdapter discoverJingXuanAdapter;
    private boolean is_loading;
    private int page = 0;

    // 分享的信息
    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    public int source = 0;// 来源 1:发现 2:发现-精选

    public String book_id = ""; //id
    public String book_title = ""; //书名

    private View saveView = null;
    public boolean needSaveView = false;
    public boolean is_refresh = false; // 是否刷新

    public ChoicenessBooksDetailsFragment() {
    }

    public static ChoicenessBooksDetailsFragment fragment = new ChoicenessBooksDetailsFragment();

    public static ChoicenessBooksDetailsFragment getInstance() {
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
        page = 0;

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = pullToRefreshListView.getRefreshableView();

        list = new ArrayList<>();
        discoverJingXuanAdapter = new GroupAdapter(fragment, list, 9);
        listView.setAdapter(discoverJingXuanAdapter);

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
                page = 0;
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
                        page++;
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
        tv_title_mid.setText(book_title);
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
            case R.id.rl_books:

                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 精选随笔
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("book_id", book_id);
        map.put("page", page + "");
        map.put("type", "1");
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.find_getChosenNote, new RequestCallBack<String>() {

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
                                if (page == 0) {
                                    list.clear();
                                    if (json.note == null
                                            || json.note.size() == 0) {
                                        // 没有数据

                                    } else {
                                        list.addAll(json.note);
                                    }
                                } else {
                                    if (json.note == null
                                            || json.note.size() == 0) {
                                        page--;
                                    } else {
                                        list.addAll(json.note);
                                    }
                                }
                                discoverJingXuanAdapter.notifyDataSetChanged();
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
     * 跳转 他人主页
     */
    public void goOther(int position) {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                OtherUserFragment.getInstance());
        transaction.commit();
        OtherUserFragment.getInstance().source = 7;
        OtherUserFragment.getInstance().ta_user_id = list.get(position).user_data.user_id;
        OtherUserFragment.getInstance().needSaveView = false;
    }

    /**
     * 跳转 讨论详情
     */
    public void goDetails(int position) {
        mPosition = position;
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                GroupDetailsFragment.getInstance());
        transaction.commit();
        GroupDetailsFragment.getInstance().source = 10;
        GroupDetailsFragment.getInstance().groupPosition = position;
        GroupDetailsFragment.getInstance().id = list.get(position).id;
        GroupDetailsFragment.getInstance().needSaveView = false;
    }

    /**
     * 分享的点击
     */
    public void fenxiang(int position) {
//        UMengUtils.onCountListener(getActivity(), "youSQ_FX");
        mPosition = position;
        FragmentActivity activity = fragment.getActivity();
        sharePopupWindow = new SelectSharePopupWindow(activity, itemsOnClick);
        sharePopupWindow.showAtLocation(activity.findViewById(R.id.ll_activity_next),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        // 分享的内容
        if (list.get(position).img_str != null && list.get(position).img_str.length != 0) {
            this.imageUrl = list.get(position).img_str[0];
        } else if (list.get(position).user_data.avatar != null) {
            this.imageUrl = list.get(position).user_data.avatar;
        } else {
            this.imageUrl = "";
        }
        this.h5Url = GlobalConstant.ServerDomain + "share/note?note_id="
                + list.get(position).id;
    }

    // 分享的弹出窗体类
    private SelectSharePopupWindow sharePopupWindow;
    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentActivity activity = fragment.getActivity();
            if (activity == null) {
                return;
            }
            int num = 0;
            if (!TextUtils.isEmpty(list.get(mPosition).title)) {
                title = list.get(mPosition).title;
            } else {
                title = list.get(mPosition).user_data.name + "的随笔";
            }
            switch (v.getId()) {
                case R.id.ll_popupwindow_wx:
                    num = 1;
                    content = list.get(mPosition).content;
                    break;
                case R.id.ll_popupwindow_pyq:
                    num = 2;
                    content = list.get(mPosition).content;
                    break;
                case R.id.ll_popupwindow_qq:
                    num = 3;
                    content = list.get(mPosition).content;
                    break;
                case R.id.ll_popupwindow_wb:
                    num = 4;
                    if (TextUtils.isEmpty(list.get(mPosition).title)) {
                        content = "推荐" + list.get(mPosition).user_data.name + "的共读随笔,来自@有书共读" + h5Url;
                    } else {
                        content = "推荐" + list.get(mPosition).user_data.name + "的共读随笔《" + list.get(mPosition).title + "》,来自@有书共读" + h5Url;
                    }
                    break;
                default:
                    break;
            }
            UMShare.setUMeng(activity, num, title, content, imageUrl, h5Url, list.get(mPosition).id, "note");
            sharePopupWindow.dismiss();
            if ("1".equals(UMShare.getLevel())) {
                startActivity(new Intent(getActivity(), UpgradeActivity.class));
            }
        }
    };

    /**
     * 点赞的点击
     */
    public void dianzan(final int position) {
//        UMengUtils.onCountListener(getActivity(), "youSQ_DZ");

        String act = "";
        // 已经点赞过
        if ("1".equals(list.get(position).is_digg)) {
            act = "c";
        } else {
            act = "";
        }
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(position).id);
        map.put("type", "note");
        map.put("act", act);
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.digg_add,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                refresh(position, "dianzan" + position, 0);
                            } else {

                            }
                        } catch (Exception e) {

                        }
                    }
                }, true, null);
    }

    /**
     * 记录位置
     */
    private int mPosition;

    /**
     * 删除后刷新方法
     */
    public void refresh() {
        list.remove(mPosition);
        discoverJingXuanAdapter.notifyDataSetChanged();
    }

    /**
     * 点赞后的刷新
     */
    public void refresh(int position, String tag, int num) {
        if (tag.startsWith("dianzan")) {
            TextView textView = (TextView) listView
                    .findViewWithTag("dianzan_tv" + position);
            if (textView != null) {
                if ("1".equals(list.get(position).is_digg)) {
                    // 已经赞过了
                    list.get(position).is_digg = "0";
                    Drawable drawable = getResources().getDrawable(
                            R.drawable.comment_zan);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    textView
                            .setCompoundDrawables(drawable, null, null, null);
                    textView.setTextColor(getActivity().getResources()
                            .getColor(R.color.text_98));
                    try {
                        int count = Integer
                                .valueOf(list.get(position).digg_count);
                        textView.setText((count - 1) + "");
                        list.get(position).digg_count = (count - 1) + "";
                    } catch (Exception e) {
                    }
                } else {
                    // 设置为赞过
                    list.get(position).is_digg = "1";
                    Drawable drawable = getResources().getDrawable(
                            R.drawable.comment_zan_hou);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    textView
                            .setCompoundDrawables(drawable, null, null, null);
                    textView.setTextColor(getActivity().getResources()
                            .getColor(R.color.zan_text_color));
                    try {
                        int count = Integer
                                .valueOf(list.get(position).digg_count);
                        textView.setText((count + 1) + "");
                        list.get(position).digg_count = (count + 1) + "";
                    } catch (Exception e) {
                    }
                }
            }
            return;
        }
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
    public void onStart() {
        super.onStart();
        if (is_refresh) {
            is_refresh = false;
            getData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChoicenessBooksDetailsFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("ChoicenessBooksDetailsFragment");
    }
}
