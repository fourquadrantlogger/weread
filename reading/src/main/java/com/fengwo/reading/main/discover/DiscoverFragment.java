package com.fengwo.reading.main.discover;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.HotActivity;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.discover.hottopics.HotFragment;
import com.fengwo.reading.main.discover.hottopics.HotListBean;
import com.fengwo.reading.main.group.GroupAdapter;
import com.fengwo.reading.main.group.GroupBean;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.main.read.Fragment_BookList;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.view.MyGridView;
import com.fengwo.reading.view.MyListView;
import com.fengwo.reading.zxing.activity.CaptureActivity;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发现
 *
 * @author Luo Sheng
 * @date 2016-3-23
 */
public class DiscoverFragment extends Fragment implements OnClickListener {

    public CustomProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView iv_title_left, iv_title_right, iv_discover_activity, iv_discover_topic, iv_discover_books, iv_discover_selection, iv_discover_book_img;
    private TextView tv_title_mid, tv_discover_all_jingxuan, tv_discover_all_daren, tv_discover_xinren;
    private LinearLayout ll_discover_jingxuan, ll_discover_daren, ll_discover_layout;
    private RelativeLayout rl_discover_xinren;
    private MyGridView gv_discover_xinren;

    private MyListView lv_discover_jingxuan, lv_discover_daren;

    private GroupAdapter mJingXuanAdapter;
    private ACEAdapter mDaRenAdapter;
    private MyDiscoverXRBAdapter mXinRenBangAdapter;
    public List<GroupBean> JXList;
    public List<HotListBean> HTList;
    public List<ACEBean> DRList;
    public List<ACEBean> XRBList;

    // 分享的信息
    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    public String action = ""; //活动页面链接地址
    public String book_id = "";
    public String book_title = "";
    private boolean is_loading;

    private View saveView = null;
    public boolean needSaveView = false;

    public DiscoverFragment() {
    }

    public static DiscoverFragment fragment = new DiscoverFragment();

    public static DiscoverFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // if (needSaveView && saveView != null) {
        // return saveView;
        // }
        // needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_discover, container, false);
//		progressDialog = CustomProgressDialog.createDialog(getActivity());

        findViewById(view);
        setTitle();

        HTList = new ArrayList<>();
        JXList = new ArrayList<>();
        DRList = new ArrayList<>();
        XRBList = new ArrayList<>();
        mJingXuanAdapter = new GroupAdapter(fragment, JXList, 7);
        mDaRenAdapter = new ACEAdapter(fragment, DRList);
        mXinRenBangAdapter = new MyDiscoverXRBAdapter(getActivity(), XRBList);

        addHeaderView();
        lv_discover_jingxuan.setAdapter(mJingXuanAdapter);
        lv_discover_daren.setAdapter(mDaRenAdapter);
        gv_discover_xinren.setAdapter(mXinRenBangAdapter);

