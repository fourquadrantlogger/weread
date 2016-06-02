package com.fengwo.reading.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.player.PlayerService;
import com.fengwo.reading.utils.MLog;

import java.util.Date;

import cn.reflector.SmoothCheckBox;

/**
 *
 */
public class PopupWindow_MediaClock extends PopupWindow implements View.OnClickListener {


    TextView  textView_popupwindow_mediaclock_noclock,textView_popupwindow_mediaclock_0,textView_popupwindow_mediaclock_10,
            textView_popupwindow_mediaclock_30,textView_popupwindow_mediaclock_60,textView_popupwindow_mediaclock_90;
    TextView textView_popupwindow_mediaclock_existtime_10,textView_popupwindow_mediaclock_existtime_30,textView_popupwindow_mediaclock_existtime_60,textView_popupwindow_mediaclock_existtime_90;
    SmoothCheckBox checkbox_popupwindow_mediaclock_noclock,checkbox_popupwindow_mediaclock_0,checkbox_popupwindow_mediaclock_10,
            checkbox_popupwindow_mediaclock_30,checkbox_popupwindow_mediaclock_60,checkbox_popupwindow_mediaclock_90;
    RelativeLayout relativelayout_popupwindow_mediaclock_noclock,relativelayout_popupwindow_mediaclock_0,relativelayout_popupwindow_mediaclock_10,relativelayout_popupwindow_mediaclock_30,relativelayout_popupwindow_mediaclock_60,relativelayout_popupwindow_mediaclock_90;

