package com.fengwo.reading.main.discover.hottopics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.group.GridViewAdapter;
import com.fengwo.reading.qq.ImageBrowserActivity;
import com.fengwo.reading.utils.DateUtils;
import com.fengwo.reading.utils.DisplayImageUtils;
import com.fengwo.reading.utils.ListUtils;
import com.fengwo.reading.utils.VipImageUtil;

import java.util.List;

public class TopicsAdapter extends BaseAdapter {

    private Context context;
    private List<TopicsBean> list;
    private String topic_title = "";// 话题名称,不带#

    public TopicsAdapter(Context context, List<TopicsBean> list,
                         String topic_title) {
        super();
        this.context = context;
        this.list = list;
        this.topic_title = topic_title;
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
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_group, parent, false);
            holder.rl_group_other = (RelativeLayout) convertView
                    .findViewById(R.id.rl_group_other);
            holder.rl_group_pinglun = (RelativeLayout) convertView
                    .findViewById(R.id.rl_group_pinglun);
            holder.rl_group_fenxiang = (RelativeLayout) convertView
                    .findViewById(R.id.rl_group_fenxiang);
            holder.rl_group_dianzan = (RelativeLayout) convertView
                    .findViewById(R.id.rl_group_dianzan);
            holder.relativeLayout = (RelativeLayout) convertView
                    .findViewById(R.id.relativeLayout1);
            holder.ll_group_details = (LinearLayout) convertView
                    .findViewById(R.id.ll_group_details);
            holder.iv_group_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_group_avatar);
            holder.iv_group_sex = (ImageView) convertView
                    .findViewById(R.id.iv_group_sex);
            holder.iv_group_down = (ImageView) convertView
                    .findViewById(R.id.iv_group_down);
            holder.gv_group_gridview = (GridView) convertView
                    .findViewById(R.id.gv_group_gridview);
            holder.tv_group_bookname = (TextView) convertView
                    .findViewById(R.id.tv_group_bookname);
            holder.tv_group_name = (TextView) convertView
                    .findViewById(R.id.tv_group_name);
            holder.tv_group_time = (TextView) convertView
                    .findViewById(R.id.tv_group_time);
            holder.tv_group_title = (TextView) convertView
                    .findViewById(R.id.tv_group_title);
            holder.tv_group_content = (TextView) convertView
                    .findViewById(R.id.tv_group_content);
            holder.tv_group_pinglun = (TextView) convertView
                    .findViewById(R.id.tv_group_pinglun);
            holder.tv_group_zannum = (TextView) convertView
                    .findViewById(R.id.tv_group_zannum);
            holder.tv_group_pub = (TextView) convertView
                    .findViewById(R.id.tv_group_pub);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.iv_group_down.setVisibility(View.GONE);
        // 头像
        if (list.get(position).user_data != null) {
            if (!TextUtils.isEmpty(list.get(position).user_data.avatar)) {
                DisplayImageUtils.displayImage(
                        list.get(position).user_data.avatar,
                        holder.iv_group_avatar, 100, R.drawable.avatar);
            }
            // 等级
            if (!TextUtils.isEmpty(list.get(position).user_data.level)) {
                int i = Integer.valueOf(list.get(position).user_data.level).intValue();
                VipImageUtil.getVipGrade(context, holder.iv_group_sex, i, 1);
            } else {
                holder.iv_group_sex.setVisibility(View.GONE);
            }
            // 姓名
            if (!TextUtils.isEmpty(list.get(position).user_data.name)) {
                holder.tv_group_name.setText(list.get(position).user_data.name);
//                holder.tv_group_name.setCompoundDrawables(null,null,list.get(position).user_data.badge_Drawable(0.12f, 0.12f),null);
            } else {
                holder.tv_group_name.setText("");
            }
        }
        // 发布时间
        holder.tv_group_time
                .setText(DateUtils.getTime((list.get(position).create_time)));
        //书名
        if (!TextUtils.isEmpty(list.get(position).title)) {
            holder.tv_group_bookname.setText("《" + list.get(position).title + "》");
        } else {
            holder.tv_group_bookname.setText("");
        }
        // 标题
        if (!TextUtils.isEmpty(list.get(position).title)) {
            holder.tv_group_title.setVisibility(View.VISIBLE);
            holder.tv_group_title.setText(list.get(position).title);
        } else {
            holder.tv_group_title.setVisibility(View.GONE);
        }
        // 内容 #话题内容# 变色 点击
        if (list.get(position).content != null) {
            ListUtils.getNewTextView(context, list.get(position).content,
                    holder.tv_group_content);
        }
        // 附带图片
        if (list.get(position).img_str != null) {
            holder.gv_group_gridview.setVisibility(View.VISIBLE);
            DisplayMetrics metric = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay()
                    .getMetrics(metric);
            int width = (metric.widthPixels / 3);
            GridViewAdapter gridViewAdapter = new GridViewAdapter(context,
                    list.get(position).img_str, width);
            holder.gv_group_gridview.setAdapter(gridViewAdapter);
            holder.gv_group_gridview.setFocusable(false);
        } else {
            holder.gv_group_gridview.setVisibility(View.GONE);
        }
        // 评论数
        if (list.get(position).comment_count != null) {
            if (!list.get(position).comment_count.equals("0")) {
                holder.tv_group_pinglun
                        .setText(list.get(position).comment_count);
            } else {
                holder.tv_group_pinglun.setText("");
            }
        }
        // 点赞数
        if (list.get(position).digg_count != null) {
            if (!list.get(position).digg_count.equals("0")) {
                holder.tv_group_zannum.setText(list.get(position).digg_count);
            } else {
                holder.tv_group_zannum.setText("");
            }
        }
        // 是否点赞过
        if ("1".equals(list.get(position).is_digg)) {
            Drawable drawable = context.getResources().getDrawable(
                    R.drawable.comment_zan_hou);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            holder.tv_group_zannum
                    .setCompoundDrawables(drawable, null, null, null);
            holder.tv_group_zannum.setTextColor(context.getResources()
                    .getColor(R.color.zan_text_color));
        } else {
            Drawable drawable = context.getResources().getDrawable(
                    R.drawable.comment_zan);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                    drawable.getMinimumHeight());
            holder.tv_group_zannum
                    .setCompoundDrawables(drawable, null, null, null);
            holder.tv_group_zannum.setTextColor(context.getResources()
                    .getColor(R.color.text_98));
        }

        // 图片放大
        holder.gv_group_gridview
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int num, long id) {
                        // 采用 多图浏览Activity 模式
                        ImageBrowserActivity.position = num;
                        ImageBrowserActivity.mList = java.util.Arrays.asList(list.get(position).img_str_orignsize());
                        context.startActivity(new Intent(context, ImageBrowserActivity.class));

                    }
                });

        // 跳转他人主页的点击
        holder.rl_group_other.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TopicsActivity.Activity != null) {
                    TopicsActivity.Activity.goOther(position);
                }
            }
        });

        // 跳转讨论详情的点击
        holder.ll_group_details.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TopicsActivity.Activity != null) {
                    TopicsActivity.Activity.goDetails(position);
                }
            }
        });
        holder.tv_group_title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TopicsActivity.Activity != null) {
                    TopicsActivity.Activity.goDetails(position);
                }
            }
        });
        holder.tv_group_content.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TopicsActivity.Activity != null) {
                    TopicsActivity.Activity.goDetails(position);
                }
            }
        });

        // 分享的点击
        holder.rl_group_fenxiang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TopicsActivity.Activity != null) {
                    TopicsActivity.Activity.fenxiang(position);
                }
            }
        });

        // 评论的点击
        holder.rl_group_pinglun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TopicsActivity.Activity != null) {
                    TopicsActivity.Activity.goDetails(position);
                }
            }
        });

        // 点赞的点击
        holder.rl_group_dianzan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TopicsActivity.Activity != null) {
                    TopicsActivity.Activity.dianzan(position);
                }
            }
        });
        // 记录位置
        holder.tv_group_zannum.setTag("dianzan_tv" + position);
        holder.tv_group_pinglun.setTag("pinglun" + position);

        return convertView;
    }

    private static class ViewHolder {
        private RelativeLayout rl_group_other, rl_group_dianzan,
                rl_group_pinglun, rl_group_fenxiang, relativeLayout;
        private LinearLayout ll_group_details;
        private GridView gv_group_gridview;
        private ImageView iv_group_avatar, iv_group_sex, iv_group_down;
        private TextView tv_group_name, tv_group_time, tv_group_title,
                tv_group_content, tv_group_pinglun, tv_group_zannum, tv_group_bookname;
        // TODO
        private TextView tv_group_pub;
    }

}
