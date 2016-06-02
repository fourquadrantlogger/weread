package com.fengwo.reading.main.group.qun;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.my.RankBean;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.UMengUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.view.RoundProgressBar;
import com.fengwo.reading.view.Viewsimpletitle;
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
 * Created by timeloveboy on 16/3/24.
 */

public class AllQunWeekRankFragment extends Fragment implements View.OnClickListener {
    public AllQunWeekRankFragment() {
        super();
    }
    public static AllQunWeekRankFragment fragment = new AllQunWeekRankFragment();

    public CommonHandler handler;

    private View saveView = null;
    public boolean needSaveView = false;

    private QunRankAdapter qunRankAdapter;
    private List<groupRankInfoJson.Group> rankBeanList;

    public static AllQunWeekRankFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        handler=new CommonHandler(getActivity(),null);
        View view = inflater.inflate(R.layout.fragment_allqun_weekrank, container, false);
        findViewById(view);
        onClickListener();
        //标题栏部分
        viewsimpletitle.getImageView_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        viewsimpletitle.getTextView_title().setText("群间周排名");
        viewsimpletitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //群姐排名 - 历史排名
                UMengUtils.onCountListener(getActivity(), "GD_03_01_01_02_01");
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);

                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, WebFragment.getInstance());
                transaction.commit();
                WebFragment.getInstance().source = 2;
                WebFragment.getInstance().needSaveView = false;
                WebFragment.getInstance().url =GlobalConstant.SERVERURL.substring(0,GlobalConstant.SERVERURL.length()-3)+ GlobalConstant.history;


            }
        });
        viewsimpletitle.getImageView_history().setVisibility(View.VISIBLE);


        //listview部分

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getData();
            }
        }, 500);

        return view;
    }

    //    布局控件
    Viewsimpletitle viewsimpletitle;

    //第一栏
    ImageView iv_allqun_book;
    TextView tv_qun_weekrank_book_title,tv_allqun_journal,tv_allqun_readtime;
    TextView textView_link_jifen;
    //第二栏
    TextView tv_allqun_groupname;
    Button bt_allqun_qun_score,bt_allqun_qun_rank;

    RoundProgressBar roundprogressbar_weekrank_allqun;

    //第三栏
    ListView lv_qun_allrank;

    private void findViewById(View view) {
        viewsimpletitle = (Viewsimpletitle) view.findViewById(R.id.viewsimpletitle_qun_detail);
        iv_allqun_book=(ImageView)view.findViewById(R.id.iv_allqun_book);
        tv_qun_weekrank_book_title=(TextView)view.findViewById(R.id.tv_qun_weekrank_book_title);
        tv_allqun_journal=(TextView)view.findViewById(R.id.tv_allqun_journal);
        tv_allqun_readtime=(TextView)view.findViewById(R.id.tv_allqun_readtime);
        textView_link_jifen=(TextView) view.findViewById(R.id.textView_link_jifen);

        tv_allqun_groupname=(TextView)view.findViewById(R.id.tv_allqun_groupname);
        bt_allqun_qun_score=(Button)view.findViewById(R.id.bt_allqun_qun_score);
        bt_allqun_qun_rank=(Button)view.findViewById(R.id.bt_allqun_qun_rank);
        roundprogressbar_weekrank_allqun=(RoundProgressBar)view.findViewById(R.id.roundprogressbar_weekrank_allqun);

        lv_qun_allrank=(ListView)view.findViewById(R.id.lv_qun_allrank);
    }
    private void onClickListener() {
        textView_link_jifen.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {

        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        switch (v.getId()) {
            case R.id.textView_link_jifen:
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, WebFragment.getInstance());
                transaction.commit();
                WebFragment.getInstance().source = 2;
                WebFragment.getInstance().needSaveView = false;
                WebFragment.getInstance().url =GlobalConstant.SERVERURL.substring(0,GlobalConstant.SERVERURL.length()-3)+ GlobalConstant.jifen;
                break;

        }
    }
    /**
     * 群间排名
     *
     */
    private void Test_getData(){
        rankBeanList.clear();
        RankBean rankBean=new RankBean();
        for(int i=0;i<10;i++){

        }
        lv_qun_allrank.setAdapter(qunRankAdapter);
    }
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("group_id",QunDetailFragment.getInstance().group_id);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.group_groupWeekInfo,
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

                        try {
                            groupRankInfoJson json = new Gson().fromJson(jsonString, groupRankInfoJson.class);

                            if ("1".equals(json.code)) {

                                //book
                                DisplayImageUtils.displayImage(json.book.book_cover, iv_allqun_book, 0, R.color.white);
                                tv_qun_weekrank_book_title.setText(json.book.book_title);

                                tv_allqun_journal.setText("第" + json.book.journal + "期");
                                tv_allqun_readtime.setText(json.book.start_time+"-"+json.book.end_time);


                                //


                                rankBeanList = new ArrayList<>();
                                rankBeanList.addAll(json.groupList);
                                qunRankAdapter= new QunRankAdapter(fragment, rankBeanList,false);
                                lv_qun_allrank.setAdapter(qunRankAdapter);

                                //groupInfo
                                tv_allqun_groupname.setText(json.groupInfo.group_name);
                                bt_allqun_qun_score.setText(json.groupInfo.score);
                                bt_allqun_qun_rank.setText(json.groupInfo.rank);
                                roundprogressbar_weekrank_allqun.setProgress(Integer.parseInt(json.groupInfo.rate.replace("%", "")));

                            }
                        } catch(Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                CustomToast.showToast(context,context.getString(R.string.json_error));
                            }
                        }

                    }
                }, true, null);
    }


    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("groupMemRankFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("groupMemRankFragment");
    }
}
