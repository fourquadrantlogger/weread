package com.fengwo.reading.main.group.qun;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.main.group.GroupUserBean;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.UMengUtils;
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
 * 群详情
 */
public class QunDetailFragment extends Fragment implements View.OnClickListener {
    public QunDetailFragment() {
        super();
    }

    private CustomProgressDialog progressDialog;
    public String group_id = "";

    public static QunDetailFragment fragment = new QunDetailFragment();
    private List<GroupUserBean> list;
    boolean showorhide = false;

    private List<GroupUserBean> list_to_show() {
        if (!showorhide)
            if (list.size() >= 10)
                return list.subList(0, 10);
            else return list;
        else
            return list;
    }

    private NameandFaceAdapter nameandFaceAdapter;

    public static QunDetailFragment getInstance() {
        return fragment;
    }

    private View saveView = null;
    public boolean needSaveView = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;
        View view = inflater.inflate(R.layout.fragment_qun_detail, container, false);
        findViewById(view);
        onClickListener();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 400);
        return view;
    }

    //  布局控件
    ScrollView sv_qun_detail;
    //  顶部
    Viewsimpletitle viewsimpletitle_qun_detail;

    //  第一栏
    LinearLayout linearLayout_qun_detail_memrank;

    TextView tv_qun_detail_userinfo_name, tv_qun_detail_userinfo_score, tv_qun_detail_userinfo_rank;

    ImageView iv_qun_detail_userinfo_avstar;

    //  第二栏
    LinearLayout linearLayout_qun_detail_grouprank;


    TextView tv_qun_detail_qunname, tv_qun_detail_rank, tv_qun_detail_rate, tv_qun_detail_score;

    //gridview

    GridView gv_qun_detail;
    TextView tv_qun_detail_more;

    private void findViewById(View view) {


        viewsimpletitle_qun_detail = (Viewsimpletitle) view.findViewById(R.id.viewsimpletitle_qun_detail);

        sv_qun_detail = (ScrollView) view.findViewById(R.id.sv_qun_detail);

        linearLayout_qun_detail_memrank = (LinearLayout) view.findViewById(R.id.linearLayout_qun_detail_memrank);
        linearLayout_qun_detail_grouprank = (LinearLayout) view.findViewById(R.id.linearLayout_qun_detail_grouprank);

        iv_qun_detail_userinfo_avstar = (ImageView) view.findViewById(R.id.iv_qun_detail_userface);
        tv_qun_detail_userinfo_name = (TextView) view.findViewById(R.id.tv_qun_detail_userinfo_name);
        tv_qun_detail_userinfo_rank = (TextView) view.findViewById(R.id.tv_qun_detail_userinfo_rank);
        tv_qun_detail_userinfo_score = (TextView) view.findViewById(R.id.tv_qun_detail_userinfo_score);

        tv_qun_detail_qunname = (TextView) view.findViewById(R.id.tv_qun_detail_qunname);
        tv_qun_detail_rank = (TextView) view.findViewById(R.id.tv_qun_detail_rank);
        tv_qun_detail_rate = (TextView) view.findViewById(R.id.tv_qun_detail_rate);
        tv_qun_detail_score = (TextView) view.findViewById(R.id.tv_qun_detail_score);

        gv_qun_detail = (GridView) view.findViewById(R.id.gv_qun_detail);
        tv_qun_detail_more = (TextView) view.findViewById(R.id.tv_qun_detail_more);
    }

    private void onClickListener() {
        viewsimpletitle_qun_detail.getImageView_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        linearLayout_qun_detail_memrank.setOnClickListener(this);

        linearLayout_qun_detail_grouprank.setOnClickListener(this);

        tv_qun_detail_more.setOnClickListener(this);
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
            case R.id.linearLayout_qun_detail_memrank:
                //群内排名
                UMengUtils.onCountListener(getActivity(), "GD_03_01_01_01");
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, groupMemRankFragment.getInstance());
                transaction.commit();
                groupMemRankFragment.getInstance().needSaveView = false;
                break;
            case R.id.linearLayout_qun_detail_grouprank:
                //群间排名
                UMengUtils.onCountListener(getActivity(), "GD_03_01_01_02");
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next, AllQunWeekRankFragment.getInstance());
                transaction.commit();
                AllQunWeekRankFragment.getInstance().needSaveView = false;
                break;
            case R.id.tv_qun_detail_more:
                showorhide = !showorhide;
                nameandFaceAdapter.notifyDataSetChanged();
                nameandFaceAdapter = new NameandFaceAdapter(fragment, list_to_show(), false);
                gv_qun_detail.setAdapter(nameandFaceAdapter);
                if (!showorhide) {
                    tv_qun_detail_more.setText("收起 ∧");

                } else {
                    tv_qun_detail_more.setText("查看全部成员 ∨");
                }
            default:
                break;
        }
    }

    /**
     * 群详情
     */
    private void Test_addAll() {
        for (int i = 0; i < 53; i++) {

            GroupUserBean groupUserBean = new GroupUserBean();
            groupUserBean.avatar = "http://avatarimg.fengwo.com/readwith/20160318/56ebb5aae5c79.jpeg@170w_170h.jpg";
            groupUserBean.name = "sdf";
            list.add(groupUserBean);
        }
    }

    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("group_id", group_id);

        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.group_groupinfo,
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
                            QunDetailJson json = new Gson().fromJson(jsonString, QunDetailJson.class);
                            if ("1".equals(json.code)) {
                                //groupUser
                                list = new ArrayList<>();
                                list.addAll(json.data.groupUser);
                                if (json.data.groupUser.size() <= 10) {
                                    tv_qun_detail_more.setText("收起 ∧");
                                } else {
                                    tv_qun_detail_more.setText("查看全部成员 ∨");
                                }
                                nameandFaceAdapter = new NameandFaceAdapter(fragment, list_to_show(), false);
                                gv_qun_detail.setAdapter(nameandFaceAdapter);

                                //groupInfo
                                if (json.data.groupInfo != null) {
                                    if (TextUtils.isEmpty(json.data.groupInfo.group_name) == false) {
                                        tv_qun_detail_qunname.setText(json.data.groupInfo.group_name);
                                    }
                                    if (TextUtils.isEmpty(json.data.groupInfo.rank) == false) {
                                        tv_qun_detail_rank.setText(json.data.groupInfo.rank);
                                    }
                                    if (TextUtils.isEmpty(json.data.groupInfo.score) == false) {
                                        tv_qun_detail_score.setText(json.data.groupInfo.score);
                                    }
                                    if (TextUtils.isEmpty(json.data.groupInfo.rate) == false) {
                                        tv_qun_detail_rate.setText(json.data.groupInfo.rate);
                                    }
                                }

                                //userInfo
                                if (json.data.userInfo != null) {
                                    if (TextUtils.isEmpty(json.data.userInfo.avatar) == false) {
                                        DisplayImageUtils.displayImage(json.data.userInfo.avatar, iv_qun_detail_userinfo_avstar, 100, R.drawable.avatar);
                                    }
                                    if (TextUtils.isEmpty(json.data.userInfo.rank) == false) {
                                        tv_qun_detail_userinfo_rank.setText(json.data.userInfo.rank);
                                    }
                                    if (TextUtils.isEmpty(json.data.userInfo.score) == false) {
                                        tv_qun_detail_userinfo_score.setText(json.data.userInfo.score);
                                    }
                                    if (TextUtils.isEmpty(json.data.userInfo.name) == false) {
                                        tv_qun_detail_userinfo_name.setText(json.data.userInfo.name);
                                    }
                                }
                                sv_qun_detail.setVisibility(View.VISIBLE);
                            } else {
                                iv_qun_detail_userinfo_avstar.setImageResource(R.drawable.avatar);
                                sv_qun_detail.setVisibility(View.INVISIBLE);
                                CustomToast.showToast(fragment.getActivity(), "您尚未加入群组");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
                        CustomToast.showToast(context, context.getString(R.string.network_check));
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
        MobclickAgent.onPageEnd("QunDetailFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("QunDetailFragment");
    }
}
