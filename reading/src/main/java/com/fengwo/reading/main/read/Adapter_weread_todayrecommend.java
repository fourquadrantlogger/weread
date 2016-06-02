package com.fengwo.reading.main.read;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.utils.DisplayImageUtils;

import java.util.List;

/**
 * Created by timeloveboy on 16/4/27.
 */
public class Adapter_weread_todayrecommend extends BaseAdapter {

    private Fragment fragment;
    private List<IndexBean> list;

    public Adapter_weread_todayrecommend(Fragment fragment, List<IndexBean> list) {
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
            convertView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.item_weread_todayrecommend, parent, false);

            holder.image_todayrecommend = (ImageView) convertView.findViewById(R.id.image_todayrecommend);
            holder.textView_todayrecommend_title=(TextView)convertView.findViewById(R.id.textView_item_title_weread_nodayrecommend);
            holder.textView_todayrecommend_content=(TextView)convertView.findViewById(R.id.textView_item_content_weread_nodayrecommend);
            holder.textView_todayrecommend_timetype =(TextView)convertView.findViewById(R.id.textView_item_timetype_weread_nodayrecommend);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView_todayrecommend_title.setText(list.get(position).title);
        holder.textView_todayrecommend_content.setText(list.get(position).pack_abs);
        holder.textView_todayrecommend_timetype.setText(list.get(position).timetype_tostring());
        DisplayImageUtils.loadImage(list.get(position).top_img,holder.image_todayrecommend,fragment.getActivity());
        return convertView;
    }

    private static class ViewHolder {
        private ImageView image_todayrecommend;
        private TextView textView_todayrecommend_title;
        private TextView textView_todayrecommend_content;
        private TextView textView_todayrecommend_timetype;
    }
}
