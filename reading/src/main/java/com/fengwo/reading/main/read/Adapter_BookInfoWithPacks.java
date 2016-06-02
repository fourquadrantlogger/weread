package com.fengwo.reading.main.read;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Adapter_BookInfoWithPacks extends BaseAdapter {

    private Fragment fragment;
    private List<IndexBean> list;

    public Adapter_BookInfoWithPacks(Fragment fragment, List<IndexBean> list) {
        super();
        this.fragment = fragment;
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
            convertView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.item_weread_nowweek, parent, false);
            holder.iv_item_index_readornot = (ImageView) convertView.findViewById(R.id.iv_item_index_readornot);
            holder.tv_item_index_week = (TextView) convertView
                    .findViewById(R.id.tv_item_index_week);
            holder.tv_item_index_time = (TextView) convertView
                    .findViewById(R.id.tv_item_index_time);
            holder.tv_item_index_title = (TextView) convertView
                    .findViewById(R.id.tv_item_index_title);
            holder.tv_item_index_new = (TextView) convertView
                    .findViewById(R.id.tv_item_index_new);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_item_index_week.setText("周"+list.get(position).getPub_time_week());
        holder.tv_item_index_time.setText(list.get(position).timetype_tostring());


        // time_type 1早读，2晚读
        if ("1".equals(list.get(position).time_type)) {

            if (list.get(position).readornot != null) {
                if ("1".equals(list.get(position).readornot)) {
                    holder.iv_item_index_readornot.setImageResource(R.drawable.read_taiyang_yes);
                } else {
                    holder.iv_item_index_readornot.setImageResource(R.drawable.read_taiyang_no);
                }
            }
        } else if ("2".equals(list.get(position).time_type))  {

            if (list.get(position).readornot != null) {
                if ("1".equals(list.get(position).readornot)) {
                    holder.iv_item_index_readornot.setImageResource(R.drawable.read_yueliang_yes);
                } else {
                    holder.iv_item_index_readornot.setImageResource(R.drawable.read_yueliang_no);
                }
            }
        }else if ("3".equals(list.get(position).time_type))   {
            holder.iv_item_index_readornot.setImageResource(R.drawable.timetype3);
        }

        holder.tv_item_index_title.setText(list.get(position).title);

        holder.tv_item_index_new.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {
        private ImageView iv_item_index_readornot;
        private TextView tv_item_index_week;
        private TextView tv_item_index_time;
        private TextView tv_item_index_title;
        private TextView tv_item_index_new;
    }
}
