package com.fengwo.reading.main.my.achieve;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.utils.DateUtils;

import java.util.List;

/**
 * Created by timeloveboy on 16/4/21.
 */
public class Adapter_jingyanzhi extends BaseAdapter {
    private Fragment fromFragment;
    private List<Json_jingyanzhi> list;

    public Adapter_jingyanzhi(Fragment fromFragment, List<Json_jingyanzhi> list) {
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
            convertView = LayoutInflater.from(fromFragment.getActivity()).inflate(R.layout.item_jingyanzhi, parent, false);

            holder.textView_jingyanzhi_item_type = (TextView) convertView.findViewById(R.id.textView_jingyanzhi_item_type);
            holder.textView_jingyanzhi_item_create_time = (TextView) convertView.findViewById(R.id.textView_jingyanzhi_item_create_time);
            holder.textView_jingyanzhi_item_day_exp = (TextView) convertView.findViewById(R.id.textView_jingyanzhi_item_day_exp);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView_jingyanzhi_item_type.setText(list.get(position).type);
        holder.textView_jingyanzhi_item_create_time.setText(DateUtils.getTime(list.get(position).create_time));
        holder.textView_jingyanzhi_item_day_exp.setText("+" + list.get(position).exp_val);

        return convertView;
    }

    private static class ViewHolder {

        private TextView textView_jingyanzhi_item_type, textView_jingyanzhi_item_create_time, textView_jingyanzhi_item_day_exp;
    }

    public class Json_jingyanzhi {
        public String type;
        public String create_time;
        public String exp_val;
    }
}
