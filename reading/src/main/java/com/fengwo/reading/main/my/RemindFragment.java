package com.fengwo.reading.main.my;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.alarmclock.Alarm;
import com.fengwo.reading.alarmclock.AlarmClockManager;
import com.fengwo.reading.alarmclock.AlarmHandle;
import com.fengwo.reading.alarmclock.AlarmUtils;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.CustomProgressDialog;
import com.fengwo.reading.common.SelectTimePopupWindow;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.task.config.Bean_nao_ling;
import com.fengwo.reading.utils.CustomToast;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.utils.MLog;
import com.fengwo.reading.utils.localdata.NOsqlUtil;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.fengwo.reading.utils.VersionUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lxq 早晚读设置
 */
public class RemindFragment extends Fragment implements OnClickListener {

    private ImageView iv_title_left;
    private TextView tv_title_left, tv_title_mid, tv_title_right;

    private RelativeLayout rl_remind_am, rl_remind_pm;
    private TextView tv_remind_am, tv_remind_pm;

    private Button bt_remind_switch;
    private boolean remind_OnorOff;
    private SelectTimePopupWindow timePopupWindow;
    private CustomProgressDialog progressDialog;
    private boolean isAmOrPm;

    private LinearLayout ll_remind_show;

    public int type;

    private View saveView = null;
    public boolean needSaveView = false;

    public RemindFragment() {
    }

    public static RemindFragment fragment = new RemindFragment();

    public static RemindFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater
                .inflate(R.layout.fragment_remind, container, false);

        findViewById(view);
        onClickListener();

        progressDialog = CustomProgressDialog.createDialog(fragment
                .getActivity());

        iv_title_left.setVisibility(View.GONE);
        tv_title_left.setVisibility(View.VISIBLE);
        tv_title_left.setText("取消");
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText("早晚读设置");
        tv_title_right.setVisibility(View.VISIBLE);
        tv_title_right.setText("保存");

