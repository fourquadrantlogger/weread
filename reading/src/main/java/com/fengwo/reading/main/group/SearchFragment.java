package com.fengwo.reading.main.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectSharePopupWindow;
import com.fengwo.reading.main.discover.CarouselLayout;
import com.fengwo.reading.main.discover.hottopics.HotListBean;
import com.fengwo.reading.main.discover.hottopics.TopicsActivity;
import com.fengwo.reading.main.my.UserReadJson;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.umeng.UMShare;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.EditTextUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.view.MyListView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 有书圈 - 搜索
 *
 * @author Luo Sheng
 * @date 2016-3-28
 */
public class SearchFragment extends Fragment implements OnClickListener {

    public CustomProgressDialog progressDialog;
    private PullToRefreshScrollView sv_search;
    private ScrollView scrollView;

    private TextView tv_search_detele, tv_search_huati_all, tv_search_jingxuan;
    private ImageView iv_title_left, iv_title_right, iv_title_right2;
    private MyListView lv_search_jilu, lv_search_huati, lv_search_shuping, lv_search_jingxuan;
    private LinearLayout ll_search_all, ll_search_remen, ll_search_jilu, ll_search_huati, ll_search_jingxuan, ll_search_shuping;
    private EditText et_title_edittext;
    private View view_title_view;

    private MyListViewAdapter mListViewAdapter;
    private MyListViewAdapter mHuaTiAdapter;
    private GroupAdapter mJingXuanAdapter;
    private GroupAdapter mShuPingAdapter;

    private List<HotListBean> sousuoList; //历史记录
    public List<HotListBean> HTList;
    public List<GroupBean> JXList;
    public List<GroupBean> ALLList;

    public String type = ""; //网络请求的类型 all huati shuping
    private String name = "";//搜索名称(避免中途删除字符)
    private int page;
    private int fx_type;
    private boolean is_loading;

    // 分享的信息
    private String title = "";
    private String content = "";
    private String imageUrl = "";
    private String h5Url = "";

    private View saveView = null;
    public boolean needSaveView = false;

    public SearchFragment() {
    }

    public static SearchFragment fragment = new SearchFragment();

    public static SearchFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        progressDialog = CustomProgressDialog.createDialog(getActivity());

        findViewById(view);
        setTitle();
        is_loading = false;
        page = 0;

        sousuoList = new ArrayList<>();
        mListViewAdapter = new MyListViewAdapter(getActivity(), sousuoList, 1);
        lv_search_jilu.setAdapter(mListViewAdapter);
        HTList = new ArrayList<>();
        mHuaTiAdapter = new MyListViewAdapter(getActivity(), HTList, 2);
        lv_search_huati.setAdapter(mHuaTiAdapter);
        JXList = new ArrayList<>();
        mJingXuanAdapter = new GroupAdapter(fragment, JXList, 5);
        lv_search_jingxuan.setAdapter(mJingXuanAdapter);
        ALLList = new ArrayList<>();
        mShuPingAdapter = new GroupAdapter(fragment, ALLList, 6);
        lv_search_shuping.setAdapter(mShuPingAdapter);

