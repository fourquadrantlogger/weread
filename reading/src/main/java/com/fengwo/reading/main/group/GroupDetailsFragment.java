package com.fengwo.reading.main.group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.EditTextActivity;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectListViewPopupWindow;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.comment.EmojiActivity;
import com.fengwo.reading.main.comment.CommentAdapter;
import com.fengwo.reading.main.comment.CommentBean;
import com.fengwo.reading.main.comment.CommentListJson;
import com.fengwo.reading.main.comment.ConversationFragment;
import com.fengwo.reading.main.discover.ACEFragment;
import com.fengwo.reading.main.discover.ChoicenessBooksDetailsFragment;
import com.fengwo.reading.main.discover.DiscoverFragment;
import com.fengwo.reading.main.discover.hottopics.TopicsActivity;
import com.fengwo.reading.main.my.Fragment_Suibi;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.qq.ImageBrowserActivity;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.DateUtils;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.ListUtils;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.view.TextView_copypaste;
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
 * 有书圈 - 评论详情
 *
 * @author Luo Sheng
 * @date 2016-1-27
 */
public class GroupDetailsFragment extends Fragment implements OnClickListener {

    private SelectListViewPopupWindow listViewPopupWindow;
    //    private PullToRefreshScrollView sv_groupdetails;
//    private ScrollView scrollView;
    private CustomProgressDialog progressDialog;

    private ImageView iv_groupdetails_avatar, iv_groupdetails_sex,
            iv_title_left, iv_title_right, iv_groupdetails_zan_img, iv_bookpart_wx, iv_bookpart_pyq;
    private TextView_copypaste tv_groupdetails_content;
    private TextView tv_groupdetails_name, tv_groupdetails_time,
            tv_groupdetails_title, tv_groupdetails_num, tv_groupdetails_liuyan,
            tv_groupdetails_dianzan, tv_groupdetails_zan_num, tv_title_mid,
            tv_groupdetails_liuyan_num, tv_bookpart_sc, tv_groupdetails_header_num;
    private GridView gv_groupdetails_show, gv_groupdetails_header_gv;
    private LinearLayout ll_groupdetails_z;
    private RelativeLayout rl_groupdetails, rl_groupdetails_fenxiang,
            rl_groupdetails_liuyan, rl_groupdetails_zan,
            rl_groupdetails_avatar, rl_bookpart_sc, rl_groupdetails_header_all;

    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;

    private MyGroupRankingAdapter gridViewAdapter; //头部点赞Gridview
    private CommentAdapter adapter;
    private List<CommentBean> list;
    private CommentBean bean = new CommentBean();
    private int digg_count;
    private int comment_count;
    private int comment_type;

    private GroupDetailsJson detailsJson;
    private float density;

    public String id = "";// 笔记id
    private String user_id = "";
    public String is_digg = "0";// 1:赞过
    private String is_fav = "";// 1:已收藏
    private String act = "";// c取消赞，空值代表点赞
    private boolean isMy = false;// 是否是自己
    public int groupPosition = 0;// 有书圈的位置

    private boolean is_shanchu; //
    private boolean is_loading; //
    private int page; // 当前页
    private int mPosition = 0;// 点击ListView的位置标记

    private View saveView = null;
    public boolean needSaveView = false;

    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    public int source = 0;
    // 来源 1:有书圈 2:他人主页 3:Fragment_Suibi,4:ReadFragment,5消息通知
    // 6话题详情,7搜索-精选,8搜索-所有,9发现-书评,10发现-精选

    public GroupDetailsFragment() {
    }

    public static GroupDetailsFragment fragment = new GroupDetailsFragment();

    public static GroupDetailsFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_groupdetails, container,
                false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());
        findViewById(view);
        setTitle();

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = pullToRefreshListView.getRefreshableView();
        // 添加头部
        addHeaderView();
        onClickListener();

        is_loading = false;
        page = 1;
        list = new ArrayList<>();
        adapter = new CommentAdapter(fragment, list);
        listView.setAdapter(adapter);
