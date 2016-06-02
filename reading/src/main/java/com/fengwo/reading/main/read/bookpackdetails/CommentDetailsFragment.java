package com.fengwo.reading.main.read.bookpackdetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
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
import com.fengwo.reading.common.SelectListViewPopupWindow;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.comment.EmojiActivity;
import com.fengwo.reading.main.comment.CommentBean;
import com.fengwo.reading.main.comment.ConversationFragment;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.main.read.Fragment_Bookpack;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.qq.ImageBrowserActivity;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DateUtils;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.EmojiUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.ListUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.utils.VipImageUtil;
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
 * 拆书包详情 - 评论详情
 *
 * @author Luo Sheng
 * @date 2016-5-10
 */
public class CommentDetailsFragment extends Fragment implements OnClickListener {

    private SelectListViewPopupWindow listViewPopupWindow;
    private CustomProgressDialog progressDialog;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;

    private ImageView iv_commentdetails_avatar, iv_commentdetails_sex,
            iv_title_left, iv_title_right, iv_commentdetails_zan_img;
    private TextView_copypaste tv_commentdetails_content;
    private TextView tv_commentdetails_name, tv_commentdetails_time, tv_commentdetails_num, tv_commentdetails_liuyan,
            tv_commentdetails_dianzan, tv_commentdetails_zan_num, tv_title_mid,
            tv_commentdetails_liuyan_num, tv_commentdetails_bookname;
    private RelativeLayout rl_commentdetails, rl_commentdetails_fenxiang,
            rl_commentdetails_liuyan, rl_commentdetails_zan,
            rl_commentdetails_avatar;

    private CommentDetailsJson detailsJson;
    private CommentDetailsAdapter adapter;
    private List<CommentDetailsBean> list;
    private CommentDetailsBean bean;
    private int digg_count;
    private int comment_count;
    private int comment_type;

    public String bpc_id = "";// 评论id
    public String bookname = "";// 书名
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
    // 来源 1:热门评论-hot 2:最新评论-new 3:消息

    public CommentDetailsFragment() {
    }

    public static CommentDetailsFragment fragment = new CommentDetailsFragment();

    public static CommentDetailsFragment getInstance() {
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
                        adapter.notifyDataSetChanged();
                    }
                });

        bean = new CommentDetailsBean();
        list = new ArrayList<>();
        adapter = new CommentDetailsAdapter(fragment, list, 1);
        listView.setAdapter(adapter);
