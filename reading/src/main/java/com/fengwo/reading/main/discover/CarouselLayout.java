package com.fengwo.reading.main.discover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.NextActivity;
import com.fengwo.reading.main.discover.hottopics.HotListBean;
import com.fengwo.reading.main.discover.hottopics.TopicsActivity;
import com.fengwo.reading.main.my.WebFragment;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.UMengUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现 - 轮播图 - 有下拉刷新控件
 */
public class CarouselLayout {

    private List<View> Views;
    private List<ImageView> pointViews;
    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private MyPagerAdapter adapter;

    List<HotListBean> list;

    private int index;
    private int size;
    public static boolean is_show = false;

    public View createHeaderLayout(final Context context,
                                   final List<HotListBean> list,
                                   final SwipeRefreshLayout swipeRefreshLayout) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_discover_header, null);
        viewPager = (ViewPager) view.findViewById(R.id.vp_banner_show);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll_banner_layout);

        if (list == null || list.size() == 0) {
            return view;
        }
        if (context == null || ((Activity) context).isFinishing()) {
            return view;
        }

        size = list.size();

        Views = new ArrayList<View>();
        pointViews = new ArrayList<ImageView>();

        for (int i = 0; i < list.size(); i++) {
            View view1 = LayoutInflater.from(context).inflate(R.layout.head_topics, null);
            ImageView iv_topcs_bg = (ImageView) view1.findViewById(R.id.iv_topcs_bg);
            TextView tv_topcs_num = (TextView) view1.findViewById(R.id.tv_topcs_num);
            TextView tv_topcs_title = (TextView) view1.findViewById(R.id.tv_topcs_title);
            TextView tv_topcs_content = (TextView) view1.findViewById(R.id.tv_topcs_content);
            RelativeLayout rl_topcs_up = (RelativeLayout) view1.findViewById(R.id.rl_topcs_up);
            RelativeLayout rl_topcs = (RelativeLayout) view1.findViewById(R.id.rl_topcs);

            DisplayImageUtils.displayImage(list.get(i).img, iv_topcs_bg, 5,
                    R.drawable.btn_white);

            switch (list.get(i).action_type) {
                case "banner":
                    tv_topcs_title.setText(list.get(i).title);
                    tv_topcs_num.setText("");
                    break;
                case "topic":
                    tv_topcs_title.setText(list.get(i).topic_title);
                    tv_topcs_num.setText("已有" + list.get(i).join_nums + "人参与");
                    break;
            }
            tv_topcs_content.setVisibility(View.GONE);
            rl_topcs_up.setVisibility(View.GONE);

            rl_topcs.setTag(i);
            final int finalI = i;
            rl_topcs.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int location = (Integer) v.getTag();
                    switch (list.get(finalI).action_type) {
                        case "banner":
                            // 跳转 H5
                            Intent intent2 = new Intent(context, NextActivity.class);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("fragmentname", WebFragment.class.getSimpleName());
                            WebFragment.getInstance().needSaveView = false;
                            WebFragment.getInstance().source = 1;
                            WebFragment.getInstance().url = list.get(finalI).href;
                            intent2.putExtras(bundle2);
                            context.startActivity(intent2);
                            ((Activity) context).overridePendingTransition(R.anim.in_from_right,
                                    R.anim.out_to_left);
                            break;
                        case "topic":
                            // 跳转 话题详情
                            UMengUtils.onCountListener(context, "GD_04_01");
                            Intent intent = new Intent(context, TopicsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name", list.get(location).topic_title);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                            ((Activity) context).overridePendingTransition(R.anim.in_from_right,
                                    R.anim.out_to_left);
                            break;
                    }
                }
            });
            Views.add(view1);

            ImageView iv = new ImageView(context);
            if (i == 0) {
                iv.setBackgroundResource(R.drawable.dot_focused);
            } else {
                iv.setBackgroundResource(R.drawable.dot_normal);
            }
            iv.setScaleType(ScaleType.FIT_XY);
            linearLayout.addView(iv);
            if (i != list.size() - 1) {
                View v = new View(context);
                v.setLayoutParams(new LayoutParams((int) context.getResources()
                        .getDimension(R.dimen.y10), (int) context
                        .getResources().getDimension(R.dimen.y10)));
                linearLayout.addView(v);
            }
            pointViews.add(iv);
        }

        index = 0;

        adapter = new MyPagerAdapter();
        viewPager.setAdapter(adapter);
        // TODO 冲突
        viewPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (is_show) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_DOWN:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true);
                        break;

                    default:
                        break;
                }
                viewPager.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                pointViews.get(index).setBackgroundResource(
                        R.drawable.dot_normal);
                index = position;
                pointViews.get(index).setBackgroundResource(
                        R.drawable.dot_focused);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 5000);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state == 1) {
                    handler.removeCallbacks(runnable);
                } else if (state == 0) {
                    handler.postDelayed(runnable, 5000);
                }

            }
        });

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 5000);

        return view;
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            pointViews.get(index).setBackgroundResource(R.drawable.dot_normal);
            index++;
            if (index >= size) {
                index = 0;
            }
            viewPager.setCurrentItem(index);
            pointViews.get(index).setBackgroundResource(R.drawable.dot_focused);
            handler.postDelayed(this, 5000);
        }
    };

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(Views.get(position));
            return Views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(Views.get(position));
        }
    }

}
