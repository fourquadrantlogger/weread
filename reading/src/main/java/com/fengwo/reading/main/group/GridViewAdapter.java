package com.fengwo.reading.main.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fengwo.reading.R;
import com.fengwo.reading.utils.DisplayImageUtils;

public class GridViewAdapter extends BaseAdapter {
	private Context context;
	private String[] strings;
	private int width;

	public GridViewAdapter(Context context, String[] strings, int width) {
		super();
		this.context = context;
		this.strings = strings;
		this.width = width;
	}

	@Override
	public int getCount() {
		if (strings == null) {
			return 0;
		} else {
			return this.strings.length;
		}
	}

	@Override
	public Object getItem(int position) {
		if (strings == null) {
			return null;
		} else {
			return this.strings[position];
		}
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
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_group_gridview, null, false);
			holder.iv_group_gridview = (ImageView) convertView
					.findViewById(R.id.iv_group_gridview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (this.strings != null) {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					width, width);
			holder.iv_group_gridview.setLayoutParams(params);
			DisplayImageUtils.displayImage(strings[position],
					holder.iv_group_gridview, 0, R.drawable.youshu);
			// holder.iv_group_gridview.setOnClickListener(new OnClickListener()
			// {
			// @Override
			// public void onClick(View v) {
			//
			// }
			// });
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView iv_group_gridview;
	}

}