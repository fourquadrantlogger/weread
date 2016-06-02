package com.fengwo.reading.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fengwo.reading.R;

/**
 * 拍照  和发布随笔
 *
 * @author lxq
 */
public class SelectPicPopupWindow extends PopupWindow {

    private Button btn1, btn2, btn_cancel;

    /**
     * context 上下文
     * <p/>
     * itemsOnClick 点击事件
     */
    public SelectPicPopupWindow(Context context, OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_pic, null);

        btn1 = (Button) mView.findViewById(R.id.btn_popupwindow_takephoto);
        btn2 = (Button) mView.findViewById(R.id.btn_popupwindow_pickphoto);
        btn_cancel = (Button) mView.findViewById(R.id.btn_popupwindow_cancel);
        // 取消按钮
        btn_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });

        RelativeLayout relativeLayout = (RelativeLayout) mView
                .findViewById(R.id.rl_popupwindow_layout);
        relativeLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });

        // 设置按钮监听
        btn1.setOnClickListener(itemsOnClick);
        btn2.setOnClickListener(itemsOnClick);
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

    public void setBtnText(String string1, String string2) {
        btn1.setText(string1);
        btn2.setText(string2);
    }

    public void setColor(int color1, int color2) {
        btn1.setTextColor(color1);
        btn2.setTextColor(color2);
    }

    public void setFinish(String string) {
        btn_cancel.setText(string);
    }

    public void setBtn2Visibility(boolean b) {
        if (b) {
            btn2.setVisibility(View.VISIBLE);
        } else {
            btn2.setVisibility(View.GONE);
        }
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }
}
