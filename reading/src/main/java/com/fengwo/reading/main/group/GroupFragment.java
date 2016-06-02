package com.fengwo.reading.main.group;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.EditTextActivity;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.GroupPopupWindow;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.group.qun.QunDetailFragment;
import com.fengwo.reading.main.group.widget.Tag;
import com.fengwo.reading.main.group.widget.TagListView.OnTagClickListener;
import com.fengwo.reading.main.group.widget.TagView;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.VersionUtils;
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
 * 有书圈
 *
 * @author Luo Sheng
 * @date 2016-1-26
 */
public class GroupFragment extends Fragment implements OnClickListener {

    private GroupPopupWindow groupPopupWindow;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;

    public CustomProgressDialog progressDialog;
    private RelativeLayout rl_group_rm, rl_group_zx, rl_group_header_all;
    private View view_group_rm, view_group_zx;
    private GridView gv_group_header_gv;
    private ImageView iv_title_left, iv_title_right, iv_title_right2;
    private TextView tv_title_left, tv_group_rm, tv_group_zx, tv_group_book, tv_group_header_num;

    private List<GroupBean> list;
    private GroupAdapter groupAdapter;
    // 分享的信息
    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    public String level_is_up = ""; //升级

    public String order = "";       // 排序，默认hot热门，new最新
    public String book_id = "";     // 筛选的书籍id
    public String book_title = "";  // 筛选的书籍名称

    public String group_id = "";    //群组id
    private int page; // 当前页

    public boolean isOne = false; // 是否未筛选
    private boolean is_loading;
    public boolean is_refresh = false; // 是否刷新

    private MyGroupRankingAdapter gridViewAdapter; //头部Gridview

    private View saveView = null;
    public boolean needSaveView = false;

    public GroupFragment() {
    }

    public static GroupFragment fragment = new GroupFragment();

