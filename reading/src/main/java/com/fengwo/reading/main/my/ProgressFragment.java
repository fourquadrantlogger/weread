package com.fengwo.reading.main.my;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.view.MyListView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fengwo.reading.main.my.ProgressJson.*;

/**
 * 我的阅历
 */
public class ProgressFragment extends Fragment implements OnClickListener {

    private SelectSharePopupWindow sharePopupWindow;
    private ImageView iv_title_left, iv_title_right;
    private LinearLayout title_layout;
    private TextView tv_title_mid;
    //头部
    private ImageView iv_progress_avatar, iv_progress_bookimg;
    private TextView tv_progress_day, tv_progress_duguo, tv_progress_qiandao, tv_progress_zan, tv_progress_suibi, tv_progress_bookname, tv_progress_name, tv_progress_gone, tv_progress_time;

    private CustomProgressDialog progressDialog;
    private PullToRefreshListView pullToRefreshListView;
    private ListView listView;
    private List<ProgressBean2> list;
    private List<ProgressBean2> allList;
    private ProgressAdapter adapter;
    private ProgressBean2 bean = new ProgressBean2();
    private boolean is_loading;
    private int page;

    private int num = 3;//分页加载

    private ProgressJson json = null;

    private View saveView = null;
    public boolean needSaveView = false;

    public static ProgressFragment fragment = new ProgressFragment();

