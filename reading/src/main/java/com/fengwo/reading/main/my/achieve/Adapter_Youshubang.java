package com.fengwo.reading.main.my.achieve;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.main.my.RankBean;
import com.fengwo.reading.utils.DisplayImageUtils;

import java.util.List;

public class Adapter_Youshubang extends BaseAdapter {
    private Fragment fromFragment;
    private List<RankBean> list;

    private String rank;

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Adapter_Youshubang(Fragment fromFragment, List<RankBean> list) {
        super();
        this.fromFragment = fromFragment;
        this.list = list;

        rank = "0";
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
            convertView = LayoutInflater.from(fromFragment.getActivity()).inflate(R.layout.item_youshubang, parent, false);
            holder.rl_item_achieve_layout = (RelativeLayout) convertView.findViewById(R.id.rl_item_achieve_layout);
            holder.tv_item_achieve_rank = (TextView) convertView.findViewById(R.id.tv_item_achieve_rank);
            holder.tv_item_achieve_info = (TextView) convertView.findViewById(R.id.tv_item_achieve_info);
            holder.tv_item_achieve_time = (TextView) convertView.findViewById(R.id.tv_item_achieve_time);
            holder.tv_item_achieve_name = (TextView) convertView.findViewById(R.id.tv_item_achieve_name);
            holder.iv_item_achieve_avatar = (ImageView) convertView.findViewById(R.id.iv_item_achieve_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.tv_item_achieve_info.setText("第" + rank + "名");
            holder.tv_item_achieve_rank.setText("");
            holder.tv_item_achieve_name.setTextColor(fromFragment.getActivity().getResources().getColor(R.color.green));
            holder.tv_item_achieve_rank.setTextColor(fromFragment.getActivity().getResources().getColor(R.color.green));
            holder.tv_item_achieve_time.setTextColor(fromFragment.getActivity().getResources().getColor(R.color.green));
            holder.rl_item_achieve_layout.setBackgroundColor(fromFragment.getActivity().getResources().getColor(R.color.green_ec));
        } else {
            holder.tv_item_achieve_info.setText("");
            holder.tv_item_achieve_rank.setText(position + "");
            holder.tv_item_achieve_name.setTextColor(fromFragment.getActivity().getResources().getColor(R.color.text_23));
            holder.tv_item_achieve_rank.setTextColor(fromFragment.getActivity().getResources().getColor(R.color.text_23));
            holder.tv_item_achieve_time.setTextColor(fromFragment.getActivity().getResources().getColor(R.color.text_23));
            holder.rl_item_achieve_layout.setBackgroundColor(fromFragment.getActivity().getResources().getColor(R.color.white));
        }

        if (list.get(position).user_data == null) {
            holder.iv_item_achieve_avatar.setImageResource(R.drawable.avatar);
            holder.tv_item_achieve_name.setText("");
        } else {
            DisplayImageUtils.displayImage(list.get(position).user_data.avatar, holder.iv_item_achieve_avatar, 100, R.drawable.avatar);
            holder.tv_item_achieve_name.setText(list.get(position).user_data.name);
//			holder.tv_item_achieve_name.setCompoundDrawables(null,null,list.get(position).user_data.badge_Drawable(0.12f, 0.12f),null);
        }

        holder.tv_item_achieve_time.setText(list.get(position).r + "");

        //跳转  他人主页
        holder.rl_item_achieve_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = fromFragment.getActivity()
                        .getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right,
                        R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                transaction.addToBackStack(null);
                transaction.replace(R.id.ll_activity_next,
                        OtherUserFragment.getInstance());
                transaction.commit();
                OtherUserFragment.getInstance().source = 7;
                OtherUserFragment.getInstance().needSaveView = false;
                OtherUserFragment.getInstance().ta_user_id = list.get(position).user_data.user_id;
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        private RelativeLayout rl_item_achieve_layout;
        private TextView tv_item_achieve_rank, tv_item_achieve_name, tv_item_achieve_info, tv_item_achieve_time;
        private ImageView iv_item_achieve_avatar;
    }

}