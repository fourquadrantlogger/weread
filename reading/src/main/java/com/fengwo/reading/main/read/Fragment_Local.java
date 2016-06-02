package com.fengwo.reading.main.read;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的离线
 */
public class Fragment_Local extends Fragment implements View.OnClickListener {
//    private Viewsimpletitle titleview;
    private TextView textView_left,textView_right,tv_local_nothing;
    private ImageView iv_return;
    public CommonHandler handler;
    boolean left = true;

    List<Json_BookInfoWithPacks> list1;

    private ListView listView;

    private List<Json_BookInfoWithPacks> list;
    private Adapter_BookList booksAdapter;

    private View saveView = null;
    public boolean needSaveView = false;

    public Fragment_Local() {
    }

    public static Fragment_Local fragment = new Fragment_Local();

    public static Fragment_Local getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_local, container, false);
        handler = new CommonHandler(getActivity(), null);
//        titleview = ((Viewsimpletitle) view.findViewById(R.id.viewsimpletitle_fragment_local));
//        titleview.getTextView_title().setText("我的离线");
//        titleview.getImageView_back().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().finish();
//                getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//            }
//        });
        findViewById(view);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });

        list1 = NOsqlUtil.get_BookInfoWithPacks_local();
        for (Json_BookInfoWithPacks json_bookInfoWithPacks:list1){
            Log.i("szf",json_bookInfoWithPacks.data.get(0).time_type);
        }
        listChange();

        textView_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = true;
//                pullToRefreshListView_note_fav.setVisibility(View.VISIBLE);
//                pullToRefreshListView_pack_fav.setVisibility(View.GONE);
                textView_left.setTextColor(Color.parseColor("#ffffff"));
                textView_left.setBackgroundResource(R.drawable.deep_nav_left);
                textView_right.setBackgroundColor(Color.parseColor("#00ffffff"));
                textView_right.setTextColor(Color.parseColor("#dddddd"));
//                getData(true);
                listChange();

            }
        });
        textView_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                left = false;
//                pullToRefreshListView_note_fav.setVisibility(View.GONE);
//                pullToRefreshListView_pack_fav.setVisibility(View.VISIBLE);
                textView_right.setBackgroundResource(R.drawable.deep_nav_left);
                textView_right.setTextColor(Color.parseColor("#ffffff"));
                textView_left.setBackgroundColor(Color.parseColor("#00ffffff"));
                textView_left.setTextColor(Color.parseColor("#dddddd"));
//                getData(false);
                listChange();
            }
        });



        return view;
    }

    private void listChange() {
        list = null;
        list = new ArrayList<>();
        //通过left判断当前是什么按钮状态，每次添加list内容前，清空其中内容
        if (left){
            list.clear();
//            for (Json_BookInfoWithPacks json_bookInfoWithPacks:list1){
//                if (json_bookInfoWithPacks.data.get(0).time_type.equals(1)||json_bookInfoWithPacks.data.get(0).time_type.equals(2)){
            list.addAll(list1);
            Log.i("szf1",String.valueOf(list.size()));
//                }
//            }
        }else{
            list.clear();
            for (Json_BookInfoWithPacks json_bookInfoWithPacks:list1){
                if (json_bookInfoWithPacks.data.get(0).time_type.equals(4)){
                    list.add(json_bookInfoWithPacks);
                }
            }
            Log.i("szf2",String.valueOf(list.size()));
        }
        if (list.size()==0){
            tv_local_nothing.setVisibility(View.VISIBLE);
        }else{
            tv_local_nothing.setVisibility(View.GONE);
        }
//        list = NOsqlUtil.get_BookInfoWithPacks_local();
        booksAdapter = new Adapter_BookList(fragment, list, false);
        listView.setAdapter(booksAdapter);
        booksAdapter.notifyDataSetChanged();
    }

    private void findViewById(View view) {
        tv_local_nothing = (TextView) view.findViewById(R.id.tv_local_nothing);
        listView = (ListView) view.findViewById(R.id.local_books);
        textView_left = (TextView) view.findViewById(R.id.textView_left);
        textView_right = (TextView) view.findViewById(R.id.textView_right);
        iv_return = (ImageView) view.findViewById(R.id.iv_return);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment_BookList");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        MobclickAgent.onPageEnd("Fragment_BookList");
    }
}