    public PopupWindow_MediaClock(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_mediaclock, null);
        findViewById(mView);
        select(PlayerService.getMediaclock_length());
        existReceiver = new ExisttimeReceiver();
        IntentFilter currentFilter = new IntentFilter();
        currentFilter.addAction("MediaClock");
        context.registerReceiver(existReceiver, currentFilter);
        //region 灰暗区域点击dismiss
        RelativeLayout relativeLayout = (RelativeLayout) mView
                .findViewById(R.id.rl_popupwindow_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });

        this.setContentView(mView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }
    private ExisttimeReceiver existReceiver;
    public int exist_second=0;
    public String exist_str(){
        int minite = (exist_second / 60);
        int second = (exist_second - minite * 60) ;

        String now = (minite > 9 ? minite : "0" + minite) + ":" + (second > 9 ? second : "0" + second);
        return "倒计时 "+now;
    }
    private class ExisttimeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            exist_second = intent.getIntExtra("exist_second", -1);
            MLog.v("reading", "exist_second" + exist_second);

            textView_popupwindow_mediaclock_existtime_10.setText( exist_str());
            textView_popupwindow_mediaclock_existtime_30.setText(exist_str());
            textView_popupwindow_mediaclock_existtime_60.setText(exist_str());
            textView_popupwindow_mediaclock_existtime_90.setText(exist_str());
        }
    }

    @Override
    public void onClick(View v) {
        int clockminite = Integer.parseInt(v.getTag().toString());
        select(clockminite);
        PlayerService.starttime = new Date().getTime();
        PlayerService.setMediaclock_length(clockminite);
    }

    void findViewById(View view) {
        checkbox_popupwindow_mediaclock_noclock = (SmoothCheckBox) view.findViewById(R.id.checkbox_popupwindow_mediaclock_noclock);
        checkbox_popupwindow_mediaclock_0 = (SmoothCheckBox) view.findViewById(R.id.checkbox_popupwindow_mediaclock_0);
        checkbox_popupwindow_mediaclock_10 = (SmoothCheckBox) view.findViewById(R.id.checkbox_popupwindow_mediaclock_10);

        checkbox_popupwindow_mediaclock_30 = (SmoothCheckBox) view.findViewById(R.id.checkbox_popupwindow_mediaclock_30);
        checkbox_popupwindow_mediaclock_60 = (SmoothCheckBox) view.findViewById(R.id.checkbox_popupwindow_mediaclock_60);
        checkbox_popupwindow_mediaclock_90 = (SmoothCheckBox) view.findViewById(R.id.checkbox_popupwindow_mediaclock_90);

        textView_popupwindow_mediaclock_noclock = (TextView) view.findViewById(R.id.textView_popupwindow_mediaclock_noclock);
        textView_popupwindow_mediaclock_0 = (TextView) view.findViewById(R.id.textView_popupwindow_mediaclock_0);
        textView_popupwindow_mediaclock_10 = (TextView) view.findViewById(R.id.textView_popupwindow_mediaclock_10);


        textView_popupwindow_mediaclock_existtime_10=(TextView)view.findViewById(R.id.textView_popupwindow_mediaclock_existtime_10);
        textView_popupwindow_mediaclock_existtime_30=(TextView)view.findViewById(R.id.textView_popupwindow_mediaclock_existtime_30);
        textView_popupwindow_mediaclock_existtime_60=(TextView)view.findViewById(R.id.textView_popupwindow_mediaclock_existtime_60);
        textView_popupwindow_mediaclock_existtime_90=(TextView)view.findViewById(R.id.textView_popupwindow_mediaclock_existtime_90);

        relativelayout_popupwindow_mediaclock_noclock=(RelativeLayout)view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_noclock);
        relativelayout_popupwindow_mediaclock_0=(RelativeLayout)view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_0);
        relativelayout_popupwindow_mediaclock_10=(RelativeLayout)view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_10);

        textView_popupwindow_mediaclock_30 = (TextView) view.findViewById(R.id.textView_popupwindow_mediaclock_30);
        textView_popupwindow_mediaclock_60 = (TextView) view.findViewById(R.id.textView_popupwindow_mediaclock_60);
        textView_popupwindow_mediaclock_90 = (TextView) view.findViewById(R.id.textView_popupwindow_mediaclock_90);


        relativelayout_popupwindow_mediaclock_noclock = (RelativeLayout) view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_noclock);
        relativelayout_popupwindow_mediaclock_0 = (RelativeLayout) view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_0);
        relativelayout_popupwindow_mediaclock_10 = (RelativeLayout) view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_10);

        relativelayout_popupwindow_mediaclock_30 = (RelativeLayout) view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_30);
        relativelayout_popupwindow_mediaclock_60 = (RelativeLayout) view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_60);
        relativelayout_popupwindow_mediaclock_90 = (RelativeLayout) view.findViewById(R.id.relativeLayout_popupwindow_mediaclock_90);

        relativelayout_popupwindow_mediaclock_noclock.setOnClickListener(this);
        relativelayout_popupwindow_mediaclock_0.setOnClickListener(this);
        relativelayout_popupwindow_mediaclock_10.setOnClickListener(this);

        relativelayout_popupwindow_mediaclock_30.setOnClickListener(this);
        relativelayout_popupwindow_mediaclock_60.setOnClickListener(this);
        relativelayout_popupwindow_mediaclock_90.setOnClickListener(this);

        relativelayout_popupwindow_mediaclock_noclock.setTag(-1);
        relativelayout_popupwindow_mediaclock_0.setTag(0);
        relativelayout_popupwindow_mediaclock_10.setTag(10);

        relativelayout_popupwindow_mediaclock_30.setTag(30);
        relativelayout_popupwindow_mediaclock_60.setTag(60);
        relativelayout_popupwindow_mediaclock_90.setTag(90);
    }

    public void select(Integer minites) {
        setCheckfalse();
        switch (minites) {
            case -1:
                checkbox_popupwindow_mediaclock_noclock.setChecked(true);
                textView_popupwindow_mediaclock_noclock.setTextColor(Color.parseColor("#76e19b"));
                break;
            case 0:
                checkbox_popupwindow_mediaclock_0.setChecked(true);
                textView_popupwindow_mediaclock_0.setTextColor(Color.parseColor("#76e19b"));
                break;
            case 10:
                checkbox_popupwindow_mediaclock_10.setChecked(true);
                textView_popupwindow_mediaclock_10.setTextColor(Color.parseColor("#76e19b"));

                textView_popupwindow_mediaclock_existtime_10.setVisibility(View.VISIBLE);

                break;
            case 30:
                checkbox_popupwindow_mediaclock_30.setChecked(true);
                textView_popupwindow_mediaclock_30.setTextColor(Color.parseColor("#76e19b"));
                textView_popupwindow_mediaclock_existtime_30.setVisibility(View.VISIBLE);
                break;
            case 60:
                checkbox_popupwindow_mediaclock_60.setChecked(true);
                textView_popupwindow_mediaclock_60.setTextColor(Color.parseColor("#76e19b"));
                textView_popupwindow_mediaclock_existtime_60.setVisibility(View.VISIBLE);
                break;
            case 90:
                checkbox_popupwindow_mediaclock_90.setChecked(true);
                textView_popupwindow_mediaclock_90.setTextColor(Color.parseColor("#76e19b"));
                textView_popupwindow_mediaclock_existtime_90.setVisibility(View.VISIBLE);
                break;
        }
    }

    void setCheckfalse() {
        checkbox_popupwindow_mediaclock_noclock.setChecked(false);
        checkbox_popupwindow_mediaclock_0.setChecked(false);
        checkbox_popupwindow_mediaclock_10.setChecked(false);
        checkbox_popupwindow_mediaclock_30.setChecked(false);
        checkbox_popupwindow_mediaclock_60.setChecked(false);
        checkbox_popupwindow_mediaclock_90.setChecked(false);

        textView_popupwindow_mediaclock_existtime_10.setVisibility(View.INVISIBLE);
        textView_popupwindow_mediaclock_existtime_30.setVisibility(View.INVISIBLE);
        textView_popupwindow_mediaclock_existtime_60.setVisibility(View.INVISIBLE);
        textView_popupwindow_mediaclock_existtime_90.setVisibility(View.INVISIBLE);

        textView_popupwindow_mediaclock_noclock.setTextColor(Color.parseColor("#333333"));
        textView_popupwindow_mediaclock_0.setTextColor(Color.parseColor("#333333"));
        textView_popupwindow_mediaclock_10.setTextColor(Color.parseColor("#333333"));
        textView_popupwindow_mediaclock_30.setTextColor(Color.parseColor("#333333"));
        textView_popupwindow_mediaclock_60.setTextColor(Color.parseColor("#333333"));
        textView_popupwindow_mediaclock_90.setTextColor(Color.parseColor("#333333"));
    }
}
