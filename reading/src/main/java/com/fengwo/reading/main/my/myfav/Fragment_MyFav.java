package com.fengwo.reading.main.my.myfav;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.group.GroupAdapter;
import com.fengwo.reading.main.group.GroupBean;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.group.GroupJson;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.main.read.Fragment_Bookpack;
import com.fengwo.reading.main.read.IndexBean;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
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
 * 我的收藏
 *
 * @author lipeng
 * @date 2016-5-7
 */
public class Fragment_MyFav extends Fragment implements OnClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonHandler handler;

    //region 标题栏
    private ImageView iv_title_left;
    private TextView textView_deepnav_left, textView_deepnav_right;

    //note
    private PullToRefreshListView pullToRefreshListView_note_fav;
    private ListView listView_note_fav;
    private List<GroupBean> list;
    private GroupAdapter groupAdapter;

    //pack
    private PullToRefreshListView pullToRefreshListView_pack_fav;
    private ListView listView_pack_fav;
    private TextView tv_group_empty;
    private List<Pack> list_pack;
    private Adapter_Pack adapter_pack;

    public class Pack {
        public String user_id;
        public String pack_id;//": "35",
        public String create_time;//": "2016-05-06 00:00:00",

        public IndexBean packInfo;//
    }

    // 分享的信息
    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    boolean left = true; //true:随笔  false:早晚读
    private int mPosition; //记录位置
    private boolean is_loading; //
    private int page_note; // 当前页
    private int page_pack;// 拆书包
    private View saveView = null;
    public boolean needSaveView = false;
    public boolean is_refresh = false; // 是否刷新

    public static Fragment_MyFav fragment = new Fragment_MyFav();

    public static Fragment_MyFav getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_my_myfav, container, false);
        handler = new CommonHandler(getActivity(), null);

        findViewById(view);
        pullToRefreshListView_note_fav.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView_note_fav = pullToRefreshListView_note_fav.getRefreshableView();
        pullToRefreshListView_pack_fav.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView_pack_fav = pullToRefreshListView_pack_fav.getRefreshableView();

        left = true;
        is_loading = false;
        page_note = 1;
        page_pack = 1;

        list = new ArrayList<>();
        groupAdapter = new GroupAdapter(fragment, list, 3);
        listView_note_fav.setAdapter(groupAdapter);

        list_pack = new ArrayList<>();
        adapter_pack = new Adapter_Pack(fragment, list_pack);
        listView_pack_fav.setAdapter(adapter_pack);

        // 控件的颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);

        //下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (is_loading) {
                    return;
                }
                is_loading = true;
                if (left) {
                    page_note = 1;
                } else {
                    page_pack = 1;
                }
                getData(left);
            }
        });

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                is_loading = true;
                getData(true);
                getData(false);
            }
        }, 300);

        pullToRefreshListView_note_fav.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                            pullToRefreshListView_note_fav.onRefreshComplete();
                        }
                    }, 300);
                    return;
                }
                is_loading = true;
                page_note++;
                swipeRefreshLayout.setEnabled(false);
                getData(true);
            }
        });
        pullToRefreshListView_pack_fav.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                            pullToRefreshListView_pack_fav.onRefreshComplete();
                        }
                    }, 300);
                    return;
                }
                is_loading = true;
                page_pack++;
                swipeRefreshLayout.setEnabled(false);
                getData(false);
            }
        });

        pullToRefreshListView_pack_fav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position -= 1;
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, Fragment_Bookpack.getInstance());
                transaction.commit();
                Fragment_Bookpack.getInstance().needSaveView = false;
                Fragment_Bookpack.getInstance().source = 3;
                Fragment_Bookpack.getInstance().pb_id = list_pack.get(position).packInfo.pb_id;
                Fragment_Bookpack.getInstance().id = list_pack.get(position).pack_id;
            }
        });

        // 下拉控件调用此方法解决滑动的冲突
        SwpipeListViewOnScrollListener scrollListener = new SwpipeListViewOnScrollListener(swipeRefreshLayout);
        pullToRefreshListView_note_fav.setOnScrollListener(scrollListener);
        pullToRefreshListView_pack_fav.setOnScrollListener(scrollListener);

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        textView_deepnav_left = (TextView) view.findViewById(R.id.textView_left);
        textView_deepnav_right = (TextView) view.findViewById(R.id.textView_right);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_group_refresh);
        pullToRefreshListView_note_fav = (PullToRefreshListView) view.findViewById(R.id.lv_group);
        pullToRefreshListView_pack_fav = (PullToRefreshListView) view.findViewById(R.id.lv_my_shoucang_pack_fav);
        tv_group_empty = (TextView) view.findViewById(R.id.tv_group_empty);

        iv_title_left.setOnClickListener(this);
        textView_deepnav_left.setOnClickListener(this);
        textView_deepnav_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_return:
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
            case R.id.textView_left:
                //随笔
                left = true;
                pullToRefreshListView_note_fav.setVisibility(View.VISIBLE);
                pullToRefreshListView_pack_fav.setVisibility(View.GONE);
                textView_deepnav_left.setTextColor(Color.parseColor("#ffffff"));
                textView_deepnav_left.setBackgroundResource(R.drawable.deep_nav_left);
                textView_deepnav_right.setBackgroundColor(Color.parseColor("#00ffffff"));
                textView_deepnav_right.setTextColor(Color.parseColor("#dddddd"));
