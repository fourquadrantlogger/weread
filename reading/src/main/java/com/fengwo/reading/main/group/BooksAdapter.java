package com.fengwo.reading.main.group;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.my.UserReadBean;

import java.util.List;

import java.util.List;
public class BooksAdapter extends BaseAdapter {

	private Fragment fromFragment;
	private List<UserReadBean> list;
	private String book_id;

	public BooksAdapter(Fragment fromFragment, List<UserReadBean> list,
			String book_id) {
		super();
		this.fromFragment = fromFragment;
		this.list = list;
		this.book_id = book_id;
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
					.inflate(R.layout.item_books, parent, false);
			holder.tv_books_bookname = (TextView) convertView
					.findViewById(R.id.tv_books_bookname);
			holder.iv_books_gou = (ImageView) convertView
					.findViewById(R.id.iv_books_gou);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 共读书名
		holder.tv_books_bookname.setText("《" + list.get(position).book_title
				+ "》");
		if (!TextUtils.isEmpty(book_id)) {
			if (book_id.equals(list.get(position).book_id)) {
				holder.iv_books_gou.setVisibility(View.VISIBLE);
			} else {
				holder.iv_books_gou.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	private static class ViewHolder {
		private TextView tv_books_bookname;
		private ImageView iv_books_gou;
	}

}
