package com.fengwo.reading.main.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.comment.CommentEditFragment;
import com.fengwo.reading.main.discover.ChoicenessBooksDetailsFragment;
import com.fengwo.reading.main.discover.hottopics.TopicsActivity;
import com.fengwo.reading.main.my.Fragment_Suibi;
import com.fengwo.reading.main.my.UserinfoSaveJson;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.qq.ImageBrowserActivity;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 他人主页
 *
 * @author Luo Sheng
 * @date 2016-1-27
 */
public class OtherUserFragment extends Fragment implements OnClickListener {

    private RelativeLayout rl_otheruser_top;
    private ImageView iv_otheruser_lift2, iv_otheruser_lift;
    private TextView tv_otheruser_name2,tv_otheruser_empty;
    //头部
    private RelativeLayout rl_header;
    private TextView tv_my_suibi_name, tv_my_suibi_name2;
    private ImageView iv_my_suibi_icon, iv_my_suibi_gender, iv_my_suibi_xunzhang, iv_bg;
    // private SwipeRefreshLayout swipeRefreshLayout;
    // private MoreLinearLayout moreLinearLayout;
    // private PullToRefreshScrollView sv_groupdetails;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private List<GroupBean> list;
    private GroupAdapter groupAdapter;
    private int page;
    public int source = 0;
    // 来源 1:Activity 2:Fragment 3:有书圈 4:发现-书评 5话题
    // 6我的随笔和收藏 7有书榜
    private boolean isMy = false;// 是否是自己
    private boolean is_loading; //
    private int mPosition;

    private OtherUserJson json;

    public String ta_user_id = ""; // 他人id ， 点击的时候传过来
    private String img_url = "";

    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    private View saveView = null;
    public boolean needSaveView = false;

    public OtherUserFragment() {
    }

    public static OtherUserFragment fragment = new OtherUserFragment();

    public static OtherUserFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_otheruser, container,
                false);

        findViewById(view);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = pullToRefreshListView.getRefreshableView();
        // 添加头部
        addHeaderView();

        list = new ArrayList<>();
        groupAdapter = new GroupAdapter(fragment, list, 2);
        listView.setAdapter(groupAdapter);
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
                        // 执行刷新函数
                        if (is_loading) {
                            return;
                        }
                        is_loading = true;
                        page++;
                        getData();
                        groupAdapter.notifyDataSetChanged();
                    }
                });

        // 滑动监听
