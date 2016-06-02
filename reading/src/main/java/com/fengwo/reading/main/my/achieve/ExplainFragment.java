package com.fengwo.reading.main.my.achieve;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.EditTextActivity;
import com.fengwo.reading.activity.MainActivity;
import com.fengwo.reading.activity.UpgradeActivity;
import com.fengwo.reading.main.group.PublishFeelingsFragment;
import com.fengwo.reading.main.read.Fragment_Bookpack;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.view.MyListView;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.fengwo.reading.player.Playlist_Cache;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 我的成就 - 任务说明
 *
 * @author Luo Sheng
 * @date 2016-4-25
 */
public class ExplainFragment extends Fragment implements OnClickListener {

    private ImageView iv_title_left;
    private MyListView mlv_explain_show1, mlv_explain_show2;
    private TextView tv_title_mid, tv_explain_left, tv_explain_zhushi,
            tv_explain_num, tv_explain_right, tv_explain_ratio, tv_explain_green, tv_explain_tv2;
    private RelativeLayout rl_explain_ok, rl_explain_go, rl_explain_top2, rl_explain_top3;

    private ListViewAdapter adapter1;
    private ListViewAdapter adapter2;

    public boolean is_refresh = false; // 是否刷新
    public String level_is_up = ""; //是否升级

    public String source = "";
    //    share_pack 分享拆书包
    //    share_note 分享随笔
    //    week_book 每周一本书
    //    continu_check 签到2次 sign字段是 如何打卡签到
    //    note_add 发布随笔
    //    comm_note 评论随笔
    //    fav_note 收藏随笔
    private View saveView = null;
    public boolean needSaveView = false;

    public ExplainFragment() {
    }

    public static ExplainFragment fragment = new ExplainFragment();

