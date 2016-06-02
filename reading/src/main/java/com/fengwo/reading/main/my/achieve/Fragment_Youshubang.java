package com.fengwo.reading.main.my.achieve;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.main.my.RankBean;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.view.MyListView;
import com.fengwo.reading.view.Viewsimpletitle;
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
 * @author lipeng - 有书榜
 */
public class Fragment_Youshubang extends Fragment implements OnClickListener {
    //region 顶部
    private Viewsimpletitle view_title;

    //region 上部分信息
    private View layout_headerview_info;
    private MyListView mylistview_jingyanzhi;

    class Dayexp {
        public List<Adapter_jingyanzhi.Json_jingyanzhi> data;
        public String code;
        public String msg;
    }

    private List<Adapter_jingyanzhi.Json_jingyanzhi> list_jingyanzhi;
    Map<String, String> jingyanzhi_kv = new HashMap<>();

    private void list_jingyanzhi_replacename() {
        for (int i = 0; i < list_jingyanzhi.size(); i++) {
            switch (list_jingyanzhi.get(i).type) {
                case "save_info":
                    list_jingyanzhi.get(i).type = "完善个人信息";
                    break;
                case "share_pack":
                    list_jingyanzhi.get(i).type = "分享拆书包";
                    break;
                case "share_note":
                    list_jingyanzhi.get(i).type = "分享随笔";
                    break;
                case "week_book":
                    list_jingyanzhi.get(i).type = "每周一本书";
                    break;
                case "continu_check":
                    list_jingyanzhi.get(i).type = "连续签到";
                    break;
                case "note_add":
                    list_jingyanzhi.get(i).type = "发布随笔";
                    break;
                case "comm_note":
                    list_jingyanzhi.get(i).type = "评论随笔";
                    break;
                case "fav_note":
                    list_jingyanzhi.get(i).type = "收藏随笔";
                    break;
            }
        }
    }

    private List<Adapter_jingyanzhi.Json_jingyanzhi> list_jingyanzhi_3() {
        if (list_jingyanzhi.size() >= 3) {
            return list_jingyanzhi.subList(0, 3);
        } else {
            return list_jingyanzhi;
        }
    }

    private Adapter_jingyanzhi adapter_jingyanzhi;
    private TextView textView_youshubang_more, textView_youshubang_null;
    private RelativeLayout rl_youshubang_more;

    //region switchrank
    private LinearLayout switchrank;
    private LinearLayout linearLayout_switchrank_left, linearLayout_switchrank_right;
    private TextView switchrank_left_textview, switchrank_right_textview;
    private LinearLayout switchrank_line;

    private LinearLayout ll_achieve_title2_rank1, ll_achieve_title2_rank2;
    private TextView tv_achieve_title2_rank1, tv_achieve_title2_rank2;
    private LinearLayout ll_achieve_title2_line;

    private ImageView iv_achieve_no_qiandao;

    //region 下部分信息 listview
    private PullToRefreshListView listView;
    private List<RankBean> list1, list2;
    private Adapter_Youshubang adapter1, adapter2;

    private int nowPage = 1;//今日排行的page
    private int sumPage = 1;//总排行的page
    private boolean is_loading;

    private boolean isLeftOrRight;
    private boolean isFirst;

    private View saveView = null;
    public boolean needSaveView = false;

    private static Fragment_Youshubang fragment = new Fragment_Youshubang();

    public static Fragment_Youshubang getInstance() {
        return fragment;
    }

    CommonHandler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        //region 主view
        View view = inflater.inflate(R.layout.fragment_youshubang, container, false);
        findViewById(view);

        view_title.getTextView_title().setText("有书榜");
        view_title.getImageView_back().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        handler = new CommonHandler(getActivity(), null);

        //region headerview_info
        layout_headerview_info = LayoutInflater.from(getActivity()).inflate(R.layout.layout_youshubang_headerview_info, null);
        findViewById_headerview(layout_headerview_info);
        listView.getRefreshableView().addHeaderView(layout_headerview_info);
        list_jingyanzhi = new ArrayList<>();

        adapter_jingyanzhi = new Adapter_jingyanzhi(fragment, list_jingyanzhi_3());
        mylistview_jingyanzhi.setAdapter(adapter_jingyanzhi);

        //region switchrank
        View switchrank_new = LayoutInflater.from(getActivity()).inflate(R.layout.layout_switchrank, null);
        listView.getRefreshableView().addHeaderView(switchrank_new);
        findViewById1(switchrank_new);
        //endregion

        onClickListener();

        //region listview
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        adapter1 = new Adapter_Youshubang(fragment, list1);
        adapter2 = new Adapter_Youshubang(fragment, list2);
        listView.setAdapter(adapter1);


        switchrank.setVisibility(View.GONE);
        isLeftOrRight = true;
        isFirst = true;
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (is_loading) {
                    return;
                }
                is_loading = true;

