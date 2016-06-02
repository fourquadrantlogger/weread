package com.fengwo.reading.main.my.myfav;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.ImageUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import java.util.List;

public class Adapter_Pack extends BaseAdapter {
    private List<Fragment_MyFav.Pack> list;
    private Fragment fromFragment;

    public Adapter_Pack(Fragment fromFragment, List<Fragment_MyFav.Pack> list) {
        super();
        this.list = list;
        this.fromFragment = fromFragment;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (list != null) {
            return list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(fromFragment.getActivity()).inflate(R.layout.item_my_shoucang_pack, parent, false);
            holder.bg = (ImageView) convertView.findViewById(R.id.iv_shoucang_pack_bg);
            holder.pack_title = (TextView) convertView.findViewById(R.id.textView_shoucang_pack_title);
            holder.createtime = (TextView) convertView.findViewById(R.id.textView_shoucang_pack_createtime);
            holder.book = (TextView) convertView.findViewById(R.id.textView_shoucang_pack_book);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pack_title.setText(list.get(position).packInfo.title);
        holder.book.setText(list.get(position).packInfo.book_title);
        holder.createtime.setText("周" + list.get(position).packInfo.getPub_time_week() + list.get(position).packInfo.timetype_tostring());

        DisplayImageUtils.displayImage(list.get(position).packInfo.top_img, holder.bg, 0, R.drawable.zanwufengmian);

        return convertView;
    }

    public static class ViewHolder {
        public ImageView bg;
        public TextView pack_title, createtime, book;
    }

}
