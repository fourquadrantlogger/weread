package com.fengwo.reading.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fengwo.reading.R;

/**
 * Created by timeloveboy on 16/3/29.
 */
public class SavePicPopupWindow extends PopupWindow {

    private Button btn1, btn_cancel;

    /**
     * context 上下文
     *
     * itemsOnClick 点击事件
     */
    public SavePicPopupWindow(Context context, View.OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_savepic, null);

        btn1 = (Button) mView.findViewById(R.id.btn_popupwindow_savephoto);
        btn_cancel = (Button) mView.findViewById(R.id.btn_popupwindow_cancel);

        RelativeLayout relativeLayout = (RelativeLayout) mView
                .findViewById(R.id.rl_popupwindow_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        // 取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        // 设置按钮监听
        btn1.setOnClickListener(itemsOnClick);

        // 设置SelectPicPopupWindow的View
        this.setContentView(mView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    public void setBtnText(String string1) {
        btn1.setText(string1);
    }


    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }
}
