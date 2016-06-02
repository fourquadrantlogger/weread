package com.fengwo.reading.main.my;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 系统通知的适配器
 */
public class MyNotifyAdapter extends BaseAdapter {

    private Fragment fromFragment;
    private List<MyNotifyBean> list;

    public MyNotifyAdapter(Fragment fromFragment, List<MyNotifyBean> list) {
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
                    .inflate(R.layout.item_mynotify, parent, false);
            holder.tv_item_notity_time = (TextView) convertView
                    .findViewById(R.id.tv_item_notity_time);
            holder.tv_item_notity_content = (TextView) convertView
                    .findViewById(R.id.tv_item_notity_content);
            holder.iv_item_notity_img = (ImageView) convertView
                    .findViewById(R.id.iv_item_notity_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            if ("0000-00-00 00:00:00".equals(list.get(position).create_time)) {
                holder.tv_item_notity_time.setText("");
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                Date date = simpleDateFormat
                        .parse(list.get(position).create_time);
                long timeStemp = date.getTime();

                holder.tv_item_notity_time.setText(TimeUtil
                        .getTimeMsgNoneMM(timeStemp + ""));
                // list.get(position).create_time + "000"
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tv_item_notity_time.setText("");
        }
        holder.tv_item_notity_content.setText(list.get(position).content);
        DisplayImageUtils.displayImage(list.get(position).right,
                holder.iv_item_notity_img, -1, R.drawable.cover);

        //增加字段跳转 - 手机浏览器


        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_item_notity_time, tv_item_notity_content;
        private ImageView iv_item_notity_img;
    }

}