                if (isLeftOrRight) {
                    nowPage++;
                } else {
                    sumPage++;
                }
                getData_rank(isLeftOrRight);
            }
        });
        listView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < 1) {
                    switchrank.setVisibility(View.GONE);
                } else {
                    switchrank.setVisibility(View.VISIBLE);
                }
            }
        });
        //endregion

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData_rank(true);
            }
        }, 1000);

        return view;
    }

    //region findViewById
    private void findViewById_headerview(View view) {
        mylistview_jingyanzhi = (MyListView) view.findViewById(R.id.mylistview_jingyanzhi);
        rl_youshubang_more = (RelativeLayout) view.findViewById(R.id.rl_youshubang_more);
        textView_youshubang_more = (TextView) view.findViewById(R.id.textView_youshubang_more);
        textView_youshubang_null = (TextView) view.findViewById(R.id.textView_youshubang_null);
    }

    private void findViewById(View view) {
        view_title = (Viewsimpletitle) view.findViewById(R.id.Viewsimpletitle_youshubang);
        switchrank = (LinearLayout) view.findViewById(R.id.switchrank);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_achieve_show);

        linearLayout_switchrank_left = (LinearLayout) view.findViewById(R.id.linearLayout_switchrank_left);
        linearLayout_switchrank_right = (LinearLayout) view.findViewById(R.id.linearLayout_switchrank_right);
        switchrank_left_textview = (TextView) view.findViewById(R.id.switchrank_left_textview);
        switchrank_right_textview = (TextView) view.findViewById(R.id.switchrank_right_textview);
        switchrank_line = (LinearLayout) view.findViewById(R.id.switchrank_line);
    }

    private void findViewById1(View switchrank_new) {
        ll_achieve_title2_rank1 = (LinearLayout) switchrank_new.findViewById(R.id.linearLayout_switchrank_left);
        ll_achieve_title2_rank2 = (LinearLayout) switchrank_new.findViewById(R.id.linearLayout_switchrank_right);
        tv_achieve_title2_rank1 = (TextView) switchrank_new.findViewById(R.id.switchrank_left_textview);
        tv_achieve_title2_rank2 = (TextView) switchrank_new.findViewById(R.id.switchrank_right_textview);
        ll_achieve_title2_line = (LinearLayout) switchrank_new.findViewById(R.id.switchrank_line);

        iv_achieve_no_qiandao = (ImageView) switchrank_new.findViewById(R.id.iv_achieve_no_qiandao);
    }

    //endregion
    //region onClick
    private void onClickListener() {
        rl_youshubang_more.setOnClickListener(this);
        linearLayout_switchrank_left.setOnClickListener(this);
        linearLayout_switchrank_right.setOnClickListener(this);
        switchrank_left_textview.setOnClickListener(this);
        switchrank_right_textview.setOnClickListener(this);
        ll_achieve_title2_rank1.setOnClickListener(this);
        ll_achieve_title2_rank2.setOnClickListener(this);
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
                getActivity().overridePendingTransition(
                        R.anim.in_from_left, R.anim.out_to_right);
                break;
            case R.id.ll_achieve_title2_rank1:
            case R.id.linearLayout_switchrank_left:
            case R.id.switchrank_left_textview:
                if (isLeftOrRight) {
                    return;
                }
                isLeftOrRight = true;
//                System.out.println("------987: left");
                switchrank_left_textview.setTextColor(context.getResources().getColor(R.color.green));
                tv_achieve_title2_rank1.setTextColor(context.getResources().getColor(R.color.green));

                switchrank_right_textview.setTextColor(context.getResources().getColor(R.color.text_64));
                tv_achieve_title2_rank2.setTextColor(context.getResources().getColor(R.color.text_64));

                switchrank_line.setGravity(Gravity.LEFT);
                ll_achieve_title2_line.setGravity(Gravity.LEFT);

                listView.setAdapter(adapter1);

                break;
            case R.id.ll_achieve_title2_rank2:
            case R.id.linearLayout_switchrank_right:
            case R.id.switchrank_right_textview:
                if (!isLeftOrRight) {
                    return;
                }
                isLeftOrRight = false;
                switchrank_left_textview.setTextColor(context.getResources()
                        .getColor(R.color.text_64));
                tv_achieve_title2_rank1.setTextColor(context.getResources()
                        .getColor(R.color.text_64));

                switchrank_right_textview.setTextColor(context.getResources().getColor(R.color.green));
                tv_achieve_title2_rank2.setTextColor(context.getResources().getColor(R.color.green));

                switchrank_line.setGravity(Gravity.RIGHT);
                ll_achieve_title2_line.setGravity(Gravity.RIGHT);

                listView.setAdapter(adapter2);
                if (isFirst) {
                    getData_rank(false);
                    isFirst = false;
                }
                break;
            case R.id.rl_youshubang_more:
                if (textView_youshubang_more.getTag() == null) {
                    adapter_jingyanzhi = new Adapter_jingyanzhi(fragment, list_jingyanzhi);
                    mylistview_jingyanzhi.setAdapter(adapter_jingyanzhi);
                    textView_youshubang_more.setTag(new Object());
                    textView_youshubang_more.setText("");
                    Drawable drawable = getResources().getDrawable(R.drawable.jiantou_shang);
                    drawable.setBounds(0, 0, 40, 40);
                    textView_youshubang_more.setCompoundDrawables(null, null, drawable, null);
                } else {
                    adapter_jingyanzhi = new Adapter_jingyanzhi(fragment, list_jingyanzhi_3());
                    mylistview_jingyanzhi.setAdapter(adapter_jingyanzhi);
                    textView_youshubang_more.setTag(null);
                    textView_youshubang_more.setText("查看更多详情");
                    Drawable drawable = getResources().getDrawable(R.drawable.jiantou_bottom);
                    drawable.setBounds(0, 0, 40, 40);
                    textView_youshubang_more.setCompoundDrawables(null, null, drawable, null);
                }
                break;
            default:
                break;
        }
    }

    //endregion
    //region 有书榜- 网络请求
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("is_all", "1");
        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.SERVERURL + "day/exp",
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
                        MLog.v("reading", jsonString);
                        try {
                            Dayexp json = new Gson().fromJson(jsonString, Dayexp.class);

                            if ("1".equals(json.code)) {
                                if (json.data != null) {
                                    list_jingyanzhi = json.data;
                                    list_jingyanzhi_replacename();
                                    adapter_jingyanzhi = new Adapter_jingyanzhi(fragment, list_jingyanzhi_3());
                                    mylistview_jingyanzhi.setAdapter(adapter_jingyanzhi);

                                    textView_youshubang_null.setVisibility(View.GONE);
                                    rl_youshubang_more.setVisibility(View.VISIBLE);
                                } else {
                                    textView_youshubang_null.setVisibility(View.VISIBLE);
                                    rl_youshubang_more.setVisibility(View.GONE);
                                }
                            } else {
                                Context context = getActivity();
                                if (context != null) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Context context = getActivity();
                            if (context != null) {
                                CustomToast.showToast(context, context.getString(R.string.json_error));
                            }
                        }
                    }
                }, true, null);
    }

    //成就排行
    //sum总排行
    private void getData_rank(final boolean nowrank) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("type", nowrank ? "now" : "sum");
        map.put("page", "" + (nowrank ? nowPage : sumPage));

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.youshubang_rank, new RequestCallBack<String>() {
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
                listView.onRefreshComplete();
                is_loading = false;
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
                listView.onRefreshComplete();
                is_loading = false;
                new Thread() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                }.start();
                String jsonString = responseInfo.result;
                MLog.v("reading", jsonString);
                try {
                    AchieveListJson json = new Gson().fromJson(jsonString, AchieveListJson.class);
                    iv_achieve_no_qiandao.setVisibility(View.GONE);
                    if ("1".equals(json.code)) {

                        if (json.data != null) {
                            if (json.data.list != null && json.data.list.size() != 0) {
                                //region 数据
                                if (nowrank) {
                                    if (nowPage == 1) {
                                        list1.clear();
                                        list1.addAll(json.data.list);

                                        adapter1.setRank(json.data.paihang);

                                    } else {
                                        if (json.data == null || json.data.list.size() == 0) {
                                            nowPage--;
                                            // 没有数据
                                        } else {
                                            list1.addAll(json.data.list);
                                        }
                                        adapter1.setRank(json.data.paihang);

                                    }
                                    adapter1.notifyDataSetChanged();
                                } else {
                                    if (sumPage == 1) {
                                        list2.clear();
                                        list2.addAll(json.data.list);


                                        adapter2.setRank(json.data.paihang);

                                    } else {
                                        if (json.data == null || json.data.list.size() == 0) {
                                            sumPage--;
                                            // 没有数据
                                        } else {
                                            list2.addAll(json.data.list);
                                        }
                                        adapter2.setRank(json.data.paihang);

                                    }
                                    adapter2.notifyDataSetChanged();
                                }

                                //endregion
                                //region catch
                            } else {
                                Context context = getActivity();
                                if (context != null) {
                                    if (!nowrank) {
                                        iv_achieve_no_qiandao.setVisibility(View.GONE);
                                    }
                                    CustomToast.showToast(context, "暂无数据");
                                }
                            }
                        } else {
                            Context context = getActivity();
                            if (context != null) {
                                if (!nowrank) {
                                    iv_achieve_no_qiandao.setVisibility(View.GONE);
                                }
                                CustomToast.showToast(context, "暂无数据");
                            }
                        }
                    } else {
                        Context context = getActivity();
                        if (context != null) {
                            MLog.v("reading", "我的成就:" + json.msg);
                            if (nowrank) {
                                if (json.code.equals("0")) {
                                    iv_achieve_no_qiandao.setImageResource(R.drawable.no_qiandao);
                                } else if (json.code.equals("2")) {
                                    iv_achieve_no_qiandao.setImageResource(R.drawable.huashuwan);
                                }
                                iv_achieve_no_qiandao.setVisibility(View.VISIBLE);
                                listView.getRefreshableView().setDividerHeight(0);
                            } else {
                                iv_achieve_no_qiandao.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Context context = getActivity();
                    if (context != null) {
                        CustomToast.showToast(context, context.getString(R.string.json_error));
                    }
                }
                //endregion
            }
        }, true, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment_Youshubang");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("Fragment_Youshubang");
    }


}
