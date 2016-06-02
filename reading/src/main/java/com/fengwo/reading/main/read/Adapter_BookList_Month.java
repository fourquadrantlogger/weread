package com.fengwo.reading.main.read;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.view.MyListView;

import java.util.List;

public class Adapter_BookList_Month extends BaseAdapter {

    private Fragment fromFragment;
    private List<Bean_BookList> list;

    public Adapter_BookList_Month(Fragment fromFragment, List<Bean_BookList> list) {
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
                    .inflate(R.layout.fragment_partbooks, parent, false);
            holder.tv_partbooks_month = (TextView) convertView
                    .findViewById(R.id.tv_partbooks_month);
            holder.lv_partbooks = (MyListView) convertView
                    .findViewById(R.id.lv_partbooks);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).data.get(0).start_time != null) {
            String str = list.get(position).data.get(0).start_time.substring(0, 4);
            holder.tv_partbooks_month.setText(str + "年" + list.get(position).mouth + "月");
        } else {
            holder.tv_partbooks_month.setText(list.get(position).mouth + "月");
        }

        Adapter_BookList detailsAdapter = new Adapter_BookList(fromFragment, list.get(position).data);
        holder.lv_partbooks.setAdapter(detailsAdapter);

        return convertView;

    }

    private static class ViewHolder {
        private TextView tv_partbooks_month;
        private MyListView lv_partbooks;
    }



}
