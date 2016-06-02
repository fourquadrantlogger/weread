package com.fengwo.reading.main.my;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.read.Fragment_BookInfoWithPacks;
import com.fengwo.reading.utils.DisplayImageUtils;

import java.util.List;

public class ProgressAdapter extends BaseAdapter {

    private Fragment fromFragment;
    private List<ProgressBean2> list;

    public ProgressAdapter(Fragment fromFragment, List<ProgressBean2> list) {
        super();
        this.fromFragment = fromFragment;
        this.list = list;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(fromFragment.getActivity())
                    .inflate(R.layout.item_progress, parent, false);
            holder.rl_progress_item_all = (RelativeLayout) convertView
                    .findViewById(R.id.rl_progress_item_all);
            holder.tv_progress_item_month = (TextView) convertView
                    .findViewById(R.id.tv_progress_item_month);
            holder.tv_progress_item_year = (TextView) convertView
                    .findViewById(R.id.tv_progress_item_year);
            holder.gv_progress_item_gridview = (GridView) convertView
                    .findViewById(R.id.gv_progress_item_gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (list.get(position).book == null) {
            holder.rl_progress_item_all.setVisibility(View.GONE);
        } else {
            holder.rl_progress_item_all.setVisibility(View.VISIBLE);

            String str1 = list.get(position).month.substring(5, list.get(position).month.length());
            String str2 = list.get(position).month.substring(0, 4);
            holder.tv_progress_item_month.setText(str1 + "月");
            holder.tv_progress_item_year.setText(str2);

            GridViewAdapter gridViewAdapter = new GridViewAdapter(fromFragment, list.get(position).book);
            holder.gv_progress_item_gridview.setAdapter(gridViewAdapter);
        }

        return convertView;
    }

    private static class ViewHolder {

        private RelativeLayout rl_progress_item_all;
        private GridView gv_progress_item_gridview;
        private TextView tv_progress_item_month, tv_progress_item_year;
    }

    private class GridViewAdapter extends BaseAdapter {

        private Fragment fromFragment;
        private List<ProgressBean> list;

        public GridViewAdapter(Fragment fromFragment, List<ProgressBean> list) {
            super();
            this.fromFragment = fromFragment;
            this.list = list;
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
                convertView = LayoutInflater.from(fromFragment.getActivity())
                        .inflate(R.layout.item_search_top, parent, false);
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
            holder.tv_search_remen_title.setVisibility(View.GONE);
            holder.tv_search_remen_time.setText(list.get(position).book_title);

            DisplayImageUtils.displayImage(list.get(position).book_cover,
                    holder.iv_search_remen_img, 0, R.color.bg);

            holder.rl_search_remen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction transaction = fromFragment.getActivity().getSupportFragmentManager()
                            .beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right,
                            R.anim.out_to_left, R.anim.in_from_left,
                            R.anim.out_to_right);
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.ll_activity_next,
                            Fragment_BookInfoWithPacks.getInstance());
                    transaction.commit();

                    Fragment_BookInfoWithPacks.getInstance().pb_id = list.get(position).id;
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