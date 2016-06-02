package com.fengwo.reading.main.discover;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengwo.reading.R;
import com.fengwo.reading.bean.BaseJson;
import com.fengwo.reading.common.PrivilegeDialog;
import com.fengwo.reading.main.read.Fragment_BookList;
import com.fengwo.reading.myinterface.GlobalConstant;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.HttpParamsUtil;
import com.fengwo.reading.zxing.encoding.EncodingHandler;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 发现 - 往期书单 - 解锁更多共读
 */
public class PrivilegeFragment extends Fragment implements OnClickListener {

    private PrivilegeDialog dialog;

    private ImageView iv_title_left, iv_privilege;
    private TextView tv_title_mid;

    Bitmap qrCodeBitmap = null;
    private String content = ""; // 类型|用户id|时间戳(秒)
    private int type; //类型 1:刷新二维码 2:前往往期书单

    private View saveView = null;
    public boolean needSaveView = false;

    public PrivilegeFragment() {
    }

    public static PrivilegeFragment fragment = new PrivilegeFragment();

    public static PrivilegeFragment getInstance() {
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (needSaveView && saveView != null) {
            return saveView;
        }
        needSaveView = true;

        View view = inflater.inflate(R.layout.fragment_privilege, container, false);
        findViewById(view);
        setTitle();
        setPrivilege();

        return view;
    }

    private void findViewById(View view) {
        iv_title_left = (ImageView) view.findViewById(R.id.iv_return);
        tv_title_mid = (TextView) view.findViewById(R.id.tv_title_mid);
        iv_privilege = (ImageView) view.findViewById(R.id.iv_privilege);

        iv_title_left.setOnClickListener(this);
    }

    private void setTitle() {
        tv_title_mid.setVisibility(View.VISIBLE);
        tv_title_mid.setText("解锁更多共读");
    }

    private void setPrivilege() {
        if (GlobalParams.time == 0 || System.currentTimeMillis() - GlobalParams.time >= 300 || qrCodeBitmap == null) {
            GlobalParams.time = System.currentTimeMillis() / 1000;
            //生成二维码
            content = "pack|" + GlobalParams.uid + "|" + GlobalParams.time;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!"".equals(content)) {
                        try {
                            qrCodeBitmap = EncodingHandler.createQRCode(content, 400);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                        if (qrCodeBitmap != null) {
                            iv_privilege.setImageBitmap(qrCodeBitmap);
                        } else {
                            Toast.makeText(getActivity(), "二维码生成错误", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Text can not be empty",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000);
        } else {
            iv_privilege.setImageBitmap(qrCodeBitmap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            default:
                break;
        }
    }

    /**
     * 网络请求 - 轮询是否解锁特权
     */
    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", GlobalParams.uid);

        HttpParamsUtil.sendData(map, GlobalParams.uid,
                GlobalConstant.user_islock, new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonString = responseInfo.result;
                        try {
                            BaseJson json = new Gson().fromJson(jsonString,
                                    BaseJson.class);
                            if ("1".equals(json.code)) {
                                //解锁成功
                                PrivilegeUtil.stopPollingService(getActivity(), PrivilegeService.class, PrivilegeService.ACTION);
                                Fragment_BookList.getInstance().refresh();
                                type = 2;
                                dialog = new PrivilegeDialog(fragment.getActivity(),
                                        new OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                                getActivity().getSupportFragmentManager().popBackStack();
                                            }
                                        }, type);
                                dialog.show();
                            } else {
                                Context context = fragment.getActivity();
                                if (context != null) {
//                                    Toast.makeText(context, json.msg,
//                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Context context = fragment.getActivity();
                            if (context != null) {
//                                Toast.makeText(context,
//                                        context.getString(R.string.json_error),
//                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, true, null);
    }

    /**
     * 轮询检测解锁
     */
    public void refresh() {
        getData();
    }

    /**
     * 二维码超时,弹框
     */
    public void refresh1() {
        PrivilegeUtil.stopPollingService(getActivity(), PrivilegeService.class, PrivilegeService.ACTION);
        type = 1;
        dialog = new PrivilegeDialog(fragment.getActivity(),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        setPrivilege();
                        PrivilegeUtil.startPollingService(getActivity(), 2, PrivilegeService.class, PrivilegeService.ACTION);
                    }
                }, type);
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        PrivilegeUtil.startPollingService(getActivity(), 2, PrivilegeService.class, PrivilegeService.ACTION);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PrivilegeFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        saveView = getView();
        Intent intent = new Intent(getActivity(), PrivilegeService.class);
        getActivity().stopService(intent);
        PrivilegeUtil.stopPollingService(getActivity(), PrivilegeService.class, PrivilegeService.ACTION);
        MobclickAgent.onPageEnd("PrivilegeFragment");
    }

}
