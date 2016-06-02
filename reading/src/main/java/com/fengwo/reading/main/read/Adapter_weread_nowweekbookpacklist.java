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
import com.fengwo.reading.utils.MLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Adapter_weread_nowweekbookpacklist extends BaseAdapter {

    private Fragment fragment;
    private List<IndexBean> list;

    public Adapter_weread_nowweekbookpacklist(Fragment fragment, List<IndexBean> list) {
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
            //holder.iv_item_index_ampm = (ImageView) convertView.findViewById(R.id.iv_item_index_ampm);
            holder.tv_item_index_title = (TextView) convertView
                    .findViewById(R.id.tv_item_index_title);
            holder.tv_item_index_new = (TextView) convertView
                    .findViewById(R.id.tv_item_index_new);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_item_index_week.setText("周" +  list.get(position).getPub_time_week() + ""+ list.get(position).timetype_tostring());
        holder.tv_item_index_time.setText( list.get(position).getdate());

        // time_type 1早读，2晚读
        switch (Integer.parseInt(list.get(position).time_type)){
            case 1:

                holder.iv_item_index_readornot.setImageResource(R.drawable.read_taiyang_no);
                //TODO
                if (list.get(position).readornot != null) {
                    if ("1".equals(list.get(position).readornot)) {
                        holder.iv_item_index_readornot.setImageResource(R.drawable.read_taiyang_yes);
                    } else {
                        holder.iv_item_index_readornot.setImageResource(R.drawable.read_taiyang_no);
                    }
                } else {
                    MLog.v("reading", "没有 签到 字段:" + list.get(position));
                };
                break;
            case 2:
                //holder.iv_item_index_ampm.setImageResource(R.drawable.read_pm);
                holder.iv_item_index_readornot.setImageResource(R.drawable.read_yueliang_no);
                //TODO
                if (list.get(position).readornot != null) {
                    if ("1".equals(list.get(position).readornot)) {
                        holder.iv_item_index_readornot.setImageResource(R.drawable.read_yueliang_yes);
                    } else {
                        holder.iv_item_index_readornot.setImageResource(R.drawable.read_yueliang_no);
                    }
                }
                break;
            case 3:
                holder.iv_item_index_readornot.setImageResource(R.drawable.timetype3);
            default:
                break;
        }



        holder.tv_item_index_title.setText(list.get(position).title);
        if (position == 0) {
            holder.tv_item_index_new.setVisibility(View.VISIBLE);
        } else {
            holder.tv_item_index_new.setVisibility(View.GONE);
        }

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
