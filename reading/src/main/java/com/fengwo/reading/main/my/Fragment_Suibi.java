package com.fengwo.reading.main.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectPicPopupWindow;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.group.GroupAdapter;
import com.fengwo.reading.main.group.GroupBean;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.group.GroupJson;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的随笔
 *
 * @author Luo Sheng
 * @date 2016-4-11
 */
public class Fragment_Suibi extends Fragment implements OnClickListener {

    private ImageView iv_title_left;
    private TextView tv_title_mid;
    private RelativeLayout ll_group_all;
    private LinearLayout title_layout;

    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    //头部
    private RelativeLayout rl_header;
    private TextView tv_my_suibi_name, tv_my_suibi_name2;
    private ImageView iv_my_suibi_icon, iv_my_suibi_gender, iv_my_suibi_xunzhang, iv_bg;
    private Button iv_return;

    private TextView tv_group_empty;

    private CustomProgressDialog progressDialog;

    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    // 分享的信息
    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    private List<GroupBean> list;
    private GroupAdapter groupAdapter;
    private boolean is_loading; //
    private int page; // 当前页

    private View saveView = null;
    public boolean needSaveView = false;
    public boolean is_refresh = false; // 是否刷新

    private SelectPicPopupWindow picPopupWindow;

    private OnClickListener avatarListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_popupwindow_takephoto:// 拍照
                    picPopupWindow.dismiss();
                    takePhone();
                    break;
                case R.id.btn_popupwindow_pickphoto:// 照片
                    picPopupWindow.dismiss();
                    seelectPhoto();
                    break;
                default:
                    break;
            }
        }
    };

    public static Fragment_Suibi fragment = new Fragment_Suibi();

    public static Fragment_Suibi getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_suibi, container, false);
        findViewById(view);
        setTitle();

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = pullToRefreshListView.getRefreshableView();

        // 添加头部
        addHeaderView();

        progressDialog = CustomProgressDialog.createDialog(fragment
                .getActivity());

        list = new ArrayList<>();
        groupAdapter = new GroupAdapter(fragment, list, 4);
        listView.setAdapter(groupAdapter);

        is_loading = false;
        page = 1;

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                is_loading = true;
                getData();
            }
        }, 300);

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
                        getData();
                    }
                });

        //滚动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当状态发生改变时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:// 屏幕停止滚动时
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:// 惯性滑动时
                        break;
                }
            }

            /**
             * firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
             * visibleItemCount：当前能看见的列表项个数（小半个也算） totalItemCount：列表项共数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    // 滚动到顶部
                    title_layout.setBackgroundColor(Color.TRANSPARENT);
                    tv_title_mid.setVisibility(View.GONE);
                } else {
                    title_layout.setBackgroundColor(getActivity().getResources().getColor(R.color.green_17));
                    tv_title_mid.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private void findViewById(View view) {
        title_layout = (LinearLayout) view.findViewById(R.id.title_layout);
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        ll_group_all = (RelativeLayout) view.findViewById(R.id.ll_group_all1);
        pullToRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.lv_group);

        tv_group_empty = (TextView) view.findViewById(R.id.tv_group_empty);

        iv_title_left.setOnClickListener(this);
    }

    private void setTitle() {
        title_layout.setBackgroundColor(Color.TRANSPARENT);
        tv_title_mid.setText("我的随笔");
        tv_title_mid.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_return:
                fragment.getActivity().finish();
                fragment.getActivity().overridePendingTransition(
                        R.anim.in_from_left, R.anim.out_to_right);
                // fragment.getActivity().getSupportFragmentManager().popBackStack();
                break;

            default:
                break;
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
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
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
     * 有书圈列表 - 网络请求
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("page", page + "");
        map.put("type", "me");

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.user_fav,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
                        is_loading = false;
                        if (pullToRefreshListView != null) {
                            // 收起尾部局动画
                            pullToRefreshListView.onRefreshComplete();
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        is_loading = false;
                        if (pullToRefreshListView != null) {
                            pullToRefreshListView.onRefreshComplete();
                        }
                        String jsonString = responseInfo.result;
                        try {
//                            System.out.println("----jsonString: " + jsonString);
                            GroupJson json = new Gson().fromJson(jsonString,
                                    GroupJson.class);
                            if ("1".equals(json.code)) {
                                if (page == 1) {
                                    list.clear();
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        // 没有数据
                                        // Context context =
                                        // fragment.getActivity();
                                        // if (context != null
                                        // && !((Activity)
                                        // context).isFinishing()) {
                                        // CustomToast.showToast(context,
                                        // "暂无数据");
                                        // }
                                        tv_group_empty
                                                .setVisibility(View.VISIBLE);
//                                        swipeRefreshLayout.setVisibility(View.GONE);
                                        tv_group_empty.setText("你还没有发布随笔哦");
                                    } else {
                                        tv_group_empty.setVisibility(View.GONE);
                                        list.addAll(json.data);
                                    }
                                } else {
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        page--;
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
     * 有书圈 - 删除
     */
    private void getDelete() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(mPosition).id);
        map.put("soft", VersionUtils.getVersion(getActivity()));
        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.note_del,
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
                                list.remove(mPosition);
                                groupAdapter.notifyDataSetChanged();
                                if (list.size() == 0) {
                                    tv_group_empty.setVisibility(View.VISIBLE);
                                }
                                Context context = fragment.getActivity();
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, "删除成功");
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
     * 有书圈 - 收藏
     */
    private void getFav() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(mPosition).id);
        map.put("soft", VersionUtils.getVersion(getActivity()));
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
     * 有书圈 - 举报
     */
    private void getJuBao() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", list.get(mPosition).id);
        map.put("soft", VersionUtils.getVersion(getActivity()));
        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.note_jubao, new RequestCallBack<String>() {

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
                                Context context = fragment.getActivity();
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, "举报成功");
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
     * 记录位置
     */
    private int mPosition;

    public void refresh1() {
        getData();
    }

    /**
     * 刷新方法
     */
    public void refresh(String level_is_up) {
        TextView textView = (TextView) listView.findViewWithTag("pinglun"
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
        }

        ;
    };


    @Override
    public void onStart() {
        super.onStart();
        //给头部控件赋初始值
        if (GlobalParams.userInfoBean == null) {
            iv_my_suibi_icon.setImageResource(R.drawable.avatar);
            iv_my_suibi_gender.setImageResource(R.drawable.boy);
            Drawable drawable = new BitmapDrawable();
            drawable.setBounds(0, 0, 40, 50);
            iv_my_suibi_xunzhang.setImageDrawable(drawable);
            tv_my_suibi_name.setText("");
            tv_my_suibi_name2.setText("亲，您还没有签名哦");
        } else {
//            DisplayImageUtils.displayImage("File://"+Environment.getExternalStorageDirectory()+"suibi.png", iv_bg, 100, R.drawable.avatar);
//            todo
            if (new File(Environment.getExternalStorageDirectory() + "/suibi.png").exists()){
                iv_bg.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/suibi.png"));

            }else {
            DisplayImageUtils.displayImage(GlobalParams.userInfoBean.user_img, iv_bg, 0, R.drawable.background);
            }
            DisplayImageUtils.displayImage(GlobalParams.userInfoBean.avatar, iv_my_suibi_icon, 100, R.drawable.avatar);
            DisplayImageUtils.displayImage(GlobalParams.userInfoBean.sex, iv_my_suibi_gender, 100, R.drawable.boy);
            iv_my_suibi_xunzhang.setImageDrawable(GlobalParams.userInfoBean.badge_Drawable(0.12f, 0.12f));
            tv_my_suibi_name.setText(GlobalParams.userInfoBean.name);
            if (GlobalParams.userInfoBean.intro.isEmpty()) {
                tv_my_suibi_name2.setText("亲，您还没有签名哦");
            } else {
                tv_my_suibi_name2.setText(GlobalParams.userInfoBean.intro);
            }

            iv_return.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.in_from_left,
                            R.anim.out_to_right);
                }
            });
            iv_bg.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    picPopupWindow = new SelectPicPopupWindow(getActivity(),
                            avatarListener);
                    picPopupWindow.setBtnText("拍照", "相册");
                    picPopupWindow.showAtLocation(
                            getActivity().findViewById(R.id.ll_group_all1), Gravity.BOTTOM
                                    | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            });

        }
        if (is_refresh) {
            is_refresh = false;
            getData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment_Suibi");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("Fragment_Suibi");
    }

    private void addHeaderView() {
        View view1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.my_suibi, null);
        iv_return = (Button) view1.findViewById(R.id.iv_return);
        rl_header = (RelativeLayout) view1.findViewById(R.id.rl_header);
        iv_bg = (ImageView) view1.findViewById(R.id.iv_bg);
        tv_my_suibi_name = (TextView) view1.findViewById(R.id.tv_my_suibi_name);
        tv_my_suibi_name2 = (TextView) view1.findViewById(R.id.tv_my_suibi_name2);
        iv_my_suibi_icon = (ImageView) view1.findViewById(R.id.iv_my_suibi_icon);
        iv_my_suibi_gender = (ImageView) view1.findViewById(R.id.iv_my_suibi_gender);
        iv_my_suibi_xunzhang = (ImageView) view1.findViewById(R.id.iv_my_suibi_xunzhang);

        iv_return.setVisibility(View.VISIBLE);

        // 添加头部
        listView.addHeaderView(view1);
    }

    private void takePhone() {// 点击拍照按钮选择头像的事件
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            doTakePhoto();// 执行从照相机获取
        } else {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "没有SD卡", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory().getPath() + GlobalParams.FolderPath_Shoot);
    private File mCurrentPhotoFile;// 照相机拍照得到的图片

    /**
     * 用当前时间给取得的图片命名
     */
    @SuppressLint("SimpleDateFormat")
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {// protected
        try {
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用系统相机
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            Uri imageUri = Uri.fromFile(mCurrentPhotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//指定照片保存路径
            startActivityForResult(intent, CAMERA_WITH_DATA);  //用户点击了从相机获取
        } catch (ActivityNotFoundException e) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "选择的照片未发现", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /* 用来标识请求gallery的activity */
    private static final int PHOTO_REQUEST_GALLERY = 3021;// 从相册中选择

    private void seelectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode != Activity.RESULT_OK)
                return;
            switch (requestCode) {
                case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                    scanPhotos(mCurrentPhotoFile.getAbsolutePath(), getActivity());// 获得新拍图片路径,然后刷新图库,让其出现在图库中
                    doCropPhoto(mCurrentPhotoFile);
                    break;
                }
                case CROP_PHOTO:// 剪辑完成后返回调用
                    if (data == null) {
                        return;
                    }
                    final Bitmap photo = data.getParcelableExtra("data");
//                    Bitmap bitmap = PicUtil.getRoundedCornerBitmap(photo, 2);
                    //todo

                    iv_bg.setImageBitmap(photo);
                    try {
                        boolean flag = photo.compress(
                                Bitmap.CompressFormat.PNG,
                                100,
                                new FileOutputStream(new File(Environment
                                        .getExternalStorageDirectory(),
                                        "suibi.png")));

                        if (flag) {
//                            iv_bg.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/suibi.png"));
                            getDataAvatar();
//                            getDataAvatar1();
//                            new Task_updateuserinfo();
//                            Log.i("szf11", GlobalParams.userInfoBean.user_img);
//                            DisplayImageUtils.displayImage(GlobalParams.userInfoBean.user_img, iv_bg, 0, R.drawable.background);

                        } else {
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    }
                    break;

            }
        } catch (Exception e) {
        }
    }

    /**
     * 扫描、刷新相册
     */
    private void scanPhotos(String filePath, Context context) {// public static
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    private void doCropPhoto(File f) {// protected
        try {
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(Uri.fromFile(f));
            startActivityForResult(intent, CROP_PHOTO);
        } catch (Exception e) {
        }
    }

    /* 用来标识请求照片剪辑功能的activity */
    private static final int CROP_PHOTO = 3024;

    /**
     * 调用图片剪辑程序
     */
    private Intent getCropImageIntent(Uri photoUri) {// public static
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 0.6);
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 60);
//        intent.putExtra("scale", false);
        intent.putExtra("return-data", true);

        return intent;
    }

    /**
     * 获取数据(个人信息保存)
     */
    private void getDataAvatar() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("soft", VersionUtils.getVersion(getActivity()));
        map.put("type", "me");
        Map<String, File> map2 = new HashMap<String, File>();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "suibi.png");
        map2.put("user_img", file);

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
                        Log.i("suibi", jsonString);
                        MLog.v("suibi", jsonString);
                        try {
                            UserinfoSaveJson json = new Gson().fromJson(
                                    jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                if (json.user_data != null) {
                                    GlobalParams.userInfoBean = json.user_data;
                                    Log.i("szf", GlobalParams.userInfoBean.user_img);
                                    NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
                                }
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    CustomToast.showToast(context, "背景修改成功");
//                                    CustomToast.showToast(context, jsonString);
                                }
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
                }, true, map2);

    }

    /*private void getDataAvatar1() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("soft", VersionUtils.getVersion(getActivity()));
        map.put("type", "me");
//        Map<String, File> map2 = new HashMap<String, File>();
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
//                "suibi.png");
//        map2.put("user_img", file);

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
                        Log.i("suibi", jsonString);
                        MLog.v("suibi", jsonString);
                        try {
                            UserinfoSaveJson json = new Gson().fromJson(
                                    jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                if (json.user_data != null) {
                                    GlobalParams.userInfoBean = json.user_data;
                                    Log.i("szf", GlobalParams.userInfoBean.user_img);
                                    NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
                                }
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    CustomToast.showToast(context, "背景修改成功");
//                                    CustomToast.showToast(context, jsonString);
                                }
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
        DisplayImageUtils.displayImage(GlobalParams.userInfoBean.user_img, iv_bg, 0, R.drawable.background);
    }*/


    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 0.6);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 90);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, CROP_PHOTO);
    }
}
