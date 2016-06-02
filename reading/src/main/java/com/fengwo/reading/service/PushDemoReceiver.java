package com.fengwo.reading.service;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;

import com.fengwo.reading.activity.MainActivity;
import com.fengwo.reading.main.my.Fragment_My;
import com.fengwo.reading.main.my.MyNotifyBean;
import com.fengwo.reading.myinterface.GlobalParams;
import com.fengwo.reading.utils.CidUtils;
import com.fengwo.reading.utils.MySQLiteOpenHelper;
import com.fengwo.reading.utils.localdata.SPUtils;
import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

/**
 * @author lxq 个推的广播
 */
public class PushDemoReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,
     * 保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        System.out.println("action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(
                        context, taskid, messageid, 90001);
                System.out.println("reading=====第三方回执接口调用" + (result ? "成功" : "失败"));
                if (payload != null) {
                    String data = new String(payload);
                    payloadData.append(data);
                    payloadData.append("\n");
                    System.out.println("reading--data==========" + data);
                    dealWithData(context, data);
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                // TODO
                System.out.println("reading--cid=====" + cid);
                GlobalParams.cid = cid;
                SPUtils.setUserCid(context, cid);
                CidUtils.setCid();
                break;
            case PushConsts.THIRDPART_FEEDBACK:
            /*
             * String appid = bundle.getString("appid"); String taskid =
			 * bundle.getString("taskid"); String actionid =
			 * bundle.getString("actionid"); String result =
			 * bundle.getString("result"); long timestamp =
			 * bundle.getLong("timestamp");
			 * 
			 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo",
			 * "taskid = " + taskid); Log.d("GetuiSdkDemo", "actionid = " +
			 * actionid); Log.d("GetuiSdkDemo", "result = " + result);
			 * Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
			 */
                break;

            default:
                break;
        }
    }

    private void dealWithData(Context context, String data) {
        try {
            MyNotifyBean json = new Gson().fromJson(data, MyNotifyBean.class);
            if (json == null) {
                System.out.println("个推=====json--null");
                return;
            }
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context);
            SQLiteDatabase database = helper.getReadableDatabase();
            ContentValues values = new ContentValues();
            values.put("uid", TextUtils.isEmpty(json.notify_user_id) ? ""
                    : json.notify_user_id);//GlobalParams.uid

            values.put("id", TextUtils.isEmpty(json.id) ? ""
                    : json.id);
            values.put("notify_user_id", TextUtils.isEmpty(json.notify_user_id) ? ""
                    : json.notify_user_id);
            values.put("source", TextUtils.isEmpty(json.source) ? ""
                    : json.source);
            values.put("type", TextUtils.isEmpty(json.type) ? ""
                    : json.type);
            values.put("name", TextUtils.isEmpty(json.name) ? ""
                    : json.name);
            values.put("avatar", TextUtils.isEmpty(json.avatar) ? "" : json.avatar);
            values.put("sex", TextUtils.isEmpty(json.sex) ? "" : json.sex);
            values.put("right", TextUtils.isEmpty(json.right) ? "" : json.right);
            values.put("content", TextUtils.isEmpty(json.content) ? "" : json.content);
            values.put("create_time", TextUtils.isEmpty(json.create_time) ? ""
                    : json.create_time);

            values.put("is_read", "0");

            long l = database.insert("tb_notify", null, values);
            if (l > 0) {
                System.out.println("个推=====insert--ok");
                if (Fragment_My.getInstance().is_notify) {
                    Fragment_My.getInstance().refresh();
                }
            } else {
                System.out.println("个推=====insert--no");
            }
            database.close();
//			helper.close();
            helper = null;
        } catch (Exception e) {
            System.out.println("个推=====insert--error");
        }
    }

}
