package com.fengwo.reading.main.my.achieve;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.group.SearchFragment;
import com.fengwo.reading.main.my.MyInfoFragment;
import com.fengwo.reading.main.my.UserReadBean;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.ImageUtils;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.VipImageUtil;
import com.fengwo.reading.view.MyListView;
import com.fengwo.reading.view.MyScrollView;
import com.google.gson.Gson;
import com.igexin.a.a.b.e;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的成就
 */
public class MyAchieveFragment extends Fragment implements OnClickListener, MyScrollView.OnScrollListener {

    private CustomProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SelectSharePopupWindow sharePopupWindow;

    private ImageView iv_title_left, iv_title_right, iv_achieve_avatar, iv_achieve_avatar_bg, iv_achieve_vip,
            iv_achieve_dengji_qian, iv_achieve_dengji_hou;
    private TextView tv_title_mid,
            tv_achieve_jingyan_top, tv_achieve_jingyan_bottom, tv_achieve_medal, tv_achieve_sponsor;
    private RelativeLayout rl_achieve_tuceng1, rl_achieve_medal, rl_achieve_jiangli_all;
    private RelativeLayout rl_achieve_exe, rl_achieve_chaoyue;
    private TextView tv_achieve_exe, tv_achieve_chaoyue;
    private ProgressBar pb_achieve_show;
    private LinearLayout title_layout;
    private MyScrollView sv_discover_scrollview;
    private MyListView lv_achieve_renwu;

    private GridView gv_achieve_gridview;

    private List<TaskBean> list;
    private MyAchieveAdapter adapter;

    private AchieveInfoJson json;

    public boolean is_refresh = false; // 是否刷新

    private View saveView = null;
    public boolean needSaveView = false;

    private static MyAchieveFragment fragment = new MyAchieveFragment();