        // 控件的颜色
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // 下拉控件的监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (is_loading) {
                    return;
                }
                is_loading = true;
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

        lv_discover_daren.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //每周达人,跳转他人主页
                Intent intent = new Intent(getActivity(), NextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("fragmentname", OtherUserFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);

                OtherUserFragment.getInstance().source = 4;
                OtherUserFragment.getInstance().ta_user_id = DRList.get(i).user_id;
                OtherUserFragment.getInstance().needSaveView = false;
            }
        });

        gv_discover_xinren.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //新人榜,跳转他人主页
                Intent intent = new Intent(getActivity(), NextActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("fragmentname", OtherUserFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);

                OtherUserFragment.getInstance().source = 4;
                OtherUserFragment.getInstance().ta_user_id = XRBList.get(i).user_id;
                OtherUserFragment.getInstance().needSaveView = false;
            }
        });

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        iv_title_right = (ImageView) view.findViewById(R.id.iv_title_right);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.srl_discover_refresh);

        ll_discover_layout = (LinearLayout) view.findViewById(R.id.ll_discover_layout);

        ll_discover_jingxuan = (LinearLayout) view.findViewById(R.id.ll_discover_jingxuan);
        lv_discover_jingxuan = (MyListView) view.findViewById(R.id.lv_discover_jingxuan);
        tv_discover_all_jingxuan = (TextView) view.findViewById(R.id.tv_discover_all_jingxuan);

        ll_discover_daren = (LinearLayout) view.findViewById(R.id.ll_discover_daren);
        lv_discover_daren = (MyListView) view.findViewById(R.id.lv_discover_daren);
        tv_discover_all_daren = (TextView) view.findViewById(R.id.tv_discover_all_daren);

        rl_discover_xinren = (RelativeLayout) view.findViewById(R.id.rl_discover_xinren);
        tv_discover_xinren = (TextView) view.findViewById(R.id.tv_discover_xinren);
        gv_discover_xinren = (MyGridView) view.findViewById(R.id.gv_discover_xinren);
        gv_discover_xinren.setSelector(new ColorDrawable(Color.TRANSPARENT));

        iv_title_right.setOnClickListener(this);
        tv_discover_all_jingxuan.setOnClickListener(this);
        tv_discover_all_daren.setOnClickListener(this);
        tv_discover_xinren.setOnClickListener(this);
    }

    private void setTitle() {
        iv_title_left.setVisibility(View.GONE);
        iv_title_right.setVisibility(View.VISIBLE);
        tv_title_mid.setVisibility(View.VISIBLE);
        iv_title_right.setImageResource(R.drawable.discover_scan);
        tv_title_mid.setText("发现");
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.iv_title_right:
                //扫描二维码(特权)
                UMengUtils.onCountListener(getActivity(), "GD_04_08");
                intent.setClass(getActivity(), CaptureActivity.class);
                bundle.putString("type", "pack");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                break;
            case R.id.iv_discover_topic:
                UMengUtils.onCountListener(getActivity(), "GD_04_02");
                //热门话题
                intent.setClass(getActivity(), HotActivity.class);
                bundle.putInt("key", 1);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                HotFragment.getInstance().needSaveView = false;
                break;
            case R.id.iv_discover_activity:
                UMengUtils.onCountListener(getActivity(), "GD_04_03");
                //有书活动
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", WebFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                WebFragment.getInstance().needSaveView = false;
                WebFragment.getInstance().url = action;
                WebFragment.getInstance().source = 1;
                break;
            case R.id.iv_discover_books:
                UMengUtils.onCountListener(getActivity(), "GD_04_04");
                //往期书单
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", Fragment_BookList.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                Fragment_BookList.getInstance().needSaveView = false;
                break;
            case R.id.iv_discover_selection:
                UMengUtils.onCountListener(getActivity(), "GD_04_05");
                //精选随笔(书籍)
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", ChoicenessBooksFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                ChoicenessBooksFragment.getInstance().needSaveView = false;
                break;
            case R.id.iv_discover_book_img:
                //共读书籍图片
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", ChoicenessBooksDetailsFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);

                ChoicenessBooksDetailsFragment.getInstance().source = 1;
                ChoicenessBooksDetailsFragment.getInstance().book_id = book_id;
                ChoicenessBooksDetailsFragment.getInstance().book_title = book_title;
                break;
            case R.id.tv_discover_all_jingxuan:
                UMengUtils.onCountListener(getActivity(), "faxian_all_JX");
                //查看更多精选随笔
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", ChoicenessBooksDetailsFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                ChoicenessBooksDetailsFragment.getInstance().source = 1;
                ChoicenessBooksDetailsFragment.getInstance().book_id = book_id;
                ChoicenessBooksDetailsFragment.getInstance().book_title = book_title;
                break;
            case R.id.tv_discover_all_daren:
                UMengUtils.onCountListener(getActivity(), "GD_04_06");
                //查看更多达人
                intent.setClass(getActivity(), NextActivity.class);
                bundle.putString("fragmentname", ACEFragment.class.getSimpleName());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                ACEFragment.getInstance().needSaveView = false;
                ACEFragment.getInstance().source = 2;
                break;
            case R.id.tv_discover_xinren:
                UMengUtils.onCountListener(getActivity(), "GD_04_07");
                //新人榜 - 换一换
                getXRBData();
                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 发现首页
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.find_findInfo, new RequestCallBack<String>() {

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
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        is_loading = false;
                        swipeRefreshLayout.setEnabled(true);
                        String jsonString = responseInfo.result;
                        try {
//                            System.out.println("------jsonString:"+jsonString);
                            DiscoverJson json = new Gson().fromJson(jsonString,
                                    DiscoverJson.class);
                            if ("1".equals(json.code)) {
                                HTList.clear();
                                JXList.clear();
                                DRList.clear();
                                XRBList.clear();

                                action = json.action;
                                book_id = json.book.book_id;
                                book_title = json.book.book_title;

                                HTList.addAll(json.topic);
                                if (json.note.size() >= 3) {
                                    for (int i = 0; i < 3; i++) {
                                        JXList.add(json.note.get(i));
                                    }
                                }

                                if (json.export.size() == 0) {
                                    ll_discover_daren.setVisibility(View.GONE);
                                } else {
                                    ll_discover_daren.setVisibility(View.VISIBLE);
                                    DRList.addAll(json.export);
                                }
                                mDaRenAdapter.notifyDataSetChanged();

                                if (json.weekNewer.size() == 0) {
                                    rl_discover_xinren.setVisibility(View.GONE);
                                } else {
                                    rl_discover_xinren.setVisibility(View.VISIBLE);
                                    XRBList.addAll(json.weekNewer);
                                }
                                mXinRenBangAdapter.notifyDataSetChanged();

                                lv_discover_jingxuan.removeHeaderView(topicView);
                                lv_discover_jingxuan.removeHeaderView(view1);
                                addHeaderView();
                                DisplayImageUtils.displayImage(json.book.chosen_img,
                                        iv_discover_book_img, 0, R.drawable.zanwufengmian);
                                mJingXuanAdapter.notifyDataSetChanged();
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
                                e.printStackTrace();
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 网络请求 - 新人榜(换一换)
     */
    private void getXRBData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("type", "0");
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.find_exportNewer, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {
                            DiscoverJson json = new Gson().fromJson(jsonString,
                                    DiscoverJson.class);
                            if ("1".equals(json.code)) {
                                XRBList.clear();
                                if (json.data == null || json.data.size() == 0) {
                                    rl_discover_xinren.setVisibility(View.GONE);
                                } else {
                                    rl_discover_xinren.setVisibility(View.VISIBLE);
                                    XRBList.addAll(json.data);
                                }

                                mXinRenBangAdapter.notifyDataSetChanged();
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

    private View view1;
    private View topicView;

    private void addHeaderView() {
        view1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.head_discover, null);
        iv_discover_activity = (ImageView) view1.findViewById(R.id.iv_discover_activity);
        iv_discover_topic = (ImageView) view1.findViewById(R.id.iv_discover_topic);
        iv_discover_books = (ImageView) view1.findViewById(R.id.iv_discover_books);
        iv_discover_selection = (ImageView) view1.findViewById(R.id.iv_discover_selection);
        iv_discover_book_img = (ImageView) view1.findViewById(R.id.iv_discover_book_img);
        iv_discover_activity.setOnClickListener(this);
        iv_discover_topic.setOnClickListener(this);
        iv_discover_books.setOnClickListener(this);
        iv_discover_selection.setOnClickListener(this);
        iv_discover_book_img.setOnClickListener(this);

        CarouselLayout.is_show = false;
        topicView = new CarouselLayout()
                .createHeaderLayout(
                        fragment.getActivity(),
                        HTList,
                        swipeRefreshLayout);

        lv_discover_jingxuan.addHeaderView(topicView);
        lv_discover_jingxuan.addHeaderView(view1);
    }

    /**
     * Gridview适配器
     */
    private class MyDiscoverXRBAdapter extends BaseAdapter {

        private Context context;
        private List<ACEBean> list;

        public MyDiscoverXRBAdapter(Context context, List<ACEBean> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
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
                        R.layout.item_discover_xinren, parent, false);
                holder.tv_discover_xinren_name = (TextView) convertView
                        .findViewById(R.id.tv_discover_xinren_name);
                holder.iv_discover_xinren_avatar = (ImageView) convertView
                        .findViewById(R.id.iv_discover_xinren_avatar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DisplayImageUtils.displayImage(list.get(position).avatar,
                    holder.iv_discover_xinren_avatar, 100, R.drawable.avatar);
            holder.tv_discover_xinren_name.setText(list.get(position).name);

            return convertView;
        }

        private class ViewHolder {
            private TextView tv_discover_xinren_name;
            private ImageView iv_discover_xinren_avatar;
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

        OtherUserFragment.getInstance().source = 4;
        OtherUserFragment.getInstance().ta_user_id = JXList.get(position).user_data.user_id;
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

        GroupDetailsFragment.getInstance().groupPosition = position;
        GroupDetailsFragment.getInstance().source = 9;
        GroupDetailsFragment.getInstance().id = JXList.get(position).id;
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
        sharePopupWindow.showAtLocation(activity.findViewById(R.id.ll_discover_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        // 分享的内容
        if (JXList.get(position).img_str != null && JXList.get(position).img_str.length != 0) {
            this.imageUrl = JXList.get(position).img_str[0];
        } else if (JXList.get(position).user_data.avatar != null) {
            this.imageUrl = JXList.get(position).user_data.avatar;
        } else {
            this.imageUrl = "";
        }
        this.h5Url = GlobalConstant.ServerDomain + "share/note?note_id="
                + JXList.get(position).id;
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
            if (!TextUtils.isEmpty(JXList.get(mPosition).title)) {
                title = JXList.get(mPosition).title;
            } else {
                title = JXList.get(mPosition).user_data.name + "的随笔";
            }
            switch (v.getId()) {
                case R.id.ll_popupwindow_wx:
                    num = 1;
                    content = JXList.get(mPosition).content;
                    break;
                case R.id.ll_popupwindow_pyq:
                    num = 2;
                    if (!TextUtils.isEmpty(JXList.get(mPosition).title)) {
                        content = JXList.get(mPosition).title;
                    } else {
                        content = JXList.get(mPosition).content;
                    }
                    break;
                case R.id.ll_popupwindow_qq:
                    num = 3;
                    content = JXList.get(mPosition).content;
                    break;
                case R.id.ll_popupwindow_wb:
                    num = 4;
                    if (TextUtils.isEmpty(JXList.get(mPosition).title)) {
                        content = "推荐+" + JXList.get(mPosition).user_data.name + "的共读随笔,来自@有书共读" + h5Url;
                    } else {
                        content = "推荐+" + JXList.get(mPosition).user_data.name + "的共读随笔《" + JXList.get(mPosition).title + "》,来自@有书共读" + h5Url;
                    }
                    break;
                default:
                    break;
            }
            UMShare.setUMeng(activity, num, title, content, imageUrl, h5Url, JXList.get(mPosition).id, "note");
            if ("1".equals(UMShare.getLevel())) {
                startActivity(new Intent(getActivity(), UpgradeActivity.class));
            }
            sharePopupWindow.dismiss();
        }
    };

    /**
     * 点赞的点击
     */
    public void dianzan(final int position) {
//        UMengUtils.onCountListener(getActivity(), "youSQ_DZ");

        String act = "";
        // 已经点赞过
        if ("1".equals(JXList.get(position).is_digg)) {
            act = "c";
        } else {
            act = "";
        }
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("id", JXList.get(position).id);
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
        getData();
    }

    /**
     * 点赞后的刷新
     */
    public void refresh(int position, String tag, int num) {
        if (tag.startsWith("dianzan")) {
            TextView textView = (TextView) lv_discover_jingxuan
                    .findViewWithTag("dianzan_tv" + position);
            if (textView != null) {
                if ("1".equals(JXList.get(position).is_digg)) {
                    // 已经赞过了
                    JXList.get(position).is_digg = "0";
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
                                .valueOf(JXList.get(position).digg_count);
                        textView.setText((count - 1) + "");
                        JXList.get(position).digg_count = (count - 1) + "";
                    } catch (Exception e) {
                    }
                } else {
                    // 设置为赞过
                    JXList.get(position).is_digg = "1";
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
                                .valueOf(JXList.get(position).digg_count);
                        textView.setText((count + 1) + "");
                        JXList.get(position).digg_count = (count + 1) + "";
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("DiscoverFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("DiscoverFragment");
    }
}
