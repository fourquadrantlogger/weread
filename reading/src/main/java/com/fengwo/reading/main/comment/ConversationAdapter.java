package com.fengwo.reading.main.comment;

import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.EmojiUtils;

import java.util.List;

/**
 * 
 * @author lxq
 * 
 */
public class ConversationAdapter extends BaseAdapter {

	private Fragment fragment;
	private List<CommentTalkBean> list;
	private UserInfoBean meuser;
	private UserInfoBean tauser;

	public ConversationAdapter(Fragment fragment, List<CommentTalkBean> list,UserInfoBean meuser,UserInfoBean tauser) {
		super();
		this.fragment = fragment;
		this.list = list;
		this.meuser = meuser;
		this.tauser = tauser;
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
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return list.get(position).type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			if (type == 0) {
				convertView = LayoutInflater.from(fragment.getActivity()).inflate(
						R.layout.item_conversation_right, parent, false);
			}else {
				convertView = LayoutInflater.from(fragment.getActivity()).inflate(
						R.layout.item_conversation_left, parent, false);
			}
			holder.iv_item_questiontalk_avatar = (ImageView) convertView
					.findViewById(R.id.iv_item_questiontalk_avatar);
			holder.tv_item_questiontalk_content = (TextView) convertView
					.findViewById(R.id.tv_item_questiontalk_content);
			holder.ll_item_questiontalk_layout = (LinearLayout) convertView
					.findViewById(R.id.ll_item_questiontalk_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}


		if (tauser.user_id.equals(list.get(position).user_id)) {
			DisplayImageUtils.displayImage(meuser.avatar, holder.iv_item_questiontalk_avatar, 100, R.drawable.avatar);
		}else {
			DisplayImageUtils.displayImage(tauser.avatar, holder.iv_item_questiontalk_avatar, 100, R.drawable.avatar);
		}

		//表情
		Spannable span = EmojiUtils.getSmiledText(
				fragment.getActivity(),
				list.get(position).content);
		holder.tv_item_questiontalk_content.setText(span,
				TextView.BufferType.SPANNABLE);

		return convertView;
	}
	
	private static class ViewHolder {
		private ImageView iv_item_questiontalk_avatar;
		private TextView tv_item_questiontalk_content;
		private LinearLayout ll_item_questiontalk_layout;
	}

}