//		listView.setOnTouchListener();
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
                    rl_otheruser_top.setVisibility(View.GONE);
                    iv_otheruser_lift.setVisibility(View.VISIBLE);
                } else {
                    rl_otheruser_top.setVisibility(View.VISIBLE);
                    iv_otheruser_lift.setVisibility(View.GONE);
                }
            }
        });
        getDataAvatar1();

        return view;
    }

    private void addHeaderView() {
        /*View view1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.head_otheruser, null);
        iv_otheruser_avatar = (ImageView) view1
                .findViewById(R.id.iv_otheruser_avatar);
        iv_otheruser_sex = (ImageView) view1
                .findViewById(R.id.iv_otheruser_sex);
        tv_otheruser_name = (TextView) view1
                .findViewById(R.id.tv_otheruser_name);
        tv_otheruser_home = (TextView) view1
                .findViewById(R.id.tv_otheruser_home);
        tv_otheruser_content = (TextView) view1
                .findViewById(R.id.tv_otheruser_content);

        iv_otheruser_avatar.setOnClickListener(this);*/
        View view1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.my_suibi, null);
        rl_header = (RelativeLayout) view1.findViewById(R.id.rl_header);
        iv_bg = (ImageView) view1.findViewById(R.id.iv_bg);
        tv_my_suibi_name = (TextView) view1.findViewById(R.id.tv_my_suibi_name);
        tv_my_suibi_name2 = (TextView) view1.findViewById(R.id.tv_my_suibi_name2);
        iv_my_suibi_icon = (ImageView) view1.findViewById(R.id.iv_my_suibi_icon);
        iv_my_suibi_gender = (ImageView) view1.findViewById(R.id.iv_my_suibi_gender);
        iv_my_suibi_xunzhang = (ImageView) view1.findViewById(R.id.iv_my_suibi_xunzhang);


        // DisplayMetrics metric = new DisplayMetrics();
        // getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        // int height = (metric.heightPixels / 5);
        // listView.setHeaderViewSize(LayoutParams.FILL_PARENT, height);
        //
        // // 设置拉伸head显示的图片
        // listView.getHeaderView().setImageResource(R.drawable.otheruser_bg);
        // // 设置图片显示方式
        // listView.getHeaderView().setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 添加头部
        listView.addHeaderView(view1);
    }

    private void getDataAvatar1() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", ta_user_id);
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
//                                handler.sendEmptyMessage(0);
                            }
                        }.start();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        new Thread() {
                            @Override
                            public void run() {
//                                handler.sendEmptyMessage(1);
//                                handler.sendEmptyMessage(2);
                            }
                        }.start();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        new Thread() {
                            @Override
                            public void run() {
//                                handler.sendEmptyMessage(1);
                            }
                        }.start();
                        String jsonString = responseInfo.result;
                        Log.i("suibi", jsonString);
                        MLog.v("suibi", jsonString);
                        try {
                            UserinfoSaveJson json1 = new Gson().fromJson(
                                    jsonString, UserinfoSaveJson.class);
                            if ("1".equals(json.code)) {
                                if (json.user_data != null) {
//                                    GlobalParams.userInfoBean = json.user_data;
                                    Log.i("szf", json1.user_data.user_img);
//                                    NOsqlUtil.set_userInfoBean(GlobalParams.userInfoBean);
                                    DisplayImageUtils.displayImage(json1.user_data.user_img, iv_bg, 100, R.drawable.background);
                                }
                                Context context = fragment.getActivity();
                                if (context != null) {
//                                    CustomToast.showToast(context, "背景修改成功");
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
    }

    /**
     * 请求网络
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("ta_user_id", ta_user_id);
        map.put("page", page + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.user_data, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        pullToRefreshListView.onRefreshComplete();
                        is_loading = false;
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        pullToRefreshListView.onRefreshComplete();
                        is_loading = false;
                        String jsonString = responseInfo.result;
                        try {
                            System.out.println("-----jsonString:" + jsonString);
                            json = new Gson().fromJson(jsonString,
                                    OtherUserJson.class);
                            Log.i("otherinfo",json.user_data.user_img);
                            if ("1".equals(json.code)) {
                                setInfo(json);
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
                                if (list.size()==0){
                                    tv_otheruser_empty.setVisibility(View.VISIBLE);
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
   /* @Override
    public void onStart() {
        super.onStart();
        if (json.user_data == null) {
            iv_my_suibi_icon.setImageResource(R.drawable.avatar);
            iv_my_suibi_gender.setImageResource(R.drawable.boy);
            Drawable drawable = new BitmapDrawable();
            drawable.setBounds(0, 0, 40, 50);
            iv_my_suibi_xunzhang.setImageDrawable(drawable);
            tv_my_suibi_name.setText("");
            tv_my_suibi_name2.setText("亲，您还没有签名哦");
        } else {
//            DisplayImageUtils.displayImage("File://"+Environment.getExternalStorageDirectory()+"suibi.png", iv_bg, 100, R.drawable.avatar);
//            iv_bg.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/suibi.png"));
            DisplayImageUtils.displayImage(json.user_data.user_img, iv_bg, 100, R.drawable.background);
            DisplayImageUtils.displayImage(json.user_data.avatar, iv_my_suibi_icon, 100, R.drawable.avatar);
            DisplayImageUtils.displayImage(json.user_data.sex, iv_my_suibi_gender, 100, R.drawable.boy);
            iv_my_suibi_xunzhang.setImageDrawable(json.user_data.badge_Drawable(0.12f,0.12f));
            tv_my_suibi_name.setText(json.user_data.name);
            if (json.user_data.intro.isEmpty()) {
                tv_my_suibi_name2.setText("亲，您还没有签名哦");
            }else{
                tv_my_suibi_name2.setText(GlobalParams.userInfoBean.intro);
            }

        }

    }*/

    /**
     * 设置信息
     */
    private void setInfo(OtherUserJson json) {
        String home = "";
        DisplayImageUtils.displayImage(json.user_data.avatar, iv_my_suibi_icon,
                100, R.drawable.avatar);
//        DisplayImageUtils.displayImage(json.user_data.user_img,
//                iv_bg, 100, R.drawable.background);
//        DisplayImageUtils.displayImage(json.user_data.avatar,
//                iv_otheruser_avatar, 100, R.drawable.avatar);
        img_url = json.user_data.avatar;
        if (!TextUtils.isEmpty(json.user_data.name)) {

//            DisplayImageUtils.displayImage(json.user_data.user_img, iv_bg, 100, R.drawable.background);
            DisplayImageUtils.displayImage(json.user_data.avatar, iv_my_suibi_icon, 100, R.drawable.avatar);
            DisplayImageUtils.displayImage(json.user_data.sex, iv_my_suibi_gender, 100, R.drawable.boy);
            //todo  在他人主页上不显示
            iv_my_suibi_xunzhang.setImageDrawable(json.user_data.badge_Drawable(0.12f, 0.12f));
            tv_my_suibi_name.setText(json.user_data.name);
            if (TextUtils.isEmpty(json.user_data.intro)) {
                tv_my_suibi_name2.setText("亲，您还没有签名哦");
            } else {
                tv_my_suibi_name2.setText(json.user_data.intro);
            }
//            tv_otheruser_name.setText(json.user_data.name);
//            tv_otheruser_name.setCompoundDrawables(null, null, json.user_data.badge_Drawable(0.12f, 0.12f), null);
            tv_otheruser_name2.setText(json.user_data.name);
            tv_otheruser_name2.setCompoundDrawables(null, null, json.user_data.badge_Drawable(0.12f, 0.12f), null);
        } else {
//            tv_otheruser_name.setText("还没有填写");
            tv_otheruser_name2.setText("");
        }
        if (!TextUtils.isEmpty(json.user_data.job)) {
            home = home + "职业:" + json.user_data.job;
        }
        if (!TextUtils.isEmpty(json.user_data.city)) {
            home = home + "\t\t地区:" + json.user_data.city;
        }
        if (!TextUtils.isEmpty(home)) {
//            tv_otheruser_home.setText(home);
        } else {
//            tv_otheruser_home.setText("还没有填写");
        }
        if (!TextUtils.isEmpty(json.user_data.intro)) {
//            tv_otheruser_content.setText(json.user_data.intro);
        } else {
//            tv_otheruser_content.setText("还没有填写");
        }

        // 性别
        switch (json.user_data.sex) {
            case "0":
//                iv_otheruser_sex.setVisibility(View.GONE);
                break;
            case "1":
//                iv_otheruser_sex.setImageResource(R.drawable.myinfo_nan);
                break;
            case "2":
//                iv_otheruser_sex.setImageResource(R.drawable.myinfo_nv);
                break;
            default:
                break;
        }
    }

    /**
     * 跳转 讨论详情
     */
    public void goDetails(int position) {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                GroupDetailsFragment.getInstance());
        transaction.commit();
        GroupDetailsFragment.getInstance().source = 2;
        GroupDetailsFragment.getInstance().groupPosition = position;
        GroupDetailsFragment.getInstance().id = list.get(position).id;
        GroupDetailsFragment.getInstance().needSaveView = false;
    }

    /**
     * ∨ 符号的点击
     */
    public void duihaoShow(int position) {
        mPosition = position;
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
        mPosition = position;
        FragmentActivity activity = fragment.getActivity();
        sharePopupWindow = new SelectSharePopupWindow(activity, itemsOnClick);
        sharePopupWindow.showAtLocation(
                activity.findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);
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
            if ("1".equals(UMShare.getLevel())) {
                startActivity(new Intent(getActivity(), UpgradeActivity.class));
            }
            sharePopupWindow.dismiss();
        }
    };

    /**
     * 跳转留言
     */
    public void goPublishMessageFragment(int position) {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                CommentEditFragment.getInstance());
        transaction.commit();
        CommentEditFragment.getInstance().type = 2;
        CommentEditFragment.getInstance().id = list.get(position).id;
        CommentEditFragment.getInstance().comment_type = 0;
        CommentEditFragment.getInstance().needSaveView = false;
    }

    /**
     * 点赞的点击
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
                                switch (source) {
                                    case 3:
                                        GroupFragment.getInstance().is_refresh = true;
                                        break;
                                    case 5:
                                        TopicsActivity.Activity.is_refresh = true;
                                        break;
                                    case 6:
                                        Fragment_Suibi.getInstance().is_refresh = true;
                                        break;
                                    case 7:
                                        ChoicenessBooksDetailsFragment.getInstance().is_refresh = true;
                                        break;
                                }
                            } else {

                            }
                        } catch (Exception e) {

                        }
                    }
                }, true, null);
    }

    public void refresh(int position, String tag, int num) {
        if (tag.startsWith("dianzan")) {
            TextView textView = (TextView) listView
                    .findViewWithTag("dianzan_tv" + position);
            if (textView != null) {
                if ("1".equals(list.get(position).is_digg)) {
                    // 赞过 - 取消
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

    private void findViewById(View view) {
        rl_otheruser_top = (RelativeLayout) view
                .findViewById(R.id.rl_otheruser_top);
        iv_otheruser_lift2 = (ImageView) view
                .findViewById(R.id.iv_otheruser_lift2);
        tv_otheruser_name2 = (TextView) view
                .findViewById(R.id.tv_otheruser_name2);
        iv_otheruser_lift = (ImageView) view
                .findViewById(R.id.iv_otheruser_lift);
        pullToRefreshListView = (PullToRefreshListView) view
                .findViewById(R.id.lv_otheruser);

        tv_otheruser_empty  = (TextView) view.findViewById(R.id.tv_otheruser_empty);
        // sv_groupdetails = (PullToRefreshScrollView) view
        // .findViewById(R.id.sv_groupdetails);

        // swipeRefreshLayout = (SwipeRefreshLayout) view
        // .findViewById(R.id.srl_otheruser_refresh);

        rl_otheruser_top.setVisibility(View.GONE);

        iv_otheruser_lift.setOnClickListener(this);
        iv_otheruser_lift2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_otheruser_lift:
            case R.id.iv_otheruser_lift2:
                switch (source) {
                    case 1:
                    case 3:
                    case 4:
                    case 5:
                        getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.in_from_left,
                                R.anim.out_to_right);
                        break;
                    case 2:
                    case 6:
                    case 7:
                        getActivity().getSupportFragmentManager().popBackStack();
                        break;

                    default:
                        break;
                }
                break;

            case R.id.iv_otheruser_avatar:
                // 头像
                if (json.user_data.avatar != null && img_url != null) {
                    ImageBrowserActivity.position = 0;
                    ImageBrowserActivity.mList = java.util.Arrays.asList(img_url.substring(0, img_url.indexOf("@")));
                    getActivity().startActivity(new Intent(getActivity(), ImageBrowserActivity.class));
                }
                break;

            default:
                break;

        }
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
     * 删除后刷新方法
     */
    public void refresh() {
        list.remove(mPosition);
        groupAdapter.notifyDataSetChanged();

        // if (list.size() == 0) {
        // rl_forumlist_layout.setVisibility(View.GONE);
        // iv_forumlist_null.setVisibility(View.VISIBLE);
        // } else {
        // rl_forumlist_layout.setVisibility(View.VISIBLE);
        // iv_forumlist_null.setVisibility(View.GONE);
        // }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("OtherUserFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OtherUserFragment");
    }

}