package com.fengwo.reading.main.group;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.my.UserReadBean;
import com.fengwo.reading.utils.DisplayImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索 - 热门搜索 - ViewPager
 */
public class SearchTopLayout {

    private GridView gv_search_gridview;
    private List<View> views;

    private List<ImageView> pointViews;
    private ViewPager viewPager;
    private LinearLayout linearLayout;

    private MyPagerAdapter adapter;
    private MyGroupRankingAdapter gridViewAdapter;

    private String str = "";
    private int index;
    private int size;

    public static boolean is_show = false;

    public View createHeaderLayout(final Context context,
                                   final List<UserReadBean> list) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_search_viewpager, null);
        viewPager = (ViewPager) view.findViewById(R.id.vp_banner_show);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll_banner_layout);

        if (list == null || list.size() == 0) {
            return view;
        }
        if (context == null || ((Activity) context).isFinishing()) {
            return view;
        }

        List<UserReadBean> list1 = new ArrayList();
        List list2 = new ArrayList<>();

        UserReadBean bean = new UserReadBean();
        bean.book_id = "0";
        bean.book_title = "全部图书";
        bean.book_cover = "0";
        bean.start_time = "现在";
        list1.add(bean);

        for (int i = 0; i < list.size(); i++) {
            if (list1.size() < 4) {
                list1.add(list.get(i));
            }
            if (list1.size() == 4) {
                list2.add(list1);
                list1 = new ArrayList();
            } else if (list.size() == i + 1) {
                list2.add(list1);
            }
        }

        size = list2.size();

        views = new ArrayList<>();
        pointViews = new ArrayList<>();

        for (int i = 0; i < list2.size(); i++) {

            View view1 = LayoutInflater.from(context).inflate(
                    R.layout.layout_search_gridview, null);
            gv_search_gridview = (GridView) view1.findViewById(R.id.gv_search_gridview);
            gridViewAdapter = new MyGroupRankingAdapter(context, (List<UserReadBean>) list2.get(i));
            gv_search_gridview.setAdapter(gridViewAdapter);
            view1.setTag(i);
            views.add(view1);

            //点
            ImageView iv = new ImageView(context);
            if (i == 0) {
                iv.setBackgroundResource(R.drawable.dot_focused);
            } else {
                iv.setBackgroundResource(R.drawable.dot_normal);
            }
            iv.setScaleType(ScaleType.FIT_XY);
            linearLayout.addView(iv);
            if (i != list2.size() - 1) {
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
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                    case MotionEvent.ACTION_DOWN:
//                        swipeRefreshLayout.setEnabled(false);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        swipeRefreshLayout.setEnabled(true);
//                        break;
//
//                    default:
//                        break;
//                }
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
//                handler.removeCallbacks(runnable);
//                handler.postDelayed(runnable, 5000);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

        });

//        handler.removeCallbacks(runnable);
//        handler.postDelayed(runnable, 5000);

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
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }

    /**
     * Gridview适配器
     */
    private class MyGroupRankingAdapter extends BaseAdapter {

        private Context context;
        private List<UserReadBean> list;

        public MyGroupRankingAdapter(Context context, List<UserReadBean> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
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
                        R.layout.item_search_top, parent, false);
                holder.rl_search_remen = (RelativeLayout) convertView
                        .findViewById(R.id.rl_search_remen);
                holder.iv_search_remen_img = (ImageView) convertView
                        .findViewById(R.id.iv_search_remen_img);
                holder.tv_search_remen_title = (TextView) convertView
                        .findViewById(R.id.tv_search_remen_title);
                holder.tv_search_remen_time = (TextView) convertView
                        .findViewById(R.id.tv_search_remen_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if ("0".equals(list.get(position).book_cover)) {
                holder.iv_search_remen_img.setImageResource(R.drawable.search_all);
            } else {
                DisplayImageUtils.displayImage(list.get(position).book_cover,
                        holder.iv_search_remen_img, 0, R.drawable.zanwufengmian);
            }

            holder.tv_search_remen_title.setText(list.get(position).book_title);
            holder.tv_search_remen_time.setText(list.get(position).start_time);

            holder.rl_search_remen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFragment.getInstance().refresh(list.get(position).book_id, list.get(position).book_title);
                }
            });
            return convertView;
        }

        private class ViewHolder {
            private RelativeLayout rl_search_remen;
            private ImageView iv_search_remen_img;
            private TextView tv_search_remen_title, tv_search_remen_time;
        }
    }

}
