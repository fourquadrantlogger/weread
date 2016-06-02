package com.fengwo.reading.main.comment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.group.GroupDetailsFragment;
import com.fengwo.reading.main.group.GroupFragment;
import com.fengwo.reading.main.group.OtherUserFragment;
import com.fengwo.reading.utils.DateUtils;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.EmojiUtils;
import com.fengwo.reading.utils.VipImageUtil;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private Fragment fromFragment;
    private List<CommentBean> list;

    public CommentAdapter(Fragment fromFragment, List<CommentBean> list) {
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
                    .inflate(R.layout.item_layout_comment, parent, false);
            holder.rl_item_comment_all = (RelativeLayout) convertView
                    .findViewById(R.id.rl_item_comment_all);
            holder.iv_item_comment_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_item_comment_avatar);
            holder.iv_item_comment_sex = (ImageView) convertView
                    .findViewById(R.id.iv_item_comment_sex);
            holder.tv_item_comment_name = (TextView) convertView
                    .findViewById(R.id.tv_item_comment_name);
            holder.tv_item_comment_time = (TextView) convertView
                    .findViewById(R.id.tv_item_comment_time);
            holder.tv_item_comment_huifu = (TextView) convertView
                    .findViewById(R.id.tv_item_comment_huifu);
            holder.tv_item_comment_content = (TextView) convertView
                    .findViewById(R.id.tv_item_comment_content);
            holder.rl_item_comment_zan = (RelativeLayout) convertView
                    .findViewById(R.id.rl_item_comment_zan);
            holder.tv_item_comment_zan = (TextView) convertView
                    .findViewById(R.id.tv_item_comment_zan);
            holder.tv_item_comment_null = (TextView) convertView
                    .findViewById(R.id.tv_item_comment_null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (list.get(position).user_data == null) {
            holder.tv_item_comment_null.setVisibility(View.VISIBLE);
            holder.rl_item_comment_all.setVisibility(View.GONE);
        } else {
            holder.tv_item_comment_null.setVisibility(View.GONE);
            holder.rl_item_comment_all.setVisibility(View.VISIBLE);

            if (list.get(position).user_data != null) {
                DisplayImageUtils.displayImage(
                        list.get(position).user_data.avatar,
                        holder.iv_item_comment_avatar, 100, R.drawable.avatar);

                holder.tv_item_comment_name.setText(list.get(position).user_data.name);
//            holder.tv_item_comment_name.setCompoundDrawables(null, null, list.get(position).user_data.badge_Drawable(0.12f, 0.12f), null);
                switch (list.get(position).user_data.sex) {
                    case "1":
                        holder.iv_item_comment_sex
                                .setImageResource(R.drawable.myinfo_nan);
                        break;
                    case "2":
                        holder.iv_item_comment_sex
                                .setImageResource(R.drawable.myinfo_nv);
                        break;
                }
                if (!TextUtils.isEmpty(list.get(position).user_data.level)) {
                    int i = Integer.valueOf(list.get(position).user_data.level).intValue();
                    VipImageUtil.getVipGrade(fromFragment.getActivity(), holder.iv_item_comment_sex, i, 1);
                } else {
                    holder.iv_item_comment_sex.setVisibility(View.GONE);
                }
            } else {
                holder.iv_item_comment_avatar.setImageResource(R.drawable.avatar);
                holder.tv_item_comment_name.setText("");
            }
            // 发布时间
            holder.tv_item_comment_time
                    .setText(DateUtils.getTime((list.get(position).create_time)));
            //是否是回复
            if ("1".equals(list.get(position).comment_type)) {
                holder.tv_item_comment_huifu.setVisibility(View.VISIBLE);

                String name = list.get(position).reply_user_data.name + ":";
                String content = name + list.get(position).re_content;
                ForegroundColorSpan redSpan = new ForegroundColorSpan(fromFragment.getActivity().getResources().getColor(R.color.green_17));
                Spannable spannable = EmojiUtils.getSmiledText(
                        fromFragment.getActivity(), content);
                spannable.setSpan(redSpan, 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tv_item_comment_huifu.setText(spannable, TextView.BufferType.SPANNABLE);
            } else {
                holder.tv_item_comment_huifu.setVisibility(View.GONE);
            }

            //正文(表情)
            holder.tv_item_comment_content.setText(EmojiUtils.getSmiledText(
                            fromFragment.getActivity(),
                            list.get(position).content),
                    TextView.BufferType.SPANNABLE);
            // 点赞数
            holder.tv_item_comment_zan.setText(list.get(position).digg_count);
            // 我是否点赞过
            if ("1".equals(list.get(position).is_digg)) {
                Drawable drawable = fromFragment.getActivity().getResources().getDrawable(
                        R.drawable.comment_zan_hou);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                holder.tv_item_comment_zan
                        .setCompoundDrawables(drawable, null, null, null);
                holder.tv_item_comment_zan.setTextColor(fromFragment.getResources()
                        .getColor(R.color.zan_text_color));
            } else {
                Drawable drawable = fromFragment.getActivity().getResources().getDrawable(
                        R.drawable.comment_zan);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                holder.tv_item_comment_zan
                        .setCompoundDrawables(drawable, null, null, null);
                holder.tv_item_comment_zan.setTextColor(fromFragment.getResources()
                        .getColor(R.color.text_98));
            }
            //点赞的点击
            holder.rl_item_comment_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupDetailsFragment.getInstance().dianzan(position);
                }
            });

            //他人头像
            holder.iv_item_comment_avatar.setOnClickListener(new View.OnClickListener() {
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
                    OtherUserFragment.getInstance().source = 2;
                    OtherUserFragment.getInstance().ta_user_id = list.get(position).user_data.user_id;
                    OtherUserFragment.getInstance().needSaveView = false;
                }
            });
        }
        // 记录位置
        holder.tv_item_comment_zan.setTag("dianzan_tv" + position);
//        holder.tv_item_comment_pinglun.setTag("pinglun" + position);
        return convertView;
    }

    private void setText(TextView textView, String text1, String changetext,
                         String text2, Context context, int color) {
        SpannableString ss = new SpannableString(text1 + changetext + text2);
        ss.setSpan(
                //设置TextView前景色
                new ForegroundColorSpan(context.getResources().getColor(color)),
                text1.length(), text1.length() + changetext.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
    }

    private static class ViewHolder {
        private RelativeLayout rl_item_comment_zan, rl_item_comment_all;
        private ImageView iv_item_comment_avatar, iv_item_comment_sex;
        private TextView tv_item_comment_name, tv_item_comment_time,
                tv_item_comment_huifu, tv_item_comment_content, tv_item_comment_zan, tv_item_comment_null;
    }

}