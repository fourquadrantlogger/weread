package com.fengwo.reading.main.my.achieve;

import android.content.Context;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CommonHandler;
import com.fengwo.reading.main.my.Fragment_My;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.task.Task_updateuserinfo;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.ImageUtils;
import com.fengwo.reading.utils.MLog;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by timeloveboy on 16/5/5.
 */

public class PopupWindow_usexunzhang extends PopupWindow {
    //region Xunzhang
    ImageView imageView_popupwindow_usexunzhang;
    TextView textView_popupwindow_usexunzhang_badge, textView_popupwindow_usexunzhang_get_rule, textView_popubwindow_usexunzhang_get_rule_copy;//
    Button button_popupwindow_usexunzhang_use;
    boolean list_data;
    int list_data_position;
    //endregion
    public PopupWindow_usexunzhang(Context context, final Xunzhang xunzhang,final boolean list_data,  int list_data_position) {
        super(context);
        this.list_data=list_data;
        this.list_data_position=list_data_position;
        handler = new CommonHandler(context,null);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_popupwindow_usexunzhang, null);
        findViewById(mView);
        Bitmap bitmapsource = ImageUtils.getLoacalBitmap(xunzhang.localPath());
        if (!xunzhang.got) {
            button_popupwindow_usexunzhang_use.setVisibility(View.GONE);
            textView_popupwindow_usexunzhang_get_rule.setVisibility(View.INVISIBLE);
            textView_popubwindow_usexunzhang_get_rule_copy.setVisibility(View.VISIBLE);
        } else {
            if (GlobalParams.userInfoBean.badge_id.equals(xunzhang.id)) {
                button_popupwindow_usexunzhang_use.setText(R.string.不使用勋章);

            } else {
                button_popupwindow_usexunzhang_use.setText(R.string.使用勋章);

            }

            button_popupwindow_usexunzhang_use.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    use=!GlobalParams.userInfoBean.badge_id.equals(xunzhang.id);
                    badge_id=xunzhang.id;
                    getData();
                    new Task_updateuserinfo();
                    dismiss();
                }
            });

        }
        imageView_popupwindow_usexunzhang.setImageBitmap(bitmapsource);
        textView_popupwindow_usexunzhang_badge.setText(xunzhang.badge);
        textView_popupwindow_usexunzhang_get_rule.setText(xunzhang.get_rule);
        textView_popubwindow_usexunzhang_get_rule_copy.setText(xunzhang.get_rule);

        //region 灰暗区域点击dismiss
        RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.rl_popupwindow_layout);
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
//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        // 设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
        //endregion
    }

    void findViewById(View view) {
        imageView_popupwindow_usexunzhang = (ImageView) view.findViewById(R.id.imageView_popupwindow_usexunzhang);
        textView_popupwindow_usexunzhang_badge = (TextView) view.findViewById(R.id.textView_popubwindow_usexunzhang_badge);
        textView_popupwindow_usexunzhang_get_rule = (TextView) view.findViewById(R.id.textView_popubwindow_usexunzhang_get_rule);
        button_popupwindow_usexunzhang_use = (Button) view.findViewById(R.id.button_popupwindow_usexunzhang_use);
        textView_popubwindow_usexunzhang_get_rule_copy = (TextView) view.findViewById(R.id.textView_popubwindow_usexunzhang_get_rule_copy);
    }

    CommonHandler handler;
    private String badge_id;
    private boolean use;
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        map.put("badge_id", badge_id);

        String url=use?"use/badge":"cancel/badge";
        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.SERVERURL + url,
                new RequestCallBack<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0);
                            }
                        }.start();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }.start();
                        String jsonString = responseInfo.result;
                        MLog.v("reading", jsonString);
                        try {
                            BaseJson json = new Gson().fromJson(jsonString, BaseJson.class);
                            if (json.code.equals("1")) {
                                setData();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(2);
                            }
                        }.start();
                    }
                }, true, null);
    }
    private void setData(){
        //// TODO: 16/5/6
        GlobalParams.userInfoBean.badge_id=(use?badge_id:"0");
        if(list_data) {

            Fragment_wodexunzhang.getInstance().adapter_wodexunzhang.notifyDataSetChanged();
            Fragment_wodexunzhang.getInstance().adapter_wodexunzhang_teshu.notifyDataSetChanged();
        }else {
            Fragment_wodexunzhang.getInstance().adapter_wodexunzhang.notifyDataSetChanged();
            Fragment_wodexunzhang.getInstance().adapter_wodexunzhang_teshu.notifyDataSetChanged();
        }
        Fragment_My.getInstance().refresh_userinfo();
    }
}
