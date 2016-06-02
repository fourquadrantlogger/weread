package com.fengwo.reading.main.group.qun;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.main.my.RankBean;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.VersionUtils;
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
public class groupMemRankFragment extends Fragment implements View.OnClickListener {
    public groupMemRankFragment() {
        super();
    }
    public static groupMemRankFragment fragment = new groupMemRankFragment();

    private CustomProgressDialog progressDialog;

    private View saveView = null;
    public boolean needSaveView = false;

    private QunRankAdapter qunRankAdapter;
    private List<groupMemRankJson.GroupUser> groupUserList;

    public static groupMemRankFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (needSaveView && saveView != null) {
//            return saveView;
//        }
//        needSaveView = true;
        View view = inflater.inflate(R.layout.fragment_groupmemrank, container, false);
        findViewById(view);
        onClickListener();
        //标题栏部分
        viewsimpletitle.getImageView_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        viewsimpletitle.getTextView_title().setText("组内周排名");

        //第一栏


        //listview部分
        lv_qun_allrank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goOther(position);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 500);
        return view;
    }
    /**
     * 跳转 他人主页
     */
    public void goOther(int position) {
        FragmentTransaction transaction =fragment.getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                OtherUserFragment.getInstance());
        transaction.commit();
        OtherUserFragment.getInstance().source = 2;
        OtherUserFragment.getInstance().needSaveView = false;
        OtherUserFragment.getInstance().ta_user_id =groupUserList.get(position).user_id;
    }
    //    布局控件
    Viewsimpletitle viewsimpletitle;
    //第一栏
    ImageView iv_allqun_book;
    TextView tv_qun_weekrank_book_title,tv_allqun_journal,tv_allqun_readtime;
    TextView textView_link_jifen;
    //第二栏
    TextView tv_groupmemRank_userinfo_rank,tv_groupmemRank_userinfo_name,tv_groupmemRank_userinfo_score;
    ImageView iv_groupmemRank_userinfo_userface;

    //第三栏
    ListView lv_qun_allrank;

    private void findViewById(View view) {
        viewsimpletitle = (Viewsimpletitle) view.findViewById(R.id.viewsimpletitle_qun_detail);

        iv_allqun_book=(ImageView)view.findViewById(R.id.iv_allqun_book);
        tv_qun_weekrank_book_title=(TextView)view.findViewById(R.id.tv_qun_weekrank_book_title);
        tv_allqun_journal=(TextView)view.findViewById(R.id.tv_allqun_journal);
        tv_allqun_readtime=(TextView)view.findViewById(R.id.tv_allqun_readtime);
        textView_link_jifen=(TextView) view.findViewById(R.id.textView_link_jifen);

        tv_groupmemRank_userinfo_rank=(TextView)view.findViewById(R.id.tv_item_rank_index);
        iv_groupmemRank_userinfo_userface=(ImageView)view.findViewById(R.id.iv_item_rank_userface);
        tv_groupmemRank_userinfo_name=(TextView)view.findViewById(R.id.tv_item_rank_username);
        tv_groupmemRank_userinfo_score=(TextView)view.findViewById(R.id.tv_item_rank_score);


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
                WebFragment.getInstance().url =GlobalConstant.SERVERURL.substring(0,GlobalConstant.SERVERURL.length()-3)+GlobalConstant.jifen;
                break;
        }
    }
    /**
     * 群内排名
     *
     */
    private void Test_getData(){
        groupUserList.clear();
        RankBean rankBean=new RankBean();
        for(int i=0;i<10;i++){
            rankBean.rankscore=i*10.5+"";
            rankBean.user_data=new UserInfoBean();
            rankBean.user_data.name=i+"";

        }
        lv_qun_allrank.setAdapter(qunRankAdapter);
    }
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("group_id",QunDetailFragment.getInstance().group_id);
        map.put("soft", VersionUtils.getVersion(getActivity()));

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.group_groupMemRank,
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
                            groupMemRankJson json = new Gson().fromJson(jsonString,groupMemRankJson .class);

                            if ("1".equals(json.code)) {

                                //userInfo
                                tv_groupmemRank_userinfo_rank.setText(json.userInfo.rank);
                                DisplayImageUtils.displayImage(json.userInfo.avatar, iv_groupmemRank_userinfo_userface, 100, R.drawable.avatar);
                                tv_groupmemRank_userinfo_name.setText(json.userInfo.name);
                                tv_groupmemRank_userinfo_score.setText(json.userInfo.score);

                                //listview
                                groupUserList = new ArrayList<>();
                                groupUserList.addAll(json.groupUser);
                                qunRankAdapter= new QunRankAdapter(fragment, groupUserList,0);
                                lv_qun_allrank.setAdapter(qunRankAdapter);

                                //book
                                DisplayImageUtils.displayImage(json.book.book_cover, iv_allqun_book, 0, R.color.white);
                                tv_qun_weekrank_book_title.setText(json.book.book_title);

                                tv_allqun_journal.setText("第" + json.book.journal + "期");
                                tv_allqun_readtime.setText(json.book.start_time + "-" + json.book.end_time);

                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
                                CustomToast.showToast(context, context.getString(R.string.json_error));
                            }
                        }

                    }
                }, true, null);
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