        // 上拉、下拉设定
        sv_search.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        scrollView = sv_search.getRefreshableView();
        // 上拉监听函数
        sv_search
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        if (is_loading) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sv_search.onRefreshComplete();
                                }
                            }, 300);
                            return;
                        }
                        // 执行刷新函数
                        switch (type) {
                            case "":
                                sv_search.onRefreshComplete();
                                break;
                            case "all":
                                is_loading = true;
                                page++;
                                getSuiBiData();
                                break;
                            case "huati":
                                is_loading = true;
                                page++;
                                getHuaTiAllData();
                                break;
                            case "shuping":
                                is_loading = true;
                                page++;
                                getSuiBiAllData();
                                break;
                        }
                    }
                });

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                is_loading = true;
                getReMenData();
            }
        }, 300);

        //SP历史记录
        refreshJL();

        //搜索记录
        lv_search_jilu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 1) {
                    type = "all";
                    page = 0;
                    et_title_edittext.setText(sousuoList.get(i - 1).topic_title);
                    name = sousuoList.get(i - 1).topic_title;
                    et_title_edittext.setSelection(name.length());
                    EditTextUtils.hideSoftInput(et_title_edittext, getActivity());
                    //搜索
                    getJieGuoData();
                    ll_search_jilu.setVisibility(View.GONE);
                }
            }
        });

        //相关话题
        lv_search_huati.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 跳转 话题详情
                Intent intent = new Intent(getActivity(), TopicsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", HTList.get(i).topic_title);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
            }
        });

        return view;
    }

    private View topicView;

    private void findViewById(View view) {
        sv_search = (PullToRefreshScrollView) view
                .findViewById(R.id.sv_search);
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        iv_title_right = (ImageView) view.findViewById(R.id.iv_title_right);
        iv_title_right2 = (ImageView) view.findViewById(R.id.iv_title_right2);
        view_title_view = (View) view.findViewById(R.id.view_title_view);
        et_title_edittext = (EditText) view.findViewById(R.id.et_title_edittext);
        ll_search_all = (LinearLayout) view.findViewById(R.id.ll_search_all);
        ll_search_jilu = (LinearLayout) view.findViewById(R.id.ll_search_jilu);
        lv_search_jilu = (MyListView) view.findViewById(R.id.lv_search_jilu);
        tv_search_detele = (TextView) view.findViewById(R.id.tv_search_detele);
        ll_search_huati = (LinearLayout) view.findViewById(R.id.ll_search_huati);
        lv_search_huati = (MyListView) view.findViewById(R.id.lv_search_huati);
        tv_search_huati_all = (TextView) view.findViewById(R.id.tv_search_huati_all);
        ll_search_jingxuan = (LinearLayout) view.findViewById(R.id.ll_search_jingxuan);
        lv_search_jingxuan = (MyListView) view.findViewById(R.id.lv_search_jingxuan);
        tv_search_jingxuan = (TextView) view.findViewById(R.id.tv_search_jingxuan);
        ll_search_shuping = (LinearLayout) view.findViewById(R.id.ll_search_shuping);
        lv_search_shuping = (MyListView) view.findViewById(R.id.lv_search_shuping);

        ll_search_jilu.setVisibility(View.VISIBLE);
        ll_search_huati.setVisibility(View.GONE);
        ll_search_jingxuan.setVisibility(View.GONE);
        ll_search_shuping.setVisibility(View.GONE);

        iv_title_left.setOnClickListener(this);
        iv_title_right.setOnClickListener(this);
        iv_title_right2.setOnClickListener(this);
        tv_search_detele.setOnClickListener(this);
        tv_search_huati_all.setOnClickListener(this);
        tv_search_jingxuan.setOnClickListener(this);

        // 输入框的监听
        et_title_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(et_title_edittext.getText().toString()
                        .trim())) {
                    iv_title_right2.setVisibility(View.VISIBLE);
                } else {
                    iv_title_right2.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setTitle() {
        iv_title_left.setVisibility(View.VISIBLE);
        iv_title_right.setVisibility(View.VISIBLE);
        iv_title_right2.setVisibility(View.INVISIBLE);
        view_title_view.setVisibility(View.VISIBLE);
        et_title_edittext.setVisibility(View.VISIBLE);
        iv_title_right.setImageResource(R.drawable.group_sousuo_white);
        iv_title_right2.setImageResource(R.drawable.cancel_qian);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                EditTextUtils.hideSoftInput(et_title_edittext, getActivity());
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_to_right);
                break;
            case R.id.iv_title_right:
                //搜索
                if (TextUtils.isEmpty(et_title_edittext.getText().toString()
                        .trim())) {
                    Toast.makeText(getActivity(), "内容不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                name = et_title_edittext.getText().toString().trim();
                type = "all";
                page = 0;
                EditTextUtils.hideSoftInput(et_title_edittext, getActivity());

                getJieGuoData();

                //添加搜索记录
                List<String> list1 = SPUtils.getSouSuo(getActivity());
                list1.add(0, et_title_edittext.getText().toString()
                        .trim());
                SPUtils.setSouSuo(getActivity(), list1, false);

                ll_search_jilu.setVisibility(View.GONE);
                tv_search_huati_all.setVisibility(View.VISIBLE);
                tv_search_jingxuan.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_title_right2:
                //删除已输入
                et_title_edittext.setText("");
                break;
            case R.id.tv_search_detele:
                //清楚历史记录
                SPUtils.setSouSuo(getActivity(), SPUtils.getSouSuo(getActivity()), true);
                tv_search_detele.setVisibility(View.GONE);
                sousuoList.clear();
                mListViewAdapter.notifyDataSetChanged();
                break;
            case R.id.tv_search_huati_all:
                //更多相关话题
                type = "huati";
                page = 0;
                ll_search_jilu.setVisibility(View.GONE);
                ll_search_huati.setVisibility(View.VISIBLE);
                ll_search_jingxuan.setVisibility(View.GONE);
                ll_search_shuping.setVisibility(View.GONE);
                tv_search_huati_all.setVisibility(View.GONE);

                getHuaTiAllData();
                break;
            case R.id.tv_search_jingxuan:
                //更多精选书评
                type = "shuping";
                page = 0;
                ll_search_jilu.setVisibility(View.GONE);
                ll_search_huati.setVisibility(View.GONE);
                ll_search_jingxuan.setVisibility(View.VISIBLE);
                ll_search_shuping.setVisibility(View.GONE);

                tv_search_jingxuan.setVisibility(View.GONE);

                getSuiBiAllData();
                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 热门搜索
     */
    private void getReMenData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.get_pbook, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                        String jsonString = responseInfo.result;
                        try {
                            UserReadJson json = new Gson().fromJson(jsonString,
                                    UserReadJson.class);
                            if ("1".equals(json.code)) {

                                CarouselLayout.is_show = false;
                                topicView = new SearchTopLayout()
                                        .createHeaderLayout(
                                                fragment.getActivity(),
                                                json.data);
                                lv_search_jilu.addHeaderView(topicView);

                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
                                    Toast.makeText(context, json.msg,
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            MLog.v("SearchFrgment_remen", "" + e);
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
     * 网络请求 - 搜索结果
     */
    private void getJieGuoData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("search", name);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.search_searchResult, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                        String jsonString = responseInfo.result;
                        try {
                            SearchDetailsJson json = new Gson().fromJson(jsonString,
                                    SearchDetailsJson.class);
                            if ("1".equals(json.code)) {
                                HTList.clear();
                                JXList.clear();
                                ALLList.clear();

                                if (json.allNote == null || json.allNote.size() == 0) {
                                    ll_search_shuping.setVisibility(View.GONE);
                                } else {
                                    ll_search_shuping.setVisibility(View.VISIBLE);
                                    ALLList.addAll(json.allNote);
                                }
                                mShuPingAdapter.notifyDataSetChanged();

                                if (json.note == null || json.note.size() == 0) {
                                    ll_search_jingxuan.setVisibility(View.GONE);
                                } else {
                                    ll_search_jingxuan.setVisibility(View.VISIBLE);
                                    JXList.addAll(json.note);
                                }
                                mJingXuanAdapter.notifyDataSetChanged();

                                if (json.topic == null) {
                                    ll_search_huati.setVisibility(View.GONE);
                                } else {
                                    ll_search_huati.setVisibility(View.VISIBLE);
                                    HTList.add(json.topic);
                                }
                                mHuaTiAdapter.notifyDataSetChanged();
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
     * 网络请求 - 所有随笔
     */
    private void getSuiBiData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("search", name);
        map.put("page", page + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.search_allNote, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                        String jsonString = responseInfo.result;
                        try {
                            SearchJson json = new Gson().fromJson(jsonString,
                                    SearchJson.class);
                            if ("1".equals(json.code)) {
                                if (json.allNote == null
                                        || json.allNote.size() == 0) {
                                    // 没有数据
                                    page--;
                                } else {
                                    ALLList.addAll(json.allNote);
                                }
                                mShuPingAdapter.notifyDataSetChanged();
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
     * 网络请求 - 所有话题
     */
    private void getHuaTiAllData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("search", name);
        map.put("page", page + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.search_getMoreTopic, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                        String jsonString = responseInfo.result;
                        try {
                            SearchJson json = new Gson().fromJson(jsonString,
                                    SearchJson.class);
                            if ("1".equals(json.code)) {
                                if (json.topic == null
                                        || json.topic.size() == 0) {
                                    // 没有数据
                                    page--;
                                } else {
                                    HTList.addAll(json.topic);
                                }
                                mHuaTiAdapter.notifyDataSetChanged();
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
     * 网络请求 - 所有精选随笔
     */
    private void getSuiBiAllData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("search", name);
        map.put("page", page + "");

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.search_getMoreNote, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        is_loading = false;
                        sv_search.onRefreshComplete();
                        String jsonString = responseInfo.result;
                        try {
                            SearchJson json = new Gson().fromJson(jsonString,
                                    SearchJson.class);
                            if ("1".equals(json.code)) {
                                if (json.note == null
                                        || json.note.size() == 0) {
                                    // 没有数据
                                    page--;
                                } else {
                                    JXList.addAll(json.note);
                                }
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
                                Toast.makeText(context,
                                        context.getString(R.string.json_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 历史记录和话题的ListView适配器
     */
    private class MyListViewAdapter extends BaseAdapter {

        private Context context;
        private List<HotListBean> list;
        private int source; //来源 1:搜索历史记录 2:话题

        public MyListViewAdapter(Context context, List<HotListBean> list, int source) {
            super();
            this.context = context;
            this.list = list;
            this.source = source;
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
                        R.layout.item_search_sousuo, parent, false);
                holder.ll_search_sousuo = (LinearLayout) convertView
                        .findViewById(R.id.ll_search_sousuo);
                holder.rl_search_huati = (RelativeLayout) convertView
                        .findViewById(R.id.rl_search_huati);
                holder.tv_search_sousuo_name = (TextView) convertView
                        .findViewById(R.id.tv_search_sousuo_name);
                holder.tv_search_huati_img = (ImageView) convertView
                        .findViewById(R.id.tv_search_huati_img);
                holder.tv_search_huati_title = (TextView) convertView
                        .findViewById(R.id.tv_search_huati_title);
                holder.tv_search_huati_content = (TextView) convertView
                        .findViewById(R.id.tv_search_huati_content);
                holder.tv_search_huati_num = (TextView) convertView
                        .findViewById(R.id.tv_search_huati_num);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            switch (source) {
                case 1:
                    holder.ll_search_sousuo.setVisibility(View.VISIBLE);
                    holder.rl_search_huati.setVisibility(View.GONE);

                    holder.tv_search_sousuo_name.setText(list.get(position).topic_title);
                    break;
                case 2:
                    holder.ll_search_sousuo.setVisibility(View.GONE);
                    holder.rl_search_huati.setVisibility(View.VISIBLE);

                    holder.tv_search_huati_title.setText(list.get(position).topic_title);
                    holder.tv_search_huati_content.setText(list.get(position).topic_content);
                    holder.tv_search_huati_num.setText(list.get(position).join_nums + "人参加");
                    DisplayImageUtils.displayImage(list.get(position).img,
                            holder.tv_search_huati_img, 0, R.drawable.zanwufengmian);
                    break;
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView tv_search_sousuo_name, tv_search_huati_title, tv_search_huati_content, tv_search_huati_num;
            private ImageView tv_search_huati_img;
            private LinearLayout ll_search_sousuo;
            private RelativeLayout rl_search_huati;
        }
    }

    /**
     * 跳转 他人主页
     */
    public void goOther(int position, int type) {
        FragmentTransaction transaction = fragment.getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                OtherUserFragment.getInstance());
        transaction.commit();
        switch (type) {
            case 5:
                OtherUserFragment.getInstance().ta_user_id = JXList.get(position).user_data.user_id;
                break;
            case 6:
                OtherUserFragment.getInstance().ta_user_id = ALLList.get(position).user_data.user_id;
                break;
        }
        OtherUserFragment.getInstance().source = 2;
        OtherUserFragment.getInstance().needSaveView = false;
    }

    /**
     * 跳转 讨论详情
     */
    public void goDetails(int position, int type) {
        FragmentTransaction transaction = fragment.getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                GroupDetailsFragment.getInstance());
        transaction.commit();
        switch (type) {
            case 5:
                GroupDetailsFragment.getInstance().id = JXList.get(position).id;
                GroupDetailsFragment.getInstance().source = 7;
                break;
            case 6:
                GroupDetailsFragment.getInstance().id = ALLList.get(position).id;
                GroupDetailsFragment.getInstance().source = 8;
                break;
        }
        GroupDetailsFragment.getInstance().groupPosition = position;
        GroupDetailsFragment.getInstance().needSaveView = false;
    }

    /**
     * 分享的点击
     */
    private int mPosition;

    public void fenxiang(int position, int type) {
        mPosition = position;
        fx_type = type;
        FragmentActivity activity = fragment.getActivity();
        sharePopupWindow = new SelectSharePopupWindow(activity, itemsOnClick);
        sharePopupWindow.showAtLocation(activity.findViewById(R.id.ll_activity_next),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        switch (type) {
            case 5:
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
                break;
            case 6:
                // 分享的内容
                if (ALLList.get(position).img_str != null && ALLList.get(position).img_str.length != 0) {
                    this.imageUrl = ALLList.get(position).img_str[0];
                } else if (ALLList.get(position).user_data.avatar != null) {
                    this.imageUrl = ALLList.get(position).user_data.avatar;
                } else {
                    this.imageUrl = "";
                }
                this.h5Url = GlobalConstant.SERVERURL
                        .equals("http://api.fengwo.com/m/") ? "http://api.fengwo.com/"
                        : "http://gongdu.youshu.cc/" + "share/note?note_id="
                        + ALLList.get(position).id;
                break;
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
            String id = "";
            switch (v.getId()) {
                case R.id.ll_popupwindow_wx:
                    num = 1;
                    switch (fx_type) {
                        case 5:
                            title = JXList.get(mPosition).title;
                            content = JXList.get(mPosition).content;
                            id = JXList.get(mPosition).id;
                            break;
                        case 6:
                            title = ALLList.get(mPosition).title;
                            content = ALLList.get(mPosition).content;
                            id = ALLList.get(mPosition).id;
                            break;
                    }
                    break;
                case R.id.ll_popupwindow_pyq:
                    num = 2;
                    switch (fx_type) {
                        case 5:
                            if (!TextUtils.isEmpty(JXList.get(mPosition).title)) {
                                title = JXList.get(mPosition).title;
                                content = JXList.get(mPosition).title;
                            } else {
                                title = JXList.get(mPosition).content;
                                content = JXList.get(mPosition).content;
                            }
                            id = JXList.get(mPosition).id;
                            break;
                        case 6:
                            if (!TextUtils.isEmpty(ALLList.get(mPosition).title)) {
                                title = ALLList.get(mPosition).title;
                                content = ALLList.get(mPosition).title;
                            } else {
                                title = ALLList.get(mPosition).content;
                                content = ALLList.get(mPosition).content;
                            }
                            id = ALLList.get(mPosition).id;
                            break;
                    }
                    break;
                case R.id.ll_popupwindow_qq:
                    num = 3;
                    switch (fx_type) {
                        case 5:
                            title = JXList.get(mPosition).title;
                            content = JXList.get(mPosition).content;
                            id = JXList.get(mPosition).id;
                            break;
                        case 6:
                            title = ALLList.get(mPosition).title;
                            content = ALLList.get(mPosition).content;
                            id = ALLList.get(mPosition).id;
                            break;
                    }
                    break;
                case R.id.ll_popupwindow_wb:
                    num = 4;
                    switch (fx_type) {
                        case 5:
                            if (TextUtils.isEmpty(JXList.get(mPosition).title)) {
                                content = "推荐+" + JXList.get(mPosition).user_data.name + "的共读随笔,来自@有书共读" + h5Url;
                            } else {
                                content = "推荐+" + JXList.get(mPosition).user_data.name + "的共读随笔《" + JXList.get(mPosition).title + "》,来自@有书共读" + h5Url;
                            }
                            id = JXList.get(mPosition).id;
                            break;
                        case 6:
                            if (TextUtils.isEmpty(ALLList.get(mPosition).title)) {
                                content = "推荐+" + ALLList.get(mPosition).user_data.name + "的共读随笔,来自@有书共读" + h5Url;
                            } else {
                                content = "推荐+" + ALLList.get(mPosition).user_data.name + "的共读随笔《" + ALLList.get(mPosition).title + "》,来自@有书共读" + h5Url;
                            }
                            id = ALLList.get(mPosition).id;
                            break;
                    }
                    break;
                default:
                    break;
            }
            UMShare.setUMeng(activity, num, title, content, imageUrl, h5Url, id, "note");
            if ("1".equals(UMShare.getLevel())) {
                startActivity(new Intent(getActivity(), UpgradeActivity.class));
            }
            sharePopupWindow.dismiss();
        }
    };

    /**
     * 点赞的点击
     */
    public void dianzan(final int position, final int type) {
        String act = "";
        Map<String, String> map = new HashMap<>();
        switch (type) {
            case 5:
                // 已经点赞过
                if ("1".equals(JXList.get(position).is_digg)) {
                    act = "c";
                } else {
                    act = "";
                }
                map.put("id", JXList.get(position).id);
                break;
            case 6:
                // 已经点赞过
                if ("1".equals(ALLList.get(position).is_digg)) {
                    act = "c";
                } else {
                    act = "";
                }
                map.put("id", ALLList.get(position).id);
                break;
        }
        map.put("user_id", GlobalParams.uid);
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
                                refresh(position, "dianzan" + position, type);
                            } else {

                            }
                        } catch (Exception e) {

                        }
                    }
                }, true, null);
    }

    /**
     * 点赞后的刷新
     */
    public void refresh(int position, String tag, int type) {
        if (tag.startsWith("dianzan")) {
            switch (type) {
                case 5:
                    TextView textView = (TextView) lv_search_jingxuan
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
                    break;
                case 6:
                    TextView textView1 = (TextView) lv_search_shuping
                            .findViewWithTag("dianzan_tv" + position);
                    if (textView1 != null) {
                        if ("1".equals(ALLList.get(position).is_digg)) {
                            // 已经赞过了
                            ALLList.get(position).is_digg = "0";
                            Drawable drawable = getResources().getDrawable(
                                    R.drawable.comment_zan);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                    drawable.getMinimumHeight());
                            textView1
                                    .setCompoundDrawables(drawable, null, null, null);
                            textView1.setTextColor(getActivity().getResources()
                                    .getColor(R.color.text_98));
                            try {
                                int count = Integer
                                        .valueOf(ALLList.get(position).digg_count);
                                textView1.setText((count - 1) + "");
                                ALLList.get(position).digg_count = (count - 1) + "";
                            } catch (Exception e) {
                            }
                        } else {
                            // 设置为赞过
                            ALLList.get(position).is_digg = "1";
                            Drawable drawable = getResources().getDrawable(
                                    R.drawable.comment_zan_hou);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                                    drawable.getMinimumHeight());
                            textView1
                                    .setCompoundDrawables(drawable, null, null, null);
                            textView1.setTextColor(getActivity().getResources()
                                    .getColor(R.color.green));
                            try {
                                int count = Integer
                                        .valueOf(ALLList.get(position).digg_count);
                                textView1.setText((count + 1) + "");
                                ALLList.get(position).digg_count = (count + 1) + "";
                            } catch (Exception e) {
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 搜索历史记录
     */
    public void refreshJL() {
        List<String> list = SPUtils.getSouSuo(getActivity());
        if (list == null || list.size() == 0) {
//            ll_search_jilu.setVisibility(View.INVISIBLE);
            ll_search_jilu.setVisibility(View.VISIBLE);
            tv_search_detele.setVisibility(View.GONE);
        } else {
            ll_search_jilu.setVisibility(View.VISIBLE);
            tv_search_detele.setVisibility(View.VISIBLE);
            HotListBean bean0 = new HotListBean();
            bean0.topic_title = "历史记录";
            sousuoList.add(bean0);
            for (int i = 0; i < list.size(); i++) {
                HotListBean bean = new HotListBean();
                bean.topic_title = list.get(i);
                sousuoList.add(bean);
            }
        }
        mListViewAdapter.notifyDataSetChanged();
    }

    /**
     * 点图书搜索
     */
    public void refresh(String id, String title) {
        EditTextUtils.hideSoftInput(et_title_edittext, getActivity());
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.in_from_left,
                R.anim.out_to_right);
        GroupFragment.getInstance().refresh(id, title);
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
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("SearchFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SearchFragment");
    }
}
