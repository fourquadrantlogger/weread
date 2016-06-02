package com.fengwo.reading.main.discover.hottopics;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.BaseActivity;
import com.fengwo.reading.activity.EditTextActivity;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.main.group.PublishFeelingsFragment;
import com.fengwo.reading.main.my.MyInfoFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.ActivityUtil;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.ListUtils;
import com.fengwo.reading.utils.MLog;
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
import java.util.StringTokenizer;

/**
 * 话题详情
 *
 * @author Luo Sheng
 * @date 2016-2-26
 */
public class TopicsActivity extends BaseActivity implements OnClickListener {

    public static TopicsActivity Activity;

    public CustomProgressDialog progressDialog;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private LinearLayout title_layout2;
    private RelativeLayout rl_topcs_up, rl_title_layout1;
    private ImageView iv_title_left, iv_title_left1, iv_topics_left,
            iv_topcs_bg, iv_topcs_up;
    private TextView tv_title_mid, tv_title_mid1, tv_topcs_num, tv_topcs_title,
            tv_topcs_content, tv_topics_down;

    private TopicsJson json;
    private String img = "";// 图片地址
    private String topic_title = ""; // 话题名称(不带#)
    public String topicName = ""; // 话题名称(传过来,带#)

    public String level_is_up = ""; //升级

    // 分享的信息
    private String id = "";
    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    private TopicsAdapter adapter;
    private List<TopicsBean> list;
    private int page; // 当前页

    private boolean is_loading; //
    public boolean is_refresh = false; // 是否刷新
    public boolean is_content = false; // 内容展开
    private int lastVisibleItemPosition = 0;// 标记上次滑动位置

    private View saveView = null;
    public boolean needSaveView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_topics);
        // if (needSaveView && saveView != null) {
        // return saveView;
        // }
        // needSaveView = true;

        progressDialog = CustomProgressDialog.createDialog(this);

        ActivityUtil.topicsActivity = this;
        Activity = this;
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            topicName = bundle.getString("name", "");
        }

        findViewById();
        setTitle();

        StringTokenizer token = new StringTokenizer(topicName, "#");
        topic_title = token.nextToken() + "";

        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        listView = pullToRefreshListView.getRefreshableView();

        // 添加头部
        addHeaderView();

        // 下拉控件调用此方法解决滑动的冲突
        // listView.setOnScrollListener(new SwpipeListViewOnScrollListener(
        // swipeRefreshLayout));

        list = new ArrayList<>();
        adapter = new TopicsAdapter(this, list, topic_title);
        listView.setAdapter(adapter);

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
                        page = 1;
                        getData();
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

        // 滑动监听
