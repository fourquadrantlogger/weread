package com.fengwo.reading.main.group.qun;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.main.my.RankBean;
import com.fengwo.reading.utils.DisplayImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timeloveboy on 16/3/28.
 */
public class QunRankAdapter extends BaseAdapter {
    private Fragment fromFragment;
    private List<RankBean> list;

    public QunRankAdapter(Fragment fromFragment, List<RankBean> list) {
        super();
        this.fromFragment = fromFragment;
        this.list = list;
    }

    //群间 排行
    public QunRankAdapter(Fragment fromFragment, List<groupRankInfoJson.Group> groupList, boolean no_use_arg) {
        super();
        this.fromFragment = fromFragment;
        this.list = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            RankBean rankBean = new RankBean();
            rankBean.num = i + 1;
            rankBean.rankscore = groupList.get(i).score;
            rankBean.user_data = new UserInfoBean();

            rankBean.user_data.name = groupList.get(i).group_name;
            list.add(rankBean);
        }
    }

    //群内 排行
    public QunRankAdapter(Fragment fromFragment, List<groupMemRankJson.GroupUser> groupList, int no_use_arg) {
        super();
        this.fromFragment = fromFragment;
        this.list = new ArrayList<>();
        for (int i = 0; i < groupList.size(); i++) {
            RankBean rankBean = new RankBean();
            rankBean.num = groupList.get(i).num;
            rankBean.rankscore = groupList.get(i).score;
            rankBean.user_data = new UserInfoBean();
            rankBean.user_data.avatar = groupList.get(i).avatar;
            rankBean.user_data.name = groupList.get(i).name;
            list.add(rankBean);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(fromFragment.getActivity()).inflate(R.layout.item_rank, parent, false);

            holder.tv_item_qun_rank_index = (TextView) convertView.findViewById(R.id.tv_item_rank_index);
            holder.tv_item_qun_rank_username = (TextView) convertView.findViewById(R.id.tv_item_rank_username);
            holder.tv_item_qun_rank_score = (TextView) convertView.findViewById(R.id.tv_item_rank_score);
            holder.iv_item_qun_rank_userface = (ImageView) convertView.findViewById(R.id.iv_item_rank_userface);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_item_qun_rank_index.setText(list.get(position).num + "");
        holder.tv_item_qun_rank_username.setText(list.get(position).user_data.name);
//        holder.tv_item_qun_rank_username.setCompoundDrawables(null, null, list.get(position).user_data.badge_Drawable(0.12f, 0.12f), null);

        holder.tv_item_qun_rank_score.setText(list.get(position).rankscore + "");


        if (list.get(position).user_data.avatar != null) {
            DisplayImageUtils.displayImage(list.get(position).user_data.avatar, holder.iv_item_qun_rank_userface, 100, R.drawable.avatar);
        } else {
            holder.iv_item_qun_rank_userface.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    private static class ViewHolder {
        private TextView tv_item_qun_rank_index, tv_item_qun_rank_score, tv_item_qun_rank_username;
        private ImageView iv_item_qun_rank_userface;
    }

}
