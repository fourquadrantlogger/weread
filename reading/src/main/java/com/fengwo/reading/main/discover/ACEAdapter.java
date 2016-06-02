package com.fengwo.reading.main.discover;

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

import java.util.List;

public class ACEAdapter extends BaseAdapter {

    private Fragment fromFragment;
    private List<ACEBean> list;

    public ACEAdapter(Fragment fromFragment, List<ACEBean> list) {
        super();
        this.fromFragment = fromFragment;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(fromFragment.getActivity())
                    .inflate(R.layout.item_discover_daren, parent, false);
            holder.tv_discover_daren_name = (TextView) convertView
                    .findViewById(R.id.tv_discover_daren_name);
            holder.tv_discover_daren_content = (TextView) convertView
                    .findViewById(R.id.tv_discover_daren_content);
            holder.iv_discover_daren_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_discover_daren_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(list.get(position) == null){

        }else{
            if(!TextUtils.isEmpty(list.get(position).name)){
                holder.tv_discover_daren_name.setText(list.get(position).name);
            }else{
                holder.tv_discover_daren_name.setText("匿名");
            }

            if(!TextUtils.isEmpty(list.get(position).intro)){
                holder.tv_discover_daren_content.setText(list.get(position).intro);
            }else{
                holder.tv_discover_daren_content.setText("还没有填写签名哦");
            }

            DisplayImageUtils.displayImage(list.get(position).avatar,
                    holder.iv_discover_daren_avatar, 100, R.drawable.zanwufengmian);
        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_discover_daren_name, tv_discover_daren_content;
        private ImageView iv_discover_daren_avatar;
    }

}