    public static MyAchieveFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_myachieve, container,
                false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());

        findViewById(view);
        setTitle();

        list = new ArrayList<>();
        adapter = new MyAchieveAdapter(fragment, list);
        lv_achieve_renwu.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // 下拉控件的监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getData1();
                getData();
            }
        });

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getData1();
                getData();
            }
        }, 300);

        //Y轴移动监听
        sv_discover_scrollview.setScrollViewListener(this);

        lv_achieve_renwu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if ("save_info".equals(list.get(position).type)) {
                    UMengUtils.onCountListener(getActivity(), "GD_05_03_08");
                    FragmentTransaction transaction = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right,
                            R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.ll_activity_next,
                            MyInfoFragment.getInstance());
                    transaction.commit();
                    MyInfoFragment.getInstance().needSaveView = false;
                    MyInfoFragment.getInstance().source = 1;
                    is_refresh = true;
                } else {
                    FragmentTransaction transaction = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right,
                            R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.ll_activity_next,
                            ExplainFragment.getInstance());
                    transaction.commit();
                    ExplainFragment.getInstance().needSaveView = false;
                    ExplainFragment.getInstance().source = list.get(position).type;
                }
            }
        });

        //分享的弹出窗体类
        sharePopupWindow = new SelectSharePopupWindow(getActivity(),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        FragmentActivity activity = getActivity();
                        if (activity == null) {
                            return;
                        }
                        sharePopupWindow.imageUrl = "";
                        sharePopupWindow.h5Url = GlobalConstant.ServerDomain + "share/achieve?user_id=" + GlobalParams.uid;
                        sharePopupWindow.title = GlobalParams.userInfoBean.name + "的「有书共读」成就";
                        sharePopupWindow.content = " 我已参加“有书共读”【" + json.data.sum_date + "】天,坚持阅读让时间成为朋友";
                        int num = 0;
                        switch (v.getId()) {
                            case R.id.ll_popupwindow_wx:
                                num = 1;
                                break;
                            case R.id.ll_popupwindow_pyq:
                                num = 2;
                                sharePopupWindow.content = GlobalParams.userInfoBean.name + "的「有书共读」成就";
                                break;
                            case R.id.ll_popupwindow_qq:
                                num = 3;
                                break;
                            case R.id.ll_popupwindow_wb:
                                num = 4;
                                sharePopupWindow.content = GlobalParams.userInfoBean.name + "的「有书共读」成就，坚持阅读让时间成为朋友，来自@有书共读" + sharePopupWindow.h5Url;
                                break;
                            default:
                                break;
                        }
//                        UMengUtils.onCountListener(getActivity(), "shouye_CSB_FX");
                        UMShare.setUMeng(activity, num, sharePopupWindow.title, sharePopupWindow.content, sharePopupWindow.imageUrl, sharePopupWindow.h5Url, "", "");
//                        if ("1".equals(UMShare.getLevel())) {
//                            startActivity(new Intent(getActivity(), UpgradeActivity.class));
//                        }
                        sharePopupWindow.dismiss();
                    }
                });

        return view;
    }

    private void findViewById(View view) {
        sv_discover_scrollview = (MyScrollView) view
                .findViewById(R.id.sv_discover_scrollview);
        swipeRefreshLayout = (SwipeRefreshLayout) view
                .findViewById(R.id.srl_discover_refresh);
        title_layout = (LinearLayout) view.findViewById(R.id.title_layout);
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        iv_title_right = (ImageView) view.findViewById(R.id.iv_title_right);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        rl_achieve_tuceng1 = (RelativeLayout) view.findViewById(R.id.rl_achieve_tuceng1);
        iv_achieve_avatar_bg = (ImageView) view.findViewById(R.id.iv_achieve_avatar_bg);
        iv_achieve_avatar = (ImageView) view.findViewById(R.id.iv_achieve_avatar);
        iv_achieve_vip = (ImageView) view.findViewById(R.id.iv_achieve_vip);

        rl_achieve_exe = (RelativeLayout) view.findViewById(R.id.rl_achieve_exe);
        tv_achieve_exe = (TextView) view.findViewById(R.id.tv_achieve_exe);
        rl_achieve_chaoyue = (RelativeLayout) view.findViewById(R.id.rl_achieve_chaoyue);
        tv_achieve_chaoyue = (TextView) view.findViewById(R.id.tv_achieve_chaoyue);

        iv_achieve_dengji_qian = (ImageView) view.findViewById(R.id.iv_achieve_dengji_qian);
        tv_achieve_jingyan_top = (TextView) view.findViewById(R.id.tv_achieve_jingyan_top);
        iv_achieve_dengji_hou = (ImageView) view.findViewById(R.id.iv_achieve_dengji_hou);
        pb_achieve_show = (ProgressBar) view.findViewById(R.id.pb_achieve_show);
        tv_achieve_jingyan_bottom = (TextView) view.findViewById(R.id.tv_achieve_jingyan_bottom);
        rl_achieve_medal = (RelativeLayout) view.findViewById(R.id.rl_achieve_medal);
        tv_achieve_medal = (TextView) view.findViewById(R.id.tv_achieve_medal);

        rl_achieve_jiangli_all = (RelativeLayout) view.findViewById(R.id.rl_achieve_jiangli_all);
        gv_achieve_gridview = (GridView) view.findViewById(R.id.gv_achieve_gridview);

        lv_achieve_renwu = (MyListView) view.findViewById(R.id.lv_achieve_renwu);
        tv_achieve_sponsor = (TextView) view.findViewById(R.id.tv_achieve_sponsor);

        iv_title_left.setOnClickListener(this);
        iv_title_right.setOnClickListener(this);
        rl_achieve_tuceng1.setOnClickListener(this);
        rl_achieve_medal.setOnClickListener(this);
        tv_achieve_sponsor.setOnClickListener(this);
    }

    private void setTitle() {
        tv_title_mid.setText("我的成就");
        tv_title_mid.setVisibility(View.GONE);
        iv_title_right.setVisibility(View.VISIBLE);
        title_layout.setBackgroundColor(Color.TRANSPARENT);
        iv_title_right.setImageResource(R.drawable.share_white);
    }

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left,
                R.anim.out_to_right);
        switch (v.getId()) {
            case R.id.iv_return:
                UMengUtils.onCountListener(getActivity(), "GD_05_03_01");
                getActivity().finish();
                getActivity().overridePendingTransition(
                        R.anim.in_from_left, R.anim.out_to_right);
                break;
            case R.id.iv_title_right:
                //分享
                sharePopupWindow.showAtLocation(
                        getActivity().findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.rl_achieve_tuceng1:
                //顶部 - 跳转有书榜
                UMengUtils.onCountListener(getActivity(), "GD_05_03_02");
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, Fragment_Youshubang.getInstance());
                transaction.commit();
                Fragment_Youshubang.getInstance().needSaveView = false;
                break;
            case R.id.rl_achieve_medal:
                //勋章
                UMengUtils.onCountListener(getActivity(), "GD_05_03_07");
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, Fragment_wodexunzhang.getInstance());
                transaction.commit();
                Fragment_wodexunzhang.getInstance().needSaveView = false;
                break;
            case R.id.tv_achieve_sponsor:
                //商务合作  - 赞助
                UMengUtils.onCountListener(getActivity(), "GD_05_03_15");
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        WebFragment.getInstance());
                transaction.commit();
                WebFragment.getInstance().needSaveView = false;
                WebFragment.getInstance().url = "http://api.fengwo.com/share/hezuo";
                WebFragment.getInstance().source = 2;
                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 我的成就
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.user_cj, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(true);
                        String jsonString = responseInfo.result;
                        try {
//                            System.out.println("---------1111:" + GlobalParams.uid + " , " + jsonString);
                            json = new Gson().fromJson(jsonString,
                                    AchieveInfoJson.class);
                            if ("1".equals(json.code)) {
                                //设置参数
                                setInfo();
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
     * 网络请求 - 成长任务
     */
    private void getData1() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.grow_task, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        // 收起下拉动画
//                        swipeRefreshLayout.setRefreshing(false);
//                        swipeRefreshLayout.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
//                        swipeRefreshLayout.setRefreshing(false);
//                        swipeRefreshLayout.setEnabled(true);
                        String jsonString = responseInfo.result;
                        try {
//                            System.out.println("------: " + jsonString);
                            TaskJson json = new Gson().fromJson(jsonString,
                                    TaskJson.class);
                            if ("1".equals(json.code)) {
                                list.clear();
                                if (json.data == null || json.data.size() == 0) {

                                } else {
                                    if (json.data.get(0).cnum.equals(json.data.get(0).sum)) {
                                        json.data.remove(0);
                                    }
                                    list.addAll(json.data);
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
     * 参数设置
     */
    private void setInfo() {
        //头像高斯背景
        new BitmapUtils(getActivity()).display(iv_achieve_avatar_bg, GlobalParams.userInfoBean.avatar, new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadCompleted(ImageView imageView, String s, Bitmap bitmap, BitmapDisplayConfig bitmapDisplayConfig, BitmapLoadFrom bitmapLoadFrom) {
                Drawable drawable = new BitmapDrawable(getResources(), ImageUtils.gaosiFilter(bitmap, 40, 40));
                if (Build.VERSION.SDK_INT >= 16) {
                    imageView.setImageDrawable(drawable);
                }
            }

            @Override
            public void onLoadFailed(ImageView imageView, String s, Drawable drawable) {
            }
        });
        //头像
        DisplayImageUtils.displayImage(GlobalParams.userInfoBean.avatar,
                iv_achieve_avatar, 150, R.drawable.avatar);
        //有关等级首先调用此方法
        VipImageUtil.getExp(json.data.exp);
        GlobalParams.userInfoBean.exp = json.data.exp;
        VipImageUtil.getVipGrade(getActivity(), iv_achieve_vip, VipImageUtil.getGrade(), 1);
        int exp = 0;
        try {
            //经验进度
            exp = Integer.valueOf(json.data.exp).intValue();
            pb_achieve_show.setMax(VipImageUtil.getExpAll() - VipImageUtil.getExpStart());
            pb_achieve_show.setProgress(exp - VipImageUtil.getExpStart());
        } catch (Exception e) {
            MLog.v("MyAchieveFragment", e + "");
        }
        //今日经验值  超越%书友
        tv_achieve_exe.setText(json.data.day_exp);
        String chao = json.data.chao.substring(0, json.data.chao.length() - 1);
        tv_achieve_chaoyue.setText(chao);
        //当前等级 下一等级  所差经验
        VipImageUtil.getVipGrade(getActivity(), iv_achieve_dengji_qian, VipImageUtil.getGrade(), 2);
        VipImageUtil.getVipGrade(getActivity(), iv_achieve_dengji_hou, VipImageUtil.getGrade() + 1, 3);
        tv_achieve_jingyan_bottom.setText("再获得" + (VipImageUtil.getExpAll() - exp) + "点经验值即可升级为V" + (VipImageUtil.getGrade() + 1));
        //现有经验  勋章数量
        tv_achieve_jingyan_top.setText(json.data.exp);
        tv_achieve_medal.setText(json.data.badge_num);
        //成长奖励
        if (json.data.grow_prize == null || json.data.grow_prize.size() == 0) {
            rl_achieve_jiangli_all.setVisibility(View.GONE);
        } else {
            rl_achieve_jiangli_all.setVisibility(View.VISIBLE);
            gv_achieve_gridview.setAdapter(new MyAdapter(getActivity(), json.data.grow_prize));
        }
    }

    /**
     * Gridview适配器
     */
    private class MyAdapter extends BaseAdapter {

        private Context context;
        private List<AchieveInfoBean.GrowPrizeBean> list;

        public MyAdapter(Context context, List<AchieveInfoBean.GrowPrizeBean> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
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
                            final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_myachieve_gridview, parent, false);
                holder.iv_achieve_jiangli = (ImageView) convertView
                        .findViewById(R.id.iv_achieve_jiangli);
                holder.tv_achieve_jiangli = (TextView) convertView
                        .findViewById(R.id.tv_achieve_jiangli);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DisplayImageUtils.displayImage(list.get(position).img,
                    holder.iv_achieve_jiangli, 0, R.drawable.zanwufengmian);

            try {
                final int level = Integer.valueOf(list.get(position).need_level).intValue();
                if (VipImageUtil.getGrade() >= level) {
                    holder.tv_achieve_jiangli.setVisibility(View.GONE);
                } else {
                    holder.tv_achieve_jiangli.setVisibility(View.VISIBLE);
                    holder.tv_achieve_jiangli.setText("V" + list.get(position).need_level + "可领取");
                    holder.tv_achieve_jiangli.setBackgroundResource(R.drawable.myachieve_jiantouqian);
                }

                holder.iv_achieve_jiangli.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (VipImageUtil.getGrade() >= level) {
                            UMengUtils.onCountListener(getActivity(), "GD_05_03_0" + (position + 1));
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                    .beginTransaction();
                            transaction.setCustomAnimations(R.anim.in_from_right,
                                    R.anim.out_to_left, R.anim.in_from_left,
                                    R.anim.out_to_right);
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.ll_activity_next,
                                    WebFragment.getInstance());
                            transaction.commit();
                            WebFragment.getInstance().needSaveView = false;
                            WebFragment.getInstance().url = list.get(position).href;
                            WebFragment.getInstance().source = 2;
                        } else {
                            Toast.makeText(getActivity(), "等级达到V" + list.get(position).need_level + "才可领取哦", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
            }

            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_achieve_jiangli;
            private TextView tv_achieve_jiangli;
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    Context context = fragment.getActivity();
                    if (context != null) {
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
        if (is_refresh) {
            is_refresh = false;
            getData1();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("MyAchieveFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyAchieveFragment");
    }

    /**
     * Sclooview Y轴移动距离监听
     *
     * @param y
     */
    @Override
    public void onScroll(int y) {
        if (y < 5) {
            tv_title_mid.setVisibility(View.GONE);
            title_layout.setBackgroundColor(Color.TRANSPARENT);
        } else {
            tv_title_mid.setVisibility(View.VISIBLE);
            title_layout.setBackgroundColor(getActivity().getResources().getColor(R.color.green_17));
        }
    }
}
