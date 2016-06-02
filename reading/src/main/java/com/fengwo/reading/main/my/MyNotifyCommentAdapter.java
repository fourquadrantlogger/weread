package com.fengwo.reading.main.my;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.EmojiUtils;
import com.fengwo.reading.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyNotifyCommentAdapter extends BaseAdapter {

    private Fragment fromFragment;
    private List<MyNotifyBean> list;

    public MyNotifyCommentAdapter(Fragment fromFragment, List<MyNotifyBean> list) {
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
                    .inflate(R.layout.item_mynotify_comment, parent, false);
            holder.iv_item_notity_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_item_notity_avatar);
            holder.iv_item_notity_sex = (ImageView) convertView
                    .findViewById(R.id.iv_item_notity_sex);
            holder.iv_item_notity_zan = (ImageView) convertView
                    .findViewById(R.id.iv_item_notity_zan);

            holder.tv_item_notity_name = (TextView) convertView
                    .findViewById(R.id.tv_item_notity_name);
            holder.tv_item_notity_time = (TextView) convertView
                    .findViewById(R.id.tv_item_notity_time);
            holder.tv_item_notity_content = (TextView) convertView
                    .findViewById(R.id.tv_item_notity_content);
            holder.tv_item_notity_title = (TextView) convertView
                    .findViewById(R.id.tv_item_notity_title);

            holder.v_item_notity_line = (View) convertView
                    .findViewById(R.id.v_item_notity_line);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DisplayImageUtils.displayImage(list.get(position).avatar,
                holder.iv_item_notity_avatar, 100, R.drawable.avatar);
        if (TextUtils.isEmpty(list.get(position).sex)
                || "0".equals(list.get(position).sex)) {
            holder.iv_item_notity_sex.setVisibility(View.GONE);
        } else if ("1".equals(list.get(position).sex)) {
            holder.iv_item_notity_sex.setVisibility(View.VISIBLE);
            holder.iv_item_notity_sex.setImageResource(R.drawable.myinfo_nan);
        } else if ("2".equals(list.get(position).sex)) {
            holder.iv_item_notity_sex.setVisibility(View.VISIBLE);
            holder.iv_item_notity_sex.setImageResource(R.drawable.myinfo_nv);
        }
        if ("comment".equals(list.get(position).source)) {
            holder.tv_item_notity_content.setVisibility(View.VISIBLE);
            holder.tv_item_notity_content.setText(list.get(position).content);
            holder.iv_item_notity_zan.setVisibility(View.GONE);
        } else if ("digg".equals(list.get(position).source)) {
            holder.tv_item_notity_content.setVisibility(View.GONE);
            holder.tv_item_notity_content.setText("");
            holder.iv_item_notity_zan.setVisibility(View.VISIBLE);
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
        holder.tv_item_notity_name.setText(list.get(position).name);

        holder.tv_item_notity_title.setText(EmojiUtils.getSmiledText(
                        fromFragment.getActivity(), list.get(position).right),
                TextView.BufferType.SPANNABLE);

        if (position == list.size() - 1) {
            holder.v_item_notity_line.setVisibility(View.INVISIBLE);
        } else {
            holder.v_item_notity_line.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        private ImageView iv_item_notity_avatar, iv_item_notity_sex,
                iv_item_notity_zan;
        private TextView tv_item_notity_name, tv_item_notity_time,
                tv_item_notity_content, tv_item_notity_title;
        private View v_item_notity_line;
    }

}