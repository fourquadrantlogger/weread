package com.fengwo.reading.main.discover.hottopics;

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

public class HotAdapter extends BaseAdapter {

	private Fragment fromFragment;
	private List<HotListBean> list;

	public HotAdapter(Fragment fromFragment, List<HotListBean> list) {
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
					.inflate(R.layout.item_hot, parent, false);
			holder.iv_item_hottopics_cover = (ImageView) convertView
					.findViewById(R.id.iv_item_hottopics_cover);
			holder.tv_item_hottopics_title = (TextView) convertView
					.findViewById(R.id.tv_item_hottopics_title);
			holder.tv_item_hottopics_count = (TextView) convertView
					.findViewById(R.id.tv_item_hottopics_count);
			holder.tv_item_hottopics_content = (TextView) convertView
					.findViewById(R.id.tv_item_hottopics_content);
			holder.v_item_hottopics_line = (View) convertView
					.findViewById(R.id.v_item_hottopics_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DisplayImageUtils.loadImage(list.get(position).img,
				holder.iv_item_hottopics_cover,fromFragment.getActivity());
		holder.tv_item_hottopics_title.setText(list.get(position).topic_title);
		holder.tv_item_hottopics_count.setText(list.get(position).join_nums+"人参加");
		holder.tv_item_hottopics_content.setText(list.get(position).topic_content);
		if (position == list.size() - 1) {
			holder.v_item_hottopics_line.setVisibility(View.GONE);
		} else {
			holder.v_item_hottopics_line.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

	private static class ViewHolder {
		private ImageView iv_item_hottopics_cover;
		private TextView tv_item_hottopics_title, tv_item_hottopics_count,
				tv_item_hottopics_content;
		private View v_item_hottopics_line;
	}

}