//        listView.setFocusable(false);

        handler.sendEmptyMessageDelayed(3, 300);

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
                        //回复
                        comment_type = 1;
                        bundle1.putInt("source", 5);
                        bundle1.putInt("comment_type", 1);
                        bundle1.putString("id", bpc_id);
                        bundle1.putString("receive_user", list
                                .get(mPosition).user_data.user_id);
                        bundle1.putString("name", list
                                .get(mPosition).user_data.name);
                        bundle1.putString("cid", list
                                .get(mPosition).bpc_id);
                        intent1.putExtras(bundle1);
                        getActivity().startActivity(intent1);
                        getActivity().overridePendingTransition(R.anim.push_bottom_in,
                                R.anim.push_bottom_out);
                        break;
                    case 1:
                        //投诉
                        getTouSu();
                        break;
                    case 2:
                        //查看对话
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.ll_activity_next,
                                ConversationFragment.getInstance());
                        transaction.commit();

                        ConversationFragment.getInstance().needSaveView = false;
                        ConversationFragment.getInstance().type = 1;
                        ConversationFragment.getInstance().id = bpc_id;
                        ConversationFragment.getInstance().ruser_id = list
                                .get(mPosition).user_data.user_id;
                        ConversationFragment.getInstance().cuser_id = list
                                .get(mPosition).reply_user_data.user_id;
                        break;
                    case 3:
                        //分享
                        FragmentActivity activity = fragment.getActivity();
                        sharePopupWindow = new SelectSharePopupWindow(activity,
                                itemsOnClick);
                        sharePopupWindow.showAtLocation(
                                activity.findViewById(R.id.ll_activity_next),
                                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        break;
                    case 4:
                        // 删除 true:自己发的某条评论 false:整个评论详情
                        if (is_shanchu) {
                            // 刷新页面
                            getDelete();
                        } else {
                            getDataDelete();
                        }
                        break;
                    case 5:
                        // 举报
//                        UMengUtils.onCountListener(getActivity(), "suibiXQ_JB");
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
                    listViewPopupWindow.setData(3, "");
                    listViewPopupWindow.setData(4, "删除");
                    listViewPopupWindow.setData(5, "");
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
                    is_shanchu = false;
                }
                listViewPopupWindow.showAtLocation(fragment.getActivity()
                        .findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        listViewPopupWindow.addData(new String[]{"回复", "", "查看对话", "分享",
                "删除", "举报"});

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        iv_title_right = (ImageView) view.findViewById(R.id.iv_title_right);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_groupdetails_show);

        rl_commentdetails_fenxiang = (RelativeLayout) view
                .findViewById(R.id.rl_groupdetails_fenxiang);
        tv_commentdetails_liuyan = (TextView) view
                .findViewById(R.id.tv_groupdetails_liuyan);
        rl_commentdetails_liuyan = (RelativeLayout) view
                .findViewById(R.id.rl_groupdetails_liuyan);
        tv_commentdetails_liuyan_num = (TextView) view
                .findViewById(R.id.tv_groupdetails_liuyan_num);
        iv_commentdetails_zan_img = (ImageView) view
                .findViewById(R.id.iv_groupdetails_zan_img);
        rl_commentdetails_zan = (RelativeLayout) view
                .findViewById(R.id.rl_groupdetails_zan);
        tv_commentdetails_zan_num = (TextView) view
                .findViewById(R.id.tv_groupdetails_zan_num);
    }

    private void setTitle() {
        tv_title_mid.setVisibility(View.VISIBLE);
        iv_title_right.setVisibility(View.VISIBLE);
        tv_title_mid.setText("评论详情");
        iv_title_right.setImageResource(R.drawable.groupdetails_dian_black);
    }

    private void addHeaderView() {
        View view1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.head_commentdetails, null);

        iv_commentdetails_avatar = (ImageView) view1
                .findViewById(R.id.iv_commentdetails_avatar);
        iv_commentdetails_sex = (ImageView) view1
                .findViewById(R.id.iv_commentdetails_sex);

        rl_commentdetails = (RelativeLayout) view1
                .findViewById(R.id.rl_commentdetails);
        rl_commentdetails_avatar = (RelativeLayout) view1
                .findViewById(R.id.rl_commentdetails_avatar);

        tv_commentdetails_bookname = (TextView) view1
                .findViewById(R.id.tv_commentdetails_bookname);

        tv_commentdetails_name = (TextView) view1
                .findViewById(R.id.tv_commentdetails_name);
        tv_commentdetails_time = (TextView) view1
                .findViewById(R.id.tv_commentdetails_time);
        tv_commentdetails_num = (TextView) view1
                .findViewById(R.id.tv_commentdetails_num);
        tv_commentdetails_content = (TextView_copypaste) view1
                .findViewById(R.id.tv_commentdetails_content);

        // 添加头部
        listView.addHeaderView(view1);
    }

    private void onClickListener() {
        rl_commentdetails_avatar.setOnClickListener(this);
        rl_commentdetails_fenxiang.setOnClickListener(this);
        rl_commentdetails_liuyan.setOnClickListener(this);
        tv_commentdetails_liuyan.setOnClickListener(this);
        rl_commentdetails_zan.setOnClickListener(this);
        iv_title_left.setOnClickListener(this);
        iv_title_right.setOnClickListener(this);
        rl_commentdetails.setOnClickListener(this);
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
                    case 2:
                    case 3:
//                        getActivity().finish();
//                        getActivity().overridePendingTransition(R.anim.in_from_left,
//                                R.anim.out_to_right);
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.iv_title_right:
                //更多
                if (GlobalParams.uid.equals(detailsJson.data.user.user_id)) {
                    is_shanchu = false;
                    listViewPopupWindow.setData(0, "");
                    listViewPopupWindow.setData(1, "");
                    listViewPopupWindow.setData(2, "");
                    listViewPopupWindow.setData(3, "分享");
                    listViewPopupWindow.setData(4, "删除");
                    listViewPopupWindow.setData(5, "");
                } else {
                    listViewPopupWindow.setData(0, "");
                    listViewPopupWindow.setData(1, "");
                    listViewPopupWindow.setData(2, "");
                    listViewPopupWindow.setData(3, "分享");
                    listViewPopupWindow.setData(4, "");
                    listViewPopupWindow.setData(5, "举报");
                }
                listViewPopupWindow.showAtLocation(fragment.getActivity()
                        .findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.rl_commentdetails:
                //他人主页
                transaction.replace(R.id.ll_activity_next,
                        OtherUserFragment.getInstance());
                transaction.commit();
                OtherUserFragment.getInstance().source = 2;
                OtherUserFragment.getInstance().ta_user_id = detailsJson.data.user.user_id;
                OtherUserFragment.getInstance().needSaveView = false;
                break;
            case R.id.rl_groupdetails_fenxiang:
                //分享
                FragmentActivity activity = fragment.getActivity();
                sharePopupWindow = new SelectSharePopupWindow(activity,
                        itemsOnClick);
                sharePopupWindow.showAtLocation(
                        activity.findViewById(R.id.ll_activity_next),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.rl_groupdetails_liuyan:
                //跳转 评论区
                listView.setSelected(true);
                if (android.os.Build.VERSION.SDK_INT >= 8) {
                    listView.smoothScrollToPosition(2);
                } else {
                    listView.setSelection(2);
                }
                break;
            case R.id.tv_groupdetails_liuyan:
                //写评论
                comment_type = 0;
                Intent intent = new Intent(getActivity(), EmojiActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("source", 5);
                bundle.putInt("comment_type", 0);
                bundle.putString("id", bpc_id);
                bundle.putString("name", "");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_bottom_in,
                        R.anim.push_bottom_out);
                break;
            case R.id.rl_groupdetails_zan:
                //点赞
                zanGetData();
                break;
            case R.id.rl_commentdetails_avatar:
                if (detailsJson.data.user_data.avatar != null) {
                    ImageBrowserActivity.position = 0;
                    String newStr = detailsJson.data.user_data.avatar.substring(0,
                            detailsJson.data.user_data.avatar.indexOf("@"));
                    List<String> stringList = new ArrayList<>();
                    stringList.add(newStr);
                    ImageBrowserActivity.mList = stringList;
                    getActivity().startActivity(new Intent(getActivity(), ImageBrowserActivity.class));
                }
                break;

            default:
                break;
        }
    }

    /**
     * 拆书包评论详情 - 网络请求
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("bpc_id", bpc_id);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.comment_comInfo,
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
//                            System.out.println("----------11:" + jsonString);
                            detailsJson = new Gson().fromJson(jsonString,
                                    CommentDetailsJson.class);
                            if ("1".equals(detailsJson.code)) {
                                if (detailsJson.data != null) {
                                    // 设置数据
                                    setinfo(detailsJson);
                                }
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
     * 拆书包评论详情 - 评论列表网络请求
     */
    private void getData1() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("bpc_id", bpc_id);
        map.put("page", page + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.comment_comList, new RequestCallBack<String>() {

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
                            System.out.println("----------22:" + jsonString);
                            CommentDetailsJson json = new Gson().fromJson(
                                    jsonString, CommentDetailsJson.class);
                            if ("1".equals(json.code)) {
                                if (page == 1) {
                                    list.clear();
                                    if (json.comment_list == null
                                            || json.comment_list.size() == 0) {
                                        // 没有数据
                                        page = 0;
                                        list.add(bean);
                                    } else {
                                        list.addAll(json.comment_list);
                                    }
                                } else {
                                    if (json.comment_list == null
                                            || json.comment_list.size() == 0) {
                                        page--;
                                    } else {
                                        list.addAll(json.comment_list);
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
        map.put("id", list.get(mPosition).id);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.comment_comDel, new RequestCallBack<String>() {

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

    //评论 删除
    private void getDataDelete() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("cid", bpc_id);
        map.put("type", "bp");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.comment_del, new RequestCallBack<String>() {

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
                    public void onFailure(HttpException arg0, String error) {
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
                            final BaseJson json = new Gson().fromJson(
                                    jsonString, BaseJson.class);
                            if ("1".equals(json.code)) {
                                switch (source) {
                                    case 1:
                                    case 2:
                                        // 刷新页面
                                        Fragment_Bookpack.getInstance().refresh(source, groupPosition);
                                        getActivity().getSupportFragmentManager()
                                                .popBackStack();
                                        break;
                                    case 3:
                                        //消息

                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                Context context = getActivity();
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = getActivity();
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
    private void setinfo(CommentDetailsJson json) {
        imageUrl = json.data.user.avatar;
        h5Url = GlobalConstant.ServerDomain + "/share/comment?bpc_id=" + bpc_id;
        if (json.data.user.avatar != null) {
            DisplayImageUtils.displayImage(json.data.user.avatar,
                    iv_commentdetails_avatar, 100, R.drawable.avatar);
        } else {
            iv_commentdetails_avatar.setImageResource(R.drawable.avatar);
        }
        // 等级
//        if (!TextUtils.isEmpty(json.data.user.level)) {
//            int i = Integer.valueOf(json.data.user.level).intValue();
//            VipImageUtil.getVipGrade(getActivity(), iv_commentdetails_sex, i, 1);
//        } else {
//            iv_commentdetails_sex.setVisibility(View.GONE);
//        }
        tv_commentdetails_name.setText(json.data.user.name);
//        tv_commentdetails_name.setCompoundDrawables(null, null, json.data.user.badge_Drawable(0.12f, 0.12f), null);
        tv_commentdetails_time.setText(DateUtils.getTime(json.data.comm_info.create_time));

        tv_commentdetails_num.setText(json.data.comm_info.read_count + "阅读");
        tv_commentdetails_bookname.setText("《" + bookname + "》");

        // 内容(表情)
        tv_commentdetails_content.setText(EmojiUtils.getSmiledText(
                        getActivity(), json.data.comm_info.content),
                TextView.BufferType.SPANNABLE);

        tv_commentdetails_liuyan_num.setText(json.data.comm_info.comment_count);
        tv_commentdetails_zan_num.setText(json.data.comm_info.digg_count);

        try {
            digg_count = Integer.valueOf(json.data.comm_info.digg_count);
            comment_count = Integer.valueOf(json.data.comm_info.comment_count);
        } catch (Exception e) {
        }

        if ("1".equals(json.data.comm_info.is_digg)) {
            iv_commentdetails_zan_img.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.comment_zan_hou));
        } else {
            iv_commentdetails_zan_img.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.comment_zan));
        }

        // 分享的内容
        title = json.data.user.name + "的文评";
        content = json.data.user.name + "的文评";
    }

    /**
     * 点赞
     */
    private void zanGetData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("bpc_id", bpc_id);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.comment_comdigg,
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
                                iv_commentdetails_zan_img.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.comment_zan_hou));
                                digg_count = digg_count + 1;
                                tv_commentdetails_zan_num
                                        .setText(digg_count + "");
                                tv_commentdetails_dianzan.setText(digg_count
                                        + " 赞");
                                Fragment_Bookpack.getInstance().refresh(
                                        groupPosition,
                                        "dianzan" + groupPosition, source);
                            } else if ("2".equals(json.code)) {
                                iv_commentdetails_zan_img.setImageDrawable(fragment.getActivity().getResources().getDrawable(R.drawable.comment_zan));
                                digg_count = digg_count - 1;
                                tv_commentdetails_zan_num
                                        .setText(digg_count + "");
                                tv_commentdetails_dianzan.setText(digg_count
                                        + " 赞");
                                Fragment_Bookpack.getInstance().refresh1(
                                        groupPosition,
                                        "dianzan" + groupPosition, source);
                            }
                        } catch (Exception e) {

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
                    title = "";
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
                    content = "推荐" + list.get(mPosition).user_data.name + "的文章评论,来自@有书共读" + h5Url;
                    break;
                default:
                    break;
            }
            UMShare.setUMeng(activity, num, title, content, imageUrl, h5Url, "", "");
            sharePopupWindow.dismiss();
//            if ("1".equals(UMShare.getLevel())) {
//                startActivity(new Intent(getActivity(), UpgradeActivity.class));
//            }
        }
    };

    /**
     * 删除后刷新方法
     */
    public void refresh() {
//        getData1();
        list.remove(mPosition);
        adapter.notifyDataSetChanged();
        comment_count = comment_count - 1;
        tv_commentdetails_liuyan_num.setText(comment_count + "");
    }

    /**
     * 评论后刷新方法
     */
    public void refresh(final String level_is_up) {
        if (list.get(0).user_data == null && page == 0) {
            list.remove(0);
        }
        getData1();
        comment_count = comment_count + 1;
        tv_commentdetails_liuyan_num.setText(comment_count + "");
        //是否升级
        if ("1".equals(level_is_up)) {
            startActivity(new Intent(getActivity(), UpgradeActivity.class));
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
        MobclickAgent.onPageEnd("CommentDetailsFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CommentDetailsFragment");
    }

}