//		listView.setOnTouchListener();//此方法更好,监听滑动距离而不是Item(有空再改)
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当状态发生改变时
                    case OnScrollListener.SCROLL_STATE_IDLE:// 屏幕停止滚动时
                        break;
                    case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 滚动时
                        break;
                    case OnScrollListener.SCROLL_STATE_FLING:// 惯性滑动时
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
                if (!TextUtils.isEmpty(img)) {
                    if (firstVisibleItem == 0) {
                        // 滚动到顶部
                        rl_title_layout1.setVisibility(View.GONE);
                        title_layout2.setVisibility(View.GONE);
                        iv_topics_left.setVisibility(View.VISIBLE);
                    } else {
                        rl_title_layout1.setVisibility(View.GONE);
                        title_layout2.setVisibility(View.VISIBLE);
                        iv_topics_left.setVisibility(View.GONE);
                    }
                } else {
                    rl_title_layout1.setVisibility(View.VISIBLE);
                    title_layout2.setVisibility(View.GONE);
                    iv_topics_left.setVisibility(View.GONE);
                }

                if (firstVisibleItem > lastVisibleItemPosition) {// 上滑
                    tv_topics_down.setVisibility(View.GONE);
                } else if (firstVisibleItem < lastVisibleItemPosition) {// 下滑
                    tv_topics_down.setVisibility(View.VISIBLE);
                }
                lastVisibleItemPosition = firstVisibleItem;
            }
        });

    }

    private void findViewById() {
        iv_title_left = (ImageView) findViewById(R.id.iv_return);
        tv_title_mid = (TextView) findViewById(R.id.tv_title_mid);
        iv_title_left1 = (ImageView) findViewById(R.id.iv_title_left1);
        tv_title_mid1 = (TextView) findViewById(R.id.tv_title_mid1);

        rl_title_layout1 = (RelativeLayout) findViewById(R.id.rl_title_layout1);
        title_layout2 = (LinearLayout) findViewById(R.id.title_layout2);
        iv_topics_left = (ImageView) findViewById(R.id.iv_topics_left);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_topics);
        tv_topics_down = (TextView) findViewById(R.id.tv_topics_down);

        rl_title_layout1.setVisibility(View.GONE);
        title_layout2.setVisibility(View.GONE);
        iv_topics_left.setVisibility(View.VISIBLE);
        tv_topics_down.setVisibility(View.VISIBLE);

        iv_title_left.setOnClickListener(this);
        iv_title_left1.setOnClickListener(this);
        iv_topics_left.setOnClickListener(this);
        tv_topics_down.setOnClickListener(this);
    }

    private void setTitle() {
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText(topicName);
        tv_title_mid1.setText(topicName);
    }

    private View view1;

    private void addHeaderView() {
        view1 = LayoutInflater.from(this).inflate(R.layout.head_topics, null);
        iv_topcs_bg = (ImageView) view1.findViewById(R.id.iv_topcs_bg);
        tv_topcs_num = (TextView) view1.findViewById(R.id.tv_topcs_num);
        tv_topcs_title = (TextView) view1.findViewById(R.id.tv_topcs_title);
        tv_topcs_content = (TextView) view1.findViewById(R.id.tv_topcs_content);
        rl_topcs_up = (RelativeLayout) view1.findViewById(R.id.rl_topcs_up);
        iv_topcs_up = (ImageView) view1.findViewById(R.id.iv_topcs_up);

        // 中文加粗
        TextPaint tp = tv_topcs_title.getPaint();
        tp.setFakeBoldText(true);

        rl_topcs_up.setOnClickListener(this);

        // 添加头部
        listView.addHeaderView(view1);
    }

    /**
     * 头部参数
     */
    private void setHeaderInfo() {
        try {
            DisplayImageUtils.displayImage(json.data.img, iv_topcs_bg, 0,
                    R.color.bg);
            tv_topcs_num.setText("已有" + json.data.join_nums + "人参与");
            tv_topcs_title.setText(json.data.topic_title);
            // #话题#
            ListUtils.getNewTextView(this, json.data.topic_content,
                    tv_topcs_content);

            final int lineCount = tv_topcs_content.getLayout().getLineCount();
            if (lineCount <= 4) {
                rl_topcs_up.setVisibility(View.GONE);
            } else {
                rl_topcs_up.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            MLog.v("TopicsActivity", e + "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
            case R.id.iv_title_left1:
            case R.id.iv_topics_left:
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;

            case R.id.tv_topics_down:
                // 参与话题
                Intent intent = new Intent(this, EditTextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("key", 1);
                intent.putExtras(bundle);
                this.startActivity(intent);
                this.overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);

                PublishFeelingsFragment.getInstance().title = topicName;
                PublishFeelingsFragment.getInstance().source = 3;
                PublishFeelingsFragment.getInstance().needSaveView = false;
                break;

            case R.id.rl_topcs_up:
                // 话题内容显示(缩放)
                if (!is_content) {
                    iv_topcs_up.setBackgroundResource(R.drawable.topics_suofang);
                    tv_topcs_content.setMaxLines(10000);
                    is_content = true;
                } else {
                    iv_topcs_up.setBackgroundResource(R.drawable.topics_zhankai);
                    tv_topcs_content.setMaxLines(4);
                    is_content = false;
                }
                break;

            default:
                break;
        }
    }

    /**
     * 跳转 他人主页
     */
    public void goOther(int position) {
        mPosition = position;
        Intent intent = new Intent(this, NextActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentname", OtherUserFragment.class.getSimpleName());
        intent.putExtras(bundle);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        OtherUserFragment.getInstance().source = 5;
        OtherUserFragment.getInstance().ta_user_id = list.get(position).user_data.user_id;
        OtherUserFragment.getInstance().needSaveView = false;
    }

    /**
     * 跳转 讨论详情
     */
    public void goDetails(int position) {
        mPosition = position;
        Intent intent = new Intent(this, NextActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fragmentname", GroupDetailsFragment.class.getSimpleName());
        intent.putExtras(bundle);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        GroupDetailsFragment.getInstance().source = 6;
        GroupDetailsFragment.getInstance().groupPosition = mPosition;
        GroupDetailsFragment.getInstance().id = list.get(mPosition).id;
        GroupDetailsFragment.getInstance().needSaveView = false;
    }

    /**
     * 分享的点击
     */
    public void fenxiang(int position) {
        sharePopupWindow = new SelectSharePopupWindow(this, itemsOnClick);
        sharePopupWindow.showAtLocation(this.findViewById(R.id.ll_topics),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        // 分享的内容
        this.id = list.get(position).id;
        this.title = list.get(position).title;
        this.content = list.get(position).content;
        this.imageUrl = "";
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
            if (this == null) {
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
                    content = title + "," + content + "!来自@有书共读" + h5Url;
                    break;
                default:
                    break;
            }
            UMShare.setUMeng(TopicsActivity.this, num, title, content,
                    imageUrl, h5Url, id, "note");
            sharePopupWindow.dismiss();
        }
    };

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
        map.put("soft", VersionUtils.getVersion(this));

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
     * 话题详情 - 网络请求
     */
    private void getData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("user_id", GlobalParams.uid);
        map.put("title", topic_title);
        map.put("page", page + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.topic_info, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
                        is_loading = false;
                        if (pullToRefreshListView != null) {
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
//                             System.out.println("-----topic: "+ jsonString);
                            json = new Gson().fromJson(jsonString,
                                    TopicsJson.class);
                            if ("1".equals(json.code)) {
                                if (page == 1) {
                                    list.clear();
                                    if (json.data == null
                                            || json.data.list.size() == 0) {
                                        // 没有数据
                                    } else {
                                        list.addAll(json.data.list);
                                    }
                                    if (json.data != null) {
                                        // 不是官方话题(没有图片),不显示头部
                                        if (TextUtils.isEmpty(json.data.img)) {
                                            view1.setPadding(0,
                                                    -1 * view1.getHeight(), 0,
                                                    0);
                                            view1.setVisibility(View.GONE);
                                        } else {
                                            img = json.data.img;
                                        }
                                        // 头部参数
                                        setHeaderInfo();
                                        if (!TextUtils.isEmpty(img)) {
                                            // 滚动到顶部
                                            rl_title_layout1
                                                    .setVisibility(View.GONE);
                                            title_layout2
                                                    .setVisibility(View.GONE);
                                            iv_topics_left
                                                    .setVisibility(View.VISIBLE);
                                        } else {
                                            rl_title_layout1
                                                    .setVisibility(View.VISIBLE);
                                            title_layout2
                                                    .setVisibility(View.GONE);
                                            iv_topics_left
                                                    .setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    if (json.data == null
                                            || json.data.list.size() == 0) {
                                        page--;
                                    } else {
                                        list.addAll(json.data.list);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                view1.setPadding(0, -1 * view1.getHeight(), 0,
                                        0);
                                view1.setVisibility(View.GONE);
                                if (this != null) {
                                    Toast.makeText(TopicsActivity.this,
                                            json.msg, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        } catch (Exception e) {
                            if (this != null) {
                                Toast.makeText(TopicsActivity.this,
                                        getString(R.string.json_error),
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
     * 记录位置
     */
    private static int mPosition;

    /**
     * 删除后刷新方法
     */
    public void refresh() {
        list.remove(mPosition);
        adapter.notifyDataSetChanged();
    }

    /**
     * 发表话题后刷新方法
     */
    public void refresh1() {
        getData();
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
                    textView.setTextColor(getResources()
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
                    textView.setTextColor(getResources()
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
                    if (this != null) {
                        Toast.makeText(TopicsActivity.this,
                                getString(R.string.network_check),
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
            if ("1".equals(level_is_up)) {
                startActivity(new Intent(this, UpgradeActivity.class));
                level_is_up = "";
            }
            is_refresh = false;
            getData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TopicsActivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        // saveView = getView();
        MobclickAgent.onPageEnd("TopicsActivity");
    }
}
