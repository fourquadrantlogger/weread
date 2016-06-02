package com.fengwo.reading.main.group.qun;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.bean.UserInfoBean;
import com.fengwo.reading.main.group.GroupUserBean;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.utils.DisplayImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timeloveboy on 16/3/29.
 */
public class NameandFaceAdapter extends BaseAdapter {
    private Fragment fromFragment;
    private List<UserInfoBean> list;

    public NameandFaceAdapter(Fragment fromFragment, List<GroupUserBean> list, boolean nothing) {
        super();
        this.fromFragment = fromFragment;
        this.list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            UserInfoBean userInfoBean = new UserInfoBean();
            userInfoBean.user_id = list.get(i).user_id;
            userInfoBean.name = list.get(i).name;
            userInfoBean.avatar = list.get(i).avatar;

            this.list.add(userInfoBean);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(fromFragment.getActivity()).inflate(R.layout.item_nameandface, parent, false);

            holder.tv_item_user_name = (TextView) convertView.findViewById(R.id.tv_item_user_name);
            holder.iv_item_user_face = (ImageView) convertView.findViewById(R.id.iv_item_user_face);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_item_user_name.setText(list.get(position).name);

//        holder.tv_item_user_name.setCompoundDrawables(null, null, list.get(position).badge_Drawable(), null);

        if (list.get(position).avatar != null) {
            DisplayImageUtils.displayImage(list.get(position).avatar, holder.iv_item_user_face, 100, R.drawable.avatar);
        } else {
            DisplayImageUtils.displayImage("http://avatarimg.fengwo.com/readwith/20160327/56f7bda76cb2b.jpg@170w_170h.jpg", holder.iv_item_user_face, 100, R.drawable.avatar);
        }

        holder.iv_item_user_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOther(position);
            }
        });
        holder.tv_item_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOther(position);
            }
        });

        return convertView;
    }

    /**
     * 跳转 他人主页
     */
    public void goOther(int position) {
        FragmentTransaction transaction = fromFragment.getActivity()
                .getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right,
                R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.ll_activity_next,
                OtherUserFragment.getInstance());
        transaction.commit();
        OtherUserFragment.getInstance().source = 2;
        OtherUserFragment.getInstance().needSaveView = false;
        OtherUserFragment.getInstance().ta_user_id = list.get(position).user_id;
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
        private TextView tv_item_user_name;
        private ImageView iv_item_user_face;
    }
}
