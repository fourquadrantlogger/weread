package com.fengwo.reading.main.discover.hottopics;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fengwo.reading.R;

import java.util.List;

public class LabelAdapter extends BaseAdapter {

	private Fragment fromFragment;
	private List<String> list;

	public LabelAdapter(Fragment fromFragment, List<String> list) {
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
					.inflate(R.layout.item_label, parent, false);
			holder.tv_item_addlabel_label = (TextView) convertView
					.findViewById(R.id.tv_item_addlabel_label);
			holder.v_item_addlabel_line = (View) convertView
					.findViewById(R.id.v_item_addlabel_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_item_addlabel_label.setText(list.get(position));
		if (position == list.size() - 1) {
			holder.v_item_addlabel_line.setVisibility(View.GONE);
		} else {
			holder.v_item_addlabel_line.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

	private static class ViewHolder {
		private TextView tv_item_addlabel_label;
		private View v_item_addlabel_line;
	}

}