package com.fengwo.reading.main.my.achieve;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.main.my.myfav.ListXunzhang;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.VersionUtils;
import com.fengwo.reading.view.Viewsimpletitle;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的胸章
 * Created by timeloveboy
 * on 16/4/21.
 */
public class Fragment_wodexunzhang extends Fragment implements View.OnClickListener {
    //region 顶部
    private Viewsimpletitle view_title;
    //endregion
    //region gridview
    int gridView_item_height = 400;
    public GridView gridView_wodexunzhang;
    public GridView gridView_wodexunzhang_teshu;
    Adapter_wodexunzhang adapter_wodexunzhang, adapter_wodexunzhang_teshu;

    // list
    class user_badge {
        public List<Xunzhang> teshu;
        public List<Xunzhang> data;
        public String code;
        public String msg;
    }

    List<Xunzhang> list_server_teshu = new ArrayList<>();
    List<Xunzhang> list_server_data = new ArrayList<>();
    List<Xunzhang> list_localall;

    List<Xunzhang> list_data() {

        List<Xunzhang> list_data = new ArrayList<>(Arrays.asList(new Xunzhang[list_localall.size()]));
        Collections.copy(list_data,list_localall);

       for(int j=0;j<list_server_data.size();j++){
           ListXunzhang.findbyId(list_data, list_server_data.get(j).id).got=true;
           ListXunzhang.findbyId(list_data, list_server_data.get(j).id).create_time=list_server_data.get(j).create_time;
       }

        for(int j=0;j<list_server_teshu.size();j++) {
            for(int i=0;i<list_data.size();i++) {
                if(list_server_teshu.get(j).id.equals(list_data.get(i).id)){
                    list_data.remove(i);
                    i--;
                }
            }
        }

        return list_data;
    }

    List<Xunzhang> list_teshu() {

        List<Xunzhang> list_teshu=new ArrayList<>();

        for(int j=0;j<list_server_teshu.size();j++) {
            list_teshu.add(ListXunzhang.findbyId(list_localall,list_server_teshu.get(j).id));
            list_teshu.get(list_teshu.size()-1).got=true;
            list_teshu.get(list_teshu.size()-1).create_time=list_server_teshu.get(j).create_time;
        }
        return list_teshu;
    }

    private static Fragment_wodexunzhang fragment = new Fragment_wodexunzhang();

    public static Fragment_wodexunzhang getInstance() {
        return fragment;
    }

    CommonHandler handler;
    private View saveView = null;
    public boolean needSaveView = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;
        list_localall= NOsqlUtil.get_json_wodexunzhang().data;
        //region 主view
        View view = inflater.inflate(R.layout.fragment_wodexunzhang, container, false);
        findViewById(view);
        view_title.getTextView_title().setText("我的勋章");
        view_title.getImageView_back().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        handler = new CommonHandler(getActivity(),null);
        //endregion

        //region listview
        adapter_wodexunzhang_teshu = new Adapter_wodexunzhang(fragment, list_teshu());
        LinearLayout.LayoutParams params_teshu = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (1 + list_teshu().size() / 3) * gridView_item_height);
        gridView_wodexunzhang_teshu.setLayoutParams(params_teshu);
        gridView_wodexunzhang_teshu.setAdapter(adapter_wodexunzhang_teshu);

        gridView_wodexunzhang_teshu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupWindow_usexunzhang popupWindow_usexunzhang = new PopupWindow_usexunzhang(getActivity(), list_teshu().get(position),false,position);
                popupWindow_usexunzhang.showAsDropDown(getActivity().findViewById(R.id.Viewsimpletitle_wodexunzhang));
            }
        });
        adapter_wodexunzhang = new Adapter_wodexunzhang(fragment, list_data());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (1 + list_data().size() / 3) * gridView_item_height);
        gridView_wodexunzhang.setLayoutParams(params);
        gridView_wodexunzhang.setAdapter(adapter_wodexunzhang);

        gridView_wodexunzhang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopupWindow_usexunzhang popupWindow_usexunzhang = new PopupWindow_usexunzhang(getActivity(), list_data().get(position),true,position);
                popupWindow_usexunzhang.showAsDropDown(getActivity().findViewById(R.id.Viewsimpletitle_wodexunzhang));
            }
        });
        //endregion

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1000);
        return view;
    }

    private void findViewById(View view) {
        view_title = (Viewsimpletitle) view.findViewById(R.id.Viewsimpletitle_wodexunzhang);

        gridView_wodexunzhang = (GridView) view.findViewById(R.id.gridView_wodexunzhang);
        gridView_wodexunzhang_teshu = (GridView) view.findViewById(R.id.gridView_wodexunzhang_teshu);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 有书榜- 网络请求
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("soft", VersionUtils.getVersion(getActivity()));
        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.SERVERURL + "user/badge",
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
                            user_badge json = new Gson().fromJson(jsonString, user_badge.class);
                            if ("1".equals(json.code)) {



                                if(json.teshu!=null){
                                    list_server_teshu=json.teshu;
                                    getActivity().findViewById(R.id.linearLayout_wodexunzhang_teshu).setVisibility(View.VISIBLE);

                                    adapter_wodexunzhang_teshu = new Adapter_wodexunzhang(fragment, list_teshu());

                                    LinearLayout.LayoutParams params_teshu = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (1 + list_teshu().size() / 3) * gridView_item_height);
                                    gridView_wodexunzhang_teshu.setLayoutParams(params_teshu);
                                    gridView_wodexunzhang_teshu.setAdapter(adapter_wodexunzhang_teshu);

                                }else {
                                    getActivity().findViewById(R.id.linearLayout_wodexunzhang_teshu).setVisibility(View.GONE);
                                }

                                if (json.data != null) {
                                    list_server_data = json.data;

                                    adapter_wodexunzhang = new Adapter_wodexunzhang(fragment, list_data());
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (1 + list_data().size() / 3) * gridView_item_height);
                                    gridView_wodexunzhang.setLayoutParams(params);
                                    gridView_wodexunzhang.setAdapter(adapter_wodexunzhang);

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
}