    public static GroupFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());

        findViewById(view);
        setTitle();

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = pullToRefreshListView.getRefreshableView();

        // 添加头部
        addHeaderView();

        list = new ArrayList<>();
        groupAdapter = new GroupAdapter(fragment, list, 1);
        listView.setAdapter(groupAdapter);

        is_loading = false;
        order = "hot";
        page = 1;

        sharePopupWindow = new SelectSharePopupWindow(getActivity(), itemsOnClick);

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
                    swipeRefreshLayout.setRefreshing(false);
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
                refresh(book_title);
                getData();
            }
        }, 300);

        groupPopupWindow = new GroupPopupWindow(getActivity(), new OnTagClickListener() {
            @Override
            public void onTagClick(TagView tagView, Tag tag) {
                UMengUtils.onCountListener(getActivity(), "youSQ_qunzu");
                Drawable drawable1 = getResources().getDrawable(
                        R.drawable.group_title_xia);
                drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
                        drawable1.getMinimumHeight());
                tv_title_left
                        .setCompoundDrawables(null, null, drawable1, null);
                tv_title_left.setText(tag.getTitle());
                group_id = tag.getId();
                page = 1;
                getData();

                isOK = true;
                groupPopupWindow.dismiss();
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

        // 下拉控件调用此方法解决滑动的冲突
        listView.setOnScrollListener(new SwpipeListViewOnScrollListener(
                swipeRefreshLayout));

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_left = (TextView) view.findViewById(R.id.tv_title_left);
        iv_title_right = (ImageView) view.findViewById(R.id.iv_title_right);
        iv_title_right2 = (ImageView) view.findViewById(R.id.iv_title_right2);
        tv_group_rm = (TextView) view.findViewById(R.id.tv_group_rm);
        tv_group_zx = (TextView) view.findViewById(R.id.tv_group_zx);
        tv_group_book = (TextView) view.findViewById(R.id.tv_group_book);
        view_group_rm = (View) view.findViewById(R.id.view_group_rm);
        view_group_zx = (View) view.findViewById(R.id.view_group_zx);
        rl_group_rm = (RelativeLayout) view.findViewById(R.id.rl_group_rm);
        rl_group_zx = (RelativeLayout) view.findViewById(R.id.rl_group_zx);
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.srl_group_refresh);
        pullToRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.lv_group);

        rl_group_rm.setOnClickListener(this);
        rl_group_zx.setOnClickListener(this);
        tv_title_left.setOnClickListener(this);
        iv_title_right.setOnClickListener(this);
        iv_title_right2.setOnClickListener(this);

    }

    private void setTitle() {
        iv_title_left.setVisibility(View.GONE);
        tv_title_left.setVisibility(View.VISIBLE);
        iv_title_right.setVisibility(View.VISIBLE);
        iv_title_right.setImageResource(R.drawable.group_suibi_white);
        iv_title_right2.setVisibility(View.VISIBLE);
        iv_title_right2.setImageResource(R.drawable.group_sousuo_white);

        tv_title_left.setText("有书圈");
        Drawable drawable = getResources().getDrawable(
                R.drawable.group_title_xia);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        tv_title_left
                .setCompoundDrawables(null, null, drawable, null);
        tv_title_left.setCompoundDrawablePadding(10);
    }

    private View view1;

    private void addHeaderView() {
        view1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.head_group_ranking, null);

        tv_group_header_num = (TextView) view1.findViewById(R.id.tv_group_header_num);
        gv_group_header_gv = (GridView) view1
                .findViewById(R.id.gv_group_header_gv);
        rl_group_header_all = (RelativeLayout) view1.findViewById(R.id.rl_group_header_all);
        rl_group_header_all.setOnClickListener(this);

        //去除点击背景
        gv_group_header_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 添加头部
        listView.addHeaderView(view1);
    }

    /**
     * 头部参数
     */
    private float density;

    private void setHeaderInfo(GroupJson json) {
        tv_group_header_num.setText(json.user_score + "");
        // 得到像素密度
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        density = outMetrics.density; // 像素密度

        // 根据item的数目，动态设定gridview的宽度
        ViewGroup.LayoutParams params = gv_group_header_gv.getLayoutParams();
        int itemWidth = (int) (25 * density);    //宽    dp
        int spacingWidth = (int) (10 * density); //列间距 dp

        params.width = itemWidth * 6 + (6 - 1) * spacingWidth; //显示的个数(Item)
        gv_group_header_gv.setStretchMode(GridView.NO_STRETCH); // 设置为禁止拉伸模式
        gv_group_header_gv.setNumColumns(6);
        gv_group_header_gv.setHorizontalSpacing(spacingWidth);
        gv_group_header_gv.setColumnWidth(itemWidth);
        gv_group_header_gv.setLayoutParams(params);

        gridViewAdapter = new MyGroupRankingAdapter(getActivity(), json.group_user);
        gv_group_header_gv.setAdapter(gridViewAdapter);
        gridViewAdapter.notifyDataSetChanged();
    }

    private boolean isOK = true;

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.tv_title_left:
                // 群组筛选
                UMengUtils.onCountListener(getActivity(), "GD_03_01");
                if (isOK) {
                    Drawable drawable = getResources().getDrawable(
                            R.drawable.group_title_shang);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                            drawable.getMinimumHeight());
                    tv_title_left
                            .setCompoundDrawables(null, null, drawable, null);

                    FragmentActivity activity = getActivity();
                    groupPopupWindow.showAsDropDown(activity.findViewById(R.id.title_layout));
                    isOK = false;
                } else {
                    refresh3(0);
                }
                break;
            case R.id.iv_title_right2:
                // 右上 - 搜索
                UMengUtils.onCountListener(getActivity(), "GD_03_03");
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", SearchFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);

                SearchFragment.getInstance().needSaveView = false;
                refresh3(0);
                break;
            case R.id.iv_title_right:
                // 发布随笔
                refresh3(0);

                UMengUtils.onCountListener(getActivity(), "GD_03_02");
                intent.setClass(getActivity(), EditTextActivity.class);
                bundle.putInt("key", 1);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);

                PublishFeelingsFragment.getInstance().source = 1;
                PublishFeelingsFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_group_rm:
                // 热门
                tv_group_rm.setTextColor(getActivity().getResources().getColor(
                        R.color.green_17));
                tv_group_zx.setTextColor(getActivity().getResources().getColor(
                        R.color.text_99));
                view_group_rm.setBackgroundResource(R.color.green_17);
                view_group_zx.setBackgroundResource(R.color.e5);
                order = "hot";
                page = 1;
                if (!isOne) {
                    refresh(book_title);
                }
                getData();
                break;
            case R.id.rl_group_zx:
                // 最新
                tv_group_rm.setTextColor(getActivity().getResources().getColor(
                        R.color.text_99));
                tv_group_zx.setTextColor(getActivity().getResources().getColor(
                        R.color.green_17));
                view_group_rm.setBackgroundResource(R.color.e5);
                view_group_zx.setBackgroundResource(R.color.green_17);
                order = "new";
                page = 1;
                if (!isOne) {
                    refresh("所有图书");
                }
                getData();
                break;
            case R.id.rl_group_header_all:
                //头部 - 跳转 群组详情
                UMengUtils.onCountListener(getActivity(), "GD_03_01_01");
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", QunDetailFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);

                QunDetailFragment.getInstance().needSaveView = false;
                QunDetailFragment.getInstance().group_id = group_id;
                break;

            default:
                break;
        }
    }

    /**
     * 跳转 他人主页
     */
    public void goOther(int position) {
        Intent intent = new Intent(getActivity(), NextActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentname", OtherUserFragment.class.getSimpleName());
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);

        OtherUserFragment.getInstance().source = 3;
        OtherUserFragment.getInstance().ta_user_id = list.get(position).user_data.user_id;
        OtherUserFragment.getInstance().needSaveView = false;
    }

    /**
     * 跳转 讨论详情
     */
    public void goDetails(int position) {
        mPosition = position;
        Intent intent = new Intent(getActivity(), NextActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentname", GroupDetailsFragment.class.getSimpleName());
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);

        GroupDetailsFragment.getInstance().source = 1;
        GroupDetailsFragment.getInstance().groupPosition = position;
        GroupDetailsFragment.getInstance().id = list.get(position).id;
        GroupDetailsFragment.getInstance().needSaveView = false;
    }

    /**
     * 爱心(收藏)的点击
     */
    public void duihaoShow(int position) {
        mPosition = position;
        //不作处理
        if (list.get(position).user_data == null) {
            return;
        }

        getFav();
        UMengUtils.onCountListener(getActivity(),
                "youSQ_GD_SC");
    }

    /**
     * 分享的点击
     */
    public void fenxiang(int position) {
        UMengUtils.onCountListener(getActivity(), "GD_03_04");
        mPosition = position;
        FragmentActivity activity = getActivity();
        sharePopupWindow.showAtLocation(activity.findViewById(R.id.ll_group_all),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        // 分享的内容
        if (list.get(position).img_str != null && list.get(position).img_str.length != 0) {
            this.imageUrl = list.get(position).img_str[0];
        } else if (list.get(position).user_data.avatar != null) {
            this.imageUrl = list.get(position).user_data.avatar;
        } else {
            this.imageUrl = "";
        }
        this.h5Url = GlobalConstant.SERVERURL
                .equals("http://api.fengwo.com/m/") ? "http://api.fengwo.com/"
                : "http://gongdu.youshu.cc/" + "share/note?note_id="
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
        UMengUtils.onCountListener(getActivity(), "GD_03_06");

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
     * 网络请求 - 有书圈列表
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("order", order);
        map.put("page", page + "");
        if (!TextUtils.isEmpty(group_id)) {
            map.put("group_id", group_id);
        }

        if (!isOne) {
            switch (order) {
                case "hot":
                    map.put("book_id", book_id);
                    break;
                case "new":
                    map.put("book_id", "0");
                    break;

                default:
                    break;
            }
        } else {
            if (!TextUtils.isEmpty(book_id)) {
                map.put("book_id", book_id);
            }
        }

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.note_list, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
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
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }.start();
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        swipeRefreshLayout.setEnabled(true);
                        if (pullToRefreshListView != null) {
                            pullToRefreshListView.onRefreshComplete();
                        }
                        String jsonString = responseInfo.result;
                        try {
                            GroupJson json = new Gson().fromJson(jsonString,
                                    GroupJson.class);
                            if ("1".equals(json.code)) {
                                if (page == 1) {
                                    list.clear();
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        // 没有数据
                                    } else {
                                        list.addAll(json.data);
                                    }
                                    if (!TextUtils.isEmpty(group_id)) {
                                        if (view1.getVisibility() != View.VISIBLE) {
                                            view1.setPadding(0, 1 * view1.getHeight(), 0, 0);
                                            view1.setVisibility(View.VISIBLE);
                                        }
                                        // 头部参数
                                        setHeaderInfo(json);
                                    } else {
                                        if (view1.getVisibility() != View.GONE) {
                                            view1.setPadding(0, -1 * view1.getHeight(), 0, 0);
                                            view1.setVisibility(View.GONE);
                                        }
                                    }
                                    if (json.group_data != null && json.group_data.size() != 0) {
                                        //群组筛选
                                        groupPopupWindow.setInfo(json);
                                    }
                                } else {
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        page--;
                                    } else {
                                        if (page == 2 && "hot".equals(order) && "1".equals(json.is_push_score)) {
                                            if (System.currentTimeMillis() - SPUtils.getTime(getActivity()) < 300000 || SPUtils.getTime(getActivity()) == 0) {
                                                GroupBean bean = new GroupBean();
                                                bean.setType(GroupAdapter.PINGFEN);
                                                list.add(bean);
                                            } else {
                                                getData2();
                                            }
                                            list.addAll(json.data);
                                        } else {
                                            list.addAll(json.data);
                                        }
                                    }
                                }
                                groupAdapter.notifyDataSetChanged();
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
     * 有书圈 - 引导评分
     */
    private void getData2() {
        //设置为 0
        SPUtils.setTime(getActivity(), 0);
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.note_userscore,
                new RequestCallBack<String>() {
                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                    }
                }, true, null);
    }

    /**
     * 有书圈 - 删除
     */
    private void getDelete() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(mPosition).id);
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.note_del,
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
                                Toast.makeText(getActivity(), "删除成功",
                                        Toast.LENGTH_SHORT).show();
                                // 刷新页面
                                refresh();
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
     * 有书圈 - 收藏
     */
    private void getFav() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(mPosition).id);
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.note_fav,
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
                                list.get(mPosition).is_fav = "1";
                                groupAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "收藏成功",
                                        Toast.LENGTH_SHORT).show();
                            } else if ("2".equals(json.code)) {
                                list.get(mPosition).is_fav = "0";
                                groupAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), "已取消收藏",
                                        Toast.LENGTH_SHORT).show();
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
     * 有书圈 - 举报
     */
    private void getJuBao() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(mPosition).id);
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.note_jubao, new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        Context context = fragment.getActivity();
                        try {
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                Toast.makeText(getActivity(), "举报成功",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            if (context != null) {
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

    public static class SwpipeListViewOnScrollListener implements
            AbsListView.OnScrollListener {

        private SwipeRefreshLayout mSwipeView;
        private AbsListView.OnScrollListener mOnScrollListener;

        public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView) {
            mSwipeView = swipeView;
        }

        public SwpipeListViewOnScrollListener(SwipeRefreshLayout swipeView,
                                              OnScrollListener onScrollListener) {
            mSwipeView = swipeView;
            mOnScrollListener = onScrollListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            View firstView = absListView.getChildAt(firstVisibleItem);

            // 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部,
            // 也需要刷新
            if (firstVisibleItem == 0
                    && (firstView == null || firstView.getTop() <= 30)) {
                mSwipeView.setEnabled(true);
            } else {
                mSwipeView.setEnabled(false);
            }
            if (null != mOnScrollListener) {
                mOnScrollListener.onScroll(absListView, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }
        }
    }

    /**
     * 头部Gridview适配器
     */
    private class MyGroupRankingAdapter extends BaseAdapter {

        private Context context;
        private List<GroupUserBean> list;

        public MyGroupRankingAdapter(Context context, List<GroupUserBean> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            //总数如果超过6个,设置为6
            return list != null ? list.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_group_ranking, parent, false);
                holder.iv_group_ranking_avatar = (ImageView) convertView
                        .findViewById(R.id.iv_group_ranking_avatar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            DisplayImageUtils.displayImage(list.get(position).avatar,
                    holder.iv_group_ranking_avatar, 100, R.drawable.avatar);

            holder.iv_group_ranking_avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("fragmentname", QunDetailFragment.class.getSimpleName());
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_right,
                            R.anim.out_to_left);

                    QunDetailFragment.getInstance().needSaveView = false;
                    QunDetailFragment.getInstance().group_id = group_id;
                    UMengUtils.onCountListener(getActivity(), "GD_03_01_01");
                }
            });
            //总数超过 6 个,第6个显示为总数

            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_group_ranking_avatar;
        }
    }


    /**
     * 记录位置
     */
    private int mPosition;

    /**
     * 群组筛选 - 有书圈
     */
    public void refresh3(int type) {
        Drawable drawable1 = getResources().getDrawable(
                R.drawable.group_title_xia);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
                drawable1.getMinimumHeight());
        tv_title_left
                .setCompoundDrawables(null, null, drawable1, null);

        switch (type) {
            case 1:
                tv_title_left.setText("有书圈");
                page = 1;
                group_id = "";
                getData();
                break;
        }

        isOK = true;
        groupPopupWindow.dismiss();
    }

    public void GO() {
        try {
            String mAddress = "market://details?id=" + getActivity().getPackageName();
            Intent marketIntent = new Intent("android.intent.action.VIEW");
            marketIntent.setData(Uri.parse(mAddress));
            getActivity().startActivity(marketIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "抱歉,未检测到应用商店存在", Toast.LENGTH_SHORT).show();
        }
    }

    public void refresh() {
        list.remove(mPosition);
        groupAdapter.notifyDataSetChanged();
    }

    /**
     * 请求接口,本月不再显示引导评分
     */
    public void refresh2(boolean isOK) {
        if (isOK) {
            SPUtils.setTime(getActivity(), System.currentTimeMillis());
        } else {
            getData2();
        }
    }

    /**
     * 搜索 - 选择书籍后刷新方法
     */
    public void refresh(String id, String title) {
        isOne = true;
        book_id = id;
        book_title = title;
        tv_group_book.setText("《" + book_title + "》");
        page = 1;
        getData();
    }

    /**
     * 选择按书籍排序后更新
     */
    public void refresh(String bookName) {
        if (!TextUtils.isEmpty(bookName)) {
            if ("所有图书".equals(bookName)) {
                tv_group_book.setText(bookName);
            } else {
                tv_group_book.setText("《" + bookName + "》");
            }
        } else {
            tv_group_book.setText("");
        }
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
            new Thread() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }.start();
            if ("1".equals(level_is_up)) {
                startActivity(new Intent(getActivity(), UpgradeActivity.class));
                level_is_up = "";
            }
            is_refresh = false;
            getData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("GroupFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("GroupFragment");
    }
}
