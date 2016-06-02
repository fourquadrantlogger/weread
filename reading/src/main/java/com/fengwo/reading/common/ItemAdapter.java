package com.fengwo.reading.common;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengwo.reading.R;

import java.util.List;

public class ItemAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;

	public ItemAdapter(Context context, List<String> list) {
		super();
		this.context = context;
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
	public View getView(final int position, View convertView,
			ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_popupwindow, parent, false);
			holder.textView = (TextView) convertView
					.findViewById(R.id.tv_item_popupwindow);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText(list.get(position));
		if (TextUtils.isEmpty(list.get(position))) {
			holder.textView.setVisibility(View.GONE);
		}else {
			holder.textView.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	private static class ViewHolder {
		private TextView textView;
	}

}