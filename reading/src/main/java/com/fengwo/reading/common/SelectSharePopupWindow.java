package com.fengwo.reading.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fengwo.reading.R;

public class SelectSharePopupWindow extends PopupWindow {
    // 分享的信息
    public String title = "";
    public String content = "";
    public String imageUrl = "";
    public String h5Url = "";

    public SelectSharePopupWindow(Context context,
                                  OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_share, null);
        LinearLayout wx = (LinearLayout) mView
                .findViewById(R.id.ll_popupwindow_wx);
        LinearLayout pyq = (LinearLayout) mView
                .findViewById(R.id.ll_popupwindow_pyq);
        LinearLayout qq = (LinearLayout) mView
                .findViewById(R.id.ll_popupwindow_qq);
        LinearLayout wb = (LinearLayout) mView
                .findViewById(R.id.ll_popupwindow_wb);

        RelativeLayout relativeLayout = (RelativeLayout) mView
                .findViewById(R.id.rl_popupwindow_layout);
        relativeLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });

        Button btn_cancel = (Button) mView
                .findViewById(R.id.btn_popupwindow_cancel);
        btn_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        // 设置按钮监听
        wx.setOnClickListener(itemsOnClick);
        pyq.setOnClickListener(itemsOnClick);
        qq.setOnClickListener(itemsOnClick);
        wb.setOnClickListener(itemsOnClick);
        // 设置SelectPicPopupWindow的View
        this.setContentView(mView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

}
