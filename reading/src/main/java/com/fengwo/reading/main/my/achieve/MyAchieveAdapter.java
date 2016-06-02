package com.fengwo.reading.main.my.achieve;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;

import java.util.List;

public class MyAchieveAdapter extends BaseAdapter {
    private Fragment fromFragment;
    private List<TaskBean> list;

    public MyAchieveAdapter(Fragment fromFragment, List<TaskBean> list) {
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
                    .inflate(R.layout.item_achieve, parent, false);
            holder.rl_item_achieve_rl = (RelativeLayout) convertView
                    .findViewById(R.id.rl_item_achieve_rl);
            holder.tv_item_achieve_title = (TextView) convertView
                    .findViewById(R.id.tv_item_achieve_title);
            holder.tv_item_achieve_num = (TextView) convertView
                    .findViewById(R.id.tv_item_achieve_num);
            holder.tv_item_achieve_ok = (TextView) convertView
                    .findViewById(R.id.tv_item_achieve_ok);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position % 2 == 0) {
            holder.rl_item_achieve_rl.setBackgroundResource(R.color.white);
        } else {
            holder.rl_item_achieve_rl.setBackgroundResource(R.color.bg_fa);
        }

        holder.tv_item_achieve_title.setText(list.get(position).show);

        if ("save_info".equals(list.get(position).type)) {
            holder.tv_item_achieve_num.setText("+" + list.get(position).add_exp + "经验值");
        } else if ("week_book".equals(list.get(position).type)) {
            holder.tv_item_achieve_num.setText("+" + list.get(position).add_exp + "经验值");
        } else {
            holder.tv_item_achieve_num.setText("+" + list.get(position).add_exp + "经验值 /次");
        }

        if (list.get(position).cnum.equals(list.get(position).sum)) {
            Drawable drawable = fromFragment.getActivity().getResources().getDrawable(
                    R.drawable.myachieve_finish);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            holder.tv_item_achieve_title.setCompoundDrawables(drawable, null, null, null);
            holder.tv_item_achieve_title.setTextColor(fromFragment.getActivity().getResources()
                    .getColor(R.color.text_99));
            if ("save_info".equals(list.get(position).type)) {
                holder.tv_item_achieve_ok.setText("100%");
            } else {
                holder.tv_item_achieve_ok.setText(list.get(position).cnum + "/" + list.get(position).sum);
            }
        } else {
            holder.tv_item_achieve_title.setCompoundDrawables(null, null, null, null);
            holder.tv_item_achieve_title.setTextColor(fromFragment.getActivity().getResources()
                    .getColor(R.color.text_32));
            if ("save_info".equals(list.get(position).type)) {
                holder.tv_item_achieve_ok.setVisibility(View.VISIBLE);
                int exp1 = Integer.valueOf(list.get(position).cnum).intValue() * 20;
                holder.tv_item_achieve_ok.setText("已完成\t" + exp1 + "%");
            } else if ("note_add".equals(list.get(position).type)) {
                holder.tv_item_achieve_ok.setVisibility(View.GONE);
            } else if ("week_book".equals(list.get(position).type)) {
                holder.tv_item_achieve_ok.setVisibility(View.VISIBLE);
                holder.tv_item_achieve_ok.setText("已完成\t" + list.get(position).cnum + "/" + list.get(position).sum);
            } else {
                holder.tv_item_achieve_ok.setVisibility(View.VISIBLE);
                holder.tv_item_achieve_ok.setText(list.get(position).cnum + "/" + list.get(position).sum);
            }
        }

        if ("save_info".equals(list.get(position).type)) {

        } else {

        }
        return convertView;
    }

    private static class ViewHolder {
        private RelativeLayout rl_item_achieve_rl;
        private TextView tv_item_achieve_title, tv_item_achieve_num, tv_item_achieve_ok;
    }

}