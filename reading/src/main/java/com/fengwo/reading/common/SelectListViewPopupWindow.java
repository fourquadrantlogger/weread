package com.fengwo.reading.common;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.fengwo.reading.R;

import java.util.ArrayList;
import java.util.List;

public class SelectListViewPopupWindow extends PopupWindow {

    private List<String> list;
    private ItemAdapter adapter;
    private Button btn_cancel;

    public SelectListViewPopupWindow(Context context,
                                     OnItemClickListener onItemClickListener) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_listview,
                null);

        ListView listView = (ListView) mView
                .findViewById(R.id.lv_popupwindow_show);

        list = new ArrayList<>();
        adapter = new ItemAdapter(context, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(onItemClickListener);

        RelativeLayout relativeLayout = (RelativeLayout) mView
                .findViewById(R.id.rl_popupwindow_layout);
        relativeLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });

        btn_cancel = (Button) mView
                .findViewById(R.id.btn_popupwindow_cancel);
        btn_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        // 设置按钮监听
        // TODO
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
        // 设置PopupWindow可触摸
        this.setTouchable(true);
        // 设置非PopupWindow区域可触摸
        this.setOutsideTouchable(true);
        // this.setBackgroundDrawable(new BitmapDrawable());

        this.setTouchInterceptor(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    public void setData(List<String> list) {
        list.clear();
        adapter.notifyDataSetChanged();
        if (list == null || list.size() == 0) {
            return;
        }
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
    }

    public void setData(String[] strings) {
        list.clear();
        adapter.notifyDataSetChanged();
        if (strings == null || strings.length == 0) {
            return;
        }
        for (String s : strings) {
            list.add(s);
        }
        adapter.notifyDataSetChanged();
    }

    public void setData(String string) {
        list.clear();
        adapter.notifyDataSetChanged();
        if (string == null) {
            return;
        }
        list.add(string);
        adapter.notifyDataSetChanged();
    }

    public void addData(List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.list.addAll(list);
        adapter.notifyDataSetChanged();
    }

    public void addData(String[] strings) {
        if (strings == null || strings.length == 0) {
            return;
        }
        for (String s : strings) {
            list.add(s);
        }
        adapter.notifyDataSetChanged();
    }

    public void addData(String string) {
        if (string == null) {
            return;
        }
        list.add(string);
        adapter.notifyDataSetChanged();
    }

    public void setData(int position, String string) {
        if (position >= list.size()) {
            return;
        }
        list.set(position, string);
        adapter.notifyDataSetChanged();
    }

    public void setFinish(String finish) {
        btn_cancel.setText(finish);
    }

}