        timePopupWindow = new SelectTimePopupWindow(fragment.getActivity(),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (isAmOrPm) {
                            tv_remind_am.setText(timePopupWindow.getTime());
                        } else {
                            tv_remind_pm.setText(timePopupWindow.getTime());
                        }
                        timePopupWindow.dismiss();
                    }
                });
        //设置闹铃时间
        tv_remind_am.setText(TextUtils.isEmpty(NOsqlUtil.get_naoling().atime) ? "07:00" : NOsqlUtil.get_naoling().atime);
        tv_remind_pm.setText(TextUtils.isEmpty(NOsqlUtil.get_naoling().ptime) ? "22:00" : NOsqlUtil.get_naoling().ptime);
        //闹铃是否开启
        remind_OnorOff = (NOsqlUtil.get_naoling().is_tishi.equals("1")) ? true : false;
        bt_remind_switch.setBackgroundResource(remind_OnorOff ? R.drawable.switchbutton_on : R.drawable.switchbutton_off);

        if (remind_OnorOff) {
            ll_remind_show.setVisibility(View.VISIBLE);
        } else {
            ll_remind_show.setVisibility(View.GONE);
        }
        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_left = (TextView) view.findViewById(R.id.tv_title_left);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        tv_title_right = (TextView) view.findViewById(R.id.tv_title_right);

        rl_remind_am = (RelativeLayout) view.findViewById(R.id.rl_remind_am);
        rl_remind_pm = (RelativeLayout) view.findViewById(R.id.rl_remind_pm);
        tv_remind_am = (TextView) view.findViewById(R.id.tv_remind_am);
        tv_remind_pm = (TextView) view.findViewById(R.id.tv_remind_pm);

        bt_remind_switch = (Button) view.findViewById(R.id.bt_remind_switch);
        ll_remind_show = (LinearLayout) view.findViewById(R.id.ll_remind_show);
    }

    private void onClickListener() {
        tv_title_left.setOnClickListener(this);
        tv_title_right.setOnClickListener(this);
        rl_remind_am.setOnClickListener(this);
        rl_remind_pm.setOnClickListener(this);
        bt_remind_switch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.tv_title_left:
                switch (type) {
                    case 0:
                        fragment.getActivity().getSupportFragmentManager()
                                .popBackStack();
                        break;
                    case 1:
                        fragment.getActivity().finish();
                        fragment.getActivity().overridePendingTransition(
                                R.anim.in_from_top, R.anim.out_to_bottom);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.tv_title_right:
                //保存
                if (TextUtils.isEmpty(tv_remind_am.getText().toString())
                        && TextUtils.isEmpty(tv_remind_pm.getText().toString())) {
                    CustomToast.showToast(context, "请先设置时间");
                    return;
                }
                getData();
                break;
            case R.id.rl_remind_am:
                //设置 早读 时间
                isAmOrPm = true;
                timePopupWindow.showAtLocation(
                        fragment.getActivity().findViewById(R.id.ll_remind),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                if (!TextUtils.isEmpty(tv_remind_am.getText().toString())) {
                    String[] strings = tv_remind_am.getText().toString().split(":");
                    try {
                        int hour = Integer.valueOf(strings[0]);
                        int minute = Integer.valueOf(strings[1]);
                        timePopupWindow.setTime(hour, minute);
                    } catch (Exception e) {
                    }
                }
                break;
            case R.id.rl_remind_pm:
                //设置 晚读 时间
                isAmOrPm = false;
                timePopupWindow.showAtLocation(
                        fragment.getActivity().findViewById(R.id.ll_remind),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                if (!TextUtils.isEmpty(tv_remind_pm.getText().toString())) {
                    String[] strings = tv_remind_pm.getText().toString().split(":");
                    try {
                        int hour = Integer.valueOf(strings[0]);
                        int minute = Integer.valueOf(strings[1]);
                        timePopupWindow.setTime(hour, minute);
                    } catch (Exception e) {
                    }
                }
                break;
            case R.id.bt_remind_switch:
                //闹铃开关
                remind_OnorOff = !remind_OnorOff;
                bt_remind_switch
                        .setBackgroundResource(remind_OnorOff ? R.drawable.switchbutton_on
                                : R.drawable.switchbutton_off);
                if (remind_OnorOff) {
                    ll_remind_show.setVisibility(View.VISIBLE);
                } else {
                    ll_remind_show.setVisibility(View.GONE);
                }
            default:
                break;
        }
    }

    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);
        if (!TextUtils.isEmpty(tv_remind_am.getText().toString())) {
            map.put("atime", tv_remind_am.getText().toString());
        }
        if (!TextUtils.isEmpty(tv_remind_pm.getText().toString())) {
            map.put("ptime", tv_remind_pm.getText().toString());
        }
        //是否设置闹铃提醒 1:开启  0:关闭
        map.put("is_tishi", remind_OnorOff ? "1" : "0");
        HttpParamsUtil.sendData(map, GlobalParams.uid, GlobalConstant.set_time,
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
                    public void onFailure(HttpException arg0, String error) {
                        new Thread() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                                handler.sendEmptyMessage(2);
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
                        MLog.v("reading", GlobalParams.uid + ":" + jsonString);
                        try {
                            final BaseJson json = new Gson().fromJson(jsonString, BaseJson.class);
                            if ("1".equals(json.code)) {
                                Context context = fragment.getActivity();
                                if (context != null && !((Activity) context).isFinishing()) {

                                    switch (type) {
                                        case 0:
                                            fragment.getActivity()
                                                    .getSupportFragmentManager()
                                                    .popBackStack();
                                            break;
                                        case 1:
                                            fragment.getActivity().finish();
                                            fragment.getActivity()
                                                    .overridePendingTransition(
                                                            R.anim.in_from_top,
                                                            R.anim.out_to_bottom);
                                            break;

                                        default:
                                            break;
                                    }
                                    //设置状态
                                    addAlarm();
                                }
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null
                                        && !((Activity) context).isFinishing()) {
                                    CustomToast.showToast(context, json.msg);
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null
                                    && !((Activity) context).isFinishing()) {
                                CustomToast.showToast(context,
                                        context.getString(R.string.json_error));
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 闹铃是否打开
     */
    private void addAlarm() {
        Context context = fragment.getActivity();
        if (context == null) {
            return;
        }
        deleteAlarm(context);
        //数据
        Bean_nao_ling bean = NOsqlUtil.get_naoling();
        bean.atime = tv_remind_am.getText().toString();//早上提示时间
        bean.ptime = tv_remind_pm.getText().toString();//晚上提示时间
        bean.is_tishi = "0";// 闹铃是否提醒，1开，0关
        if (remind_OnorOff) {
            bean.is_tishi = "1";// 闹铃是否提醒，1开，0关
            addAlarm(true, tv_remind_am.getText().toString(), context);
            addAlarm(false, tv_remind_pm.getText().toString(), context);
        }
        NOsqlUtil.set_naoling(bean);
    }

    /**
     * 闹铃关闭
     */
    private void deleteAlarm(Context context) {
        List<Alarm> alarms = AlarmHandle.getAlarms(context);
//        System.out.println("===========" + alarms.size());
        if (alarms != null && alarms.size() != 0) {
            for (int i = 0; i < alarms.size(); i++) {
                AlarmClockManager.cancelAlarm(context, alarms.get(i).id);
                AlarmHandle.deleteAlarm(context, alarms.get(i).id);
            }
        }
        AlarmHandle.deleteAllAlarm(context);
    }

    /**
     * 闹铃打开
     *
     * @param is_ampm true:早读  false:晚读
     * @param time    闹铃时间
     */
    public void addAlarm(boolean is_ampm, String time, Context context) {
        if (TextUtils.isEmpty(time)) {
            return;
        }
        try {
            String[] times = time.split(":");
            int hour = Integer.valueOf(times[0]);
            int minute = Integer.valueOf(times[1]);
            Alarm alarm = new Alarm();
            alarm.hour = hour;
            alarm.minutes = minute;
//            System.out.println("==========" + hour);
//            System.out.println("==========" + minute);
            alarm.repeat = "0,1,2,3,4,5,6";
            alarm.bell = AlarmUtils.getDefaultbell(context);
            alarm.label = is_ampm ? "早读提醒" : "晚读提醒";
            alarm.nextMillis = 0;
            alarm.createTime = System.currentTimeMillis();
            alarm.uid = GlobalParams.uid;
            // 插入
            AlarmHandle.addAlarm(context, alarm);
            // 打开闹钟
            AlarmClockManager.setAlarm(context, alarm, false);
        } catch (Exception e) {
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 2:
                    Context context = fragment.getActivity();
                    if (context != null && !((Activity) context).isFinishing()) {
                        CustomToast.showToast(context,
                                context.getString(R.string.network_check));
                    }
                    break;
                case 1:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                case 0:
                    if (progressDialog != null && !progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    public void onResume() {
        super.onResume();
        saveView = getView();
        MobclickAgent.onPageStart("RemindFragment");
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RemindFragment");
    }

}
