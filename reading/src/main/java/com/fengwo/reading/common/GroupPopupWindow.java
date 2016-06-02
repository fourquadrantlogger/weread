package com.fengwo.reading.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.main.group.GroupFragment;
import com.fengwo.reading.main.group.GroupJson;
import com.fengwo.reading.main.group.widget.Tag;
import com.fengwo.reading.main.group.widget.TagListView;

import java.util.ArrayList;
import java.util.List;

public class GroupPopupWindow extends PopupWindow {

    private TagListView mTagListView;
    private final List<Tag> mTags = new ArrayList<>();

    public GroupPopupWindow(Context context,
                            TagListView.OnTagClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_group, null);

        TextView tv_group_ysq = (TextView) mView.findViewById(R.id.tv_group_ysq);
        mTagListView = (TagListView) mView.findViewById(R.id.tagview);

        RelativeLayout relativeLayout = (RelativeLayout) mView
                .findViewById(R.id.rl_popupwindow_layout);
        relativeLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GroupFragment.getInstance().refresh3(0);
            }
        });
        tv_group_ysq.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GroupFragment.getInstance().refresh3(1);
            }
        });

        // 设置按钮监听
        mTagListView.setOnTagClickListener(itemsOnClick);

        // 设置SelectPicPopupWindow的View
        this.setContentView(mView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(false);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.DengLuTop);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }

    public void setInfo(GroupJson json) {
        mTagListView.removeAllViews();
        mTags.clear();
        for (int i = 0; i < json.group_data.size(); i++) {
            Tag tag = new Tag();
            tag.setId(json.group_data.get(i).group_id);
            tag.setChecked(false);
            tag.setTitle(json.group_data.get(i).group_name);
            mTags.add(tag);
        }
        mTagListView.setTags(mTags,true);
    }

}
