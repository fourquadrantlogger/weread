package com.fengwo.reading.main.my.achieve;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;


import com.fengwo.reading.R;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.ImageUtils;
import com.fengwo.reading.utils.TimeUtil;


import java.util.List;

/**
 * Created by timeloveboy on 16/5/4.
 */
public class Adapter_wodexunzhang extends BaseAdapter {

    public Adapter_wodexunzhang(Fragment fromFragment, List<Xunzhang> list) {
        this.fromFragment = fromFragment;
        this.list = list;
    }

    private Fragment fromFragment;
    private List<Xunzhang> list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(fromFragment.getActivity()).inflate(R.layout.item_wodexunzhang, parent, false);
            holder.imageView_item_wodexunzhang=(ImageView)convertView.findViewById(R.id.imageView_item_wodexunzhang);
            holder.textView_item_wodexunzhang_title=(TextView)convertView.findViewById(R.id.textView_item_wodexunzhang_title);
            holder.textView_item_wodexunzhang_createtime=(TextView)convertView.findViewById(R.id.textView_item_wodexunzhang_createtime);
            holder.item_wodexunzhang_choose=(ImageView)convertView.findViewById(R.id.item_wodexunzhang_choose);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Bitmap bitmapsource= ImageUtils.getLoacalBitmap(list.get(position).localPath());
        if(!list.get(position).got) {
            Bitmap bitmap=ImageUtils.toGray(bitmapsource);
            holder.imageView_item_wodexunzhang.setImageBitmap(bitmap);
            holder.textView_item_wodexunzhang_title.setTextColor(Color.parseColor("#646464"));
            holder.textView_item_wodexunzhang_createtime.setText("");
        }else {
            holder.imageView_item_wodexunzhang.setImageBitmap(bitmapsource);
            holder.textView_item_wodexunzhang_createtime.setText(list.get(position).create_time.substring(0, 10));
        }
        holder.textView_item_wodexunzhang_title.setText(list.get(position).badge);
        holder.item_wodexunzhang_choose.setVisibility(View.INVISIBLE);
        if(GlobalParams.userInfoBean.badge_id.equals(list.get(position).id)){
            holder.item_wodexunzhang_choose.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView textView_item_wodexunzhang_title, textView_item_wodexunzhang_createtime;
        private ImageView imageView_item_wodexunzhang,item_wodexunzhang_choose;
    }
}