//        listView.setFocusable(false);

        handler.sendEmptyMessageDelayed(3, 300);

        pullToRefreshListView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 执行刷新函数
                        is_loading = true;
                        page++;
                        getData1();
                    }
                });

        // 弹出窗体
        listViewPopupWindow = new SelectListViewPopupWindow(
                fragment.getActivity(), new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long _id) {
                FragmentTransaction transaction = fragment
                        .getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right,
                        R.anim.out_to_left, R.anim.in_from_left,
                        R.anim.out_to_right);
                Intent intent1 = new Intent(getActivity(), EmojiActivity.class);
                Bundle bundle1 = new Bundle();
                switch (position) {
                    case 0:
                        // 回复
                        comment_type = 1;
                        bundle1.putInt("source", 2);
                        bundle1.putInt("comment_type", 1);
                        bundle1.putString("id", id);
                        bundle1.putString("name", list
                                .get(mPosition).user_data.name);
                        bundle1.putString("cid", list
                                .get(mPosition).id);// 评论id
                        intent1.putExtras(bundle1);
                        getActivity().startActivity(intent1);
                        getActivity().overridePendingTransition(R.anim.push_bottom_in,
                                R.anim.push_bottom_out);
                        break;
                    case 1:
                        // 投诉
                        getTouSu();
                        break;
                    case 2:
                        // 查看对话
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.ll_activity_next,
                                ConversationFragment.getInstance());
                        transaction.commit();

                        ConversationFragment.getInstance().needSaveView = false;
                        ConversationFragment.getInstance().type = 1;
                        ConversationFragment.getInstance().id = id;
                        ConversationFragment.getInstance().ruser_id = list
                                .get(mPosition).user_data.user_id;
                        ConversationFragment.getInstance().cuser_id = list
                                .get(mPosition).reply_user_data.user_id;
                        break;
                    case 3:
                        // 删除 true:评论 false:笔记
                        if (is_shanchu) {
                            getDelete();
                        } else {
                            Dialog dialog = new AlertDialog.Builder(getActivity())
                                    .setTitle("确认要删除掉这篇随笔吗?")
                                    .setPositiveButton("删除",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    getNoteDelete();
                                                }
                                            })
                                    .setNegativeButton("取消",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int whichButton) {

                                                }
                                            }).create();
                            dialog.show();
                        }
                        break;
                    case 4:
                        // 收藏
                        UMengUtils.onCountListener(getActivity(), "GD_03_07_01_01");
                        getFav();
                        break;
                    case 5:
                        // 编辑
                        Intent intent = new Intent(getActivity(), EditTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("key", 1);
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(
                                R.anim.in_from_right, R.anim.out_to_left);

                        PublishFeelingsFragment.getInstance().source = 2;
                        PublishFeelingsFragment.getInstance().needSaveView = false;
                        PublishFeelingsFragment.getInstance().bean = detailsJson.data;
                        PublishFeelingsFragment.getInstance().id = id;
                        break;
                    case 6:
                        // 评论
                        comment_type = 0;
                        bundle1.putInt("source", 2);
                        bundle1.putInt("comment_type", 0);
                        bundle1.putString("id", detailsJson.data.id);
                        bundle1.putString("name", detailsJson.data.user_data.name);
                        intent1.putExtras(bundle1);
                        getActivity().startActivity(intent1);
                        getActivity().overridePendingTransition(R.anim.push_bottom_in,
                                R.anim.push_bottom_out);
                        break;
                    case 7:
                        // 举报
                        UMengUtils.onCountListener(getActivity(), "GD_03_07_01_02");
                        getJuBao();
                        break;

                    default:
                        break;
                }
                listViewPopupWindow.dismiss();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (list.get(0).user_data == null) {
                    return;
                }
                mPosition = position - 2;
                if (GlobalParams.uid.equals(list.get(mPosition).user_data.user_id)) {
                    listViewPopupWindow.setData(0, "");
                    listViewPopupWindow.setData(1, "");
                    if ("1".equals(list.get(mPosition).comment_type)) {
                        listViewPopupWindow.setData(2, "查看对话");
                    } else {
                        listViewPopupWindow.setData(2, "");
                    }
                    listViewPopupWindow.setData(3, "删除");
                    listViewPopupWindow.setData(4, "");
                    listViewPopupWindow.setData(5, "");
                    listViewPopupWindow.setData(6, "");
                    listViewPopupWindow.setData(7, "");
                    is_shanchu = true;
                } else {
                    listViewPopupWindow.setData(0, "回复");
                    listViewPopupWindow.setData(1, "");
                    if ("1".equals(list.get(mPosition).comment_type)) {
                        listViewPopupWindow.setData(2, "查看对话");
                    } else {
                        listViewPopupWindow.setData(2, "");
                    }
                    listViewPopupWindow.setData(3, "");
                    listViewPopupWindow.setData(4, "");
                    listViewPopupWindow.setData(5, "");
                    listViewPopupWindow.setData(6, "");
                    listViewPopupWindow.setData(7, "");
                }
                listViewPopupWindow.showAtLocation(fragment.getActivity()
                        .findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        listViewPopupWindow.addData(new String[]{"回复", "", "查看对话", "删除",
                "收藏", "编辑", "评论", "举报"});

        // GridView的尺寸
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = (metric.widthPixels / 3);

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        iv_title_right = (ImageView) view.findViewById(R.id.iv_title_right);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_groupdetails_show);

        rl_groupdetails_fenxiang = (RelativeLayout) view
                .findViewById(R.id.rl_groupdetails_fenxiang);
        tv_groupdetails_liuyan = (TextView) view
                .findViewById(R.id.tv_groupdetails_liuyan);
        rl_groupdetails_liuyan = (RelativeLayout) view
                .findViewById(R.id.rl_groupdetails_liuyan);
        tv_groupdetails_liuyan_num = (TextView) view
                .findViewById(R.id.tv_groupdetails_liuyan_num);
        iv_groupdetails_zan_img = (ImageView) view
                .findViewById(R.id.iv_groupdetails_zan_img);
        rl_groupdetails_zan = (RelativeLayout) view
                .findViewById(R.id.rl_groupdetails_zan);
        tv_groupdetails_zan_num = (TextView) view
                .findViewById(R.id.tv_groupdetails_zan_num);
    }

    private void setTitle() {
        tv_title_mid.setVisibility(View.VISIBLE);
        iv_title_right.setVisibility(View.VISIBLE);
        tv_title_mid.setText("随笔详情");
        iv_title_right.setImageResource(R.drawable.groupdetails_dian_black);
    }

    private void addHeaderView() {
        View view1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.head_groupdetails, null);

        iv_groupdetails_avatar = (ImageView) view1
                .findViewById(R.id.iv_groupdetails_avatar);
        iv_groupdetails_sex = (ImageView) view1
                .findViewById(R.id.iv_groupdetails_sex);

        rl_groupdetails = (RelativeLayout) view1
                .findViewById(R.id.rl_groupdetails);
        gv_groupdetails_show = (GridView) view1
                .findViewById(R.id.gv_groupdetails_show);
        rl_groupdetails_avatar = (RelativeLayout) view1
                .findViewById(R.id.rl_groupdetails_avatar);

        rl_bookpart_sc = (RelativeLayout) view1
                .findViewById(R.id.rl_bookpart_sc);
        tv_bookpart_sc = (TextView) view1
                .findViewById(R.id.tv_bookpart_sc);
        iv_bookpart_wx = (ImageView) view1
                .findViewById(R.id.iv_bookpart_wx);
        iv_bookpart_pyq = (ImageView) view1
                .findViewById(R.id.iv_bookpart_pyq);
        rl_groupdetails_header_all = (RelativeLayout) view1
                .findViewById(R.id.rl_groupdetails_header_all);
        tv_groupdetails_header_num = (TextView) view1
                .findViewById(R.id.tv_groupdetails_header_num);
        gv_groupdetails_header_gv = (GridView) view1
                .findViewById(R.id.gv_groupdetails_header_gv);