//                getData(true);
                break;
            case R.id.textView_right:
                //早晚读
                left = false;
                pullToRefreshListView_note_fav.setVisibility(View.GONE);
                pullToRefreshListView_pack_fav.setVisibility(View.VISIBLE);
                textView_deepnav_right.setBackgroundResource(R.drawable.deep_nav_left);
                textView_deepnav_right.setTextColor(Color.parseColor("#ffffff"));
                textView_deepnav_left.setBackgroundColor(Color.parseColor("#00ffffff"));
                textView_deepnav_left.setTextColor(Color.parseColor("#dddddd"));
//                getData(false);
                break;

            default:
                break;
        }
    }

    /**
     * 随笔 或 早晚读 的网络请求
     *
     * @param left true:随笔  false:早晚读
     */
    private void getData(boolean left) {
        if (left) {
            getData_note_fav();
        } else {
            getData_pack_fav();
        }
    }

    /**
     * 跳转 他人主页
     */
    public void goOther(int position) {
        FragmentTransaction transaction = fragment.getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                OtherUserFragment.getInstance());
        transaction.commit();
        OtherUserFragment.getInstance().source = 6;
        OtherUserFragment.getInstance().needSaveView = false;
        OtherUserFragment.getInstance().ta_user_id = list.get(position).user_data.user_id;
    }

    /**
     * 跳转 讨论详情
     */
    public void goDetails(int position) {
        FragmentTransaction transaction = fragment.getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                GroupDetailsFragment.getInstance());
        transaction.commit();
        GroupDetailsFragment.getInstance().source = 3;
        GroupDetailsFragment.getInstance().groupPosition = position;
        GroupDetailsFragment.getInstance().id = list.get(position).id;
        GroupDetailsFragment.getInstance().needSaveView = false;
    }

    /**
     * 爱心(收藏)的点击
     */
    public void duihaoShow(int position) {
        mPosition = position;
        if (list.get(position).user_data == null) {
            return;
        }
        getFav();
    }

    /**
     * 分享的点击
     */
    public void fenxiang(int position) {
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
                    if (!TextUtils.isEmpty(list.get(mPosition).title)) {
                        content = list.get(mPosition).title;
                    } else {
                        content = list.get(mPosition).content;
                    }
                    break;
                case R.id.ll_popupwindow_qq:
                    num = 3;
                    content = list.get(mPosition).content;
                    break;
                case R.id.ll_popupwindow_wb:
                    num = 4;
                    if (TextUtils.isEmpty(list.get(mPosition).title)) {
                        content = "推荐+" + list.get(mPosition).user_data.name + "的共读随笔,来自@有书共读" + h5Url;
                    } else {
                        content = "推荐+" + list.get(mPosition).user_data.name + "的共读随笔《" + list.get(mPosition).title + "》,来自@有书共读" + h5Url;
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
        // 没点赞过,请求网络
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(position).id);
        map.put("type", "note");
        map.put("act", "1".equals(list.get(position).is_digg) ? "c" : "");

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.digg_add,
                new RequestCallBack<String>() {

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
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                refresh(position, "dianzan" + position, 0);
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null
                                    && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context, context.getString(R.string.json_error));
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 网络请求 - 随笔
     */
    private void getData_note_fav() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("page", page_note + "");
        map.put("type", "fav");

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.user_fav,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        if (pullToRefreshListView_note_fav != null) {
                            // 收起尾部局动画
                            pullToRefreshListView_note_fav.onRefreshComplete();
                        }
                        swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        if (pullToRefreshListView_note_fav != null) {
                            pullToRefreshListView_note_fav.onRefreshComplete();
                        }
                        swipeRefreshLayout.setEnabled(true);
                        String jsonString = responseInfo.result;
                        MLog.v("reading", jsonString);
                        try {

                            GroupJson json = new Gson().fromJson(jsonString, GroupJson.class);
                            if ("1".equals(json.code)) {
                                if (page_note == 1) {
                                    list.clear();
                                    if (json.data == null || json.data.size() == 0) {

                                        tv_group_empty.setVisibility(View.VISIBLE);
                                        swipeRefreshLayout.setVisibility(View.GONE);
                                        tv_group_empty.setText("你还没有收藏内容");
                                    } else {
                                        tv_group_empty.setVisibility(View.GONE);
                                        swipeRefreshLayout
                                                .setVisibility(View.VISIBLE);
                                        list.addAll(json.data);
                                    }
                                } else {
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        page_note--;
                                        Context context = fragment.getActivity();
                                        if (context != null
                                                && !((Activity) context).isFinishing()) {
                                            CustomToast.showToast(context, "没有更多的数据");
                                        }
                                    } else {
                                        list.addAll(json.data);
                                    }
                                }
                                groupAdapter.notifyDataSetChanged();
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

    /**
     * 网络请求 - 收藏拆书包的列表
     */
    private void getData_pack_fav() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("page", page_pack + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.user_favList,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        if (pullToRefreshListView_pack_fav != null) {
                            // 收起尾部局动画
                            pullToRefreshListView_pack_fav.onRefreshComplete();
                        }
                        swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        if (pullToRefreshListView_pack_fav != null) {
                            pullToRefreshListView_pack_fav.onRefreshComplete();
                        }
                        swipeRefreshLayout.setEnabled(true);
                        String jsonString = responseInfo.result;
                        MLog.v("reading", jsonString);
                        try {
                            Json_pack_fav json = new Gson().fromJson(jsonString, Json_pack_fav.class);
                            if ("1".equals(json.code)) {
                                if (page_pack == 1) {
                                    list_pack.clear();
                                    if (json.data == null || json.data.size() == 0) {
                                        tv_group_empty.setVisibility(View.VISIBLE);
                                        swipeRefreshLayout.setVisibility(View.GONE);
                                        tv_group_empty.setText("你还没有收藏内容");
                                    } else {
                                        tv_group_empty.setVisibility(View.GONE);
                                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                                        list_pack.addAll(json.data);
                                    }
                                } else {
                                    if (json.data == null || json.data.size() == 0) {
                                        page_pack--;
                                        Context context = getActivity();
                                        if (context != null && !((Activity) context).isFinishing()) {
                                            CustomToast.showToast(context, "没有更多的数据");
                                        }
                                    } else {
                                        list_pack.addAll(json.data);
                                    }
                                }
                                adapter_pack.notifyDataSetChanged();
                            } else {
                                Context context = getActivity();
                                if (context != null && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = getActivity();
                            if (context != null && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context, context.getString(R.string.json_error));
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 有书圈 - 收藏
     */
    private void getFav() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(mPosition).id);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.note_fav,
                new RequestCallBack<String>() {

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
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                list.get(mPosition).is_fav = "1";
                                groupAdapter.notifyDataSetChanged();
                                Context context = fragment.getActivity();
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, "收藏成功");
                                }
                            } else if ("2".equals(json.code)) {
                                list.get(mPosition).is_fav = "0";
                                list.remove(mPosition);
                                groupAdapter.notifyDataSetChanged();
                                Context context = fragment.getActivity();
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, "已取消收藏");
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

    /**
     * 刷新方法
     */
    public void refresh(String level_is_up) {
        TextView textView = (TextView) listView_note_fav.findViewWithTag("pinglun"
                + mPosition);
        if (textView != null) {
            try {
                int count = Integer.valueOf(list.get(mPosition).comment_count);
                list.get(mPosition).comment_count = (count + 1) + "";
                textView.setText(list.get(mPosition).comment_count);
            } catch (Exception e) {
            }
        }
        //是否升级
        if ("1".equals(level_is_up)) {
            startActivity(new Intent(getActivity(), UpgradeActivity.class));
        }
    }

    /**
     * 点赞的
     */
    public void refresh(int position, String tag, int num) {
        if (tag.startsWith("dianzan")) {
            TextView textView = (TextView) listView_note_fav
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
                            .getColor(R.color.green));
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

    @Override
    public void onStart() {
        super.onStart();
        if (is_refresh) {
            is_refresh = false;
            getData(left);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment_MyFav");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("Fragment_MyFav");
    }
}
