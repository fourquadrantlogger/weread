package com.fengwo.reading.player;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.read.IndexBean;
import com.fengwo.reading.utils.ScreenUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.TimeUtil;

import java.util.List;

public class PopupWindow_SelectMusic extends PopupWindow {

    public PopupWindow_SelectMusic(Context context, List<IndexBean> list,
                                   int index, OnItemClickListener onItemClickListener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_music, null);
        TextView textView_popupwndow_music_count=(TextView)mView.findViewById(R.id.textView_popupwndow_music_count);
        textView_popupwndow_music_count.setText("顺序播放 ("+list.size()+"集)");
        ListView listView = (ListView) mView.findViewById(R.id.lv_popupwindow_show);
        MusicAdapter adapter = new MusicAdapter(context, list, index);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(onItemClickListener);

        RelativeLayout relativeLayout = (RelativeLayout) mView
                .findViewById(R.id.rl_popupwindow_layout);
        relativeLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });

        // 设置按钮监听
        // TODO
        // 设置SelectPicPopupWindow的View
        this.setContentView(mView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        if (list == null || list.size() <= 4) {
            this.setHeight(LayoutParams.WRAP_CONTENT);
        } else {
            this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        }
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // TODO
//		this.setTouchable(true);
//		this.setFocusable(true);
//		this.setBackgroundDrawable(new BitmapDrawable());
//		this.setOutsideTouchable(true);
    }

    private class MusicAdapter extends BaseAdapter {

        private Context context;
        private List<IndexBean> list;
        private int index;

        public MusicAdapter(Context context, List<IndexBean> list, int index) {
            super();
            this.context = context;
            this.list = list;
            this.index = index;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
                holder.imageView_item_music_playing=(ImageView)convertView.findViewById(R.id.imageView_item_music_playing);
                holder.tv_item_music_title = (TextView) convertView.findViewById(R.id.tv_item_music_title);
                holder.tv_item_music_time = (TextView) convertView.findViewById(R.id.tv_item_music_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }




            if (index == position) {
                String sText ="<font size=\"30px\" color=\"#17c052\">"
                        +"周"+list.get(position).getPub_time_week()+list.get(position).timetype_tostring()
                        + ":  "+"</font>"
                        +"<font size=\"26px\" color=\"#3dd571\">"+ list.get(position).title+"</font>";
                MLog.v("reading",sText);
                holder.tv_item_music_title.setText(Html.fromHtml(sText));
                holder.tv_item_music_time.setText(Html.fromHtml("<font color=\"#3dd571\">"+list.get(position).media_time+"</font>"));
                holder.imageView_item_music_playing.setVisibility(View.VISIBLE);
            } else {
                String sText ="<font size=\"30px\" color=\"#333333\">"
                        +"周"+list.get(position).getPub_time_week()+list.get(position).timetype_tostring()
                        + ":  "+"</font>"
                        +"<font size=\"26px\" color=\"#808080\">"+ list.get(position).title+"</font>";
                MLog.v("reading",sText);
                holder.tv_item_music_title.setText(Html.fromHtml(sText));
                holder.tv_item_music_time.setText(Html.fromHtml("<font color=\"#b2b2b2\">"+list.get(position).media_time+"</font>"));
                holder.imageView_item_music_playing.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder {
            private ImageView imageView_item_music_playing;
            private TextView tv_item_music_title;
            private TextView tv_item_music_time;
        }
    }

}