//        ll_groupdetails_z = (LinearLayout) view1
//                .findViewById(R.id.ll_groupdetails_z);

        tv_groupdetails_dianzan = (TextView) view1
                .findViewById(R.id.tv_groupdetails_dianzan);
        tv_groupdetails_dianzan.setVisibility(View.GONE);

        tv_groupdetails_name = (TextView) view1
                .findViewById(R.id.tv_groupdetails_name);
        tv_groupdetails_time = (TextView) view1
                .findViewById(R.id.tv_groupdetails_time);
        tv_groupdetails_title = (TextView) view1
                .findViewById(R.id.tv_groupdetails_title);
        tv_groupdetails_num = (TextView) view1
                .findViewById(R.id.tv_groupdetails_num);
        tv_groupdetails_content = (TextView_copypaste) view1
                .findViewById(R.id.tv_groupdetails_content);

        //去除点击背景
        gv_groupdetails_header_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 添加头部
        listView.addHeaderView(view1);
    }

    private void onClickListener() {
        rl_groupdetails_avatar.setOnClickListener(this);
        rl_groupdetails_fenxiang.setOnClickListener(this);
        rl_groupdetails_liuyan.setOnClickListener(this);
        tv_groupdetails_liuyan.setOnClickListener(this);
        rl_groupdetails_zan.setOnClickListener(this);
        iv_title_left.setOnClickListener(this);
        iv_title_right.setOnClickListener(this);
        rl_groupdetails.setOnClickListener(this);
        rl_bookpart_sc.setOnClickListener(this);
        iv_bookpart_wx.setOnClickListener(this);
        iv_bookpart_pyq.setOnClickListener(this);
        rl_groupdetails_header_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        switch (v.getId()) {
            case R.id.iv_return:
                switch (source) {
                    case 1:
                    case 4:
                    case 6:
                    case 9:
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.in_from_left,
                                R.anim.out_to_right);
                        break;
                    case 2:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.iv_title_right:
                //右上 - 更多
                UMengUtils.onCountListener(getActivity(), "GD_03_07_01");
                if (GlobalParams.uid.equals(detailsJson.data.user_data.user_id)) {
                    is_shanchu = false;
                    listViewPopupWindow.setData(0, "");
                    listViewPopupWindow.setData(1, "");
                    listViewPopupWindow.setData(2, "");
                    listViewPopupWindow.setData(3, "删除");
                    if ("1".equals(is_fav)) {
                        listViewPopupWindow.setData(4, "取消收藏");
                    } else {
                        listViewPopupWindow.setData(4, "收藏");
                    }
                    listViewPopupWindow.setData(5, "编辑");
                    listViewPopupWindow.setData(6, "");
                    listViewPopupWindow.setData(7, "");
                } else {
                    listViewPopupWindow.setData(0, "");
                    listViewPopupWindow.setData(1, "");
                    listViewPopupWindow.setData(2, "");
                    listViewPopupWindow.setData(3, "");
                    if ("1".equals(is_fav)) {
                        listViewPopupWindow.setData(4, "取消收藏");
                    } else {
                        listViewPopupWindow.setData(4, "收藏");
                    }
                    listViewPopupWindow.setData(5, "");
                    listViewPopupWindow.setData(6, "");
                    listViewPopupWindow.setData(7, "举报");
                }
                listViewPopupWindow.showAtLocation(fragment.getActivity()
                        .findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;

            case R.id.rl_groupdetails:
                transaction.replace(R.id.ll_activity_next,
                        OtherUserFragment.getInstance());
                transaction.commit();
                OtherUserFragment.getInstance().source = 2;
                OtherUserFragment.getInstance().ta_user_id = detailsJson.data.user_id;
                OtherUserFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_groupdetails_fenxiang:
                //分享
                UMengUtils.onCountListener(getActivity(), "GD_03_07_02");
                FragmentActivity activity = fragment.getActivity();
                sharePopupWindow = new SelectSharePopupWindow(activity,
                        itemsOnClick);
                sharePopupWindow.showAtLocation(
                        activity.findViewById(R.id.ll_activity_next),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                this.imageUrl = "";
                this.h5Url = GlobalConstant.ServerDomain + "share/note?note_id="
                        + detailsJson.data.id;
                break;
            case R.id.rl_groupdetails_liuyan:
                //跳转 评论区
                UMengUtils.onCountListener(getActivity(), "GD_03_07_04");
                listView.setSelected(true);
                if (android.os.Build.VERSION.SDK_INT >= 8) {
                    listView.smoothScrollToPosition(2);
                } else {
                    listView.setSelection(2);
                }
                break;
            case R.id.tv_groupdetails_liuyan:
                //写评论
                UMengUtils.onCountListener(getActivity(), "GD_03_07_03");
                comment_type = 0;
                Intent intent = new Intent(getActivity(), EmojiActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("source", 2);
                bundle.putInt("comment_type", 0);
                bundle.putString("id", id);
                bundle.putString("name", "");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_bottom_in,
                        R.anim.push_bottom_out);
                break;
            case R.id.rl_groupdetails_zan:
                //点赞
                UMengUtils.onCountListener(getActivity(), "GD_03_07_05");
                zanGetData();
                break;
            case R.id.rl_groupdetails_avatar:
                if (detailsJson.data.user_data.avatar != null) {
                    ImageBrowserActivity.position = 0;
                    String newStr = detailsJson.data.user_data.avatar.substring(0,
                            detailsJson.data.user_data.avatar.indexOf("@"));
                    List<String> stringList = new ArrayList<String>();
                    stringList.add(newStr);
                    ImageBrowserActivity.mList = stringList;
                    getActivity().startActivity(new Intent(getActivity(), ImageBrowserActivity.class));
                }
                break;
            case R.id.rl_bookpart_sc:
                //收藏
                UMengUtils.onCountListener(getActivity(), "GD_03_07_01_01");
                getFav();
                break;
            case R.id.iv_bookpart_wx:
                //分享 - 微信
                UMShare.setUMeng(getActivity(), 1, title, content, imageUrl, h5Url, id, "note");
                sharePopupWindow.dismiss();
                if ("1".equals(UMShare.getLevel())) {
                    startActivity(new Intent(getActivity(), UpgradeActivity.class));
                }
                break;
            case R.id.iv_bookpart_pyq:
                //分享 - 朋友圈
                UMShare.setUMeng(getActivity(), 2, title, content, imageUrl, h5Url, id, "note");
                sharePopupWindow.dismiss();
                if ("1".equals(UMShare.getLevel())) {
                    startActivity(new Intent(getActivity(), UpgradeActivity.class));
                }
                break;
            case R.id.rl_groupdetails_header_all:
                //赞过的人
                transaction.replace(R.id.ll_activity_next,
                        ACEFragment.getInstance());
                transaction.commit();
                ACEFragment.getInstance().needSaveView = false;
                ACEFragment.getInstance().source = 2;
                ACEFragment.getInstance().id = id;
                break;

            default:
                break;
        }
    }

    /**
     * 有书圈详情 - 网络请求
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);
        map.put("type", "note");

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.bp_info,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
                        // swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        // swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        // swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        // swipeRefreshLayout.setEnabled(true);
                        String jsonString = responseInfo.result;
                        try {
//                            System.out.println("------123:" + jsonString);
                            detailsJson = new Gson().fromJson(jsonString,
                                    GroupDetailsJson.class);
                            if ("1".equals(detailsJson.code)) {
                                // 设置数据
                                setinfo(detailsJson);
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, detailsJson.msg,
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
     * 有书圈详情 - 笔记评论列表 - 网络请求
     */
    private void getData1() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);
        map.put("type", "note");
        map.put("page", page + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.comment_list, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        pullToRefreshListView.onRefreshComplete();
                        // swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        // swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        pullToRefreshListView.onRefreshComplete();
                        // swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        // swipeRefreshLayout.setEnabled(true);
                        String jsonString = responseInfo.result;
                        try {
                            CommentListJson json = new Gson().fromJson(
                                    jsonString, CommentListJson.class);
//                            System.out.println("------456:" + GlobalParams.uid + " , " + id + " , " + jsonString);
                            if ("1".equals(json.code)) {
                                if (page == 1) {
                                    list.clear();
                                    if (json.data == null
                                            || json.data.size() == 0) {
                                        // 没有数据
                                        page = 0;
                                        list.add(bean);
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
                                adapter.notifyDataSetChanged();
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
     * 有书圈详情 - 评论删除
     */
    private void getDelete() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("cid", list.get(mPosition).id);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.comment_del, new RequestCallBack<String>() {

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
                                Toast.makeText(getActivity(), "评论删除成功",
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
     * 有书圈详情 - 笔记删除
     */
    private void getNoteDelete() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);

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
                                Toast.makeText(getActivity(), "笔记删除成功",
                                        Toast.LENGTH_SHORT).show();
                                switch (source) {
                                    case 1:
                                        // 刷新页面
                                        GroupFragment.getInstance().refresh();
                                        getActivity().finish();
                                        getActivity().overridePendingTransition(
                                                R.anim.in_from_left,
                                                R.anim.out_to_right);
                                        break;
                                    case 6:
                                        TopicsActivity.Activity.is_refresh = true;
                                        getActivity().finish();
                                        getActivity().overridePendingTransition(
                                                R.anim.in_from_left,
                                                R.anim.out_to_right);
                                        break;
                                    case 4:
                                    case 9:
                                        getActivity().finish();
                                        getActivity().overridePendingTransition(
                                                R.anim.in_from_left,
                                                R.anim.out_to_right);
                                        break;
                                    case 3:
                                        Fragment_Suibi.getInstance().refresh1();
                                        getActivity().getSupportFragmentManager()
                                                .popBackStack();
                                        break;
                                    case 2:
                                    case 5:
                                    case 7:
                                    case 8:
                                    case 10:
                                        getActivity().getSupportFragmentManager()
                                                .popBackStack();
                                        break;
                                    default:
                                        break;
                                }
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
     * 有书圈 - 投诉
     */
    private void getTouSu() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("cid", list.get(mPosition).id);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.comment_tousu, new RequestCallBack<String>() {

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
                                Toast.makeText(getActivity(), "投诉成功",
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
     * 有书圈详情 - 信息设置
     */
    private void setinfo(GroupDetailsJson json) {
        DisplayImageUtils.displayImage(json.data.user_data.avatar,
                iv_groupdetails_avatar, 100, R.drawable.avatar);
        // 等级
//        if (!TextUtils.isEmpty(json.data.user_data.level)) {
//            int i = Integer.valueOf(json.data.user_data.level).intValue();
//            VipImageUtil.getVipGrade(getActivity(), iv_groupdetails_sex, i, 1);
//        } else {
//            iv_groupdetails_sex.setVisibility(View.GONE);
//        }
        tv_groupdetails_name.setText(json.data.user_data.name);
//        tv_groupdetails_name.setCompoundDrawables(null, null, json.data.user_data.badge_Drawable(0.12f, 0.12f), null);
        tv_groupdetails_time.setText(DateUtils.getTime(json.data.create_time));
        if (!TextUtils.isEmpty(json.data.title)) {
            tv_groupdetails_title.setVisibility(View.VISIBLE);
            tv_groupdetails_title.setText(json.data.title);
        } else {
            tv_groupdetails_title.setVisibility(View.GONE);
        }
        tv_groupdetails_num.setText(json.data.read_count + "阅读");
        // 内容 #话题#
        ListUtils.getNewTextView(getActivity(), json.data.content,
                tv_groupdetails_content);
//		tv_groupdetails_pinglun.setText(json.data.comment_count + " 评论");

        tv_groupdetails_header_num.setText(json.data.digg_count + "人点赞");

        tv_groupdetails_liuyan_num.setText(json.data.comment_count);
        tv_groupdetails_zan_num.setText(json.data.digg_count);

        try {
            digg_count = Integer.valueOf(json.data.digg_count);
            comment_count = Integer.valueOf(json.data.comment_count);
        } catch (Exception e) {
        }

        if ("0".equals(json.data.is_digg)) {
            iv_groupdetails_zan_img.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.comment_zan_big));
            is_digg = "0";
        } else {
            iv_groupdetails_zan_img.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.comment_zan_hou));
            is_digg = "1";
        }

        if ("1".equals(json.data.is_fav)) {
            is_fav = "1";
            Drawable drawable = getResources().getDrawable(
                    R.drawable.collect_ok);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tv_bookpart_sc
                    .setCompoundDrawables(drawable, null, null, null);
        } else {
            is_fav = "0";
            Drawable drawable = getResources().getDrawable(
                    R.drawable.collect_red_no);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            tv_bookpart_sc
                    .setCompoundDrawables(drawable, null, null, null);
        }

        if (json.digg_user == null || json.digg_user.size() == 0) {

        } else {
            // 得到像素密度
            DisplayMetrics outMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
            density = outMetrics.density; // 像素密度
            // 根据item的数目，动态设定gridview的宽度
            ViewGroup.LayoutParams params = gv_groupdetails_header_gv.getLayoutParams();
            int itemWidth = (int) (25 * density);    //宽    dp
            int spacingWidth = (int) (10 * density); //列间距 dp

            params.width = itemWidth * json.digg_user.size() + (json.digg_user.size() - 1) * spacingWidth; //显示的个数(Item)
            gv_groupdetails_header_gv.setStretchMode(GridView.NO_STRETCH); // 设置为禁止拉伸模式
            gv_groupdetails_header_gv.setNumColumns(json.digg_user.size());
            gv_groupdetails_header_gv.setHorizontalSpacing(spacingWidth);
            gv_groupdetails_header_gv.setColumnWidth(itemWidth);
            gv_groupdetails_header_gv.setLayoutParams(params);

            gridViewAdapter = new MyGroupRankingAdapter(getActivity(), json.digg_user);
            gv_groupdetails_header_gv.setAdapter(gridViewAdapter);
            gv_groupdetails_header_gv.setFocusable(false);
            gridViewAdapter.notifyDataSetChanged();
        }

        // 分享的内容
        if (!TextUtils.isEmpty(json.data.title)) {
            title = json.data.title;
        } else {
            title = json.data.user_data.name + "的随笔";
        }
        this.content = json.data.content;

        readNoteDetailAdapter = new ReadNoteDetailAdapter(fragment,
                json.data.img_str);
        gv_groupdetails_show.setAdapter(readNoteDetailAdapter);
        gv_groupdetails_show.setFocusable(false);
        readNoteDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 本随笔详情点赞
     */
    private void zanGetData() {
        // 已经点赞过 - 取消赞
        if ("1".equals(is_digg)) {
            act = "c";
        } else {
            act = "";
        }
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);
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
                                if ("1".equals(is_digg)) {
                                    iv_groupdetails_zan_img.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.comment_zan_big));

                                    digg_count = digg_count - 1;
                                    tv_groupdetails_zan_num
                                            .setText(digg_count + "");
                                    tv_groupdetails_dianzan.setText(digg_count
                                            + " 赞");
                                    is_digg = "0";
                                } else {
                                    iv_groupdetails_zan_img.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.comment_zan_hou));
                                    digg_count = digg_count + 1;
                                    tv_groupdetails_zan_num
                                            .setText(digg_count + "");
                                    tv_groupdetails_dianzan.setText(digg_count
                                            + " 赞");
                                    is_digg = "1";
                                }
                                switch (source) {
                                    case 1:
                                        GroupFragment.getInstance().refresh(
                                                groupPosition,
                                                "dianzan" + groupPosition, 0);
                                        break;
                                    case 2:
                                        OtherUserFragment.getInstance().refresh(
                                                groupPosition,
                                                "dianzan" + groupPosition, 0);
                                        break;
                                    case 3:
                                        Fragment_Suibi.getInstance().refresh(groupPosition,
                                                "dianzan" + groupPosition, 0);
                                        break;
                                    case 6:
                                        TopicsActivity.Activity.refresh(groupPosition,
                                                "dianzan" + groupPosition, 0);
                                        break;
                                    case 7:
                                        SearchFragment.getInstance().refresh(
                                                groupPosition,
                                                "dianzan" + groupPosition, 5);
                                        break;
                                    case 8:
                                        SearchFragment.getInstance().refresh(
                                                groupPosition,
                                                "dianzan" + groupPosition, 6);
                                        break;
                                    case 9:
                                        DiscoverFragment.getInstance().refresh(
                                                groupPosition,
                                                "dianzan" + groupPosition, 0);
                                        break;
                                    case 10:
                                        ChoicenessBooksDetailsFragment.getInstance().refresh(
                                                groupPosition,
                                                "dianzan" + groupPosition, 0);
                                        break;

                                    default:
                                        break;
                                }
                            } else {

                            }
                        } catch (Exception e) {

                        }
                    }
                }, true, null);
    }

    /**
     * 评论的点赞
     */
    public void dianzan(final int position) {
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
     * 有书圈 - 收藏(取消收藏)
     */
    private void getFav() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", id);

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
                                Drawable drawable = getResources().getDrawable(
                                        R.drawable.collect_ok);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                        drawable.getMinimumHeight());
                                tv_bookpart_sc
                                        .setCompoundDrawables(drawable, null, null, null);
                                is_fav = "1";
                                Toast.makeText(getActivity(), "收藏成功",
                                        Toast.LENGTH_SHORT).show();
                                //是否升级
                                if (json.level_is_up != null && "1".equals(json.level_is_up)) {
                                    startActivity(new Intent(getActivity(), UpgradeActivity.class));
                                }
                            } else if ("2".equals(json.code)) {
                                Drawable drawable = getResources().getDrawable(
                                        R.drawable.collect_red_no);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                        drawable.getMinimumHeight());
                                tv_bookpart_sc
                                        .setCompoundDrawables(drawable, null, null, null);
                                is_fav = "0";
                                Toast.makeText(getActivity(), "取消成功",
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

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.note_jubao, new RequestCallBack<String>() {

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
                                Toast.makeText(getActivity(), "举报成功",
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
     * GridView的适配器
     */
    private int width = 80;
    private ReadNoteDetailAdapter readNoteDetailAdapter;

    private class ReadNoteDetailAdapter extends BaseAdapter {

        private Fragment fromFragment;
        private String[] strings;

        public ReadNoteDetailAdapter(Fragment fromFragment, String[] strings) {
            super();
            this.fromFragment = fromFragment;
            this.strings = strings;
        }

        @Override
        public int getCount() {
            if (strings != null) {
                return strings.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (strings != null) {
                return strings[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(fromFragment.getActivity())
                        .inflate(R.layout.item_group_gridview, parent, false);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.iv_group_gridview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    width, width);
            holder.imageView.setLayoutParams(params);
            DisplayImageUtils.displayImage(strings[position], holder.imageView,
                    0, R.drawable.youshu);
            holder.imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (strings != null && strings.length != 0) {
                        ImageBrowserActivity.position = position;
                        ImageBrowserActivity.mList = java.util.Arrays.asList(GroupBean.img_str_orignsize(strings));
                        fromFragment.getActivity().startActivity(new Intent(fromFragment.getActivity(), ImageBrowserActivity.class));

                    }
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private ImageView imageView;
        }
    }

    /**
     * 头部点赞Gridview适配器
     */
    private class MyGroupRankingAdapter extends BaseAdapter {

        private Context context;
        private List<UserInfoBean> list;

        public MyGroupRankingAdapter(Context context, List<UserInfoBean> list) {
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

            if (list.get(position) == null) {

            } else {
                DisplayImageUtils.displayImage(list.get(position).avatar,
                        holder.iv_group_ranking_avatar, 100, R.drawable.avatar);
            }
            holder.iv_group_ranking_avatar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction transaction = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right,
                            R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.ll_activity_next,
                            ACEFragment.getInstance());
                    transaction.commit();
                    ACEFragment.getInstance().needSaveView = false;
                    ACEFragment.getInstance().source = 2;
                    ACEFragment.getInstance().id = id;
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_group_ranking_avatar;
        }
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
            switch (v.getId()) {
                case R.id.ll_popupwindow_wx:
                    num = 1;
                    break;
                case R.id.ll_popupwindow_pyq:
                    num = 2;
                    break;
                case R.id.ll_popupwindow_qq:
                    num = 3;
                    break;
                case R.id.ll_popupwindow_wb:
                    num = 4;
                    if (TextUtils.isEmpty(detailsJson.data.title)) {
                        content = "推荐+" + detailsJson.data.user_data.name + "的共读随笔,来自@有书共读" + h5Url;
                    } else {
                        content = "推荐+" + detailsJson.data.user_data.name + "的共读随笔《" + detailsJson.data.title + "》,来自@有书共读" + h5Url;
                    }
                    break;
                default:
                    break;
            }
            UMShare.setUMeng(activity, num, title, content, imageUrl, h5Url, id, "note");
            sharePopupWindow.dismiss();
            if ("1".equals(UMShare.getLevel())) {
                startActivity(new Intent(getActivity(), UpgradeActivity.class));
            }
        }
    };

    /**
     * 删除后刷新方法
     */
    public void refresh() {
        list.remove(mPosition);
        adapter.notifyDataSetChanged();
        comment_count = comment_count - 1;
//		tv_groupdetails_pinglun.setText(comment_count + " 评论");
        tv_groupdetails_liuyan_num.setText(comment_count + "");
    }

    /**
     * 评论后刷新方法
     *
     * @param cid     评论id
     * @param content 内容
     */
    public void refresh1(final String cid, final String content, final String level_is_up) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (list.get(0).user_data == null && page == 0) {
//                    list.remove(0);
//                }
//                CommentBean bean = new CommentBean();
//                bean.user_data = GlobalParams.userInfoBean;
//                if (comment_type == 0) {
//                    bean.reply_user_data = null;
//                    bean.comment_type = "0";
//                    bean.re_content = "";
//                } else {
//                    bean.reply_user_data = list.get(mPosition).user_data;
//                    bean.comment_type = "1";
//                    bean.re_content = list.get(mPosition).content;
//                }
//                bean.id = cid;
//                bean.content = content;
//                bean.create_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                        .format(new Date(System.currentTimeMillis()));
//
//                list.add(0, bean);
//                adapter.notifyDataSetChanged();

                page = 1;
                getData1();

                comment_count = comment_count + 1;
//				tv_groupdetails_pinglun.setText(comment_count + " 评论");
                tv_groupdetails_liuyan_num.setText(comment_count + "");
                //是否升级
                if ("1".equals(level_is_up)) {
                    startActivity(new Intent(getActivity(), UpgradeActivity.class));
                }
            }
        }, 300);
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
                case 3:
                    // swipeRefreshLayout.setRefreshing(true);
                    is_loading = true;
                    getData();
                    list.add(bean);
                    getData1();
                    break;
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
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("GroupDetailsFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("GroupDetailsFragment");
    }

}