    public static ProgressFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_progress, container,
                false);

        findViewById(view);
        setTitle();
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = pullToRefreshListView.getRefreshableView();

        progressDialog = CustomProgressDialog.createDialog(fragment
                .getActivity());
        // 添加头部
        addHeaderView();

        list = new ArrayList<>();
        allList = new ArrayList<>();
        adapter = new ProgressAdapter(fragment, list);
        listView.setAdapter(adapter);
        is_loading = false;
        page = 1;
        num = 3;

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                is_loading = true;
                list.add(bean);
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
//                        if (is_loading) {
//                            return;
//                        }
//                        is_loading = true;
//                        page++;
//                        getData();

                        refresh();

                        if (allList.size() == 0 || is_loading) {
                            Toast.makeText(getActivity(), "已经没有更多数据", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            if (num + 6 > allList.size()) {
                                list.addAll(new ArrayList<>(allList.subList(num, allList.size())));
                                is_loading = true;
                            } else {
                                list.addAll(new ArrayList<>(allList.subList(num, num + 6)));
                                num += 6;
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

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
                    iv_title_right.setVisibility(View.VISIBLE);
                    tv_title_mid.setVisibility(View.GONE);
                } else {
                    title_layout.setBackgroundColor(getActivity().getResources().getColor(R.color.green_17));
                    iv_title_right.setVisibility(View.VISIBLE);
                    tv_title_mid.setVisibility(View.VISIBLE);
                }
            }
        });

        //分享的弹出窗体类
        sharePopupWindow = new SelectSharePopupWindow(getActivity(),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        UMengUtils.onCountListener(getActivity(), "GD_05_04_01");
                        FragmentActivity activity = getActivity();
                        if (activity == null) {
                            return;
                        }
                        sharePopupWindow.imageUrl = GlobalParams.userInfoBean.avatar;
                        sharePopupWindow.h5Url = GlobalConstant.ServerDomain + "share/yueli?user_id=" + GlobalParams.uid;
                        sharePopupWindow.title = GlobalParams.userInfoBean.name + "的「有书共读」阅历";
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
                                sharePopupWindow.content = "分享我的#有书共读#阅历," + sharePopupWindow.content + ",来自@有书共读";
                                break;
                            default:
                                break;
                        }

                        UMShare.setUMeng(activity, num, sharePopupWindow.title, sharePopupWindow.content, sharePopupWindow.imageUrl, sharePopupWindow.h5Url, "", "");

                        sharePopupWindow.dismiss();
                    }
                });

        return view;
    }

    private void findViewById(View view) {
        title_layout = (LinearLayout) view.findViewById(R.id.title_layout);
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        iv_title_right = (ImageView) view.findViewById(R.id.iv_title_right);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_progress_show);

        iv_title_left.setOnClickListener(this);
        iv_title_right.setOnClickListener(this);
    }

    private void setTitle() {
        title_layout.setBackgroundColor(Color.TRANSPARENT);
        tv_title_mid.setText("我的阅历");
        tv_title_mid.setVisibility(View.GONE);
        iv_title_right.setVisibility(View.VISIBLE);
        iv_title_right.setImageResource(R.drawable.share_white);
    }

    private void addHeaderView() {
        View view1 = LayoutInflater.from(getActivity()).inflate(
                R.layout.head_progress, null);
        iv_progress_avatar = (ImageView) view1
                .findViewById(R.id.iv_progress_avatar);
        tv_progress_day = (TextView) view1
                .findViewById(R.id.tv_progress_day);
        tv_progress_duguo = (TextView) view1
                .findViewById(R.id.tv_progress_duguo);
        tv_progress_qiandao = (TextView) view1
                .findViewById(R.id.tv_progress_qiandao);
        tv_progress_zan = (TextView) view1
                .findViewById(R.id.tv_progress_zan);
        tv_progress_suibi = (TextView) view1
                .findViewById(R.id.tv_progress_suibi);
        iv_progress_bookimg = (ImageView) view1
                .findViewById(R.id.iv_progress_bookimg);
        tv_progress_bookname = (TextView) view1
                .findViewById(R.id.tv_progress_bookname);
        tv_progress_name = (TextView) view1
                .findViewById(R.id.tv_progress_name);
        tv_progress_time = (TextView) view1
                .findViewById(R.id.tv_progress_time);
        tv_progress_gone = (TextView) view1
                .findViewById(R.id.tv_progress_gone);

        iv_progress_bookimg.setOnClickListener(this);

        // 添加头部
        listView.addHeaderView(view1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                fragment.getActivity().finish();
                fragment.getActivity().overridePendingTransition(
                        R.anim.in_from_left, R.anim.out_to_right);
                break;
            case R.id.iv_title_right:
                //我的阅历分享
                sharePopupWindow.showAtLocation(
                        getActivity().findViewById(R.id.ll_activity_next), Gravity.BOTTOM
                                | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.iv_progress_bookimg:
                //在读书籍封面

                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 我的阅历
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.user_read, new RequestCallBack<String>() {

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
                        pullToRefreshListView.onRefreshComplete();
//                        is_loading = false;
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }.start();
                        pullToRefreshListView.onRefreshComplete();
//                        is_loading = false;
                        String jsonString = responseInfo.result;
                        try {
//                            System.out.println("------987:" + jsonString);
                            json = new Gson().fromJson(
                                    jsonString, ProgressJson.class);
                            if ("1".equals(json.code)) {
                                //设置头部参数
                                setInfo(json);

                                list.clear();
                                allList.clear();

                                if (json.BookData == null || json.BookData.size() == 0) {
                                    tv_progress_gone
                                            .setVisibility(View.GONE);
                                    list.add(bean);
                                } else {
                                    tv_progress_gone.setVisibility(View.VISIBLE);
                                    allList.addAll(json.BookData);
                                    if (allList.size() < 3) {
                                        list.addAll(new ArrayList<>(allList.subList(0, allList.size())));
                                        is_loading = true;
                                    } else {
                                        list.addAll(new ArrayList<>(allList.subList(0, 3)));
                                    }
                                }
                                adapter.notifyDataSetChanged();
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
     * 设置信息
     */
    private void setInfo(ProgressJson json) {
        //分享的内容
        sharePopupWindow.content = "参与有书共读【" + json.achieve.sum_date + "】天，读过【" + json.achieve.book_sum + "】本书";
        DisplayImageUtils.displayImage(json.avatar,
                iv_progress_avatar, 150, R.drawable.avatar);
        DisplayImageUtils.displayImage(json.nowReading.book_cover,
                iv_progress_bookimg, 0, R.drawable.zanwufengmian);
        tv_progress_day.setText("参与有书共读 " + json.achieve.sum_date + " 天");
        tv_progress_duguo.setText(json.achieve.book_sum);
        tv_progress_qiandao.setText(json.achieve.qd_sum);
        tv_progress_zan.setText(json.TotalDigg);
        tv_progress_suibi.setText(json.achieve.note_sum);
        tv_progress_bookname.setText(json.nowReading.book_title);
        tv_progress_name.setText(json.nowReading.author);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd");// HH:mm:ss
            Date date1 = simpleDateFormat
                    .parse(json.nowReading.start_time);
            Date date2 = simpleDateFormat
                    .parse(json.nowReading.end_time);
            SimpleDateFormat format = new SimpleDateFormat("MM月dd日");

            String create = format.format(date1);
            String end = format.format(date2);
            tv_progress_time.setText("共读时间:\t" + create + "\t—\t" + end);
        } catch (ParseException e) {
            e.printStackTrace();
            tv_progress_time.setText("");
        }
    }

    public void refresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshListView.onRefreshComplete();
            }
        }, 500);
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
    };

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ProgressFragment");
    }

    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("ProgressFragment");
    }

}