    public static ExplainFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_explain, container,
                false);
        findViewById(view);
        setTitle();

        // 延时请求网络
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 300);

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        rl_explain_ok = (RelativeLayout) view.findViewById(R.id.rl_explain_ok);
        tv_explain_left = (TextView) view.findViewById(R.id.tv_explain_left);
        tv_explain_num = (TextView) view.findViewById(R.id.tv_explain_num);
        tv_explain_right = (TextView) view.findViewById(R.id.tv_explain_right);
        tv_explain_ratio = (TextView) view.findViewById(R.id.tv_explain_ratio);
        rl_explain_go = (RelativeLayout) view.findViewById(R.id.rl_explain_go);
        tv_explain_green = (TextView) view.findViewById(R.id.tv_explain_green);

        mlv_explain_show1 = (MyListView) view.findViewById(R.id.mlv_explain_show1);
        rl_explain_top2 = (RelativeLayout) view.findViewById(R.id.rl_explain_top2);
        tv_explain_tv2 = (TextView) view.findViewById(R.id.tv_explain_tv2);
        mlv_explain_show2 = (MyListView) view.findViewById(R.id.mlv_explain_show2);
        rl_explain_top3 = (RelativeLayout) view.findViewById(R.id.rl_explain_top3);

        tv_explain_zhushi = (TextView) view.findViewById(R.id.tv_explain_zhushi);

        rl_explain_ok.setVisibility(View.GONE);
        rl_explain_top2.setVisibility(View.GONE);
        rl_explain_top3.setVisibility(View.GONE);

        iv_title_left.setOnClickListener(this);
        rl_explain_go.setOnClickListener(this);
    }

    private void setTitle() {
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText("任务说明");
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
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.rl_explain_go:
                switch (source) {
                    case "week_book":
                    case "share_pack":
                    case "continu_check":
                        //每周一本,签到2次,分享拆书包
                        transaction.replace(R.id.ll_activity_next,
                                Fragment_Bookpack.getInstance());
                        Fragment_Bookpack.getInstance().needSaveView = false;
                        Fragment_Bookpack.getInstance().source = 3;

                        Fragment_Bookpack.getInstance().pb_id = Playlist_Cache.首页_list.get(0).pb_id;
                        Fragment_Bookpack.getInstance().id = Playlist_Cache.首页_list.get(0).id;
                        Fragment_Bookpack.getInstance().bookpackindex = 0;
                        transaction.commit();
                        break;
                    case "note_add":
                        //发布随笔
                        Intent intent = new Intent(getActivity(), EditTextActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("key", 1);
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        PublishFeelingsFragment.getInstance().source = 4;
                        PublishFeelingsFragment.getInstance().needSaveView = false;
                        break;
                    case "share_note":
                    case "comm_note":
                    case "fav_note":
                        //评论,分享,收藏随笔
                        getActivity().finish();
                        getActivity().overridePendingTransition(
                                R.anim.in_from_left, R.anim.out_to_right);
                        MainActivity.Activity.refresh();
                        break;
                }
                break;

            default:
                break;
        }
    }

    /**
     * 请求网络
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("type", source);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.user_rule, new RequestCallBack<String>() {

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
                            MLog.v("reading", "" + jsonString);
                            ExplainJson json = new Gson().fromJson(jsonString,
                                    ExplainJson.class);
                            if ("1".equals(json.code)) {
                                if (json.data == null) {
                                    // 没有数据

                                } else {
                                    setInfo(json);

                                    adapter1 = new ListViewAdapter(json.data.rule);
                                    mlv_explain_show1.setAdapter(adapter1);

                                    if (json.data.time != null && json.data.time.length != 0) {
                                        adapter2 = new ListViewAdapter(json.data.time);
                                        mlv_explain_show2.setAdapter(adapter2);
                                        adapter2.notifyDataSetChanged();
                                    }
                                }
                                adapter1.notifyDataSetChanged();
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
     * 信息设置
     */
    private void setInfo(ExplainJson json) {
        switch (source) {
            case "week_book":
                //每周一本
                UMengUtils.onCountListener(getActivity(), "GD_05_03_09");
                rl_explain_ok.setVisibility(View.VISIBLE);
                tv_explain_left.setText("当前任务:\t本周已签到");
                tv_explain_num.setText(json.data.cnum);
                tv_explain_right.setText("篇文章");
                tv_explain_ratio.setText(json.data.cnum + "/" + json.data.sum);
                tv_explain_green.setText("去看领读包");
                break;
            case "continu_check":
                //连续签到 - 签到2次
                UMengUtils.onCountListener(getActivity(), "GD_05_03_10");
                rl_explain_ok.setVisibility(View.VISIBLE);
                rl_explain_top2.setVisibility(View.VISIBLE);
                rl_explain_top3.setVisibility(View.VISIBLE);
                tv_explain_left.setText("当前任务:\t已签到");
                tv_explain_num.setText(json.data.cnum);
                tv_explain_right.setText("次");
                tv_explain_ratio.setText(json.data.cnum + "/" + json.data.sum);
                tv_explain_green.setText("去签到");
                tv_explain_tv2.setText("签到时间");
                tv_explain_zhushi.setText(json.data.tip);
                break;
            case "share_pack":
                //分享拆书包
                UMengUtils.onCountListener(getActivity(), "GD_05_03_11");
                rl_explain_ok.setVisibility(View.VISIBLE);
                tv_explain_left.setText("当前任务:\t已分享");
                tv_explain_num.setText(json.data.cnum);
                tv_explain_right.setText("个领读包");
                tv_explain_ratio.setText(json.data.cnum + "/" + json.data.sum);
                tv_explain_green.setText("分享领读包");
                break;
            case "note_add":
                //发布随笔
                UMengUtils.onCountListener(getActivity(), "GD_05_03_12");
                rl_explain_top2.setVisibility(View.VISIBLE);
                tv_explain_green.setText("去有书圈发表随笔");
                tv_explain_tv2.setText(json.data.tip);
                break;
            case "comm_note":
                //评论随笔
                UMengUtils.onCountListener(getActivity(), "GD_05_03_13");
                rl_explain_ok.setVisibility(View.VISIBLE);
                tv_explain_left.setText("当前任务:\t已评论");
                tv_explain_num.setText(json.data.cnum);
                tv_explain_right.setText("条随笔");
                tv_explain_ratio.setText(json.data.cnum + "/" + json.data.sum);
                tv_explain_green.setText("去有书圈评论随笔");
                break;
            case "share_note":
                //分享随笔
                rl_explain_ok.setVisibility(View.VISIBLE);
                tv_explain_left.setText("当前任务:\t已分享");
                tv_explain_num.setText(json.data.cnum);
                tv_explain_right.setText("条随笔");
                tv_explain_ratio.setText(json.data.cnum + "/" + json.data.sum);
                tv_explain_green.setText("去有书圈分享随笔");
                break;
            case "fav_note":
                //收藏随笔
                UMengUtils.onCountListener(getActivity(), "GD_05_03_14");
                rl_explain_ok.setVisibility(View.VISIBLE);
                tv_explain_left.setText("当前任务:\t已收藏");
                tv_explain_num.setText(json.data.cnum);
                tv_explain_right.setText("条随笔");
                tv_explain_ratio.setText(json.data.cnum + "/" + json.data.sum);
                tv_explain_green.setText("去有书圈收藏随笔");
                break;
        }
    }

    /**
     * ListView适配器
     */
    private class ListViewAdapter extends BaseAdapter {

        private String[] list;

        public ListViewAdapter(String[] list) {
            super();
            this.list = list;
        }

        @Override
        public int getCount() {
            return list != null ? list.length : 0;
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
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.item_explain, parent, false);
                holder.tv_explain_item_content = (TextView) convertView
                        .findViewById(R.id.tv_explain_item_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_explain_item_content.setText(list[position]);

            return convertView;
        }

        private class ViewHolder {
            private TextView tv_explain_item_content;
        }
    }

    /**
     * 完成任务后刷新
     */
    public void refresh(String level) {
        getData();
        MyAchieveFragment.getInstance().is_refresh = true;
        if (level != null && "1".equals(level)) {
            startActivity(new Intent(getActivity(), UpgradeActivity.class));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("ExplainFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ExplainFragment");
    }

}