package com.fengwo.reading.main.discover;

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

public class ChoicenessBooksAdapter extends BaseAdapter {

    private Fragment fromFragment;
    private List<ChoicenessBooksBean> list;

    public ChoicenessBooksAdapter(Fragment fromFragment, List<ChoicenessBooksBean> list) {
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
                    .inflate(R.layout.item_discover_books, parent, false);
            holder.tv_discover_books_title = (TextView) convertView
                    .findViewById(R.id.tv_discover_books_title);
            holder.tv_discover_books_num = (TextView) convertView
                    .findViewById(R.id.tv_discover_books_num);
            holder.iv_discover_books_img = (ImageView) convertView
                    .findViewById(R.id.iv_discover_books_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_discover_books_title.setText(list.get(position).book_title);
        holder.tv_discover_books_num.setText("共"+list.get(position).num+"篇精选");

        DisplayImageUtils.displayImage(list.get(position).chosen_img,
                holder.iv_discover_books_img, 0, R.color.bg);

        return convertView;
    }

    private static class ViewHolder {
        private TextView tv_discover_books_title, tv_discover_books_num;
        private ImageView iv_discover_books_img;
    }

}
